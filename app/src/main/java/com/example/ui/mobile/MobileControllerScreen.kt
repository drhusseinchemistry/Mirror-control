package com.example.ui.mobile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClickType
import com.example.model.DiscoveredTv
import com.example.model.TouchEventType
import com.example.model.TvAppState
import com.example.model.TvKey
import com.example.ui.theme.DPadHighlight
import com.example.ui.theme.DPadSurface
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TouchpadSurface
import com.example.ui.theme.TvCardBorder
import com.example.ui.theme.TvCardNavy
import com.example.ui.theme.TvDarkNavy
import com.example.ui.theme.TvSurfaceNavy
import com.example.viewmodel.ConnectionStatus
import com.example.viewmodel.MobileTab

@Composable
fun MobileControllerScreen(
    state: TvAppState,
    connectionStatus: ConnectionStatus,
    selectedTab: MobileTab,
    isAirMouseActive: Boolean,
    touchpadSensitivity: Float,
    isHapticEnabled: Boolean,
    isKurdish: Boolean,
    discoveredTvs: List<DiscoveredTv>,
    manualIpInput: String,
    onSelectTab: (MobileTab) -> Unit,
    onSwitchRole: () -> Unit,
    onToggleAirMouse: () -> Unit,
    onToggleHaptic: () -> Unit,
    onToggleLanguage: () -> Unit,
    onSensitivityChange: (Float) -> Unit,
    onManualIpChange: (String) -> Unit,
    onConnectToIp: (String, String) -> Unit,
    onDisconnect: () -> Unit,
    onMouseMove: (dx: Float, dy: Float) -> Unit,
    onMouseClick: (ClickType) -> Unit,
    onMouseScroll: (Float) -> Unit,
    onKey: (TvKey) -> Unit,
    onNumpad: (String) -> Unit,
    onTextInput: (String) -> Unit,
    onLaunchApp: (String) -> Unit,
    onScreenTouch: (xPercent: Float, yPercent: Float, eventType: TouchEventType) -> Unit
) {
    val layoutDirection = if (isKurdish) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("mobile_controller_screen"),
            color = TvDarkNavy
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                MobileTopHeader(
                    connectionStatus = connectionStatus,
                    isAirMouseActive = isAirMouseActive,
                    isHapticEnabled = isHapticEnabled,
                    isKurdish = isKurdish,
                    onSwitchRole = onSwitchRole,
                    onToggleAirMouse = onToggleAirMouse,
                    onToggleHaptic = onToggleHaptic,
                    onToggleLanguage = onToggleLanguage
                )

                // Navigation Tabs
                MobileNavigationTabs(
                    selectedTab = selectedTab,
                    isKurdish = isKurdish,
                    onSelectTab = onSelectTab
                )

                // Tab Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        MobileTab.REMOTE -> MobileRemoteTab(
                            state = state,
                            isKurdish = isKurdish,
                            onKey = onKey
                        )
                        MobileTab.TOUCHPAD -> MobileTouchpadTab(
                            isAirMouseActive = isAirMouseActive,
                            sensitivity = touchpadSensitivity,
                            isKurdish = isKurdish,
                            onMouseMove = onMouseMove,
                            onMouseClick = onMouseClick,
                            onMouseScroll = onMouseScroll,
                            onToggleAirMouse = onToggleAirMouse,
                            onSensitivityChange = onSensitivityChange
                        )
                        MobileTab.MIRROR -> MobileScreenMirrorTab(
                            state = state,
                            isKurdish = isKurdish,
                            onScreenTouch = onScreenTouch,
                            onKey = onKey
                        )
                        MobileTab.APPS -> MobileAppsTab(
                            isKurdish = isKurdish,
                            onLaunchApp = onLaunchApp
                        )
                        MobileTab.NUMPAD -> MobileNumpadTab(
                            isKurdish = isKurdish,
                            onNumpad = onNumpad,
                            onTextInput = onTextInput,
                            onKey = onKey
                        )
                        MobileTab.CONNECT -> MobileConnectTab(
                            connectionStatus = connectionStatus,
                            discoveredTvs = discoveredTvs,
                            manualIpInput = manualIpInput,
                            isKurdish = isKurdish,
                            onManualIpChange = onManualIpChange,
                            onConnect = onConnectToIp,
                            onDisconnect = onDisconnect
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// HEADER & TABS
// =========================================================================

@Composable
private fun MobileTopHeader(
    connectionStatus: ConnectionStatus,
    isAirMouseActive: Boolean,
    isHapticEnabled: Boolean,
    isKurdish: Boolean,
    onSwitchRole: () -> Unit,
    onToggleAirMouse: () -> Unit,
    onToggleHaptic: () -> Unit,
    onToggleLanguage: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TvSurfaceNavy)
            .border(width = 0.5.dp, color = TvCardBorder)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Role Switcher & App Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onSwitchRole,
                    colors = ButtonDefaults.buttonColors(containerColor = TvCardNavy),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("mobile_switch_to_tv_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isKurdish) "تیڤی بۆکس" else "TV Mode",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Connection pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (connectionStatus) {
                                is ConnectionStatus.Connected -> NeonEmerald.copy(alpha = 0.15f)
                                is ConnectionStatus.Connecting -> NeonAmber.copy(alpha = 0.15f)
                                is ConnectionStatus.Disconnected -> Color(0xFF334155).copy(alpha = 0.4f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (connectionStatus) {
                                        is ConnectionStatus.Connected -> NeonEmerald
                                        is ConnectionStatus.Connecting -> NeonAmber
                                        is ConnectionStatus.Disconnected -> Color.Gray
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (connectionStatus) {
                                is ConnectionStatus.Connected -> connectionStatus.ip
                                is ConnectionStatus.Connecting -> if (isKurdish) "پەیوەست دەبێت..." else "Connecting..."
                                is ConnectionStatus.Disconnected -> if (isKurdish) "پەیوەست نییە" else "Disconnected"
                            },
                            fontSize = 11.sp,
                            color = when (connectionStatus) {
                                is ConnectionStatus.Connected -> NeonEmerald
                                is ConnectionStatus.Connecting -> NeonAmber
                                is ConnectionStatus.Disconnected -> Color(0xFF94A3B8)
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Right: Quick Toggles (Air Mouse, Haptic, Language)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Air Mouse Icon Button
                IconButton(
                    onClick = onToggleAirMouse,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isAirMouseActive) ElectricBlue else TvCardNavy)
                        .testTag("air_mouse_quick_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiTethering,
                        contentDescription = "Air Mouse",
                        tint = if (isAirMouseActive) TvDarkNavy else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Haptic Toggle
                IconButton(
                    onClick = onToggleHaptic,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TvCardNavy)
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "Vibration",
                        tint = if (isHapticEnabled) ElectricBlue else Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Language Toggle
                IconButton(
                    onClick = onToggleLanguage,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TvCardNavy)
                ) {
                    Text(
                        text = if (isKurdish) "EN" else "کوردی",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MobileNavigationTabs(
    selectedTab: MobileTab,
    isKurdish: Boolean,
    onSelectTab: (MobileTab) -> Unit
) {
    val tabs = listOf(
        MobileTab.REMOTE to (if (isKurdish) "کۆنتڕۆڵ" else "Remote"),
        MobileTab.TOUCHPAD to (if (isKurdish) "تەختەی ماوس" else "Touchpad"),
        MobileTab.MIRROR to (if (isKurdish) "شاشەی تیڤی" else "Mirror"),
        MobileTab.APPS to (if (isKurdish) "بەرنامەکان" else "Apps"),
        MobileTab.NUMPAD to (if (isKurdish) "ژمارە و دەق" else "Numpad"),
        MobileTab.CONNECT to (if (isKurdish) "پەیوەندی" else "Connect")
    )

    ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = TvSurfaceNavy,
        contentColor = ElectricBlue,
        edgePadding = 12.dp,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                color = ElectricBlue,
                height = 3.dp
            )
        }
    ) {
        tabs.forEach { (tab, title) ->
            val isSelected = selectedTab == tab
            Tab(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                text = {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ElectricBlue else Color(0xFF94A3B8)
                    )
                },
                modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
            )
        }
    }
}

// =========================================================================
// TAB 1: ERGONOMIC REMOTE CONTROL (D-PAD & TV KEYS)
// =========================================================================

@Composable
private fun MobileRemoteTab(
    state: TvAppState,
    isKurdish: Boolean,
    onKey: (TvKey) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Row 1: Power, Mute, Menu, Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemoteRoundButton(
                icon = Icons.Default.PowerSettingsNew,
                label = if (isKurdish) "پاوەر" else "Power",
                color = NeonRose,
                size = 54.dp,
                onClick = { onKey(TvKey.POWER) },
                testTag = "key_power"
            )

            RemoteRoundButton(
                icon = Icons.Default.VolumeMute,
                label = if (isKurdish) "بێدەنگ" else "Mute",
                color = if (state.isMuted) Color.Red else Color(0xFF94A3B8),
                size = 48.dp,
                onClick = { onKey(TvKey.MUTE) },
                testTag = "key_mute"
            )

            RemoteRoundButton(
                icon = Icons.Default.Menu,
                label = if (isKurdish) "مینیۆ" else "Menu",
                color = ElectricBlue,
                size = 48.dp,
                onClick = { onKey(TvKey.MENU) },
                testTag = "key_menu"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Piece: Large Tactile D-Pad
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(DPadSurface)
                .border(2.dp, TvCardBorder, CircleShape)
                .testTag("dpad_container"),
            contentAlignment = Alignment.Center
        ) {
            // Directional Up
            IconButton(
                onClick = { onKey(TvKey.UP) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .size(56.dp)
                    .testTag("key_up")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Up",
                    tint = DPadHighlight,
                    modifier = Modifier.size(38.dp)
                )
            }

            // Directional Down
            IconButton(
                onClick = { onKey(TvKey.DOWN) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .size(56.dp)
                    .testTag("key_down")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Down",
                    tint = DPadHighlight,
                    modifier = Modifier.size(38.dp)
                )
            }

            // Directional Left
            IconButton(
                onClick = { onKey(TvKey.LEFT) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
                    .size(56.dp)
                    .testTag("key_left")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Left",
                    tint = DPadHighlight,
                    modifier = Modifier.size(38.dp)
                )
            }

            // Directional Right
            IconButton(
                onClick = { onKey(TvKey.RIGHT) },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp)
                    .size(56.dp)
                    .testTag("key_right")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Right",
                    tint = DPadHighlight,
                    modifier = Modifier.size(38.dp)
                )
            }

            // Center OK / SELECT Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                ElectricBlue,
                                NeonIndigo
                            )
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    .clickable { onKey(TvKey.OK) }
                    .testTag("key_ok"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OK",
                    color = TvDarkNavy,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Row 3: Back & Home
        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemotePillButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                label = if (isKurdish) "گەڕانەوە" else "Back",
                onClick = { onKey(TvKey.BACK) },
                testTag = "key_back"
            )

            RemotePillButton(
                icon = Icons.Default.Home,
                label = if (isKurdish) "ماڵەوە" else "Home",
                onClick = { onKey(TvKey.HOME) },
                testTag = "key_home"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dual Rockers: Volume & Channel
        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Volume Rocker
            RockerWidget(
                title = if (isKurdish) "دەنگ VOL" else "VOL",
                valueDisplay = "${state.volume}%",
                onPlus = { onKey(TvKey.VOL_UP) },
                onMinus = { onKey(TvKey.VOL_DOWN) },
                plusTag = "key_vol_up",
                minusTag = "key_vol_down"
            )

            // Channel Rocker
            RockerWidget(
                title = if (isKurdish) "کەناڵ CH" else "CH",
                valueDisplay = "CH ${state.currentChannel.number}",
                onPlus = { onKey(TvKey.CH_UP) },
                onMinus = { onKey(TvKey.CH_DOWN) },
                plusTag = "key_ch_up",
                minusTag = "key_ch_down"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Media Playback Bar
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = TvCardNavy)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onKey(TvKey.REWIND) }, modifier = Modifier.testTag("key_rewind")) {
                    Icon(imageVector = Icons.Default.FastRewind, contentDescription = "Rewind", tint = Color.White)
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue)
                        .clickable { onKey(TvKey.PLAY_PAUSE) }
                        .testTag("key_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.isMediaPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = TvDarkNavy,
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(onClick = { onKey(TvKey.FAST_FORWARD) }, modifier = Modifier.testTag("key_fast_forward")) {
                    Icon(imageVector = Icons.Default.FastForward, contentDescription = "Forward", tint = Color.White)
                }
            }
        }
    }
}

// =========================================================================
// TAB 2: SPACIOUS TOUCHPAD & AIR MOUSE
// =========================================================================

@Composable
private fun MobileTouchpadTab(
    isAirMouseActive: Boolean,
    sensitivity: Float,
    isKurdish: Boolean,
    onMouseMove: (dx: Float, dy: Float) -> Unit,
    onMouseClick: (ClickType) -> Unit,
    onMouseScroll: (Float) -> Unit,
    onToggleAirMouse: () -> Unit,
    onSensitivityChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Air Mouse Status Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isAirMouseActive) NeonIndigo.copy(alpha = 0.25f) else TvCardNavy
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WifiTethering,
                        contentDescription = null,
                        tint = if (isAirMouseActive) ElectricBlue else Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isKurdish) "ماوسی هەوایی (Air Mouse)" else "Air Mouse (Motion Sensor)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isAirMouseActive)
                                (if (isKurdish) "سێنسەر چالاکە • مۆبایل بجوڵێنە" else "Sensor Active • Move phone in air")
                            else
                                (if (isKurdish) "تەختەی پەنجە یان سێنسەر هەڵبژێرە" else "Tap button to toggle motion control"),
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Button(
                    onClick = onToggleAirMouse,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAirMouseActive) NeonEmerald else ElectricBlue
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("air_mouse_main_toggle_btn")
                ) {
                    Text(
                        text = if (isAirMouseActive) (if (isKurdish) "کارایە ✔" else "ON") else (if (isKurdish) "چالاککردن" else "ENABLE"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvDarkNavy
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Touchpad Surface + Scroll Bar on Side
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Expansive Touch Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(TouchpadSurface)
                    .border(1.5.dp, TvCardBorder, RoundedCornerShape(20.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onMouseMove(dragAmount.x, dragAmount.y)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onMouseClick(ClickType.LEFT) },
                            onDoubleTap = { onMouseClick(ClickType.DOUBLE) },
                            onLongPress = { onMouseClick(ClickType.RIGHT) }
                        )
                    }
                    .testTag("touchpad_surface"),
                contentAlignment = Alignment.Center
            ) {
                // Subtle grid pattern & helper text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = Color(0xFF334155),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isKurdish) "تەختەی تاچی ماوس" else "Touchpad Area",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isKurdish)
                            "پەنجە ڕابکێشە بۆ جوڵاندن • یەک تاپ بۆ کلیکی چەپ • دوو تاپ بۆ کلیکی ڕاست"
                        else
                            "Drag finger to move • Tap for Left Click • Long press for Right Click",
                        fontSize = 11.5.sp,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Vertical Scroll Slider on the side
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(TvCardNavy)
                    .border(1.dp, TvCardBorder, RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onMouseScroll(dragAmount.y)
                        }
                    }
                    .testTag("scroll_bar_widget"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isKurdish) "سکڕۆڵ" else "SCROLL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.rotate(90f)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sensitivity slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isKurdish) "خێرایی ماوس" else "Speed",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.width(70.dp)
            )
            Slider(
                value = sensitivity,
                onValueChange = onSensitivityChange,
                valueRange = 0.5f..2.5f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = ElectricBlue,
                    activeTrackColor = ElectricBlue,
                    inactiveTrackColor = TvCardBorder
                )
            )
            Text(
                text = String.format("%.1fx", sensitivity),
                fontSize = 12.sp,
                color = ElectricBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Footer Buttons: Left Click & Right Click & Double Click
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onMouseClick(ClickType.LEFT) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("mouse_left_click_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = TvCardNavy),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
            ) {
                Text(
                    text = if (isKurdish) "کلیکی چەپ" else "Left Click",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Button(
                onClick = { onMouseClick(ClickType.DOUBLE) },
                modifier = Modifier
                    .weight(0.8f)
                    .height(52.dp)
                    .testTag("mouse_double_click_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = TvCardNavy),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TvCardBorder)
            ) {
                Text(
                    text = if (isKurdish) "٢ کلیک" else "Double",
                    fontSize = 13.sp,
                    color = Color(0xFFCBD5E1)
                )
            }

            Button(
                onClick = { onMouseClick(ClickType.RIGHT) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("mouse_right_click_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = TvCardNavy),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple)
            ) {
                Text(
                    text = if (isKurdish) "کلیکی ڕاست" else "Right Click",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// =========================================================================
// TAB 3: MIRROR TV SCREEN ON MOBILE (ESHARE REVERSE MIRROR & TOUCH CONTROL)
// =========================================================================

@Composable
private fun MobileScreenMirrorTab(
    state: TvAppState,
    isKurdish: Boolean,
    onScreenTouch: (xPercent: Float, yPercent: Float, eventType: TouchEventType) -> Unit,
    onKey: (TvKey) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Status & Mirror Tips Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ScreenShare,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isKurdish) "پەخشی ڕاستەوخۆی شاشەی تیڤی" else "Live TV Screen Stream",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonEmerald.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "60 FPS • EShare LAN",
                    fontSize = 11.sp,
                    color = NeonEmerald,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // THE MIRRORED TV SCREEN FRAME
        // Touching anywhere on this view sends direct touch coordinates to the TV Box!
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, ElectricBlue, RoundedCornerShape(16.dp))
                .testTag("mirrored_tv_screen_card"),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                val xNorm = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                                val yNorm = (offset.y / size.height.toFloat()).coerceIn(0f, 1f)
                                onScreenTouch(xNorm, yNorm, TouchEventType.DOWN)
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                change.consume()
                                val xNorm = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                                val yNorm = (change.position.y / size.height.toFloat()).coerceIn(0f, 1f)
                                onScreenTouch(xNorm, yNorm, TouchEventType.MOVE)
                            }
                        )
                    }
            ) {
                val boxWidth = constraints.maxWidth.toFloat()
                val boxHeight = constraints.maxHeight.toFloat()

                // Render current TV State visually inside mirrored display
                MirroredTvVisualCanvas(state = state, isKurdish = isKurdish)

                // Render cursor in mirrored view
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset {
                            androidx.compose.ui.unit.IntOffset(
                                (state.cursorX * (boxWidth - 40f)).toInt().coerceAtLeast(0),
                                (state.cursorY * (boxHeight - 40f)).toInt().coerceAtLeast(0)
                            )
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Cursor",
                        tint = ElectricBlue,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(-45f)
                    )
                }

                // Interactive Touch Overlay Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isKurdish)
                                "دەست لە هەر شوێنێکی ئەم شاشەیە بدەیت لەسەر تیڤی کلیک دەبێت"
                            else
                                "Touch anywhere on this picture to control TV directly",
                            color = Color.White,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Bottom TV Action bar while viewing mirror
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onKey(TvKey.HOME) },
                colors = ButtonDefaults.buttonColors(containerColor = TvCardNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = ElectricBlue)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isKurdish) "سەرەکی" else "Home", color = Color.White, fontSize = 12.sp)
            }

            Button(
                onClick = { onKey(TvKey.BACK) },
                colors = ButtonDefaults.buttonColors(containerColor = TvCardNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isKurdish) "گەڕانەوە" else "Back", color = Color.White, fontSize = 12.sp)
            }

            Button(
                onClick = { onKey(TvKey.PLAY_PAUSE) },
                colors = ButtonDefaults.buttonColors(containerColor = TvCardNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (state.isMediaPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = NeonEmerald
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isKurdish) "پەخش" else "Play", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun MirroredTvVisualCanvas(state: TvAppState, isKurdish: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF090D16),
                        Color(0xFF020617)
                    )
                )
            )
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Simulated TV Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Android TV • ${state.activeAppId.uppercase()}",
                    color = ElectricBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "VOL: ${state.volume}%",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Active App Content in Mirror
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                when (state.activeAppId) {
                    "youtube" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                            Text(text = "YouTube 4K Playing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "دەست لێبدە بۆ وەستاندن یان پەخشکردن", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                    "live_tv" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "CH ${state.currentChannel.number}", color = ElectricBlue, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text(text = state.currentChannel.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "پەخشی ڕاستەوخۆ", color = Color.Red, fontSize = 11.sp)
                        }
                    }
                    "browser" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(42.dp))
                            Text(text = state.browserUrl, color = Color.White, fontSize = 12.sp)
                        }
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Tv, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(42.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "داشبۆردی سەرەکی تیڤی", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "کلیک لەسەر هەر بەرنامەیەک بکە بۆ کردنەوەی", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 4: TV APPS LAUNCHER
// =========================================================================

@Composable
private fun MobileAppsTab(
    isKurdish: Boolean,
    onLaunchApp: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (isKurdish) "کردنەوەی خێرای بەرنامەکانی تیڤی" else "Quick Launch TV Apps",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = if (isKurdish) "کلیک بکە بۆ کردنەوەی ڕاستەوخۆی بەرنامەکە لەسەر تیڤی بۆکس" else "Tap any app to open it instantly on your TV Box",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TvAppState.DEFAULT_APPS.forEach { app ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, TvCardBorder, RoundedCornerShape(16.dp))
                        .clickable { onLaunchApp(app.id) }
                        .testTag("launch_app_${app.id}"),
                    colors = CardDefaults.cardColors(containerColor = TvCardNavy)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(app.colorHex).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = null,
                                    tint = Color(app.colorHex),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (isKurdish) app.nameKu else app.nameEn,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = app.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Launch",
                            tint = ElectricBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 5: NUMPAD & KEYBOARD TEXT SYNC
// =========================================================================

@Composable
private fun MobileNumpadTab(
    isKurdish: Boolean,
    onNumpad: (String) -> Unit,
    onTextInput: (String) -> Unit,
    onKey: (TvKey) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Text Input & Send to TV Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TvCardNavy)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isKurdish) "ناردنی دەق بۆ تیڤی (کیبۆرد)" else "Send Text to TV Keyboard",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tv_text_input_field"),
                        placeholder = {
                            Text(
                                text = if (isKurdish) "لێرە بنووسە بۆ یوتیوب یان گەڕان..." else "Type to search on TV...",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = TvCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (textInput.isNotEmpty()) {
                                    onTextInput(textInput)
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (textInput.isNotEmpty()) {
                                onTextInput(textInput)
                                focusManager.clearFocus()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("send_text_to_tv_btn")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = TvDarkNavy)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // TV Channel Numpad Grid (0-9)
        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("Clear", "0", "OK")
            )

            for (row in rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (digit in row) {
                        Button(
                            onClick = {
                                when (digit) {
                                    "Clear" -> onNumpad("1")
                                    "OK" -> onKey(TvKey.OK)
                                    else -> onNumpad(digit)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .testTag("numpad_btn_$digit"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (digit == "OK") ElectricBlue else TvCardNavy
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TvCardBorder)
                        ) {
                            Text(
                                text = digit,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (digit == "OK") TvDarkNavy else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 6: CONNECT & PAIRING
// =========================================================================

@Composable
private fun MobileConnectTab(
    connectionStatus: ConnectionStatus,
    discoveredTvs: List<DiscoveredTv>,
    manualIpInput: String,
    isKurdish: Boolean,
    onManualIpChange: (String) -> Unit,
    onConnect: (String, String) -> Unit,
    onDisconnect: () -> Unit
) {
    var pinInput by remember { mutableStateOf("4892") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = when (connectionStatus) {
                    is ConnectionStatus.Connected -> NeonEmerald.copy(alpha = 0.15f)
                    is ConnectionStatus.Connecting -> NeonAmber.copy(alpha = 0.15f)
                    is ConnectionStatus.Disconnected -> TvCardNavy
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (connectionStatus is ConnectionStatus.Connected) Icons.Default.CastConnected else Icons.Default.Cast,
                        contentDescription = null,
                        tint = if (connectionStatus is ConnectionStatus.Connected) NeonEmerald else Color(0xFF94A3B8),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = when (connectionStatus) {
                                is ConnectionStatus.Connected -> if (isKurdish) "پەیوەستکراوە بە سەرکەوتوویی" else "Connected to TV"
                                is ConnectionStatus.Connecting -> if (isKurdish) "پەیوەست دەبێت..." else "Connecting..."
                                is ConnectionStatus.Disconnected -> if (isKurdish) "پەیوەست نەکراوە" else "Disconnected"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        if (connectionStatus is ConnectionStatus.Connected) {
                            Text(
                                text = "${connectionStatus.name} (${connectionStatus.ip})",
                                fontSize = 12.sp,
                                color = NeonEmerald
                            )
                        }
                    }
                }

                if (connectionStatus is ConnectionStatus.Connected) {
                    Button(
                        onClick = onDisconnect,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRose),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = if (isKurdish) "پچڕاندن" else "Disconnect", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Auto-discovered TVs on Wi-Fi
        Text(
            text = if (isKurdish) "تیڤی دۆزراوە لەسەر وایفای (Discovered TVs)" else "Discovered TVs on LAN",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        if (discoveredTvs.isNotEmpty()) {
            discoveredTvs.forEach { tv ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onConnect(tv.ipAddress, tv.pin) },
                    colors = CardDefaults.cardColors(containerColor = TvCardNavy)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Tv, contentDescription = null, tint = ElectricBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = tv.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "${tv.ipAddress}:${tv.port}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = { onConnect(tv.ipAddress, tv.pin) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = if (isKurdish) "پەیوەستبە" else "Connect", fontSize = 12.sp, color = TvDarkNavy)
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = TvSurfaceNavy)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = Color(0xFF64748B))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isKurdish)
                            "دڵنیابە هەردوو مۆبایل و تیڤی لەسەر هەمان تۆڕی وایفای (Wi-Fi)ن یان لە خوارەوە IP بنووسە."
                        else
                            "Ensure Mobile and TV are on the same Wi-Fi, or enter IP below.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Manual IP Connect Card
        Text(
            text = if (isKurdish) "پەیوەستبوونی دەستی بە IP" else "Manual IP Connect",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TvCardNavy)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = manualIpInput,
                    onValueChange = onManualIpChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_ip_input_field"),
                    label = { Text("TV Box IP Address") },
                    placeholder = { Text("192.168.1.105") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = TvCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { pinInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_pin_input_field"),
                    label = { Text("Pairing PIN") },
                    placeholder = { Text("4892") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = TvCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Button(
                    onClick = {
                        val targetIp = manualIpInput.ifEmpty { "192.168.1.105" }
                        onConnect(targetIp, pinInput)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("manual_connect_submit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isKurdish) "پەیوەستبوون بە تیڤی" else "Connect to TV Box",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvDarkNavy
                    )
                }
            }
        }
    }
}

// =========================================================================
// WIDGET HELPERS
// =========================================================================

@Composable
private fun RemoteRoundButton(
    icon: ImageVector,
    label: String,
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    testTag: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(TvCardNavy)
                .border(1.dp, color.copy(alpha = 0.6f), CircleShape)
                .clickable { onClick() }
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(size * 0.48f))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp)
    }
}

@Composable
private fun RemotePillButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = TvCardNavy),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TvCardBorder),
        modifier = Modifier
            .height(46.dp)
            .width(110.dp)
            .testTag(testTag)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RockerWidget(
    title: String,
    valueDisplay: String,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    plusTag: String,
    minusTag: String
) {
    Column(
        modifier = Modifier
            .width(115.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(DPadSurface)
            .border(1.dp, TvCardBorder, RoundedCornerShape(22.dp))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Plus button
        IconButton(
            onClick = onPlus,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(TvCardNavy)
                .testTag(plusTag)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Up", tint = ElectricBlue)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = valueDisplay, fontSize = 11.sp, color = ElectricBlue)

        Spacer(modifier = Modifier.height(8.dp))

        // Minus button
        IconButton(
            onClick = onMinus,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(TvCardNavy)
                .testTag(minusTag)
        ) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Down", tint = ElectricBlue)
        }
    }
}
