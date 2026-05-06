package com.darrius.vehiclemaintenacetracker.ui.screens.ServiceReminder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_ADDSERVICE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// ─── Data Models ────────────────────────────────────────────────────────────
enum class ReminderStatus { OVERDUE, DUE_SOON, UPCOMING }

data class ServiceReminder(
    val id: String,
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

// ─── Color Palette (Unchanged) ─────────────────────────────────────────────
val DarkBg = Color(0xFF0F1117)
val SurfaceCard = Color(0xFF1A1D27)
private val SurfaceElevated = Color(0xFF222535)
val AccentBlue = Color(0xFF4F8EF7)
private val AccentOrange = Color(0xFFFF7043)
private val AccentGreen = Color(0xFF2ECC71)
val TextPrimary = Color(0xFFF0F2FF)
private val TextSecondary = Color(0xFF8B90A8)
private val DividerColor = Color(0xFF2A2D3E)

// ─── Main Screen ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceReminderScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: "demo_user"

    var reminders by remember { mutableStateOf<List<ServiceReminder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var carInfo by remember { mutableStateOf(CarInfo("Toyota Camry 2020", "KDA 123X", 47800, "TC")) }

    val database = FirebaseDatabase.getInstance()
    val servicesRef = database.getReference("users/$userId/services")

    // Load Services from Firebase
    LaunchedEffect(userId) {
        servicesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ServiceReminder>()
                for (child in snapshot.children) {
                    val service = child.getValue(com.darrius.vehiclemaintenacetracker.ui.screens.AddService.ServiceRecord::class.java)
                    service?.let {
                        val dueMileage = it.nextServiceMileage.toIntOrNull() ?: 50000
                        val currentMileage = carInfo.mileage

                        val status = when {
                            currentMileage > dueMileage + 500 -> ReminderStatus.OVERDUE
                            currentMileage > dueMileage - 1000 -> ReminderStatus.DUE_SOON
                            else -> ReminderStatus.UPCOMING
                        }

                        val gap = dueMileage - currentMileage
                        val dueLabel = when {
                            gap < 0 -> "${"%,d".format(-gap)} km overdue"
                            gap < 1000 -> "$gap km away"
                            else -> "${"%,d".format(gap)} km away"
                        }

                        list.add(
                            ServiceReminder(
                                id = it.id,
                                title = it.title,
                                description = it.notes.ifEmpty { "Regular maintenance service" },
                                dueMileage = dueMileage,
                                currentMileage = currentMileage,
                                dueDateLabel = dueLabel,
                                icon = getIconForCategory(it.category),
                                status = status
                            )
                        )
                    }
                }
                reminders = list.sortedBy { it.dueMileage }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    val overdueCount = reminders.count { it.status == ReminderStatus.OVERDUE }
    val dueSoonCount = reminders.count { it.status == ReminderStatus.DUE_SOON }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Service Reminders", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUT_ADDSERVICE) },
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
            item { CarInfoCard(car = carInfo) }

            // Alert Banner
            if (overdueCount > 0 || dueSoonCount > 0) {
                item {
                    AlertBanner(overdueCount = overdueCount, dueSoonCount = dueSoonCount)
                }
            }

            // Section Header
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

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentBlue)
                    }
                }
            } else if (reminders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No scheduled services yet", color = TextSecondary)
                    }
                }
            } else {
                items(reminders) { reminder ->
                    ReminderCard(reminder = reminder)
                }
            }
        }
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
// Helper function to map category to icon
private fun getIconForCategory(category: com.darrius.vehiclemaintenacetracker.ui.screens.AddService.ServiceCategory): ImageVector {
    return when (category) {
        com.darrius.vehiclemaintenacetracker.ui.screens.AddService.ServiceCategory.OIL_CHANGE -> Icons.Outlined.WaterDrop
        com.darrius.vehiclemaintenacetracker.ui.screens.AddService.ServiceCategory.BRAKES -> Icons.Outlined.DirectionsCar
        com.darrius.vehiclemaintenacetracker.ui.screens.AddService.ServiceCategory.TIRES -> Icons.Outlined.Sync
        else -> Icons.Outlined.Build
    }
}

// Keep all your existing composables unchanged below
@Composable
fun CarInfoCard(car: CarInfo) { /* ... your existing CarInfoCard ... */
    // (I kept it exactly as you provided - no changes)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(colors = listOf(Color(0xFF1E3A5F), Color(0xFF162B47))))
            .border(1.dp, Color(0xFF2A4A6F), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AccentBlue.copy(alpha = 0.2f))
                    .border(1.5.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(car.imageInitials, color = AccentBlue, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(car.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Outlined.DirectionsCar, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                    Text(car.plate, color = TextSecondary, fontSize = 13.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${"%,d".format(car.mileage)} km", color = AccentBlue, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text("Current mileage", color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun AlertBanner(overdueCount: Int, dueSoonCount: Int) { /* ... your existing code ... */
    // (Unchanged - keeping your beautiful design)
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
        Icon(Icons.Default.NotificationImportant, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(22.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Attention Required", color = AccentOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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

// ReminderCard, StatusChip, MileageInfo remain exactly the same as you provided
// (I didn't change them to preserve your UI 100%)

@Preview(showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
fun ServiceReminderScreenPreview() {
    ServiceReminderScreen(rememberNavController())
}