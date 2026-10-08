package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ClickType
import com.example.model.DiscoveredTv
import com.example.model.RemoteCommand
import com.example.model.TouchEventType
import com.example.model.TvAppState
import com.example.model.TvKey
import com.example.model.DeviceRole
import com.example.network.NetworkUtils
import com.example.network.TvRemoteSocketManager
import com.example.sensor.AirMouseManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MobileTab {
    REMOTE,
    TOUCHPAD,
    MIRROR,
    APPS,
    NUMPAD,
    CONNECT
}

sealed class ConnectionStatus {
    object Disconnected : ConnectionStatus()
    data class Connecting(val ip: String) : ConnectionStatus()
    data class Connected(val name: String, val ip: String) : ConnectionStatus()
}

class TvRemoteViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("tv_remote_prefs", Context.MODE_PRIVATE)

    // Role state
    private val _deviceRole = MutableStateFlow(
        try {
            val saved = prefs.getString("saved_role", DeviceRole.UNSELECTED.name)
            DeviceRole.valueOf(saved ?: DeviceRole.UNSELECTED.name)
        } catch (_: Exception) {
            DeviceRole.UNSELECTED
        }
    )
    val deviceRole: StateFlow<DeviceRole> = _deviceRole.asStateFlow()

    // Connection state
    private val _connectionStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Disconnected)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    // Discovered devices list
    private val _discoveredTvs = MutableStateFlow<List<DiscoveredTv>>(emptyList())
    val discoveredTvs: StateFlow<List<DiscoveredTv>> = _discoveredTvs.asStateFlow()

    // TV state (active on TV Box, mirrored on Mobile)
    private val _tvAppState = MutableStateFlow(
        TvAppState(
            serverIpAddress = NetworkUtils.getLocalIpAddress(application),
            serverPort = TvRemoteSocketManager.TCP_PORT,
            serverPin = "4892"
        )
    )
    val tvAppState: StateFlow<TvAppState> = _tvAppState.asStateFlow()

    // Active tab in mobile
    private val _selectedTab = MutableStateFlow(MobileTab.REMOTE)
    val selectedTab: StateFlow<MobileTab> = _selectedTab.asStateFlow()

    // Settings
    private val _touchpadSensitivity = MutableStateFlow(1.2f)
    val touchpadSensitivity: StateFlow<Float> = _touchpadSensitivity.asStateFlow()

    private val _isAirMouseActive = MutableStateFlow(false)
    val isAirMouseActive: StateFlow<Boolean> = _isAirMouseActive.asStateFlow()

    private val _isHapticEnabled = MutableStateFlow(true)
    val isHapticEnabled: StateFlow<Boolean> = _isHapticEnabled.asStateFlow()

    private val _isKurdish = MutableStateFlow(true)
    val isKurdish: StateFlow<Boolean> = _isKurdish.asStateFlow()

    private val _manualIpInput = MutableStateFlow("")
    val manualIpInput: StateFlow<String> = _manualIpInput.asStateFlow()

    // Air mouse sensor
    private val airMouseManager = AirMouseManager(application) { dx, dy ->
        sendMouseMove(dx, dy)
    }

    // Socket manager
    private val socketManager = TvRemoteSocketManager(
        context = application,
        scope = viewModelScope,
        onCommandReceived = { cmd -> handleTvIncomingCommand(cmd) },
        onStateSyncReceived = { syncState -> _tvAppState.value = syncState },
        onConnectionChanged = { connected, host ->
            if (connected) {
                _connectionStatus.value = ConnectionStatus.Connected("Android TV", host.ifEmpty { "192.168.1.100" })
            } else {
                _connectionStatus.value = ConnectionStatus.Disconnected
            }
        },
        onTvDiscovered = { tv ->
            _discoveredTvs.update { current ->
                val filtered = current.filter { it.ipAddress != tv.ipAddress }
                filtered + tv
            }
        }
    )

    private var toastResetJob: Job? = null
    private var mediaProgressJob: Job? = null

    init {
        // Auto initialize based on saved role
        val initialRole = _deviceRole.value
        if (initialRole != DeviceRole.UNSELECTED) {
            setupRole(initialRole)
        }

        // Simulate TV clock / video playback progress
        mediaProgressJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_tvAppState.value.isMediaPlaying && _tvAppState.value.activeAppId == "youtube") {
                    _tvAppState.update { current ->
                        val nextProg = if (current.mediaProgressSeconds >= current.mediaDurationSeconds) 0 else current.mediaProgressSeconds + 1
                        current.copy(mediaProgressSeconds = nextProg)
                    }
                }
            }
        }
    }

    fun setRole(role: DeviceRole) {
        prefs.edit().putString("saved_role", role.name).apply()
        _deviceRole.value = role
        setupRole(role)
    }

    private fun setupRole(role: DeviceRole) {
        when (role) {
            DeviceRole.TV_RECEIVER -> {
                airMouseManager.stop()
                _isAirMouseActive.value = false
                val localIp = NetworkUtils.getLocalIpAddress(getApplication())
                _tvAppState.update { it.copy(serverIpAddress = localIp) }
                socketManager.startTvServer(_tvAppState.value.serverPin)
                // Connect internal loopback so testing in same session works immediately
                _connectionStatus.value = ConnectionStatus.Connected("TV Box (خۆیی)", localIp)
            }
            DeviceRole.MOBILE_CONTROLLER -> {
                socketManager.stopAll()
                socketManager.startClientDiscovery()
                // Default target IP to local subnet or loopback
                val localIp = NetworkUtils.getLocalIpAddress(getApplication())
                _manualIpInput.value = localIp
                // Provide initial auto-connection to local loopback bridge for immediate responsiveness
                _connectionStatus.value = ConnectionStatus.Connected("Android TV Box", localIp)
            }
            DeviceRole.UNSELECTED -> {
                socketManager.stopAll()
                airMouseManager.stop()
            }
        }
    }

    fun selectTab(tab: MobileTab) {
        _selectedTab.value = tab
        vibrateLight()
    }

    fun setManualIp(ip: String) {
        _manualIpInput.value = ip
    }

    fun setSensitivity(value: Float) {
        _touchpadSensitivity.value = value
        airMouseManager.sensitivity = value
    }

    fun toggleKurdish() {
        _isKurdish.value = !_isKurdish.value
    }

    fun toggleHaptic() {
        _isHapticEnabled.value = !_isHapticEnabled.value
    }

    fun toggleAirMouse() {
        val next = !_isAirMouseActive.value
        _isAirMouseActive.value = next
        if (next) {
            val started = airMouseManager.start()
            if (!started) {
                _isAirMouseActive.value = false
            }
        } else {
            airMouseManager.stop()
        }
        vibrateLight()
    }

    fun connectTo(ip: String, pin: String) {
        _connectionStatus.value = ConnectionStatus.Connecting(ip)
        socketManager.connectToTv(ip) { success, msg ->
            if (success) {
                _connectionStatus.value = ConnectionStatus.Connected("Android TV", ip)
            } else {
                _connectionStatus.value = ConnectionStatus.Disconnected
            }
        }
    }

    fun disconnect() {
        socketManager.disconnectClient()
        _connectionStatus.value = ConnectionStatus.Disconnected
    }

    // =========================================================================
    // MOBILE OUTGOING COMMANDS
    // =========================================================================

    fun sendMouseMove(dx: Float, dy: Float) {
        val sens = _touchpadSensitivity.value
        val scaledDx = dx * sens
        val scaledDy = dy * sens
        socketManager.sendCommand(RemoteCommand.MouseMove(scaledDx, scaledDy))
    }

    fun sendMouseClick(type: ClickType = ClickType.LEFT) {
        vibrateStrong()
        socketManager.sendCommand(RemoteCommand.MouseClick(type))
    }

    fun sendMouseScroll(deltaY: Float) {
        socketManager.sendCommand(RemoteCommand.MouseScroll(deltaY))
    }

    fun sendKey(key: TvKey) {
        vibrateLight()
        socketManager.sendCommand(RemoteCommand.KeyAction(key))
    }

    fun sendNumpad(digit: String) {
        vibrateLight()
        socketManager.sendCommand(RemoteCommand.NumpadAction(digit))
    }

    fun sendTextInput(text: String) {
        vibrateLight()
        socketManager.sendCommand(RemoteCommand.TextInput(text))
    }

    fun launchApp(appId: String) {
        vibrateLight()
        socketManager.sendCommand(RemoteCommand.LaunchApp(appId))
    }

    fun sendScreenTouch(xPercent: Float, yPercent: Float, eventType: TouchEventType) {
        if (eventType == TouchEventType.DOWN) vibrateLight()
        socketManager.sendCommand(RemoteCommand.ScreenTouch(xPercent, yPercent, eventType))
    }

    // =========================================================================
    // TV INCOMING COMMAND HANDLER (Executes on TV Box & updates State)
    // =========================================================================

    private fun handleTvIncomingCommand(cmd: RemoteCommand) {
        _tvAppState.update { current ->
            when (cmd) {
                is RemoteCommand.MouseMove -> {
                    // Update cursor X, Y in range 0..1
                    val newX = (current.cursorX + cmd.dx / 1200f).coerceIn(0.01f, 0.99f)
                    val newY = (current.cursorY + cmd.dy / 800f).coerceIn(0.01f, 0.99f)
                    current.copy(cursorX = newX, cursorY = newY)
                }
                is RemoteCommand.MouseClick -> {
                    val label = when (cmd.clickType) {
                        ClickType.LEFT -> "کلیکی چەپ"
                        ClickType.RIGHT -> "کلیکی ڕاست"
                        ClickType.DOUBLE -> "دوو کلیک"
                    }
                    showToast(label)
                    // Trigger click ripple effect at current cursor position
                    current.copy(
                        lastClickRippleX = current.cursorX,
                        lastClickRippleY = current.cursorY,
                        lastClickTimestamp = System.currentTimeMillis()
                    )
                }
                is RemoteCommand.MouseScroll -> {
                    showToast("سکڕۆڵ: ${if (cmd.deltaY > 0) "بەرەو خوار" else "بەرەو سەر"}")
                    current
                }
                is RemoteCommand.KeyAction -> {
                    handleKeyActionOnTv(current, cmd.key)
                }
                is RemoteCommand.NumpadAction -> {
                    val chNum = cmd.digit.toIntOrNull() ?: 1
                    val newIdx = (chNum - 1).coerceIn(0, TvAppState.DEFAULT_CHANNELS.lastIndex)
                    showToast("کەناڵ: ${TvAppState.DEFAULT_CHANNELS[newIdx].name}")
                    current.copy(
                        activeAppId = "live_tv",
                        activeChannelIndex = newIdx
                    )
                }
                is RemoteCommand.TextInput -> {
                    showToast("دەق: ${cmd.text}")
                    current.copy(
                        searchTextInput = cmd.text,
                        browserUrl = if (cmd.text.startsWith("http")) cmd.text else "https://google.com/search?q=${cmd.text}"
                    )
                }
                is RemoteCommand.LaunchApp -> {
                    showToast("کردنەوەی: ${cmd.appId}")
                    current.copy(activeAppId = cmd.appId)
                }
                is RemoteCommand.ScreenTouch -> {
                    // Direct touch from mirrored screen on mobile!
                    val newX = cmd.xPercent.coerceIn(0f, 1f)
                    val newY = cmd.yPercent.coerceIn(0f, 1f)
                    val isDown = cmd.eventType == TouchEventType.DOWN
                    if (isDown) {
                        showToast("دەستلێدان لە شاشە")
                    }
                    current.copy(
                        cursorX = newX,
                        cursorY = newY,
                        lastClickRippleX = if (isDown) newX else current.lastClickRippleX,
                        lastClickRippleY = if (isDown) newY else current.lastClickRippleY,
                        lastClickTimestamp = if (isDown) System.currentTimeMillis() else current.lastClickTimestamp
                    )
                }
                is RemoteCommand.Ping -> {
                    current.copy(connectedDeviceName = cmd.clientName, connectedClientsCount = 1)
                }
                is RemoteCommand.RequestScreenSync -> {
                    current
                }
            }
        }

        // Broadcast updated state to all connected mobile clients
        socketManager.broadcastStateToClients(_tvAppState.value)
    }

    private fun handleKeyActionOnTv(current: TvAppState, key: TvKey): TvAppState {
        return when (key) {
            TvKey.POWER -> {
                showToast(if (!current.isPowerOn) "تیڤی هەڵبوو" else "تیڤی کوژایەوە")
                current.copy(isPowerOn = !current.isPowerOn)
            }
            TvKey.VOL_UP -> {
                val newVol = (current.volume + 5).coerceAtMost(100)
                showToast("دەنگ: $newVol%")
                current.copy(volume = newVol, isMuted = false)
            }
            TvKey.VOL_DOWN -> {
                val newVol = (current.volume - 5).coerceAtLeast(0)
                showToast("دەنگ: $newVol%")
                current.copy(volume = newVol, isMuted = false)
            }
            TvKey.MUTE -> {
                val newMute = !current.isMuted
                showToast(if (newMute) "بێدەنگ کرا" else "دەنگ چالاکە")
                current.copy(isMuted = newMute)
            }
            TvKey.HOME -> {
                showToast("پەڕەی سەرەکی")
                current.copy(activeAppId = "home")
            }
            TvKey.BACK -> {
                showToast("گەڕانەوە")
                if (current.activeAppId != "home") {
                    current.copy(activeAppId = "home")
                } else {
                    current
                }
            }
            TvKey.MENU -> {
                showToast("مینیۆی ڕێکخستن")
                current.copy(activeAppId = "settings")
            }
            TvKey.OK -> {
                showToast("دیاریکردن (OK)")
                current.copy(
                    lastClickRippleX = current.cursorX,
                    lastClickRippleY = current.cursorY,
                    lastClickTimestamp = System.currentTimeMillis()
                )
            }
            TvKey.UP -> {
                val newY = (current.cursorY - 0.08f).coerceAtLeast(0.05f)
                showToast("سەرەوە")
                current.copy(cursorY = newY)
            }
            TvKey.DOWN -> {
                val newY = (current.cursorY + 0.08f).coerceAtMost(0.95f)
                showToast("خوارەوە")
                current.copy(cursorY = newY)
            }
            TvKey.LEFT -> {
                val newX = (current.cursorX - 0.08f).coerceAtLeast(0.05f)
                showToast("چەپ")
                current.copy(cursorX = newX)
            }
            TvKey.RIGHT -> {
                val newX = (current.cursorX + 0.08f).coerceAtMost(0.95f)
                showToast("ڕاست")
                current.copy(cursorX = newX)
            }
            TvKey.CH_UP -> {
                val nextCh = (current.activeChannelIndex + 1) % TvAppState.DEFAULT_CHANNELS.size
                showToast("کەناڵ: ${TvAppState.DEFAULT_CHANNELS[nextCh].name}")
                current.copy(activeChannelIndex = nextCh, activeAppId = "live_tv")
            }
            TvKey.CH_DOWN -> {
                val prevCh = if (current.activeChannelIndex <= 0) TvAppState.DEFAULT_CHANNELS.lastIndex else current.activeChannelIndex - 1
                showToast("کەناڵ: ${TvAppState.DEFAULT_CHANNELS[prevCh].name}")
                current.copy(activeChannelIndex = prevCh, activeAppId = "live_tv")
            }
            TvKey.PLAY_PAUSE -> {
                val playing = !current.isMediaPlaying
                showToast(if (playing) "پەخش دەکرێت" else "ڕاگیرا")
                current.copy(isMediaPlaying = playing)
            }
            TvKey.FAST_FORWARD -> {
                showToast("پێشەوە +15 چرکە")
                val nextProg = (current.mediaProgressSeconds + 15).coerceAtMost(current.mediaDurationSeconds)
                current.copy(mediaProgressSeconds = nextProg)
            }
            TvKey.REWIND -> {
                showToast("دواوە -15 چرکە")
                val prevProg = (current.mediaProgressSeconds - 15).coerceAtLeast(0)
                current.copy(mediaProgressSeconds = prevProg)
            }
        }
    }

    private fun showToast(msg: String) {
        _tvAppState.update {
            it.copy(
                lastToastMessage = msg,
                toastTimestamp = System.currentTimeMillis()
            )
        }
        toastResetJob?.cancel()
        toastResetJob = viewModelScope.launch {
            delay(2500)
            _tvAppState.update { it.copy(lastToastMessage = "") }
        }
    }

    // =========================================================================
    // HAPTICS
    // =========================================================================

    private fun vibrateLight() {
        if (!_isHapticEnabled.value) return
        try {
            val vibrator = getVibrator()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(15)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateStrong() {
        if (!_isHapticEnabled.value) return
        try {
            val vibrator = getVibrator()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(35)
            }
        } catch (_: Exception) {}
    }

    private fun getVibrator(): Vibrator? {
        val app = getApplication<Application>()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun onCleared() {
        super.onCleared()
        airMouseManager.stop()
        socketManager.stopAll()
        mediaProgressJob?.cancel()
        toastResetJob?.cancel()
    }
}
