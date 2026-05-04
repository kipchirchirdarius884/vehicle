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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// ── Data model ──────────────────────────────────────────────────────────────

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

// ── Sample data ──────────────────────────────────────────────────────────────

private val sampleNotifications = listOf(
    MaintenanceNotification(
        id = 1,
        title = "Oil Change Due",
        message = "Your Toyota Camry is 200 miles past the recommended oil change interval. Schedule service soon.",
        vehicle = "2021 Toyota Camry",
        timeAgo = "Just now",
        priority = NotificationPriority.URGENT,
        icon = Icons.Filled.Warning,
        actionLabel = "Book Service"
    ),
    MaintenanceNotification(
        id = 2,
        title = "Tire Rotation Reminder",
        message = "Rotate your tires to ensure even wear and extend tire life. Due in 150 miles.",
        vehicle = "2021 Toyota Camry",
        timeAgo = "2 hrs ago",
        priority = NotificationPriority.WARNING,
        icon = Icons.Filled.Refresh,
        actionLabel = "Schedule"
    ),
    MaintenanceNotification(
        id = 3,
        title = "Brake Inspection",
        message = "Brake pads are approaching minimum thickness. Inspection recommended within the next 500 miles.",
        vehicle = "2019 Honda CR-V",
        timeAgo = "5 hrs ago",
        priority = NotificationPriority.WARNING,
        icon = Icons.Filled.PriorityHigh,
        actionLabel = "Learn More"
    ),
    MaintenanceNotification(
        id = 4,
        title = "Service Completed",
        message = "Air filter replacement was successfully logged for your vehicle. Next service in 12,000 miles.",
        vehicle = "2019 Honda CR-V",
        timeAgo = "Yesterday",
        priority = NotificationPriority.INFO,
        icon = Icons.Filled.CheckCircle,
        isRead = true
    ),
    MaintenanceNotification(
        id = 5,
        title = "Battery Health Check",
        message = "Your vehicle battery is 4 years old. A health check is recommended to avoid unexpected failures.",
        vehicle = "2021 Toyota Camry",
        timeAgo = "2 days ago",
        priority = NotificationPriority.INFO,
        icon = Icons.Filled.BatteryAlert,
        isRead = true,
        actionLabel = "Check Now"
    ),
    MaintenanceNotification(
        id = 6,
        title = "Annual Registration Due",
        message = "Vehicle registration for your Honda CR-V expires in 30 days. Renew to stay compliant.",
        vehicle = "2019 Honda CR-V",
        timeAgo = "3 days ago",
        priority = NotificationPriority.INFO,
        icon = Icons.Filled.Description,
        isRead = true,
        actionLabel = "Renew"
    )
)

// ── Color helpers ────────────────────────────────────────────────────────────

private val UrgentColor    = Color(0xFFE53E3E)
private val WarningColor   = Color(0xFFDD6B20)
private val InfoColor      = Color(0xFF2B6CB0)
private val SurfaceColor   = Color(0xFFF7F8FC)
private val CardColor      = Color(0xFFFFFFFF)
private val TextPrimary    = Color(0xFF1A202C)
private val TextSecondary  = Color(0xFF718096)
private val DividerColor   = Color(0xFFE2E8F0)
private val AccentBlue     = Color(0xFF3182CE)

private fun priorityColor(p: NotificationPriority) = when (p) {
    NotificationPriority.URGENT  -> UrgentColor
    NotificationPriority.WARNING -> WarningColor
    NotificationPriority.INFO    -> InfoColor
}

private fun priorityBg(p: NotificationPriority) = when (p) {
    NotificationPriority.URGENT  -> Color(0xFFFFF5F5)
    NotificationPriority.WARNING -> Color(0xFFFFFAF0)
    NotificationPriority.INFO    -> Color(0xFFEBF8FF)
}

// ── Screen ───────────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {

    val notifications = remember { mutableStateListOf(*sampleNotifications.toTypedArray()) }
    val unreadCount   = notifications.count { !it.isRead }
    var filterTab     by remember { mutableIntStateOf(0) }   // 0 = All, 1 = Unread, 2 = Urgent

    val displayed = when (filterTab) {
        1    -> notifications.filter { !it.isRead }
        2    -> notifications.filter { it.priority == NotificationPriority.URGENT }
        else -> notifications
    }

    Scaffold(
        containerColor = SurfaceColor,
        topBar = {
            NotificationsTopBar(
                unreadCount  = unreadCount,
                onMarkAllRead = {
                    notifications.replaceAll { it.copy(isRead = true) }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ── Stats banner ─────────────────────────────────────────────
            StatsBanner(notifications = notifications)

            Spacer(Modifier.height(4.dp))

            // ── Filter tabs ───────────────────────────────────────────────
            FilterTabs(
                selected  = filterTab,
                unread    = notifications.count { !it.isRead },
                urgent    = notifications.count { it.priority == NotificationPriority.URGENT },
                onSelect  = { filterTab = it }
            )

            // ── List ──────────────────────────────────────────────────────
            if (displayed.isEmpty()) {
                EmptyState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    contentPadding    = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(displayed, key = { _, n -> n.id }) { index, notification ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(notification.id) { visible = true }

                        AnimatedVisibility(
                            visible = visible,
                            enter   = fadeIn(tween(300, delayMillis = index * 60)) +
                                    slideInVertically(tween(300, delayMillis = index * 60)) { it / 4 }
                        ) {
                            NotificationCard(
                                notification = notification,
                                onDismiss    = { notifications.remove(notification) },
                                onMarkRead   = {
                                    val i = notifications.indexOf(notification)
                                    if (i >= 0) notifications[i] = notification.copy(isRead = true)
                                }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsTopBar(
    unreadCount: Int,
    onMarkAllRead: () -> Unit,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor    = Color.White,
            titleContentColor = TextPrimary
        ),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = "Notifications",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp,
                    color      = TextPrimary
                )
                if (unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier        = Modifier
                            .clip(CircleShape)
                            .background(AccentBlue)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text      = "$unreadCount",
                            color     = Color.White,
                            fontSize  = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        actions = {
            if (unreadCount > 0) {
                TextButton(onClick = onMarkAllRead) {
                    Text("Mark all read", color = AccentBlue, fontSize = 13.sp)
                }
            }
        }
    )
}

// ── Stats banner ──────────────────────────────────────────────────────────────

@Composable
private fun StatsBanner(notifications: List<MaintenanceNotification>) {
    val urgent  = notifications.count { it.priority == NotificationPriority.URGENT }
    val warning = notifications.count { it.priority == NotificationPriority.WARNING }
    val info    = notifications.count { it.priority == NotificationPriority.INFO }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatChip(label = "Urgent",  count = urgent,  color = UrgentColor,  modifier = Modifier.weight(1f))
        StatChip(label = "Warning", count = warning, color = WarningColor, modifier = Modifier.weight(1f))
        StatChip(label = "Info",    count = info,    color = InfoColor,    modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$count", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = color)
            Text(text = label,   fontWeight = FontWeight.Medium,    fontSize = 11.sp, color = color.copy(alpha = 0.8f))
        }
    }
}

// ── Filter tabs ───────────────────────────────────────────────────────────────

@Composable
private fun FilterTabs(selected: Int, unread: Int, urgent: Int, onSelect: (Int) -> Unit) {
    val labels = listOf("All", "Unread ($unread)", "Urgent ($urgent)")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEachIndexed { i, label ->
            val active = selected == i
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (active) AccentBlue else Color(0xFFF0F4F8))
                    .clickable { onSelect(i) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = label,
                    color      = if (active) Color.White else TextSecondary,
                    fontSize   = 13.sp,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }

    Divider(color = DividerColor, thickness = 1.dp)
}

// ── Notification card ─────────────────────────────────────────────────────────

@Composable
private fun NotificationCard(
    notification: MaintenanceNotification,
    onDismiss:    () -> Unit,
    onMarkRead:   () -> Unit
) {
    val accentColor = priorityColor(notification.priority)
    val bgColor     = if (notification.isRead) CardColor else priorityBg(notification.priority)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 1.dp else 3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Priority stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            listOf(accentColor, accentColor.copy(alpha = 0.5f))
                        ),
                        RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
                    )
            )

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
            ) {

                // Header row
                Row(
                    modifier       = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon circle
                    Box(
                        modifier        = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = notification.icon,
                            contentDescription = null,
                            tint               = accentColor,
                            modifier           = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = notification.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 14.sp,
                            color      = TextPrimary,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                        Text(
                            text     = notification.vehicle,
                            fontSize = 11.sp,
                            color    = accentColor,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Unread dot + time
                    Column(horizontalAlignment = Alignment.End) {
                        if (!notification.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(accentColor)
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                        Text(
                            text     = notification.timeAgo,
                            fontSize = 10.sp,
                            color    = TextSecondary
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Message body
                Text(
                    text     = notification.message,
                    fontSize = 12.sp,
                    color    = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )

                // Action row
                if (!notification.isRead || notification.actionLabel != null) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier       = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        if (!notification.isRead) {
                            TextButton(
                                onClick       = onMarkRead,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Mark read", color = TextSecondary, fontSize = 12.sp)
                            }
                        }

                        if (notification.actionLabel != null) {
                            Spacer(Modifier.width(4.dp))
                            Button(
                                onClick       = { /* navigate to detail */ },
                                shape         = RoundedCornerShape(8.dp),
                                colors        = ButtonDefaults.buttonColors(containerColor = accentColor),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier      = Modifier.height(32.dp)
                            ) {
                                Text(notification.actionLabel, fontSize = 12.sp, color = Color.White)
                            }
                        }

                        Spacer(Modifier.width(4.dp))

                        IconButton(
                            onClick  = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Dismiss",
                                tint               = TextSecondary,
                                modifier           = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector        = Icons.Outlined.NotificationsNone,
            contentDescription = null,
            modifier           = Modifier.size(72.dp),
            tint               = TextSecondary.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(16.dp))
        Text("All caught up!", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextPrimary)
        Spacer(Modifier.height(6.dp))
        Text(
            text      = "No notifications at the moment.\nWe'll alert you when maintenance is due.",
            fontSize  = 13.sp,
            color     = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(rememberNavController())
}