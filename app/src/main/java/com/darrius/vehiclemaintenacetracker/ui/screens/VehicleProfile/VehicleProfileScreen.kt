package com.darrius.vehiclemaintenacetracker.ui.screens.VehicleProfile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_ADDMAINTENANCESCREEN
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_EDITVEHICLEPROFILESCREEN

data class Vehicle(
    val id: String,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val vin: String,
    val currentMileage: Int,
    val fuelType: String,
    val color: String
)

data class MaintenanceRecord(
    val id: String,
    val date: String,
    val serviceType: String,
    val mileage: Int,
    val cost: Double,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleProfileScreen(
    navController: NavController,
    vehicleId: String? = null
) {
    val vehicle = remember {
        Vehicle(
            id = vehicleId ?: "1",
            make = "Toyota",
            model = "Camry",
            year = 2022,
            licensePlate = "KBC 123A",
            vin = "1HGBH41JXMN109186",
            currentMileage = 45280,
            fuelType = "Petrol",
            color = "Silver Metallic"
        )
    }

    val recentMaintenance = remember {
        listOf(
            MaintenanceRecord("1", "2026-04-15", "Oil Change & Filter", 44800, 8500.0, "Synthetic oil used"),
            MaintenanceRecord("2", "2026-02-20", "Brake Pad Replacement", 39200, 24500.0, "Front pads only"),
            MaintenanceRecord("3", "2025-12-05", "Tire Rotation & Alignment", 35100, 6500.0, "")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehicle Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(ROUT_EDITVEHICLEPROFILESCREEN) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(ROUT_ADDMAINTENANCESCREEN)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Maintenance")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vehicle Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = null,
                            modifier = Modifier.size(88.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "${vehicle.year} ${vehicle.make} ${vehicle.model}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = vehicle.licensePlate,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            // Vehicle Information
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Vehicle Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))

                        InfoRow("Make & Model", "${vehicle.make} ${vehicle.model}")
                        InfoRow("Year", vehicle.year.toString())
                        InfoRow("License Plate", vehicle.licensePlate)
                        InfoRow("VIN", vehicle.vin)
                        InfoRow("Current Mileage", "${vehicle.currentMileage} km")
                        InfoRow("Fuel Type", vehicle.fuelType)
                        InfoRow("Color", vehicle.color)
                    }
                }
            }

            // Quick Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Spent",
                        value = "KSh 98,400",
                        icon = Icons.Default.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Last Service",
                        value = "15 Apr 2026",
                        icon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent Maintenance
            item {
                Text(
                    text = "Recent Maintenance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(recentMaintenance) { record ->
                MaintenanceItem(record)
            }

            item {
                Button(
                    onClick = { navController.navigate("maintenance_history/${vehicle.id}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Full Maintenance History")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MaintenanceItem(record: MaintenanceRecord) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(record.serviceType, fontWeight = FontWeight.Medium)
                Text("KSh ${record.cost}", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(4.dp))
            Text("${record.date} • ${record.mileage} km", style = MaterialTheme.typography.bodySmall)
            if (record.notes.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(record.notes, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VehicleProfileScreenPreview() {
    MaterialTheme {
        VehicleProfileScreen(rememberNavController())
    }
}