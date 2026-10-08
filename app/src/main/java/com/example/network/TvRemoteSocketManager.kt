package com.example.network

import android.content.Context
import android.util.Log
import com.example.model.ClickType
import com.example.model.DiscoveredTv
import com.example.model.RemoteCommand
import com.example.model.TouchEventType
import com.example.model.TvAppState
import com.example.model.TvKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CopyOnWriteArrayList

class TvRemoteSocketManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val onCommandReceived: (RemoteCommand) -> Unit,
    private val onStateSyncReceived: (TvAppState) -> Unit,
    private val onConnectionChanged: (Boolean, String) -> Unit,
    private val onTvDiscovered: (DiscoveredTv) -> Unit
) {
    companion object {
        const val TCP_PORT = 8989
        const val UDP_DISCOVERY_PORT = 8988
        private const val TAG = "TvSocketManager"

        // Singleton memory bridge for emulator / single-device testing
        var sharedTvState: TvAppState? = null
        var activeCommandReceiver: ((RemoteCommand) -> Unit)? = null
    }

    private var serverJob: Job? = null
    private var udpBroadcastJob: Job? = null
    private var clientJob: Job? = null
    private var udpDiscoveryJob: Job? = null

    private var serverSocket: ServerSocket? = null
    private val connectedClientWriters = CopyOnWriteArrayList<PrintWriter>()

    private var clientSocket: Socket? = null
    private var clientWriter: PrintWriter? = null

    var isConnectedAsClient = false
        private set
    var isServerRunning = false
        private set

    // =========================================================================
    // TV BOX / SERVER MODE
    // =========================================================================

    fun startTvServer(pin: String) {
        stopAll()
        activeCommandReceiver = onCommandReceived

        serverJob = scope.launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(TCP_PORT)
                serverSocket?.reuseAddress = true
                isServerRunning = true
                Log.d(TAG, "TV Server started on port $TCP_PORT")

                // Start UDP broadcaster
                startUdpBroadcasting(pin)

                while (isActive && !serverSocket!!.isClosed) {
                    val client = serverSocket!!.accept()
                    Log.d(TAG, "New client connected: ${client.inetAddress.hostAddress}")
                    handleClientConnection(client)
                }
            } catch (e: Exception) {
                if (isActive) {
                    Log.e(TAG, "Server error: ${e.message}")
                }
            } finally {
                isServerRunning = false
            }
        }
    }

    private fun handleClientConnection(socket: Socket) {
        scope.launch(Dispatchers.IO) {
            var writer: PrintWriter? = null
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                writer = PrintWriter(socket.getOutputStream(), true)
                connectedClientWriters.add(writer)

                onConnectionChanged(true, socket.inetAddress.hostAddress ?: "Client")

                // Send current state immediately
                sharedTvState?.let { state ->
                    writer.println(serializeTvState(state))
                }

                while (isActive && socket.isConnected) {
                    val line = reader.readLine() ?: break
                    if (line.isNotEmpty()) {
                        parseCommandJson(line)?.let { cmd ->
                            withContext(Dispatchers.Main) {
                                onCommandReceived(cmd)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Client socket closed: ${e.message}")
            } finally {
                writer?.let { connectedClientWriters.remove(it) }
                try { socket.close() } catch (_: Exception) {}
                onConnectionChanged(connectedClientWriters.isNotEmpty(), "")
            }
        }
    }

    private fun startUdpBroadcasting(pin: String) {
        udpBroadcastJob = scope.launch(Dispatchers.IO) {
            val ip = NetworkUtils.getLocalIpAddress(context)
            val msg = "ESHARE_TV_BEACON|Android TV Box|$ip|$TCP_PORT|$pin"
            val data = msg.toByteArray()

            while (isActive) {
                try {
                    DatagramSocket().use { ds ->
                        ds.broadcast = true
                        val broadcastAddr = InetAddress.getByName("255.255.255.255")
                        val packet = DatagramPacket(data, data.size, broadcastAddr, UDP_DISCOVERY_PORT)
                        ds.send(packet)
                    }
                } catch (_: Exception) {
                }
                delay(2000)
            }
        }
    }

    fun broadcastStateToClients(state: TvAppState) {
        sharedTvState = state
        val json = serializeTvState(state)
        scope.launch(Dispatchers.IO) {
            for (writer in connectedClientWriters) {
                try {
                    writer.println(json)
                } catch (_: Exception) {}
            }
        }
    }

    // =========================================================================
    // MOBILE / CLIENT MODE
    // =========================================================================

    fun startClientDiscovery() {
        udpDiscoveryJob?.cancel()
        udpDiscoveryJob = scope.launch(Dispatchers.IO) {
            var socket: DatagramSocket? = null
            try {
                socket = DatagramSocket(UDP_DISCOVERY_PORT).apply {
                    broadcast = true
                    reuseAddress = true
                }
                val buffer = ByteArray(1024)
                while (isActive) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    val text = String(packet.data, 0, packet.length)
                    if (text.startsWith("ESHARE_TV_BEACON")) {
                        val parts = text.split("|")
                        if (parts.size >= 5) {
                            val tv = DiscoveredTv(
                                name = parts[1],
                                ipAddress = parts[2],
                                port = parts[3].toIntOrNull() ?: TCP_PORT,
                                pin = parts[4]
                            )
                            withContext(Dispatchers.Main) {
                                onTvDiscovered(tv)
                            }
                        }
                    }
                }
            } catch (_: Exception) {
            } finally {
                socket?.close()
            }
        }
    }

    fun connectToTv(ip: String, port: Int = TCP_PORT, onResult: (Boolean, String) -> Unit) {
        clientJob?.cancel()
        clientJob = scope.launch(Dispatchers.IO) {
            try {
                val socket = Socket(ip, port)
                socket.tcpNoDelay = true
                clientSocket = socket
                clientWriter = PrintWriter(socket.getOutputStream(), true)
                isConnectedAsClient = true

                withContext(Dispatchers.Main) {
                    onConnectionChanged(true, ip)
                    onResult(true, "پەیوەست بوو بە $ip")
                }

                // Send ping
                sendCommand(RemoteCommand.Ping("Mobile Client", System.currentTimeMillis()))

                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                while (isActive && socket.isConnected) {
                    val line = reader.readLine() ?: break
                    parseTvStateJson(line)?.let { state ->
                        withContext(Dispatchers.Main) {
                            onStateSyncReceived(state)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Connection failed: ${e.message}")
                isConnectedAsClient = false
                withContext(Dispatchers.Main) {
                    onConnectionChanged(false, "")
                    onResult(false, "نەتوانرا پەیوەندی دروست بکرێت: ${e.localizedMessage ?: "هەڵە"}")
                }
            } finally {
                isConnectedAsClient = false
                onConnectionChanged(false, "")
            }
        }
    }

    fun disconnectClient() {
        try {
            clientSocket?.close()
        } catch (_: Exception) {}
        clientSocket = null
        clientWriter = null
        isConnectedAsClient = false
        onConnectionChanged(false, "")
    }

    fun sendCommand(command: RemoteCommand) {
        // First check internal bridge (for in-app / emulator testing or when connected locally)
        activeCommandReceiver?.let { receiver ->
            scope.launch(Dispatchers.Main) {
                receiver(command)
            }
        }

        // Send over TCP socket if connected
        val writer = clientWriter
        if (writer != null && isConnectedAsClient) {
            scope.launch(Dispatchers.IO) {
                try {
                    val json = serializeCommand(command)
                    writer.println(json)
                } catch (e: Exception) {
                    Log.e(TAG, "Send command failed: ${e.message}")
                }
            }
        }
    }

    fun stopAll() {
        serverJob?.cancel()
        udpBroadcastJob?.cancel()
        clientJob?.cancel()
        udpDiscoveryJob?.cancel()
        try {
            serverSocket?.close()
        } catch (_: Exception) {}
        for (w in connectedClientWriters) {
            try { w.close() } catch (_: Exception) {}
        }
        connectedClientWriters.clear()
        try {
            clientSocket?.close()
        } catch (_: Exception) {}
        clientSocket = null
        clientWriter = null
        isServerRunning = false
        isConnectedAsClient = false
    }

    // =========================================================================
    // JSON PROTOCOL HELPERS
    // =========================================================================

    private fun serializeCommand(cmd: RemoteCommand): String {
        val json = JSONObject()
        when (cmd) {
            is RemoteCommand.MouseMove -> {
                json.put("t", "MM")
                json.put("dx", cmd.dx.toDouble())
                json.put("dy", cmd.dy.toDouble())
            }
            is RemoteCommand.MouseClick -> {
                json.put("t", "MC")
                json.put("ct", cmd.clickType.name)
            }
            is RemoteCommand.MouseScroll -> {
                json.put("t", "MS")
                json.put("dy", cmd.deltaY.toDouble())
            }
            is RemoteCommand.KeyAction -> {
                json.put("t", "KEY")
                json.put("k", cmd.key.name)
            }
            is RemoteCommand.NumpadAction -> {
                json.put("t", "NUM")
                json.put("d", cmd.digit)
            }
            is RemoteCommand.TextInput -> {
                json.put("t", "TXT")
                json.put("text", cmd.text)
            }
            is RemoteCommand.LaunchApp -> {
                json.put("t", "APP")
                json.put("id", cmd.appId)
            }
            is RemoteCommand.ScreenTouch -> {
                json.put("t", "TOUCH")
                json.put("x", cmd.xPercent.toDouble())
                json.put("y", cmd.yPercent.toDouble())
                json.put("et", cmd.eventType.name)
            }
            is RemoteCommand.Ping -> {
                json.put("t", "PING")
                json.put("cl", cmd.clientName)
                json.put("ts", cmd.timestamp)
            }
            is RemoteCommand.RequestScreenSync -> {
                json.put("t", "SYNC")
                json.put("ts", cmd.timestamp)
            }
        }
        return json.toString()
    }

    private fun parseCommandJson(str: String): RemoteCommand? {
        return try {
            val json = JSONObject(str)
            when (json.optString("t")) {
                "MM" -> RemoteCommand.MouseMove(
                    json.optDouble("dx", 0.0).toFloat(),
                    json.optDouble("dy", 0.0).toFloat()
                )
                "MC" -> RemoteCommand.MouseClick(
                    ClickType.valueOf(json.optString("ct", "LEFT"))
                )
                "MS" -> RemoteCommand.MouseScroll(
                    json.optDouble("dy", 0.0).toFloat()
                )
                "KEY" -> RemoteCommand.KeyAction(
                    TvKey.valueOf(json.optString("k", "OK"))
                )
                "NUM" -> RemoteCommand.NumpadAction(json.optString("d", "0"))
                "TXT" -> RemoteCommand.TextInput(json.optString("text", ""))
                "APP" -> RemoteCommand.LaunchApp(json.optString("id", "home"))
                "TOUCH" -> RemoteCommand.ScreenTouch(
                    json.optDouble("x", 0.5).toFloat(),
                    json.optDouble("y", 0.5).toFloat(),
                    TouchEventType.valueOf(json.optString("et", "DOWN"))
                )
                "PING" -> RemoteCommand.Ping(
                    json.optString("cl", "Client"),
                    json.optLong("ts", 0L)
                )
                "SYNC" -> RemoteCommand.RequestScreenSync(json.optLong("ts", 0L))
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun serializeTvState(s: TvAppState): String {
        val json = JSONObject()
        json.put("t", "STATE")
        json.put("pwr", s.isPowerOn)
        json.put("vol", s.volume)
        json.put("mute", s.isMuted)
        json.put("app", s.activeAppId)
        json.put("cx", s.cursorX.toDouble())
        json.put("cy", s.cursorY.toDouble())
        json.put("msg", s.lastToastMessage)
        json.put("ch", s.activeChannelIndex)
        json.put("play", s.isMediaPlaying)
        json.put("prog", s.mediaProgressSeconds)
        json.put("dur", s.mediaDurationSeconds)
        json.put("url", s.browserUrl)
        json.put("txt", s.searchTextInput)
        json.put("rx", s.lastClickRippleX.toDouble())
        json.put("ry", s.lastClickRippleY.toDouble())
        json.put("rt", s.lastClickTimestamp)
        return json.toString()
    }

    private fun parseTvStateJson(str: String): TvAppState? {
        return try {
            val json = JSONObject(str)
            if (json.optString("t") != "STATE") return null
            TvAppState(
                isPowerOn = json.optBoolean("pwr", true),
                volume = json.optInt("vol", 40),
                isMuted = json.optBoolean("mute", false),
                activeAppId = json.optString("app", "home"),
                cursorX = json.optDouble("cx", 0.5).toFloat(),
                cursorY = json.optDouble("cy", 0.5).toFloat(),
                lastToastMessage = json.optString("msg", ""),
                activeChannelIndex = json.optInt("ch", 0),
                isMediaPlaying = json.optBoolean("play", true),
                mediaProgressSeconds = json.optInt("prog", 120),
                mediaDurationSeconds = json.optInt("dur", 600),
                browserUrl = json.optString("url", "https://google.com"),
                searchTextInput = json.optString("txt", ""),
                lastClickRippleX = json.optDouble("rx", -1.0).toFloat(),
                lastClickRippleY = json.optDouble("ry", -1.0).toFloat(),
                lastClickTimestamp = json.optLong("rt", 0L)
            )
        } catch (_: Exception) {
            null
        }
    }
}
