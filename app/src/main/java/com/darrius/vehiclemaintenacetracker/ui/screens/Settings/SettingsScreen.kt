package com.darrius.vehiclemaintenacetracker.ui.screens.Settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// ── Colour tokens ────────────────────────────────────────────────────────────
private val BackgroundDark   = Color(0xFF0F1117)
private val SurfaceDark      = Color(0xFF1A1D27)
private val CardDark         = Color(0xFF222636)
private val AccentOrange     = Color(0xFFFF6B35)
private val AccentBlue       = Color(0xFF4A9EFF)
private val TextPrimary      = Color(0xFFF0F2F8)
private val TextSecondary    = Color(0xFF8B91A8)
private val DividerColor     = Color(0xFF2E3349)
private val SuccessGreen     = Color(0xFF3DD68C)

// ── Data model ───────────────────────────────────────────────────────────────
data class SettingsItem(
    val icon: ImageVector,
    val iconTint: Color,
    val title: String,
    val subtitle: String? = null,
    val type: ItemType = ItemType.NAVIGATE
)

enum class ItemType { NAVIGATE, TOGGLE, DESTRUCTIVE }

data class SettingsSection(
    val header: String,
    val items: List<SettingsItem>
)

// ── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {

    // Toggle states
    var notificationsEnabled    by remember { mutableStateOf(true) }
    var serviceReminders        by remember { mutableStateOf(true) }
    var darkModeEnabled         by remember { mutableStateOf(true) }
    var biometricEnabled        by remember { mutableStateOf(false) }
    var autoBackupEnabled       by remember { mutableStateOf(true) }
    var fuelTrackingEnabled     by remember { mutableStateOf(true) }
    var showDeleteDialog        by remember { mutableStateOf(false) }

    val sections = listOf(
        SettingsSection(
            header = "VEHICLE",
            items = listOf(
                SettingsItem(Icons.Filled.DirectionsCar,  AccentOrange,  "My Vehicles",       "3 vehicles registered"),
                SettingsItem(Icons.Filled.Build,          AccentBlue,    "Maintenance Intervals","Customize service schedules"),
                SettingsItem(Icons.Filled.LocalGasStation,AccentOrange,  "Fuel Tracking",     type = ItemType.TOGGLE),
            )
        ),
        SettingsSection(
            header = "NOTIFICATIONS",
            items = listOf(
                SettingsItem(Icons.Filled.Notifications,  AccentBlue,  "Push Notifications", type = ItemType.TOGGLE),
                SettingsItem(Icons.Filled.Schedule,       AccentOrange,"Service Reminders",  type = ItemType.TOGGLE),
                SettingsItem(Icons.Filled.NotificationsActive, AccentBlue, "Reminder Timing","7 days before due"),
            )
        ),
        SettingsSection(
            header = "APPEARANCE",
            items = listOf(
                SettingsItem(Icons.Filled.DarkMode,       AccentBlue,   "Dark Mode",         type = ItemType.TOGGLE),
                SettingsItem(Icons.Filled.Palette,        AccentOrange, "Theme Color",       "Orange / Default"),
                SettingsItem(Icons.Filled.TextFields,     AccentBlue,   "Text Size",         "Medium"),
            )
        ),
        SettingsSection(
            header = "DATA & PRIVACY",
            items = listOf(
                SettingsItem(Icons.Filled.Backup,         SuccessGreen, "Auto Backup",       type = ItemType.TOGGLE),
                SettingsItem(Icons.Filled.FileDownload,   AccentBlue,   "Export Data",       "CSV / PDF"),
                SettingsItem(Icons.Filled.Fingerprint,    AccentOrange, "Biometric Lock",    type = ItemType.TOGGLE),
                SettingsItem(Icons.Filled.PrivacyTip,     AccentBlue,   "Privacy Policy"),
            )
        ),
        SettingsSection(
            header = "SUPPORT",
            items = listOf(
                SettingsItem(Icons.Filled.HelpOutline,    AccentBlue,   "Help & FAQ"),
                SettingsItem(Icons.Filled.Star,           AccentOrange, "Rate the App"),
                SettingsItem(Icons.Filled.Share,          AccentBlue,   "Share App"),
                SettingsItem(Icons.Filled.Info,           TextSecondary,"App Version",       "v2.4.1"),
            )
        ),
        SettingsSection(
            header = "ACCOUNT",
            items = listOf(
                SettingsItem(Icons.Filled.DeleteForever,  Color(0xFFFF4757), "Clear All Data", type = ItemType.DESTRUCTIVE),
                SettingsItem(Icons.Filled.Logout,         Color(0xFFFF4757), "Sign Out",       type = ItemType.DESTRUCTIVE),
            )
        )
    )

    // Toggle resolvers
    fun getToggleState(title: String): Boolean = when (title) {
        "Push Notifications"  -> notificationsEnabled
        "Service Reminders"   -> serviceReminders
        "Dark Mode"           -> darkModeEnabled
        "Biometric Lock"      -> biometricEnabled
        "Auto Backup"         -> autoBackupEnabled
        "Fuel Tracking"       -> fuelTrackingEnabled
        else -> false
    }
    fun onToggleChange(title: String, value: Boolean) {
        when (title) {
            "Push Notifications" -> notificationsEnabled = value
            "Service Reminders"  -> serviceReminders     = value
            "Dark Mode"          -> darkModeEnabled       = value
            "Biometric Lock"     -> biometricEnabled      = value
            "Auto Backup"        -> autoBackupEnabled     = value
            "Fuel Tracking"      -> fuelTrackingEnabled   = value
        }
    }

    // ── Delete confirmation dialog ───────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor   = CardDark,
            titleContentColor= TextPrimary,
            textContentColor = TextSecondary,
            title  = { Text("Clear All Data?", fontWeight = FontWeight.Bold) },
            text   = { Text("This will permanently delete all vehicles, service records and fuel logs. This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Delete", color = Color(0xFFFF4757), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = AccentBlue)
                }
            }
        )
    }

    // ── Root scaffold ────────────────────────────────────────────────────────
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Profile card ─────────────────────────────────────────────────
            ProfileCard()

            Spacer(Modifier.height(8.dp))

            // ── Settings sections ────────────────────────────────────────────
            sections.forEach { section ->
                SettingsSectionBlock(
                    section      = section,
                    getToggle    = ::getToggleState,
                    onToggle     = ::onToggleChange,
                    onDestructive= { title ->
                        if (title == "Clear All Data") showDeleteDialog = true
                    }
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Profile card ─────────────────────────────────────────────────────────────
@Composable
private fun ProfileCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1E2540), Color(0xFF252B45))
                )
            )
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(AccentOrange, Color(0xFFFF9A5C)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "D",
                    color      = Color.White,
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Darrius",
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp
                )
                Text(
                    "darrius@example.com",
                    color    = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip("3 Vehicles", AccentOrange)
                    StatusChip("Pro Plan", AccentBlue)
                }
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint   = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StatusChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ── Settings section block ────────────────────────────────────────────────────
@Composable
private fun SettingsSectionBlock(
    section      : SettingsSection,
    getToggle    : (String) -> Boolean,
    onToggle     : (String, Boolean) -> Unit,
    onDestructive: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

        // Section header
        Text(
            section.header,
            color      = TextSecondary,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier   = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        // Card container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardDark)
        ) {
            section.items.forEachIndexed { index, item ->
                SettingsRow(
                    item         = item,
                    toggleState  = getToggle(item.title),
                    onToggle     = { onToggle(item.title, it) },
                    onDestructive= { onDestructive(item.title) }
                )
                if (index < section.items.lastIndex) {
                    Divider(
                        color    = DividerColor,
                        modifier = Modifier.padding(start = 56.dp)
                    )
                }
            }
        }
    }
}

// ── Individual row ────────────────────────────────────────────────────────────
@Composable
private fun SettingsRow(
    item         : SettingsItem,
    toggleState  : Boolean,
    onToggle     : (Boolean) -> Unit,
    onDestructive: () -> Unit
) {
    val isDestructive = item.type == ItemType.DESTRUCTIVE

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                when (item.type) {
                    ItemType.DESTRUCTIVE -> onDestructive()
                    ItemType.TOGGLE      -> onToggle(!toggleState)
                    ItemType.NAVIGATE    -> { /* navigate */ }
                }
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon badge
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(
                    if (isDestructive) item.iconTint.copy(alpha = 0.12f)
                    else item.iconTint.copy(alpha = 0.15f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                item.icon,
                contentDescription = null,
                tint   = item.iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.title,
                color      = if (isDestructive) item.iconTint else TextPrimary,
                fontWeight = FontWeight.Medium,
                fontSize   = 15.sp
            )
            if (!item.subtitle.isNullOrBlank() && item.type != ItemType.TOGGLE) {
                Text(
                    item.subtitle,
                    color    = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Trailing control
        when (item.type) {
            ItemType.TOGGLE -> Switch(
                checked  = toggleState,
                onCheckedChange = onToggle,
                colors   = SwitchDefaults.colors(
                    checkedThumbColor  = Color.White,
                    checkedTrackColor  = AccentOrange,
                    uncheckedThumbColor= TextSecondary,
                    uncheckedTrackColor= SurfaceDark
                )
            )
            ItemType.NAVIGATE -> Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint   = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            ItemType.DESTRUCTIVE -> { /* no trailing */ }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}