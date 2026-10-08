package com.example.model

sealed class RemoteCommand {
    data class MouseMove(val dx: Float, val dy: Float) : RemoteCommand()
    data class MouseClick(val clickType: ClickType) : RemoteCommand()
    data class MouseScroll(val deltaY: Float) : RemoteCommand()
    data class KeyAction(val key: TvKey) : RemoteCommand()
    data class NumpadAction(val digit: String) : RemoteCommand()
    data class TextInput(val text: String) : RemoteCommand()
    data class LaunchApp(val appId: String) : RemoteCommand()
    data class ScreenTouch(
        val xPercent: Float,
        val yPercent: Float,
        val eventType: TouchEventType
    ) : RemoteCommand()
    data class Ping(val clientName: String, val timestamp: Long) : RemoteCommand()
    data class RequestScreenSync(val timestamp: Long) : RemoteCommand()
}

data class DiscoveredTv(
    val name: String,
    val ipAddress: String,
    val port: Int = 8989,
    val pin: String = "1234",
    val isAvailable: Boolean = true,
    val lastSeen: Long = System.currentTimeMillis()
)
