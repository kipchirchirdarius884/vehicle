package com.darrius.vehiclemaintenacetracker.ui.screens.Notifications

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// ── Data Model & Sample Data (unchanged) ───────────────────────────────────
enum class NotificationPriority { URGENT, WARNING, INFO }

data class MaintenanceNotification(
    val id: Int,
    val title: String,
    val message: String,
    val vehicle: String,
    val timeAgo: String,
    val priority: NotificationPriority,
    val icon: ImageVector,
    val isRead: Boolean = false,
    val actionLabel: String? = null
)

private val sampleNotifications = listOf(
    MaintenanceNotification(1, "Oil Change Due", "Your Toyota Camry is 200 miles past the recommended oil change interval. Schedule service soon.", "2021 Toyota Camry", "Just now", NotificationPriority.URGENT, Icons.Filled.Warning, actionLabel = "Book Service"),
    MaintenanceNotification(2, "Tire Rotation Reminder", "Rotate your tires to ensure even wear...", "2021 Toyota Camry", "2 hrs ago", NotificationPriority.WARNING, Icons.Filled.Refresh, actionLabel = "Schedule"),
    MaintenanceNotification(3, "Brake Inspection", "Brake pads are approaching minimum thickness...", "2019 Honda CR-V", "5 hrs ago", NotificationPriority.WARNING, Icons.Filled.PriorityHigh, actionLabel = "Learn More"),
    MaintenanceNotification(4, "Service Completed", "Air filter replacement was successfully logged...", "2019 Honda CR-V", "Yesterday", NotificationPriority.INFO, Icons.Filled.CheckCircle, isRead = true),
    MaintenanceNotification(5, "Battery Health Check", "Your vehicle battery is 4 years old...", "2021 Toyota Camry", "2 days ago", NotificationPriority.INFO, Icons.Filled.BatteryAlert, isRead = true, actionLabel = "Check Now"),
    MaintenanceNotification(6, "Annual Registration Due", "Vehicle registration for your Honda CR-V expires in 30 days...", "2019 Honda CR-V", "3 days ago", NotificationPriority.INFO, Icons.Filled.Description, isRead = true, actionLabel = "Renew")
)

// ── Colors (Matching Login Screen) ─────────────────────────────────────────
private val GradientBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
)

private val CardBackground = Color(0xFF1E2A35)
private val AccentColor = Color(0xFF4FC3F7)

private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFB0BEC5)

private fun priorityColor(p: NotificationPriority) = when (p) {
    NotificationPriority.URGENT -> Color(0xFFFF5252)
    NotificationPriority.WARNING -> Color(0xFFFFB300)
    NotificationPriority.INFO -> AccentColor
}

// ── Main Screen ─────────────────────────────────────────────────────────────
@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    val notifications = remember { mutableStateListOf(*sampleNotifications.toTypedArray()) }
    val unreadCount = notifications.count { !it.isRead }
    var filterTab by remember { mutableIntStateOf(0) } // 0=All, 1=Unread, 2=Urgent

    val displayed = when (filterTab) {
        1 -> notifications.filter { !it.isRead }
        2 -> notifications.filter { it.priority == NotificationPriority.URGENT }
        else -> notifications
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = GradientBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                NotificationsTopBar(
                    unreadCount = unreadCount,
                    onMarkAllRead = { notifications.replaceAll { it.copy(isRead = true) } },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                StatsBanner(notifications)

                FilterTabs(
                    selected = filterTab,
                    unread = notifications.count { !it.isRead },
                    urgent = notifications.count { it.priority == NotificationPriority.URGENT },
                    onSelect = { filterTab = it }
                )

                Spacer(Modifier.height(8.dp))

                if (displayed.isEmpty()) {
                    EmptyState(Modifier.fillMaxSize())
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(displayed, key = { _, n -> n.id }) { index, notification ->
                            var visible by remember { mutableStateOf(false) }
                            LaunchedEffect(notification.id) { visible = true }

                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(tween(300, delayMillis = index * 50)) +
                                        slideInVertically(tween(300, delayMillis = index * 50)) { it / 4 }
                            ) {
                                NotificationCard(
                                    notification = notification,
                                    onDismiss = { notifications.remove(notification) },
                                    onMarkRead = {
                                        val i = notifications.indexOf(notification)
                                        if (i >= 0) notifications[i] = notification.copy(isRead = true)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Top Bar ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsTopBar(
    unreadCount: Int,
    onMarkAllRead: () -> Unit,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = TextPrimary
        ),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
        },
        title = {
            Text(
                text = "Notifications",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            if (unreadCount > 0) {
                TextButton(onClick = onMarkAllRead) {
                    Text("Mark all read", color = AccentColor, fontWeight = FontWeight.Medium)
                }
            }
        }
    )
}

// ── Stats Banner ────────────────────────────────────────────────────────────
@Composable
private fun StatsBanner(notifications: List<MaintenanceNotification>) {
    val urgent = notifications.count { it.priority == NotificationPriority.URGENT }
    val warning = notifications.count { it.priority == NotificationPriority.WARNING }
    val info = notifications.count { it.priority == NotificationPriority.INFO }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatChip("Urgent", urgent, Color(0xFFFF5252), Modifier.weight(1f))
        StatChip("Warning", warning, Color(0xFFFFB300), Modifier.weight(1f))
        StatChip("Info", info, AccentColor, Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$count", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = color)
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

// ── Filter Tabs ─────────────────────────────────────────────────────────────
@Composable
private fun FilterTabs(selected: Int, unread: Int, urgent: Int, onSelect: (Int) -> Unit) {
    val tabs = listOf("All", "Unread ($unread)", "Urgent ($urgent)")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { i, label ->
            val active = selected == i
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (active) AccentColor else CardBackground)
                    .clickable { onSelect(i) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (active) Color(0xFF0F2027) else TextSecondary,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

// ── Notification Card (Login-style) ─────────────────────────────────────────
@Composable
private fun NotificationCard(
    notification: MaintenanceNotification,
    onDismiss: () -> Unit,
    onMarkRead: () -> Unit
) {
    val accent = priorityColor(notification.priority)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(accent, RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp))
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(accent.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(notification.icon, null, tint = accent, modifier = Modifier.size(26.dp))
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = notification.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.5.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = notification.vehicle,
                            fontSize = 13.5.sp,
                            color = accent
                        )
                    }

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(accent)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )

                if (!notification.isRead || notification.actionLabel != null) {
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!notification.isRead) {
                            TextButton(onClick = onMarkRead) {
                                Text("Mark as read", color = TextSecondary)
                            }
                        }

                        if (notification.actionLabel != null) {
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(46.dp)
                            ) {
                                Text(notification.actionLabel, color = Color(0xFF0F2027), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Dismiss", tint = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

// ── Empty State ─────────────────────────────────────────────────────────────
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(90.dp),
            tint = TextSecondary.copy(0.4f)
        )
        Spacer(Modifier.height(24.dp))
        Text("All caught up!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(
            "No notifications at the moment.\nWe'll alert you when maintenance is due.",
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    NotificationScreen(rememberNavController())
}