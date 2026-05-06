package com.darrius.vehiclemaintenacetracker.ui.screens.AddService

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*

// ==================== DATA CLASSES ====================
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

enum class ServiceCategory(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OIL_CHANGE("Oil Change", Icons.Outlined.OilBarrel),
    BRAKES("Brakes", Icons.Outlined.DirectionsCar),
    TIRES("Tires", Icons.Outlined.TireRepair),
    ENGINE("Engine", Icons.Outlined.Engineering),
    BATTERY("Battery", Icons.Outlined.BatteryFull),
    OTHER("Other", Icons.Outlined.Build)
}

// ==================== MAIN SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceScreen(
    navController: NavController,
    onAddService: (ServiceRecord) -> Unit = {}   // Optional callback
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: "demo_user"

    // ── Form State ─────────────────────────────
    var title by remember { mutableStateOf("") }
    var costInput by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var shop by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var nextService by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ServiceCategory.OTHER) }
    var selectedDate by remember { mutableStateOf(getTodayDate()) }

    // ── Date Picker ───────────────────────
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            selectedDate = "${getMonth(month)} $day, $year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // ── Firebase Reference (User-specific) ─────────────────────────
    val database = FirebaseDatabase.getInstance()
    val servicesRef = database.getReference("users/$userId/services")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Service") })
        },
        bottomBar = {
            Button(
                onClick = {
                    if (title.isBlank() || costInput.isBlank()) {
                        Toast.makeText(context, "Title and Cost are required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val formattedCost = formatCurrency(costInput)

                    val newService = ServiceRecord(
                        id = System.currentTimeMillis().toString(),
                        title = title,
                        date = selectedDate,
                        mileage = mileage,
                        cost = formattedCost,
                        shop = shop,
                        category = selectedCategory,
                        notes = notes,
                        nextServiceMileage = nextService
                    )

                    // ✅ Save to Firebase Realtime Database
                    servicesRef.child(newService.id).setValue(newService)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Service saved successfully ✅", Toast.LENGTH_SHORT).show()
                            onAddService(newService)
                            navController.popBackStack()
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(context, "Failed to save: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = title.isNotBlank() && costInput.isNotBlank()
            ) {
                Text("Save Service")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Service Title *") },
                modifier = Modifier.fillMaxWidth()
            )

            // Cost
            OutlinedTextField(
                value = costInput,
                onValueChange = { costInput = it.filter { c -> c.isDigit() } },
                label = { Text("Cost (KSh) *") },
                leadingIcon = { Icon(Icons.Outlined.Payments, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Mileage
            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it.filter { c -> c.isDigit() } },
                label = { Text("Mileage (km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Date
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                label = { Text("Service Date") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                }
            )

            // Shop
            OutlinedTextField(
                value = shop,
                onValueChange = { shop = it },
                label = { Text("Service Shop / Garage") },
                leadingIcon = { Icon(Icons.Outlined.Store, null) },
                modifier = Modifier.fillMaxWidth()
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / Description") },
                leadingIcon = { Icon(Icons.Outlined.StickyNote2, null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Next Service Mileage
            OutlinedTextField(
                value = nextService,
                onValueChange = { nextService = it.filter { c -> c.isDigit() } },
                label = { Text("Next Service Mileage") },
                supportingText = { Text("💡 Tip: Oil change every 5,000 - 10,000 km") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Chips
            Text("Category", style = MaterialTheme.typography.titleMedium)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServiceCategory.values().forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.label) },
                        leadingIcon = {
                            Icon(category.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )
                }
            }
        }
    }
}

// ==================== HELPER FUNCTIONS ====================
fun formatCurrency(amount: String): String {
    return try {
        val number = amount.toLong()
        "KSh " + NumberFormat.getInstance().format(number)
    } catch (e: Exception) {
        amount
    }
}

fun getTodayDate(): String {
    val cal = Calendar.getInstance()
    return "${getMonth(cal.get(Calendar.MONTH))} ${cal.get(Calendar.DAY_OF_MONTH)}, ${cal.get(Calendar.YEAR)}"
}

fun getMonth(month: Int): String {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return months[month]
}

// ==================== PREVIEW ====================
@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun AddServiceScreenPreview() {
    MaterialTheme {
        AddServiceScreen(
            navController = rememberNavController()
        )
    }
}