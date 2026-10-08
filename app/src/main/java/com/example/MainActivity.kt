package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DeviceRole
import com.example.ui.mobile.MobileControllerScreen
import com.example.ui.role.RoleSelectionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TvDarkNavy
import com.example.ui.tv.TvBoxReceiverScreen
import com.example.viewmodel.TvRemoteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = TvDarkNavy
                ) {
                    MainAppContent()
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: TvRemoteViewModel = viewModel()) {
    val deviceRole by viewModel.deviceRole.collectAsState()
    val tvState by viewModel.tvAppState.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isAirMouseActive by viewModel.isAirMouseActive.collectAsState()
    val sensitivity by viewModel.touchpadSensitivity.collectAsState()
    val isHaptic by viewModel.isHapticEnabled.collectAsState()
    val isKurdish by viewModel.isKurdish.collectAsState()
    val discoveredTvs by viewModel.discoveredTvs.collectAsState()
    val manualIp by viewModel.manualIpInput.collectAsState()

    var showRoleSwitcherModal by remember { mutableStateOf(false) }

    // If role is unselected or switcher is requested
    if (deviceRole == DeviceRole.UNSELECTED || showRoleSwitcherModal) {
        RoleSelectionScreen(
            currentRole = deviceRole,
            isKurdish = isKurdish,
            onToggleLanguage = { viewModel.toggleKurdish() },
            onRoleSelected = { role ->
                viewModel.setRole(role)
                showRoleSwitcherModal = false
            },
            onDismiss = if (deviceRole != DeviceRole.UNSELECTED) {
                { showRoleSwitcherModal = false }
            } else null
        )
    } else {
        when (deviceRole) {
            DeviceRole.TV_RECEIVER -> {
                BackHandler {
                    showRoleSwitcherModal = true
                }
                TvBoxReceiverScreen(
                    state = tvState,
                    isKurdish = isKurdish,
                    onSwitchRole = { showRoleSwitcherModal = true },
                    onLaunchApp = { appId -> viewModel.launchApp(appId) },
                    onBackToHome = { viewModel.launchApp("home") }
                )
            }
            DeviceRole.MOBILE_CONTROLLER -> {
                BackHandler {
                    showRoleSwitcherModal = true
                }
                MobileControllerScreen(
                    state = tvState,
                    connectionStatus = connectionStatus,
                    selectedTab = selectedTab,
                    isAirMouseActive = isAirMouseActive,
                    touchpadSensitivity = sensitivity,
                    isHapticEnabled = isHaptic,
                    isKurdish = isKurdish,
                    discoveredTvs = discoveredTvs,
                    manualIpInput = manualIp,
                    onSelectTab = { tab -> viewModel.selectTab(tab) },
                    onSwitchRole = { showRoleSwitcherModal = true },
                    onToggleAirMouse = { viewModel.toggleAirMouse() },
                    onToggleHaptic = { viewModel.toggleHaptic() },
                    onToggleLanguage = { viewModel.toggleKurdish() },
                    onSensitivityChange = { s -> viewModel.setSensitivity(s) },
                    onManualIpChange = { ip -> viewModel.setManualIp(ip) },
                    onConnectToIp = { ip, pin -> viewModel.connectTo(ip, pin) },
                    onDisconnect = { viewModel.disconnect() },
                    onMouseMove = { dx, dy -> viewModel.sendMouseMove(dx, dy) },
                    onMouseClick = { type -> viewModel.sendMouseClick(type) },
                    onMouseScroll = { dy -> viewModel.sendMouseScroll(dy) },
                    onKey = { key -> viewModel.sendKey(key) },
                    onNumpad = { digit -> viewModel.sendNumpad(digit) },
                    onTextInput = { text -> viewModel.sendTextInput(text) },
                    onLaunchApp = { appId -> viewModel.launchApp(appId) },
                    onScreenTouch = { x, y, event -> viewModel.sendScreenTouch(x, y, event) }
                )
            }
            DeviceRole.UNSELECTED -> {
                // Handled above
            }
        }
    }
}
