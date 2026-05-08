package com.darrius.vehiclemaintenacetracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darrius.vehiclemaintenacetracker.ui.screens.AddService.AddServiceScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.Home.HomeScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.MechanicFinder.MechanicFinderScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.Notifications.NotificationScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.Onboarding.OnboardingScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.ServiceHistoryLog.ServiceHistoryLogScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.ServiceReminder.ServiceReminderScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.Settings.SettingsScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.VehicleProfile.AddMaintenanceScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.VehicleProfile.EditVehicleScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.VehicleProfile.VehicleProfileScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.auth.LoginScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.auth.RegisterScreen
import com.darrius.vehiclemaintenacetracker.ui.screens.splash.SplashScreen


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_SPLASH   // This is fine
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    )
{



        composable(ROUT_ONBOARDING) {
            OnboardingScreen(navController = navController)
        }

        composable(ROUT_HOME) {
            HomeScreen(navController = navController)
        }

        composable(ROUT_REGISTER) {
            RegisterScreen(navController = navController)
        }

        composable(ROUT_LOGIN) {
            LoginScreen(navController = navController)
        }

        composable(ROUT_SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(ROUT_MECHANICFINDER) {
            MechanicFinderScreen(navController = navController)
        }

        composable(ROUT_SERVICEREMINDER) {
            ServiceReminderScreen(navController = navController)
        }

        composable(ROUT_SERVICEHISTORYLOG) {
            ServiceHistoryLogScreen(navController = navController)
        }

        composable(ROUT_VEHICLEPROFILE) {
            VehicleProfileScreen(navController = navController)
        }

        composable(ROUT_SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(ROUT_EDITVEHICLEPROFILE) {
        EditVehicleScreen(navController = navController)
        }
         composable(ROUT_ADDMAINTENANCE) {
        AddMaintenanceScreen(navController = navController)
         }
          composable(ROUT_ADDSERVICE) {
        AddServiceScreen(navController = navController) {}
          }
    }
}