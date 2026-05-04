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
    var showPrivacyDialog       by remember { mutableStateOf(false) }
    var showHelpFaqDialog       by remember { mutableStateOf(false) }

    val sections = listOf(
        SettingsSection(
            header = "VEHICLE",
            items = listOf(
                SettingsItem(Icons.Filled.DirectionsCar, AccentOrange, "My Vehicles", "3 vehicles registered"),
                SettingsItem(Icons.Filled.Build, AccentBlue, "Maintenance Intervals", "Customize service schedules"),
                SettingsItem(Icons.Filled.LocalGasStation, AccentOrange, "Fuel Tracking", type = ItemType.TOGGLE),
            )
        ),
        SettingsSection(
            header = "NOTIFICATIONS",
            items = listOf(
                SettingsItem(Icons.Filled.Notifications, AccentBlue, "Push Notifications", type = ItemType.TOGGLE),
                SettingsItem(Icons.Filled.Schedule, AccentOrange, "Service Reminders", type = ItemType.TOGGLE),
                SettingsItem(Icons.Filled.NotificationsActive, AccentBlue, "Reminder Timing", "7 days before due"),
            )
        ),
        SettingsSection(
            header = "APPEARANCE",
            items = listOf(
                SettingsItem(Icons.Filled.DarkMode, AccentBlue, "Dark Mode", type = ItemType.TOGGLE),
                SettingsItem(Icons.Filled.Palette, AccentOrange, "Theme Color", "Orange / Default"),
                SettingsItem(Icons.Filled.TextFields, AccentBlue, "Text Size", "Medium"),
            )
        ),
        SettingsSection(
            header = "DATA & PRIVACY",
            items = listOf(
                SettingsItem(Icons.Filled.Backup, SuccessGreen, "Auto Backup", type = ItemType.TOGGLE),
                SettingsItem(Icons.Filled.FileDownload, AccentBlue, "Export Data", "CSV / PDF"),
                SettingsItem(Icons.Filled.Fingerprint, AccentOrange, "Biometric Lock", type = ItemType.TOGGLE),
                SettingsItem(Icons.Filled.PrivacyTip, AccentBlue, "Privacy Policy"),
            )
        ),
        SettingsSection(
            header = "SUPPORT",
            items = listOf(
                SettingsItem(Icons.Filled.HelpOutline, AccentBlue, "Help & FAQ"),
                SettingsItem(Icons.Filled.Star, AccentOrange, "Rate the App"),
                SettingsItem(Icons.Filled.Share, AccentBlue, "Share App"),
                SettingsItem(Icons.Filled.Info, TextSecondary, "App Version", "v2.4.1"),
            )
        ),
        SettingsSection(
            header = "ACCOUNT",
            items = listOf(
                SettingsItem(Icons.Filled.DeleteForever, Color(0xFFFF4757), "Clear All Data", type = ItemType.DESTRUCTIVE),
                SettingsItem(Icons.Filled.Logout, Color(0xFFFF4757), "Sign Out", type = ItemType.DESTRUCTIVE),
            )
        )
    )

    // Toggle helpers (unchanged)
    fun getToggleState(title: String): Boolean = when (title) {
        "Push Notifications" -> notificationsEnabled
        "Service Reminders" -> serviceReminders
        "Dark Mode" -> darkModeEnabled
        "Biometric Lock" -> biometricEnabled
        "Auto Backup" -> autoBackupEnabled
        "Fuel Tracking" -> fuelTrackingEnabled
        else -> false
    }

    fun onToggleChange(title: String, value: Boolean) {
        when (title) {
            "Push Notifications" -> notificationsEnabled = value
            "Service Reminders" -> serviceReminders = value
            "Dark Mode" -> darkModeEnabled = value
            "Biometric Lock" -> biometricEnabled = value
            "Auto Backup" -> autoBackupEnabled = value
            "Fuel Tracking" -> fuelTrackingEnabled = value
        }
    }

    // ── Dialogs ──────────────────────────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = CardDark,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Clear All Data?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete all vehicles, service records and fuel logs. This action cannot be undone.") },
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

    PrivacyPolicyDialog(showPrivacyDialog) { showPrivacyDialog = false }
    HelpFaqDialog(showHelpFaqDialog) { showHelpFaqDialog = false }

    // ── Root scaffold ────────────────────────────────────────────────────────
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp) },
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
            ProfileCard()
            Spacer(Modifier.height(8.dp))

            sections.forEach { section ->
                SettingsSectionBlock(
                    section = section,
                    getToggle = ::getToggleState,
                    onToggle = ::onToggleChange,
                    onDestructive = { title ->
                        if (title == "Clear All Data") showDeleteDialog = true
                        else if (title == "Sign Out") {
                            // TODO: Clear user session / tokens if needed
                            navController.navigate("login") {
                                popUpTo(0) { saveState = true }
                            }
                        }
                    },
                    onNavigate = { title ->
                        when (title) {
                            "My Vehicles" -> navController.navigate("my_vehicles")
                            "Maintenance Intervals" -> navController.navigate("maintenance_intervals")
                            "Privacy Policy" -> showPrivacyDialog = true
                            "Help & FAQ" -> showHelpFaqDialog = true
                            // Add more navigation routes as you create the screens
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileCard() {
    TODO("Not yet implemented")
}

// ── New Dialogs ─────────────────────────────────────────────────────────────

@Composable
private fun PrivacyPolicyDialog(visible: Boolean, onDismiss: () -> Unit) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = { Text("Privacy Policy") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "Vehicle Maintenance Tracker Privacy Policy\n\n" +
                            "Last updated: May 2026\n\n" +
                            "We respect your privacy. This app collects vehicle information, service records, and fuel logs solely to provide maintenance tracking features.\n\n" +
                            "• We do not sell your data.\n" +
                            "• Data is stored locally or in your chosen backup service.\n" +
                            "• Biometric authentication is used to protect your records.\n\n" +
                            "For full details, visit our website.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AccentBlue)
            }
        }
    )
}

@Composable
private fun HelpFaqDialog(visible: Boolean, onDismiss: () -> Unit) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = { Text("Help & FAQ") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("Frequently Asked Questions\n\n", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text("Q: How do I add a new vehicle?\n", fontWeight = FontWeight.SemiBold)
                Text("A: Go to My Vehicles → Add Vehicle and fill in the details.\n\n")

                Text("Q: How are service reminders calculated?\n", fontWeight = FontWeight.SemiBold)
                Text("A: Based on the mileage interval or time interval you set in Maintenance Intervals.\n\n")

                Text("Q: Is my data backed up?\n", fontWeight = FontWeight.SemiBold)
                Text("A: Enable Auto Backup in settings to sync with your cloud account.\n\n")

                Text("Q: How do I export my records?\n", fontWeight = FontWeight.SemiBold)
                Text("A: Use the Export Data option in Data & Privacy section.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AccentBlue)
            }
        }
    )
}

// ── Updated SettingsSectionBlock with onNavigate ─────────────────────────────
@Composable
private fun SettingsSectionBlock(
    section: SettingsSection,
    getToggle: (String) -> Boolean,
    onToggle: (String, Boolean) -> Unit,
    onDestructive: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            section.header,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardDark)
        ) {
            section.items.forEachIndexed { index, item ->
                SettingsRow(
                    item = item,
                    toggleState = getToggle(item.title),
                    onToggle = { onToggle(item.title, it) },
                    onDestructive = { onDestructive(item.title) },
                    onNavigate = { onNavigate(item.title) }
                )
                if (index < section.items.lastIndex) {
                    Divider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
                }
            }
        }
    }
}

// ── Updated SettingsRow ─────────────────────────────────────────────────────
@Composable
private fun SettingsRow(
    item: SettingsItem,
    toggleState: Boolean,
    onToggle: (Boolean) -> Unit,
    onDestructive: () -> Unit,
    onNavigate: () -> Unit
) {
    val isDestructive = item.type == ItemType.DESTRUCTIVE

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                when (item.type) {
                    ItemType.DESTRUCTIVE -> onDestructive()
                    ItemType.TOGGLE -> onToggle(!toggleState)
                    ItemType.NAVIGATE -> onNavigate()
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
                tint = item.iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.title,
                color = if (isDestructive) item.iconTint else TextPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
            if (!item.subtitle.isNullOrBlank() && item.type != ItemType.TOGGLE) {
                Text(item.subtitle, color = TextSecondary, fontSize = 12.sp)
            }
        }

        when (item.type) {
            ItemType.TOGGLE -> Switch(
                checked = toggleState,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AccentOrange,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = SurfaceDark
                )
            )
            ItemType.NAVIGATE -> Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            ItemType.DESTRUCTIVE -> {}
        }
    }
}

// ProfileCard and StatusChip remain unchanged
// (Copy them from your original code)

@Preview(showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}