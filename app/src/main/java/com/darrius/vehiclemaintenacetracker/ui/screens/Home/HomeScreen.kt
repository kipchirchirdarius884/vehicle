package com.darrius.vehiclemaintenacetracker.ui.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.darrius.vehiclemaintenacetracker.navigation.*
import kotlinx.coroutines.launch

// ── Drawer menu items ─────────────────────────────────────────────────────────
data class DrawerMenuItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }

    val menuItems = listOf(
        DrawerMenuItem(Icons.Default.Home, "Home", "home"),
        DrawerMenuItem(Icons.Default.DirectionsCar, "Vehicle Profile", ROUT_VEHICLEPROFILE),
        DrawerMenuItem(Icons.Default.Build, "Service Reminder", ROUT_SERVICEREMINDER),
        DrawerMenuItem(Icons.Default.List, "Service History Log", ROUT_SERVICEHISTORYLOG),
        DrawerMenuItem(Icons.Default.CarRepair, "Mechanic Finder", ROUT_MECHANICFINDER),
        DrawerMenuItem(Icons.Default.Notifications, "Notifications", ROUT_NOTIFICATIONS),
        DrawerMenuItem(Icons.Default.Settings, "Settings", ROUT_SETTINGS),
    )

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF1E2A35)
            ) {
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color(0xFF4FC3F7),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Vehicle Tracker",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "Maintenance made easy",
                        color = Color(0xFFB0BEC5),
                        fontSize = 13.sp
                    )
                }

                Divider(color = Color.White.copy(alpha = 0.1f))
                Spacer(Modifier.height(8.dp))

                // Drawer Items
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = Color(0xFF90A4AE)
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            selectedContainerColor = Color(0xFF4FC3F7).copy(alpha = 0.15f),
                            unselectedTextColor = Color.White,
                            unselectedIconColor = Color(0xFF90A4AE)
                        )
                    )
                }
            }
        }
    ) {
        // Main Content with same style as LoginScreen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBackground),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top App Bar
                TopAppBar(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    title = {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search...", color = Color(0xFF90A4AE)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.85f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4FC3F7),
                                unfocusedBorderColor = Color(0xFF37474F),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFF4FC3F7)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(ROUT_NOTIFICATIONS) }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                        IconButton(onClick = { navController.navigate(ROUT_SETTINGS) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1E2A35).copy(alpha = 0.95f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Vehicle Maintenance Tracker",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Keeping your vehicle in good condition helps improve safety, performance, and durability.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFB0BEC5)
                )

                // Why Maintenance is Important
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A35)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Icon(
                            imageVector = Icons.Default.CarRepair,
                            contentDescription = "Repair Icon",
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Why Car Maintenance is Important",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "• Prevents sudden breakdowns\n\n" +
                                    "• Improves fuel efficiency\n\n" +
                                    "• Increases vehicle lifespan\n\n" +
                                    "• Keeps the driver and passengers safe\n\n" +
                                    "• Helps avoid expensive repairs\n\n" +
                                    "• Maintains good engine performance",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                }

                // Important Reminder
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A35)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning Icon",
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Important Reminder",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Never miss scheduled maintenance such as:\n\n" +
                                    "• Oil changes\n" +
                                    "• Brake inspection\n" +
                                    "• Tire replacement\n" +
                                    "• Battery check\n" +
                                    "• Engine servicing\n" +
                                    "• Coolant replacement\n\n" +
                                    "Missing maintenance can lead to dangerous driving conditions and costly repairs.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                }

                // Quick Maintenance Tips
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A35)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Tools Icon",
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Quick Maintenance Tips",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "✔ Check tire pressure regularly\n\n" +
                                    "✔ Replace engine oil on time\n\n" +
                                    "✔ Keep the car clean\n\n" +
                                    "✔ Monitor dashboard warning lights\n\n" +
                                    "✔ Service your vehicle regularly",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}