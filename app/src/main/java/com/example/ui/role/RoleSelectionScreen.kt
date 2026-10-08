package com.example.ui.role

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceRole
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TvCardNavy
import com.example.ui.theme.TvDarkNavy
import com.example.ui.theme.TvSurfaceNavy

@Composable
fun RoleSelectionScreen(
    currentRole: DeviceRole,
    isKurdish: Boolean,
    onToggleLanguage: () -> Unit,
    onRoleSelected: (DeviceRole) -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    var selectedRole by remember {
        mutableStateOf(if (currentRole != DeviceRole.UNSELECTED) currentRole else DeviceRole.MOBILE_CONTROLLER)
    }

    val layoutDirection = if (isKurdish) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("role_selection_screen"),
            color = TvDarkNavy
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Subtle ambient gradient background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    NeonIndigo.copy(alpha = 0.15f),
                                    TvDarkNavy,
                                    TvSurfaceNavy
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Language Switcher at Top Right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cast,
                                contentDescription = "Logo",
                                tint = ElectricBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EShare TV",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        IconButton(
                            onClick = onToggleLanguage,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(TvCardNavy)
                                .testTag("lang_toggle_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isKurdish) "EN" else "کوردی",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Title & Subtitle
                    Text(
                        text = if (isKurdish) "دیاریکردنی جۆری ئامێر" else "Choose Device Mode",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isKurdish)
                            "ئەم بەرنامەیە لەسەر مۆبایلەکەتە یان تیڤی بۆکس؟ یەکێکیان هەڵبژێرە:"
                        else
                            "Is this app running on your Mobile or TV Box? Select mode:",
                        fontSize = 15.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Choice 1: Mobile Controller
                    RoleCard(
                        title = if (isKurdish) "مۆبایل (کۆنتڕۆڵ و ماوس)" else "Mobile (Remote & Mouse)",
                        subtitle = if (isKurdish)
                            "مۆبایلەکەت بکە بە ماوس، تەختەی تاچ، کۆنتڕۆڵ و شاشەی تیڤی لەسەر مۆبایل ببینە و کۆنتڕۆڵی تەواوی بکە"
                        else
                            "Turn your phone into a mouse, touchpad, TV remote, and mirror TV screen to mobile with full control",
                        icon = Icons.Default.PhoneAndroid,
                        accentColor = ElectricBlue,
                        isSelected = selectedRole == DeviceRole.MOBILE_CONTROLLER,
                        badges = listOf(
                            if (isKurdish) "ماوسی هەوایی" else "Air Mouse",
                            if (isKurdish) "تەختەی تاچ" else "Touchpad",
                            if (isKurdish) "میرۆر شاشە" else "TV Mirror"
                        ),
                        onClick = { selectedRole = DeviceRole.MOBILE_CONTROLLER },
                        testTag = "select_mobile_role_btn"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Choice 2: TV Box Receiver
                    RoleCard(
                        title = if (isKurdish) "تیڤی بۆکس (وەرگر / TV Box)" else "TV Box (Receiver / Display)",
                        subtitle = if (isKurdish)
                            "ئەم ئامێرە بکە بە وەرگر، فەرمانەکانی کۆنتڕۆڵ و ماوس لەسەر شاشەی گەورە پیشان بدە و پەخشی شاشە بکە"
                        else
                            "Turn this device into a TV receiver, display cursor and commands on big screen, and stream display to mobile",
                        icon = Icons.Default.Tv,
                        accentColor = NeonPurple,
                        isSelected = selectedRole == DeviceRole.TV_RECEIVER,
                        badges = listOf(
                            if (isKurdish) "وەرگری ماوس" else "Cursor Display",
                            if (isKurdish) "داشبۆردی تیڤی" else "10ft TV UI",
                            if (isKurdish) "پەخشی شاشە" else "Screen Server"
                        ),
                        onClick = { selectedRole = DeviceRole.TV_RECEIVER },
                        testTag = "select_tv_role_btn"
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Confirm Button
                    Button(
                        onClick = { onRoleSelected(selectedRole) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("confirm_role_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedRole == DeviceRole.MOBILE_CONTROLLER) ElectricBlue else NeonPurple
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (selectedRole == DeviceRole.MOBILE_CONTROLLER)
                                    Icons.Default.SettingsRemote
                                else
                                    Icons.Default.Tv,
                                contentDescription = null,
                                tint = TvDarkNavy,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isKurdish) "دەستپێکردن بەم شێوازە" else "Continue with this mode",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TvDarkNavy
                            )
                        }
                    }

                    if (onDismiss != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isKurdish) "داخستن" else "Cancel",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { onDismiss() }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    badges: List<String> = emptyList(),
    onClick: () -> Unit,
    testTag: String
) {
    val borderColor = if (isSelected) accentColor else Color(0xFF1F2937)
    val backgroundBrush = if (isSelected) {
        Brush.verticalGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.18f),
                TvCardNavy
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                TvCardNavy,
                TvSurfaceNavy
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundBrush)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(accentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = subtitle,
                    fontSize = 13.5.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 20.sp
                )

                if (badges.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (badge in badges) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(0.5.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = badge,
                                    fontSize = 11.sp,
                                    color = accentColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
