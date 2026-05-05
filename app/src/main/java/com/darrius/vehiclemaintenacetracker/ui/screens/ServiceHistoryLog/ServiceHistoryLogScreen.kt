package com.darrius.vehiclemaintenacetracker.ui.screens.ServiceHistoryLog

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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

// ── Data models ──────────────────────────────────────────────────────────────

enum class ServiceCategory(val label: String, val icon: ImageVector, val color: Color) {
    OIL_CHANGE("Oil Change", Icons.Outlined.WaterDrop, Color(0xFFFF6B35)),
    TIRES("Tires", Icons.Outlined.Album, Color(0xFF4ECDC4)),
    BRAKES("Brakes", Icons.Outlined.Emergency, Color(0xFFE63946)),
    BATTERY("Battery", Icons.Outlined.ElectricBolt, Color(0xFFFFD166)),
    INSPECTION("Inspection", Icons.Outlined.FactCheck, Color(0xFF06D6A0)),
    ENGINE("Engine", Icons.Outlined.Settings, Color(0xFF118AB2)),
    OTHER("Other", Icons.Outlined.Build, Color(0xFF9B5DE5))
}

data class ServiceRecord(
    val id: String,
    val title: String,
    val date: String,
    val mileage: String,
    val cost: String,
    val shop: String,
    val category: ServiceCategory,
    val notes: String = "",
    val nextServiceMileage: String = ""
)

// ── Sample data ───────────────────────────────────────────────────────────────

val sampleServiceHistory = listOf(
    ServiceRecord(
        id = "1",
        title = "Full Synthetic Oil Change",
        date = "Apr 12, 2025",
        mileage = "54,320 mi",
        cost = "KSh 4,800",
        shop = "QuickLube Westlands",
        category = ServiceCategory.OIL_CHANGE,
        notes = "Used Mobil 1 5W-30. Air filter also replaced.",
        nextServiceMileage = "59,320 mi"
    ),
    ServiceRecord(
        id = "2",
        title = "Front Brake Pad Replacement",
        date = "Feb 28, 2025",
        mileage = "52,100 mi",
        cost = "KSh 9,500",
        shop = "AutoCare Kilimani",
        category = ServiceCategory.BRAKES,
        notes = "Replaced front pads and resurfaced rotors.",
        nextServiceMileage = "72,100 mi"
    ),
    ServiceRecord(
        id = "3",
        title = "Tire Rotation & Balancing",
        date = "Jan 15, 2025",
        mileage = "50,000 mi",
        cost = "KSh 2,200",
        shop = "MasterTyre Parklands",
        category = ServiceCategory.TIRES,
        notes = "All four tires rotated. Wheel alignment checked — within spec.",
        nextServiceMileage = "55,000 mi"
    ),
    ServiceRecord(
        id = "4",
        title = "Annual Vehicle Inspection",
        date = "Nov 5, 2024",
        mileage = "47,800 mi",
        cost = "KSh 1,500",
        shop = "NTSA Inspection Centre",
        category = ServiceCategory.INSPECTION,
        notes = "Passed all checks. Certificate valid until Nov 2025.",
        nextServiceMileage = "—"
    ),
    ServiceRecord(
        id = "5",
        title = "Battery Replacement",
        date = "Sep 20, 2024",
        mileage = "45,600 mi",
        cost = "KSh 12,000",
        shop = "AutoCare Kilimani",
        category = ServiceCategory.BATTERY,
        notes = "OEM battery replaced with Amaron 55Ah. 2-year warranty.",
        nextServiceMileage = "—"
    ),
    ServiceRecord(
        id = "6",
        title = "Engine Tune-Up",
        date = "Jul 3, 2024",
        mileage = "42,000 mi",
        cost = "KSh 8,750",
        shop = "ProMech Garage",
        category = ServiceCategory.ENGINE,
        notes = "Spark plugs, fuel filter, and PCV valve replaced.",
        nextServiceMileage = "62,000 mi"
    )
)

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceHistoryLogScreen(navController: NavController) {
    val totalSpent = "KSh 38,750"
    val vehicleName = "Toyota Corolla 2019"
    val vehiclePlate = "KCA 742X"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Service History",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "$vehicleName · $vehiclePlate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* add new service */ },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Service") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary card
            item {
                ServiceSummaryCard(
                    recordCount = sampleServiceHistory.size,
                    totalSpent = totalSpent,
                    lastService = sampleServiceHistory.first().date
                )
            }

            // Section header
            item {
                Text(
                    text = "All Records",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            // Service records
            items(sampleServiceHistory) { record ->
                ServiceRecordCard(record = record)
            }
        }
    }
}

// ── Summary card ──────────────────────────────────────────────────────────────

@Composable
fun ServiceSummaryCard(recordCount: Int, totalSpent: String, lastService: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Maintenance Overview",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryStatItem(
                        label = "Total Records",
                        value = "$recordCount",
                        icon = Icons.Outlined.Receipt,
                        tint = Color(0xFF4ECDC4)
                    )
                    SummaryStatItem(
                        label = "Total Spent",
                        value = totalSpent,
                        icon = Icons.Outlined.Payments,
                        tint = Color(0xFFFFD166)
                    )
                    SummaryStatItem(
                        label = "Last Service",
                        value = lastService,
                        icon = Icons.Outlined.CalendarToday,
                        tint = Color(0xFF06D6A0)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryStatItem(label: String, value: String, icon: ImageVector, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

// ── Service record card ───────────────────────────────────────────────────────

@Composable
fun ServiceRecordCard(record: ServiceRecord) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(250))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category icon badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(record.category.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = record.category.icon,
                        contentDescription = null,
                        tint = record.category.color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = record.date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "·",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            fontSize = 12.sp
                        )
                        Icon(
                            Icons.Outlined.Speed,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = record.mileage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Cost chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = record.cost,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Expanded details
            if (expanded) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                // Shop
                DetailRow(
                    icon = Icons.Outlined.Store,
                    label = "Shop",
                    value = record.shop
                )

                if (record.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        icon = Icons.Outlined.StickyNote2,
                        label = "Notes",
                        value = record.notes
                    )
                }

                if (record.nextServiceMileage.isNotBlank() && record.nextServiceMileage != "—") {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        icon = Icons.Outlined.Alarm,
                        label = "Next Service",
                        value = record.nextServiceMileage,
                        valueColor = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category chip
                AssistChip(
                    onClick = {},
                    label = { Text(record.category.label, style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = {
                        Icon(
                            record.category.icon,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = record.category.color
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = record.category.color.copy(alpha = 0.1f),
                        labelColor = record.category.color
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = record.category.color.copy(alpha = 0.3f),
                        enabled = true
                    )
                )
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .offset(y = 1.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = valueColor,
            modifier = Modifier.weight(1f)
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ServiceHistoryLogScreenPreview() {
    MaterialTheme {
        ServiceHistoryLogScreen(navController = rememberNavController())
    }
}