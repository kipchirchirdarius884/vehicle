package com.darrius.vehiclemaintenacetracker.ui.screens.VehicleProfile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaintenanceScreen(
    navController: NavController,
    vehicleId: String? = null
) {
    var serviceType by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    // Date formatting
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = remember(selectedDate) {
        dateFormatter.format(selectedDate.time)
    }

    // Common service types
    val commonServices = listOf(
        "Oil Change", "Brake Pad Replacement", "Tire Rotation",
        "Wheel Alignment", "Air Filter Replacement", "Battery Replacement",
        "Spark Plugs", "Coolant Flush", "Transmission Service", "Full Service"
    )

    var showDatePicker by remember { mutableStateOf(false) }
    var showServiceDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Maintenance") },
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
                text = "New Maintenance Record",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            // Date
            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Service Date") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Service Type with Dropdown
            ExposedDropdownMenuBox(
                expanded = showServiceDropdown,
                onExpandedChange = { showServiceDropdown = it }
            ) {
                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = { Text("Service Type *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showServiceDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = showServiceDropdown,
                    onDismissRequest = { showServiceDropdown = false }
                ) {
                    commonServices.forEach { service ->
                        DropdownMenuItem(
                            text = { Text(service) },
                            onClick = {
                                serviceType = service
                                showServiceDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Mileage
            OutlinedTextField(
                value = mileage,
                onValueChange = { if (it.all { char -> char.isDigit() }) mileage = it },
                label = { Text("Mileage at Service (km) *") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Cost
            OutlinedTextField(
                value = cost,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*\$"))) cost = it },
                label = { Text("Cost (KSh) *") },
                prefix = { Text("KSh ") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (serviceType.isNotBlank() && mileage.isNotBlank() && cost.isNotBlank()) {
                        // TODO: Save to ViewModel / Repository
                        val newRecord = MaintenanceRecord(
                            id = UUID.randomUUID().toString(),
                            date = formattedDate,
                            serviceType = serviceType,
                            mileage = mileage.toIntOrNull() ?: 0,
                            cost = cost.toDoubleOrNull() ?: 0.0,
                            notes = notes
                        )

                        // You can pass the new record back or use shared ViewModel
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = serviceType.isNotBlank() && mileage.isNotBlank() && cost.isNotBlank()
            ) {
                Text("Save Maintenance Record")
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

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.timeInMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate.timeInMillis = millis
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddMaintenanceScreenPreview() {
    MaterialTheme {
        AddMaintenanceScreen(rememberNavController())
    }
}

