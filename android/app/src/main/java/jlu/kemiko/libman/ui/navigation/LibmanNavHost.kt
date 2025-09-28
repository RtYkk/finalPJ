package jlu.kemiko.libman.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jlu.kemiko.libman.ui.components.LibmanPrimaryButton
import jlu.kemiko.libman.ui.components.LibmanStatusBadge
import jlu.kemiko.libman.ui.components.LibmanStatusStyle
import jlu.kemiko.libman.ui.components.LibmanSurfaceCard
import jlu.kemiko.libman.ui.dashboard.DashboardRoute
import jlu.kemiko.libman.ui.theme.LibmanTheme

/**
 * Top-level navigation destinations for the app shell.
 */
enum class LibmanDestination(val route: String) {
    DASHBOARD("dashboard"),
    INVENTORY("inventory"),
    LOANS("loans"),
    SETTINGS("settings")
}

@Composable
fun LibmanNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LibmanDestination.DASHBOARD.route,
        modifier = modifier
    ) {
        dashboardGraph()
        inventoryGraph()
        loansGraph()
        settingsGraph()
    }
}

private fun NavGraphBuilder.dashboardGraph() {
    composable(LibmanDestination.DASHBOARD.route) {
        DashboardRoute(modifier = Modifier.fillMaxSize())
    }
}

private fun NavGraphBuilder.inventoryGraph() {
    composable(LibmanDestination.INVENTORY.route) {
        PlaceholderScreen(
            title = "Inventory",
            description = "Catalog browsing, intake, and stock adjustments are coming soon."
        )
    }
}

private fun NavGraphBuilder.loansGraph() {
    composable(LibmanDestination.LOANS.route) {
        PlaceholderScreen(
            title = "Loans",
            description = "Scan ISBN codes to issue or receive loans once the flow ships."
        )
    }
}

private fun NavGraphBuilder.settingsGraph() {
    composable(LibmanDestination.SETTINGS.route) {
        PlaceholderScreen(
            title = "Settings",
            description = "Configure kiosk preferences, theming, and device policies soon."
        )
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LibmanTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(LibmanTheme.spacing.large)
    ) {
        LibmanStatusBadge(
            text = "$title coming soon",
            style = LibmanStatusStyle.Info
        )

        LibmanSurfaceCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(LibmanTheme.spacing.small)
            ) {
                Text(
                    text = title,
                    style = LibmanTheme.typography.headlineSmall
                )
                Text(
                    text = description,
                    style = LibmanTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LibmanPrimaryButton(
                    text = "View design guidelines",
                    onClick = { /* TODO hook into docs */ }
                )
            }
        }
    }
}
