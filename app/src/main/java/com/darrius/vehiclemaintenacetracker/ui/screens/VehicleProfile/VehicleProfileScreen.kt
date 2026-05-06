package com.darrius.vehiclemaintenacetracker.ui.screens.VehicleProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

/* ==================== DATA CLASSES ==================== */
data class Vehicle(
    val id: String = "",
    val make: String = "",
    val model: String = "",
    val year: Int = 0,
    val licensePlate: String = "",
    val vin: String = "",
    val currentMileage: Int = 0,
    val fuelType: String = "",
    val color: String = ""
)

data class MaintenanceRecord(
    val id: String = "",
    val date: String = "",
    val serviceType: String = "",
    val mileage: Int = 0,
    val cost: Double = 0.0,
    val notes: String = ""
)

/* ==================== MAIN SCREEN ==================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleProfileScreen(
    navController: NavController,
    vehicleId: String? = null
) {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: "demo_user"

    var vehicle by remember { mutableStateOf<Vehicle?>(null) }
    var recentMaintenance by remember { mutableStateOf<List<MaintenanceRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }

    val database = FirebaseDatabase.getInstance()
    val vehicleRef = database.getReference("users/$userId/vehicle")
    val maintenanceRef = database.getReference("users/$userId/maintenance")

    /* ===== LOAD DATA ===== */
    LaunchedEffect(userId) {
        vehicleRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                vehicle = snapshot.getValue(Vehicle::class.java)
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    LaunchedEffect(userId) {
        maintenanceRef.orderByChild("date").limitToLast(5)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<MaintenanceRecord>()
                    for (child in snapshot.children) {
                        child.getValue(MaintenanceRecord::class.java)?.let { list.add(it) }
                    }
                    recentMaintenance = list.reversed()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    /* ===== UI ===== */
    val gradientBackground = Brush.verticalGradient(
        listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Vehicle Profile", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, null, tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                containerColor = Color(0xFF4FC3F7)
            ) {
                Icon(Icons.Default.Add, null, tint = Color(0xFF0F2027))
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(padding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                /* ===== LOADING ===== */
                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF4FC3F7))
                        }
                    }
                }

                /* ===== EMPTY ===== */
                else if (vehicle == null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A35))
                        ) {
                            Column(
                                Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No Vehicle Added Yet", color = Color.White)
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = { showEditDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
                                ) {
                                    Text("Add Vehicle", color = Color(0xFF0F2027))
                                }
                            }
                        }
                    }
                }

                /* ===== CONTENT ===== */
                else {

                    val v = vehicle!!

                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A35))
                        ) {
                            Column(
                                Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.DirectionsCar,
                                    null,
                                    tint = Color(0xFF4FC3F7),
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text("${v.year} ${v.make} ${v.model}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold)
                                Text(v.licensePlate, color = Color(0xFFB0BEC5))
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A35))
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text("Vehicle Info", color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(12.dp))

                                InfoRow("Make", v.make)
                                InfoRow("Model", v.model)
                                InfoRow("Year", v.year.toString())
                                InfoRow("Plate", v.licensePlate)
                                InfoRow("Mileage", "${v.currentMileage} km")
                                InfoRow("Fuel", v.fuelType)
                                InfoRow("Color", v.color)
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Total Spent", "KSh 98,400", Icons.Default.AttachMoney, Modifier.weight(1f))
                            StatCard("Last Service",
                                recentMaintenance.firstOrNull()?.date ?: "N/A",
                                Icons.Default.CalendarToday,
                                Modifier.weight(1f))
                        }
                    }

                    item {
                        Text("Recent Maintenance", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    items(recentMaintenance) {
                        MaintenanceItem(it)
                    }
                }
            }
        }
    }

    /* ===== EDIT DIALOG (UNCHANGED LOGIC) ===== */
    if (showEditDialog) {
        val current = vehicle ?: Vehicle()

        var make by remember { mutableStateOf(current.make) }
        var model by remember { mutableStateOf(current.model) }
        var year by remember { mutableStateOf(current.year.toString()) }
        var plate by remember { mutableStateOf(current.licensePlate) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                Button(onClick = {
                    val newVehicle = Vehicle(
                        id = current.id.ifEmpty { UUID.randomUUID().toString() },
                        make = make,
                        model = model,
                        year = year.toIntOrNull() ?: 2024,
                        licensePlate = plate
                    )
                    vehicleRef.setValue(newVehicle)
                    showEditDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Edit Vehicle") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(make, { make = it }, label = { Text("Make") })
                    OutlinedTextField(model, { model = it }, label = { Text("Model") })
                    OutlinedTextField(year, { year = it }, label = { Text("Year") })
                    OutlinedTextField(plate, { plate = it }, label = { Text("Plate") })
                }
            }
        )
    }
}

/* ==================== HELPERS ==================== */

@Composable
fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF90A4AE))
        Text(value, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A35))
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = Color(0xFF4FC3F7))
            Spacer(Modifier.height(8.dp))
            Text(title, color = Color(0xFFB0BEC5))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MaintenanceItem(record: MaintenanceRecord) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A35))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(record.serviceType, color = Color.White)
                Text("KSh ${record.cost}", color = Color(0xFF4FC3F7))
            }
            Spacer(Modifier.height(4.dp))
            Text("${record.date} • ${record.mileage} km", color = Color(0xFFB0BEC5))
            if (record.notes.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(record.notes, color = Color(0xFFB0BEC5))
            }
        }
    }
}