package com.darrius.vehiclemaintenacetracker.ui.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_MECHANICFINDER
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_NOTIFICATIONS
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_SERVICEHISTORYLOG
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_SERVICEREMINDER
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_SETTINGS
import com.darrius.vehiclemaintenacetracker.navigation.ROUT_VEHICLEPROFILE
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
        DrawerMenuItem(Icons.Default.Home,          "Home",                 "home"),
        DrawerMenuItem(Icons.Default.DirectionsCar, "Vehicle Profile",      ROUT_VEHICLEPROFILE),
        DrawerMenuItem(Icons.Default.Build,         "Service Reminder",     ROUT_SERVICEREMINDER),
        DrawerMenuItem(Icons.Default.List, "Service History Log", ROUT_SERVICEHISTORYLOG),
        DrawerMenuItem(Icons.Default.CarRepair,     "Mechanic Finder",      ROUT_MECHANICFINDER),
        DrawerMenuItem(Icons.Default.Notifications, "Notifications",        ROUT_NOTIFICATIONS),
        DrawerMenuItem(Icons.Default.Settings,      "Settings",             ROUT_SETTINGS),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF0D47A1)
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
                        tint = Color.White,
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
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }

                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(Modifier.height(8.dp))

                // Drawer Items
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = Color.White
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
                            selectedContainerColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) {
        // ── Main screen content ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                title = {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(ROUT_NOTIFICATIONS) }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { navController.navigate(ROUT_SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )

            Text(
                text = "Vehicle Maintenance Tracker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )

            Text(
                text = "Keeping your vehicle in good condition helps improve safety, performance, and durability.",
                style = MaterialTheme.typography.bodyLarge
            )

            // Why Maintenance is Important
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.CarRepair,
                        contentDescription = "Repair Icon",
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.height(40.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Why Car Maintenance is Important",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "• Prevents sudden breakdowns\n\n" +
                                "• Improves fuel efficiency\n\n" +
                                "• Increases vehicle lifespan\n\n" +
                                "• Keeps the driver and passengers safe\n\n" +
                                "• Helps avoid expensive repairs\n\n" +
                                "• Maintains good engine performance",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Reminder Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning Icon",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.height(40.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Important Reminder",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Never miss scheduled maintenance such as:\n\n" +
                                "• Oil changes\n" +
                                "• Brake inspection\n" +
                                "• Tire replacement\n" +
                                "• Battery check\n" +
                                "• Engine servicing\n" +
                                "• Coolant replacement\n\n" +
                                "Missing maintenance can lead to dangerous driving conditions and costly repairs.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Maintenance Tips
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Tools Icon",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.height(40.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Quick Maintenance Tips",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "✔ Check tire pressure regularly\n\n" +
                                "✔ Replace engine oil on time\n\n" +
                                "✔ Keep the car clean\n\n" +
                                "✔ Monitor dashboard warning lights\n\n" +
                                "✔ Service your vehicle regularly",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}