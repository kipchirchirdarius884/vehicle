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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_ADDSERVICE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// ── Data Models ─────────────────────────────────────────────────────────────
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
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val mileage: String = "",
    val cost: String = "",
    val shop: String = "",
    val category: ServiceCategory = ServiceCategory.OTHER,
    val notes: String = "",
    val nextServiceMileage: String = ""
)

// ── Main Screen ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceHistoryLogScreen(navController: NavController) {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: "demo_user"

    var serviceHistory by remember { mutableStateOf<List<ServiceRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val vehicleName = "Toyota Corolla 2019"
    val vehiclePlate = "KCA 742X"

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    // Load past services from Firebase
    LaunchedEffect(userId) {
        val database = FirebaseDatabase.getInstance()
        val servicesRef = database.getReference("users/$userId/services")

        servicesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ServiceRecord>()
                for (child in snapshot.children) {
                    val record = child.getValue(ServiceRecord::class.java)
                    record?.let { list.add(it) }
                }
                // Sort by date (newest first)
                serviceHistory = list.sortedByDescending { it.date }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    Box(modifier = Modifier.fillMaxSize().background(brush = gradientBackground)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Service History",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "$vehicleName · $vehiclePlate",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB0BEC5)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Summary Card
                item {
                    ServiceSummaryCard(
                        recordCount = serviceHistory.size,
                        totalSpent = calculateTotalSpent(serviceHistory),
                        lastService = serviceHistory.firstOrNull()?.date ?: "—"
                    )
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF4FC3F7))
                        }
                    }
                } else if (serviceHistory.isEmpty()) {
                    item {
                        EmptyServiceHistoryState(
                            onAddClicked = { navController.navigate(ROUT_ADDSERVICE) }
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "All Records",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(serviceHistory) { record ->
                        ServiceRecordCard(record = record)
                    }
                }
            }
        }
    }
}

// Helper
private fun calculateTotalSpent(list: List<ServiceRecord>): String {
    if (list.isEmpty()) return "KSh 0"
    val total = list.sumOf {
        it.cost.replace("KSh ", "").replace(",", "").toLongOrNull() ?: 0L
    }
    return "KSh $total"
}

// Empty State
@Composable
fun EmptyServiceHistoryState(onAddClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.ReceiptLong,
            contentDescription = null,
            tint = Color(0xFF4FC3F7),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Service Records Yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add your first service record to start tracking",
            fontSize = 14.sp,
            color = Color(0xFFB0BEC5),
            textAlign = TextAlign.Center
        )
    }
}

// Summary Card
@Composable
fun ServiceSummaryCard(recordCount: Int, totalSpent: String, lastService: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A35))
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Maintenance Overview", color = Color(0xFFB0BEC5))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SummaryStatItem("Records", recordCount.toString(), Icons.Outlined.Receipt, Color(0xFF4ECDC4))
                    SummaryStatItem("Total Spent", totalSpent, Icons.Outlined.Payments, Color(0xFFFFD166))
                    SummaryStatItem("Last Service", lastService, Icons.Outlined.CalendarToday, Color(0xFF06D6A0))
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
                .background(tint.copy(0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color(0xFFB0BEC5), fontSize = 12.sp)
    }
}

// Service Record Card (You can expand this later)
@Composable
fun ServiceRecordCard(record: ServiceRecord) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(250))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A35))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Current compact view (you can improve later)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(record.category.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(record.category.icon, null, tint = record.category.color, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(record.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(record.date, color = Color(0xFFB0BEC5), fontSize = 13.sp)
                }

                Text(record.cost, color = Color(0xFF4FC3F7), fontWeight = FontWeight.Bold)
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF37474F))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Shop: ${record.shop}", color = Color.White)
                if (record.notes.isNotBlank()) Text("Notes: ${record.notes}", color = Color(0xFFB0BEC5))
            }
        }
    }
}

// Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ServiceHistoryLogScreenPreview() {
    MaterialTheme {
        ServiceHistoryLogScreen(navController = rememberNavController())
    }
}