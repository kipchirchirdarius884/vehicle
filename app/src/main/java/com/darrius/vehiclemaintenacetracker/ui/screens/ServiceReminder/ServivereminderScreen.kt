package com.darrius.vehiclemaintenacetracker.ui.screens.ServiceReminder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
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

// ─── Data Models ────────────────────────────────────────────────────────────

enum class ReminderStatus { OVERDUE, DUE_SOON, UPCOMING }

data class ServiceReminder(
    val id: Int,
    val title: String,
    val description: String,
    val dueMileage: Int,
    val currentMileage: Int,
    val dueDateLabel: String,
    val icon: ImageVector,
    val status: ReminderStatus
)

data class CarInfo(
    val name: String,
    val plate: String,
    val mileage: Int,
    val imageInitials: String
)

// ─── Sample Data ─────────────────────────────────────────────────────────────

private val sampleCar = CarInfo(
    name = "Toyota Camry 2020",
    plate = "KDA 123X",
    mileage = 47_800,
    imageInitials = "TC"
)

private val sampleReminders = listOf(
    ServiceReminder(
        id = 1,
        title = "Oil Change",
        description = "Engine oil and filter replacement due",
        dueMileage = 48_000,
        currentMileage = 47_800,
        dueDateLabel = "200 km away",
        icon = Icons.Outlined.WaterDrop,
        status = ReminderStatus.DUE_SOON
    ),
    ServiceReminder(
        id = 2,
        title = "Tire Rotation",
        description = "Rotate tires for even tread wear",
        dueMileage = 47_500,
        currentMileage = 47_800,
        dueDateLabel = "300 km overdue",
        icon = Icons.Outlined.Sync,
        status = ReminderStatus.OVERDUE
    ),
    ServiceReminder(
        id = 3,
        title = "Brake Inspection",
        description = "Check brake pads and rotors",
        dueMileage = 50_000,
        currentMileage = 47_800,
        dueDateLabel = "2,200 km away",
        icon = Icons.Outlined.Warning,
        status = ReminderStatus.UPCOMING
    ),
    ServiceReminder(
        id = 4,
        title = "Air Filter",
        description = "Engine air filter replacement",
        dueMileage = 52_000,
        currentMileage = 47_800,
        dueDateLabel = "4,200 km away",
        icon = Icons.Outlined.Air,
        status = ReminderStatus.UPCOMING
    ),
    ServiceReminder(
        id = 5,
        title = "Full Service",
        description = "Comprehensive vehicle inspection & service",
        dueMileage = 55_000,
        currentMileage = 47_800,
        dueDateLabel = "7,200 km away",
        icon = Icons.Outlined.Build,
        status = ReminderStatus.UPCOMING
    )
)

// ─── Color Palette ────────────────────────────────────────────────────────────

private val DarkBg = Color(0xFF0F1117)
private val SurfaceCard = Color(0xFF1A1D27)
private val SurfaceElevated = Color(0xFF222535)
private val AccentBlue = Color(0xFF4F8EF7)
private val AccentOrange = Color(0xFFFF7043)
private val AccentGreen = Color(0xFF2ECC71)
private val TextPrimary = Color(0xFFF0F2FF)
private val TextSecondary = Color(0xFF8B90A8)
private val DividerColor = Color(0xFF2A2D3E)

// ─── Main Screen ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceReminderScreen(navController: NavController) {
    val overdueCount = sampleReminders.count { it.status == ReminderStatus.OVERDUE }
    val dueSoonCount = sampleReminders.count { it.status == ReminderStatus.DUE_SOON }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Service Reminders",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add reminder",
                            tint = AccentBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = AccentBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Schedule Service", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Car Info Card
            item {
                CarInfoCard(car = sampleCar)
            }

            // Alert Banner
            if (overdueCount > 0 || dueSoonCount > 0) {
                item {
                    AlertBanner(overdueCount = overdueCount, dueSoonCount = dueSoonCount)
                }
            }

            // Section header
            item {
                Text(
                    "Upcoming Maintenance",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            // Reminder Cards
            items(sampleReminders) { reminder ->
                ReminderCard(reminder = reminder)
            }
        }
    }
}

// ─── Car Info Card ────────────────────────────────────────────────────────────

@Composable
fun CarInfoCard(car: CarInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1E3A5F), Color(0xFF162B47))
                )
            )
            .border(1.dp, Color(0xFF2A4A6F), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AccentBlue.copy(alpha = 0.2f))
                    .border(1.5.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    car.imageInitials,
                    color = AccentBlue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(car.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Outlined.DirectionsCar,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(car.plate, color = TextSecondary, fontSize = 13.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${"%,d".format(car.mileage)} km",
                    color = AccentBlue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Text("Current mileage", color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

// ─── Alert Banner ─────────────────────────────────────────────────────────────

@Composable
fun AlertBanner(overdueCount: Int, dueSoonCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AccentOrange.copy(alpha = 0.12f))
            .border(1.dp, AccentOrange.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Default.NotificationImportant,
            contentDescription = null,
            tint = AccentOrange,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Attention Required",
                color = AccentOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            val msg = buildString {
                if (overdueCount > 0) append("$overdueCount overdue")
                if (overdueCount > 0 && dueSoonCount > 0) append(" · ")
                if (dueSoonCount > 0) append("$dueSoonCount due soon")
            }
            Text(msg, color = TextSecondary, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AccentOrange)
    }
}

// ─── Reminder Card ────────────────────────────────────────────────────────────

@Composable
fun ReminderCard(reminder: ServiceReminder) {
    var expanded by remember { mutableStateOf(false) }

    val statusColor = when (reminder.status) {
        ReminderStatus.OVERDUE -> AccentOrange
        ReminderStatus.DUE_SOON -> Color(0xFFFFD54F)
        ReminderStatus.UPCOMING -> AccentGreen
    }
    val statusLabel = when (reminder.status) {
        ReminderStatus.OVERDUE -> "Overdue"
        ReminderStatus.DUE_SOON -> "Due Soon"
        ReminderStatus.UPCOMING -> "Upcoming"
    }

    val progressFraction = if (reminder.status == ReminderStatus.OVERDUE) 1f
    else (reminder.currentMileage.toFloat() / reminder.dueMileage.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(
                1.dp,
                if (reminder.status == ReminderStatus.OVERDUE)
                    AccentOrange.copy(alpha = 0.4f) else DividerColor,
                RoundedCornerShape(16.dp)
            )
            .clickable { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    reminder.icon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        reminder.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    StatusChip(label = statusLabel, color = statusColor)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    reminder.dueDateLabel,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(DividerColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressFraction)
                    .clip(RoundedCornerShape(2.dp))
                    .background(statusColor)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Expanded details
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceElevated)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    reminder.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
                HorizontalDivider(color = DividerColor)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MileageInfo("Current", "${"%,d".format(reminder.currentMileage)} km", TextSecondary)
                    MileageInfo("Due At", "${"%,d".format(reminder.dueMileage)} km", statusColor)
                    MileageInfo(
                        "Gap",
                        "${"%,d".format(Math.abs(reminder.dueMileage - reminder.currentMileage))} km",
                        TextPrimary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Dismiss", fontSize = 13.sp)
                    }
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Book Service", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MileageInfo(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
fun ServiceReminderScreenPreview() {
    ServiceReminderScreen(rememberNavController())
}