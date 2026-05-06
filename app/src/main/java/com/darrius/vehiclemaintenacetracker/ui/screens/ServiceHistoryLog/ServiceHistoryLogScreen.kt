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
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_ADDSERVICE

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

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceHistoryLogScreen(navController: NavController) {

    // Make the list mutable so we can add new services
    val serviceHistory = remember { mutableStateListOf<ServiceRecord>().apply { addAll(sampleServiceHistory) } }

    val totalSpent = remember(serviceHistory) {
        "KSh " + serviceHistory.sumOf {
            it.cost.replace("KSh ", "").replace(",", "").toLongOrNull() ?: 0L
        }
    }

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
                onClick = {
                    navController.navigate(ROUT_ADDSERVICE)
                },
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
                    recordCount = serviceHistory.size,
                    totalSpent = totalSpent,
                    lastService = serviceHistory.firstOrNull()?.date ?: "—"
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
            items(serviceHistory) { record ->
                ServiceRecordCard(record = record)
            }
        }
    }
}

// ── Summary card & other composables remain unchanged ───────────────────────

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            if (expanded) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                DetailRow(icon = Icons.Outlined.Store, label = "Shop", value = record.shop)

                if (record.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(icon = Icons.Outlined.StickyNote2, label = "Notes", value = record.notes)
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

// ── Sample Data (kept for preview) ───────────────────────────────────────────

val sampleServiceHistory = listOf(
    // ... (your original sample data remains the same)
    ServiceRecord(
        id = "1", title = "Full Synthetic Oil Change", date = "Apr 12, 2025",
        mileage = "54,320 mi", cost = "KSh 4,800", shop = "QuickLube Westlands",
        category = ServiceCategory.OIL_CHANGE, notes = "Used Mobil 1 5W-30. Air filter also replaced.",
        nextServiceMileage = "59,320 mi"
    ),
    // ... add the rest of your sample records here
)

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ServiceHistoryLogScreenPreview() {
    MaterialTheme {
        ServiceHistoryLogScreen(navController = rememberNavController())
    }
}