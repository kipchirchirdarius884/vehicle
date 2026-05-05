package com.darrius.vehiclemaintenacetracker.ui.screens.VehicleProfile


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVehicleScreen(
    navController: NavController,
    vehicleId: String? = null
) {
    // In real app, fetch from ViewModel
    val originalVehicle = remember {
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

    // Form states
    var make by remember { mutableStateOf(originalVehicle.make) }
    var model by remember { mutableStateOf(originalVehicle.model) }
    var year by remember { mutableStateOf(originalVehicle.year.toString()) }
    var licensePlate by remember { mutableStateOf(originalVehicle.licensePlate) }
    var vin by remember { mutableStateOf(originalVehicle.vin) }
    var currentMileage by remember { mutableStateOf(originalVehicle.currentMileage.toString()) }
    var fuelType by remember { mutableStateOf(originalVehicle.fuelType) }
    var color by remember { mutableStateOf(originalVehicle.color) }

    val fuelTypes = listOf("Petrol", "Diesel", "Electric", "Hybrid", "LPG")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Vehicle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Update Vehicle Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            // Basic Info
            OutlinedTextField(
                value = make,
                onValueChange = { make = it },
                label = { Text("Make") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Model") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = year,
                onValueChange = { if (it.all { char -> char.isDigit() }) year = it },
                label = { Text("Year") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = licensePlate,
                onValueChange = { licensePlate = it.uppercase() },
                label = { Text("License Plate") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = vin,
                onValueChange = { vin = it.uppercase() },
                label = { Text("VIN Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = currentMileage,
                onValueChange = { if (it.all { char -> char.isDigit() }) currentMileage = it },
                label = { Text("Current Mileage (km)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Fuel Type Dropdown
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = fuelType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fuel Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    fuelTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                fuelType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    // TODO: Save to ViewModel / Repository
                    val updatedVehicle = originalVehicle.copy(
                        make = make,
                        model = model,
                        year = year.toIntOrNull() ?: originalVehicle.year,
                        licensePlate = licensePlate,
                        vin = vin,
                        currentMileage = currentMileage.toIntOrNull() ?: originalVehicle.currentMileage,
                        fuelType = fuelType,
                        color = color
                    )

                    // Navigate back to profile
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditVehicleScreenPreview() {
    MaterialTheme {
        EditVehicleScreen(rememberNavController())
    }
}