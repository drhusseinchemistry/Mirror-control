package com.example.ui.tv

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TvAppState
import com.example.model.TvVirtualApp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TvCardBorder
import com.example.ui.theme.TvCardNavy
import com.example.ui.theme.TvDarkNavy
import com.example.ui.theme.TvSurfaceNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TvBoxReceiverScreen(
    state: TvAppState,
    isKurdish: Boolean,
    onSwitchRole: () -> Unit,
    onLaunchApp: (String) -> Unit,
    onBackToHome: () -> Unit
) {
    val layoutDirection = if (isKurdish) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Format live clock
    var currentTimeString by remember {
        mutableStateOf(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()))
    }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeString = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("tv_receiver_screen"),
            color = TvDarkNavy
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val screenWidthPx = constraints.maxWidth.toFloat()
                val screenHeightPx = constraints.maxHeight.toFloat()

                // Main TV Content Container
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top TV Status Bar
                    TvTopStatusBar(
                        state = state,
                        timeString = currentTimeString,
                        isKurdish = isKurdish,
                        onSwitchRole = onSwitchRole
                    )

                    // TV Display Area (App view or Home Desktop)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (state.activeAppId) {
                            "home" -> TvHomeDesktop(
                                state = state,
                                isKurdish = isKurdish,
                                onLaunchApp = onLaunchApp
                            )
                            "youtube" -> TvYouTubeAppView(
                                state = state,
                                isKurdish = isKurdish,
                                onBack = onBackToHome
                            )
                            "netflix" -> TvNetflixAppView(
                                state = state,
                                isKurdish = isKurdish,
                                onBack = onBackToHome
                            )
                            "live_tv" -> TvLiveTvAppView(
                                state = state,
                                isKurdish = isKurdish,
                                onBack = onBackToHome
                            )
                            "browser" -> TvBrowserAppView(
                                state = state,
                                isKurdish = isKurdish,
                                onBack = onBackToHome
                            )
                            "media" -> TvMediaPlayerView(
                                state = state,
                                isKurdish = isKurdish,
                                onBack = onBackToHome
                            )
                            "settings" -> TvSettingsView(
                                state = state,
                                isKurdish = isKurdish,
                                onBack = onBackToHome
                            )
                            else -> TvGenericAppView(
                                state = state,
                                isKurdish = isKurdish,
                                onBack = onBackToHome
                            )
                        }

                        // TV Volume Pill Overlay (if volume adjusted)
                        if (state.isMuted || state.volume != 42) {
                            TvVolumeIndicator(
                                volume = state.volume,
                                isMuted = state.isMuted,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(24.dp)
                            )
                        }
                    }

                    // Bottom info strip
                    TvBottomBar(state = state, isKurdish = isKurdish)
                }

                // Action Toast Notification Banner (Floating top-center)
                AnimatedVisibility(
                    visible = state.lastToastMessage.isNotEmpty(),
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 70.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(TvCardNavy)
                            .border(1.5.dp, ElectricBlue, RoundedCornerShape(30.dp))
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = state.lastToastMessage,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Virtual Mouse Cursor Overlay
                if (state.isCursorVisible) {
                    TvVirtualCursorOverlay(
                        cursorXNorm = state.cursorX,
                        cursorYNorm = state.cursorY,
                        rippleXNorm = state.lastClickRippleX,
                        rippleYNorm = state.lastClickRippleY,
                        rippleTimestamp = state.lastClickTimestamp,
                        containerWidth = screenWidthPx,
                        containerHeight = screenHeightPx
                    )
                }
            }
        }
    }
}

@Composable
private fun TvTopStatusBar(
    state: TvAppState,
    timeString: String,
    isKurdish: Boolean,
    onSwitchRole: () -> Unit
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
            // TV Box Branding & Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonPurple.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = "TV",
                        tint = NeonPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Android TV Box",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Connected indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (state.connectedClientsCount > 0) NeonEmerald.copy(alpha = 0.2f) else NeonAmber.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (state.connectedClientsCount > 0)
                                    (if (isKurdish) "مۆبایل پەیوەستە" else "Phone Connected")
                                else
                                    (if (isKurdish) "ئامادەی پەیوەستبوون" else "Ready to Connect"),
                                fontSize = 11.sp,
                                color = if (state.connectedClientsCount > 0) NeonEmerald else NeonAmber,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = "IP: ${state.serverIpAddress}:${state.serverPort} • PIN: ${state.serverPin}",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Right side: Clock & Switch Mode button
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Clock badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TvCardNavy)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = timeString,
                        color = ElectricBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Switch to Mobile Mode button
                Button(
                    onClick = onSwitchRole,
                    colors = ButtonDefaults.buttonColors(containerColor = TvCardNavy),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("tv_switch_to_mobile_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isKurdish) "گۆڕین بۆ مۆبایل" else "Mobile Mode",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TvHomeDesktop(
    state: TvAppState,
    isKurdish: Boolean,
    onLaunchApp: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Hero Featured Card / Live Stream Spotlight
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onLaunchApp("youtube") },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                NeonIndigo.copy(alpha = 0.7f),
                                Color(0xFF1E1B4B),
                                TvCardNavy
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonRose)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isKurdish) "ڕاستەوخۆ 4K" else "FEATURED 4K",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isKurdish) "سروشتی دڵڕفێنی کوردستان و چیاکان" else "Kurdistan Nature 4K Ultra HD",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isKurdish) "پەخش دەکرێت بە کوالێتی بەرز • دەتوانیت لە مۆبایلەوە کۆنتڕۆڵی بکەیت" else "Streaming now • Control via mobile remote",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = TvDarkNavy,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isKurdish) "بەرنامە و کەناڵەکان (Apps & Channels)" else "Apps & Channels",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Grid of TV Apps
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 130.dp),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(TvAppState.DEFAULT_APPS) { app ->
                TvAppTile(
                    app = app,
                    isKurdish = isKurdish,
                    onClick = { onLaunchApp(app.id) }
                )
            }
        }
    }
}

@Composable
private fun TvAppTile(
    app: TvVirtualApp,
    isKurdish: Boolean,
    onClick: () -> Unit
) {
    val icon = when (app.iconName) {
        "youtube" -> Icons.Default.Videocam
        "netflix" -> Icons.Default.Movie
        "tv" -> Icons.Default.LiveTv
        "browser" -> Icons.Default.Language
        "media" -> Icons.Default.Cast
        "settings" -> Icons.Default.Settings
        "files" -> Icons.Default.Folder
        else -> Icons.Default.Shop
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, TvCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("tv_app_${app.id}"),
        colors = CardDefaults.cardColors(containerColor = TvCardNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(app.colorHex).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(app.colorHex),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = app.category,
                        fontSize = 9.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Column {
                Text(
                    text = if (isKurdish) app.nameKu else app.nameEn,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.description,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TvYouTubeAppView(
    state: TvAppState,
    isKurdish: Boolean,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "YouTube TV",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(TvCardNavy)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (state.isMediaPlaying) (if (isKurdish) "پەخش دەکرێت ▶" else "PLAYING ▶") else (if (isKurdish) "ڕاگیراوە ⏸" else "PAUSED ⏸"),
                    color = if (state.isMediaPlaying) NeonEmerald else NeonAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Video Player Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background video gradient atmosphere
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF091E3A),
                                    Color(0xFF0F172A),
                                    Color(0xFF020617)
                                )
                            )
                        )
                )

                // Visual Center Play/Pause Graphic
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(if (state.isMediaPlaying) ElectricBlue.copy(alpha = 0.25f) else NeonAmber.copy(alpha = 0.25f))
                            .border(2.dp, if (state.isMediaPlaying) ElectricBlue else NeonAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (state.isMediaPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Kurdistan 4K Documentary: Nature, Mountains & Waterfalls",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if (isKurdish) "بەکارهێنانی کۆنتڕۆڵ یان تاچ پادی مۆبایل بۆ وەستاندن و ڕۆیشتن" else "Use Mobile Remote or Touchpad to Play/Pause",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                // Video Progress Bar at Bottom of Player
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(14.dp)
                ) {
                    val progressFraction = (state.mediaProgressSeconds.toFloat() / state.mediaDurationSeconds.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = Color.Red,
                        trackColor = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val currentMins = state.mediaProgressSeconds / 60
                        val currentSecs = state.mediaProgressSeconds % 60
                        val totalMins = state.mediaDurationSeconds / 60
                        val totalSecs = state.mediaDurationSeconds % 60
                        Text(
                            text = String.format("%02d:%02d / %02d:%02d", currentMins, currentSecs, totalMins, totalSecs),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "4K HDR 60fps",
                            color = ElectricBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TvLiveTvAppView(
    state: TvAppState,
    isKurdish: Boolean,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.LiveTv,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isKurdish) "پەخشی ڕاستەوخۆ (Live TV)" else "Live TV Channels",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Red.copy(alpha = 0.2f))
                    .border(1.dp, Color.Red, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE",
                        color = Color.Red,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF020617))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // TV Channel graphics
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CH ${state.currentChannel.number}",
                        color = ElectricBlue,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = state.currentChannel.name,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "بەرنامە: ${state.currentChannel.currentShow}",
                        color = Color(0xFF94A3B8),
                        fontSize = 15.sp
                    )
                }

                // Floating channel switcher helper
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TvCardNavy.copy(alpha = 0.9f))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = if (isKurdish) "دوگمەکانی CH+ یان CH- یان ژمارەکانی مۆبایل دابگرە بۆ گۆڕینی کەناڵ" else "Use CH+ / CH- or Numpad on mobile remote to change channels",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TvNetflixAppView(state: TvAppState, isKurdish: Boolean, onBack: () -> Unit) {
    TvSimplePlaceholderApp(
        title = "Netflix Movies",
        desc = if (isKurdish) "فیلم و زنجیرە جیهانییەکان • کلیک بکە بۆ هەڵبژاردن" else "Movies & TV Shows • Click to select",
        icon = Icons.Default.Movie,
        accentColor = Color(0xFFE50914),
        onBack = onBack
    )
}

@Composable
private fun TvBrowserAppView(state: TvAppState, isKurdish: Boolean, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(6.dp))
            // Address bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TvCardNavy)
                    .border(1.dp, TvCardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.searchTextInput.isNotEmpty()) state.searchTextInput else state.browserUrl,
                        color = Color.White,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TvSurfaceNavy)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isKurdish) "ماڵپەڕی ئینتەرنێت لەسەر تیڤی" else "Smart Web Browser",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isKurdish) "لە تەختەکلیلی مۆبایلەوە دەق بنووسە یان بە ماوس لینکی ماڵپەڕەکان کلیک بکە" else "Type using mobile keyboard or click links with air mouse",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TvMediaPlayerView(state: TvAppState, isKurdish: Boolean, onBack: () -> Unit) {
    TvSimplePlaceholderApp(
        title = if (isKurdish) "پەخشکەری میدیا (Media Share)" else "Media Player",
        desc = if (isKurdish) "پەخشی وێنە و ڤیدیۆی مۆبایل لەسەر تیڤی" else "Stream mobile photos and videos to TV",
        icon = Icons.Default.Cast,
        accentColor = NeonPurple,
        onBack = onBack
    )
}

@Composable
private fun TvSettingsView(state: TvAppState, isKurdish: Boolean, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isKurdish) "ڕێکخستنەکانی تیڤی بۆکس" else "TV Box Settings",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TvSurfaceNavy)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                TvSettingItem(
                    title = if (isKurdish) "ناونیشانی IP و پۆرت" else "Local IP & Port",
                    value = "${state.serverIpAddress}:${state.serverPort}",
                    icon = Icons.Default.Wifi
                )
                TvSettingItem(
                    title = if (isKurdish) "کۆدی پەیوەستبوون (PIN Code)" else "Pairing PIN",
                    value = state.serverPin,
                    icon = Icons.Default.CheckCircle
                )
                TvSettingItem(
                    title = if (isKurdish) "کوالێتی پەخشی شاشە" else "Screen Mirror Stream",
                    value = "1080p 60fps (Low Latency LAN)",
                    icon = Icons.Default.Cast
                )
                TvSettingItem(
                    title = if (isKurdish) "دەنگی تیڤی" else "TV Volume",
                    value = "${state.volume}% ${if (state.isMuted) "(بێدەنگ)" else ""}",
                    icon = Icons.Default.VolumeUp
                )
            }
        }
    }
}

@Composable
private fun TvSettingItem(title: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
        Text(text = value, color = Color(0xFF94A3B8), fontSize = 14.sp)
    }
}

@Composable
private fun TvSimplePlaceholderApp(
    title: String,
    desc: String,
    icon: ImageVector,
    accentColor: Color,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TvSurfaceNavy)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = desc, color = Color(0xFF94A3B8), fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun TvGenericAppView(state: TvAppState, isKurdish: Boolean, onBack: () -> Unit) {
    TvSimplePlaceholderApp(
        title = state.activeAppId.replace("_", " ").uppercase(),
        desc = if (isKurdish) "بەرنامەی تیڤی چالاکە" else "Active TV Application",
        icon = Icons.Default.Tv,
        accentColor = ElectricBlue,
        onBack = onBack
    )
}

@Composable
private fun TvVolumeIndicator(volume: Int, isMuted: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(TvCardNavy.copy(alpha = 0.95f))
            .border(1.dp, ElectricBlue, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isMuted) Icons.Default.VolumeMute else if (volume > 50) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                contentDescription = null,
                tint = if (isMuted) Color.Red else ElectricBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isMuted) "MUTE" else "$volume%",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TvBottomBar(state: TvAppState, isKurdish: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TvSurfaceNavy)
            .border(width = 0.5.dp, color = TvCardBorder)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isKurdish)
                    "کۆنتڕۆڵ: مۆبایل بکە بە ماوس و تاچ پاد، شاشەی تیڤی لەسەر مۆبایل ببینە"
                else
                    "EShare: Use mobile as mouse, remote, and view TV screen on phone",
                color = Color(0xFF94A3B8),
                fontSize = 11.5.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonEmerald)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Server Online",
                    color = NeonEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// =========================================================================
// VIRTUAL MOUSE CURSOR & TAP RIPPLE OVERLAY
// =========================================================================

@Composable
private fun TvVirtualCursorOverlay(
    cursorXNorm: Float,
    cursorYNorm: Float,
    rippleXNorm: Float,
    rippleYNorm: Float,
    rippleTimestamp: Long,
    containerWidth: Float,
    containerHeight: Float
) {
    val cursorPixelX = cursorXNorm * containerWidth
    val cursorPixelY = cursorYNorm * containerHeight

    val isRecentClick = (System.currentTimeMillis() - rippleTimestamp) < 700

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw expanding click ripple if recently clicked
        if (isRecentClick && rippleXNorm >= 0f && rippleYNorm >= 0f) {
            val ripplePixelX = rippleXNorm * containerWidth
            val ripplePixelY = rippleYNorm * containerHeight
            val elapsed = (System.currentTimeMillis() - rippleTimestamp).toFloat()
            val radius = 10f + (elapsed / 700f) * 60f
            val alpha = (1f - elapsed / 700f).coerceIn(0f, 1f)

            drawCircle(
                color = ElectricBlue.copy(alpha = alpha),
                radius = radius,
                center = Offset(ripplePixelX, ripplePixelY),
                style = Stroke(width = 4f)
            )
        }

        // Draw cursor pointer arrow
        val path = Path().apply {
            moveTo(cursorPixelX, cursorPixelY)
            lineTo(cursorPixelX + 22f, cursorPixelY + 34f)
            lineTo(cursorPixelX + 11f, cursorPixelY + 34f)
            lineTo(cursorPixelX + 5f, cursorPixelY + 50f)
            lineTo(cursorPixelX - 3f, cursorPixelY + 48f)
            lineTo(cursorPixelX + 3f, cursorPixelY + 32f)
            lineTo(cursorPixelX - 10f, cursorPixelY + 32f)
            close()
        }

        // Drop shadow for cursor
        val shadowPath = Path().apply {
            moveTo(cursorPixelX + 2f, cursorPixelY + 2f)
            lineTo(cursorPixelX + 24f, cursorPixelY + 36f)
            lineTo(cursorPixelX + 13f, cursorPixelY + 36f)
            lineTo(cursorPixelX + 7f, cursorPixelY + 52f)
            lineTo(cursorPixelX - 1f, cursorPixelY + 50f)
            lineTo(cursorPixelX + 5f, cursorPixelY + 34f)
            lineTo(cursorPixelX - 8f, cursorPixelY + 34f)
            close()
        }
        drawPath(shadowPath, color = Color.Black.copy(alpha = 0.5f))

        // Draw Cursor Main Body
        drawPath(path, color = ElectricBlue)
        drawPath(path, color = Color.White, style = Stroke(width = 2.5f))

        // Tiny glowing core at the tip
        drawCircle(
            color = Color.White,
            radius = 3f,
            center = Offset(cursorPixelX, cursorPixelY)
        )
    }
}
