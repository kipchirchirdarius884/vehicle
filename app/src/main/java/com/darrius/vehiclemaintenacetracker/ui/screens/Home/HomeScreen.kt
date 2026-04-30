package com.darrius.vehiclemaintenacetracker.ui.screens.Home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // TopAppBar

        var searchText by remember { mutableStateOf("") }

        TopAppBar(

            title = {

                OutlinedTextField(
                    value = searchText,

                    onValueChange = {
                        searchText = it
                    },

                    placeholder = {
                        Text(text = "Search...")
                    },

                    singleLine = true,

                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            },

            actions = {

                // Notification Icon
                IconButton(
                    onClick = {

                        // Notification click action

                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }
        )

        //end of TopAppBar

        // Title Section
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

        // Car Maintenance Image
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {

            AsyncImage(
                model = "https://images.unsplash.com/photo-1486006920555-c77dcf18193c",
                contentDescription = "Car Maintenance",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Why Maintenance is Important
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.CarRepair,
                    contentDescription = "Repair Icon",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.height(40.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Why Car Maintenance is Important",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

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
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0)
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning Icon",
                    tint = Color(0xFFE65100),
                    modifier = Modifier.height(40.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Important Reminder",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

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
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Tools Icon",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.height(40.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Quick Maintenance Tips",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

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

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}