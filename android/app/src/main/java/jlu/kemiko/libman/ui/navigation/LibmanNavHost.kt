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
import jlu.kemiko.libman.ui.inventory.InventoryRoute
import jlu.kemiko.libman.ui.loans.LoanScannerRoute
import jlu.kemiko.libman.ui.students.StudentsManagementRoute
import jlu.kemiko.libman.ui.theme.LibmanTheme

/**
 * Top-level navigation destinations for the app shell.
 */
enum class LibmanDestination(val route: String) {
    DASHBOARD("dashboard"),
    INVENTORY("inventory"),
    LOANS("loans"),
    STUDENTS("students"),
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
        dashboardGraph(navController)
        inventoryGraph(navController)
        loansGraph(navController)
        studentsGraph()
        settingsGraph()
    }
}

private fun NavGraphBuilder.dashboardGraph(navController: NavHostController) {
    composable(LibmanDestination.DASHBOARD.route) {
        DashboardRoute(
            onNavigateToBooks = {
                navController.navigate(LibmanDestination.INVENTORY.route)
            },
            onNavigateToStudents = {
                navController.navigate(LibmanDestination.STUDENTS.route)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.inventoryGraph(navController: NavHostController) {
    composable(LibmanDestination.INVENTORY.route) { backStackEntry ->
        InventoryRoute(
            savedStateHandle = backStackEntry.savedStateHandle,
            onScanRequested = {
                navController.navigate(LibmanDestination.LOANS.route)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.loansGraph(navController: NavHostController) {
    composable(LibmanDestination.LOANS.route) {
        LoanScannerRoute(
            onIsbnConfirmed = { isbn ->
                navController.previousBackStackEntry?.savedStateHandle?.set("scanned_isbn", isbn)
            },
            onClose = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.studentsGraph() {
    composable(LibmanDestination.STUDENTS.route) {
        StudentsManagementRoute(modifier = Modifier.fillMaxSize())
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
