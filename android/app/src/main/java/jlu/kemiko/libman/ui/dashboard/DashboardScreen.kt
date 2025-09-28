package jlu.kemiko.libman.ui.dashboard

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import jlu.kemiko.libman.LibmanApplication
import jlu.kemiko.libman.ui.components.LibmanPrimaryButton
import jlu.kemiko.libman.ui.components.LibmanSurfaceCard
import jlu.kemiko.libman.ui.theme.LibmanTheme

@Composable
fun DashboardRoute(
    onNavigateToBooks: () -> Unit,
    onNavigateToStudents: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = rememberDashboardViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    DashboardScreen(
        state = uiState,
        onNavigateToBooks = onNavigateToBooks,
        onNavigateToStudents = onNavigateToStudents,
        modifier = modifier
    )
}

@Composable
private fun rememberDashboardViewModelFactory(): androidx.lifecycle.ViewModelProvider.Factory {
    val context = LocalContext.current.applicationContext
    val app = context as? LibmanApplication
        ?: error("DashboardRoute must be hosted in LibmanApplication context")
    val repository = app.container.inventoryRepository
    return remember(repository) {
        DashboardViewModel.factory(repository)
    }
}

@Composable
private fun DashboardScreen(
    state: DashboardUiState,
    onNavigateToBooks: () -> Unit,
    onNavigateToStudents: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LibmanTheme.spacing
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.large, vertical = spacing.large),
        verticalArrangement = Arrangement.spacedBy(spacing.large)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text(
                text = "Library overview",
                style = LibmanTheme.typography.headlineMedium
            )
            Text(
                text = if (state.isEmpty) {
                    "Catalog is empty. Add books to see circulation metrics."
                } else {
                    "Track inventory and circulation performance at a glance."
                },
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            ManagementOptionCard(
                title = "图书管理",
                description = "浏览、录入和调整馆藏书目。",
                onClick = onNavigateToBooks,
                modifier = Modifier.weight(1f)
            )
            ManagementOptionCard(
                title = "学生管理",
                description = "维护学生信息与借阅资格。",
                onClick = onNavigateToStudents,
                modifier = Modifier.weight(1f)
            )
        }

        if (state.isEmpty) {
            LibmanSurfaceCard(
                modifier = Modifier.fillMaxWidth(),
                tonal = true
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Text(
                        text = "No cataloged titles yet",
                        style = LibmanTheme.typography.titleMedium
                    )
                    Text(
                        text = "Scan a barcode or manually add a book to populate your dashboard metrics.",
                        style = LibmanTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                DashboardMetricCard(
                    label = "Total titles",
                    value = state.totalTitles.toString(),
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricCard(
                    label = "Total copies",
                    value = state.totalCopies.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                DashboardMetricCard(
                    label = "Available",
                    value = state.availableCopies.toString(),
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricCard(
                    label = "Checked out",
                    value = state.checkedOutCopies.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ManagementOptionCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LibmanTheme.spacing
    LibmanSurfaceCard(
        modifier = modifier,
        tonal = true
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Text(
                text = title,
                style = LibmanTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LibmanPrimaryButton(
                text = "进入",
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DashboardMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    LibmanSurfaceCard(
        modifier = modifier,
        tonal = true
    ) {
        val spacing = LibmanTheme.spacing
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = LibmanTheme.typography.headlineMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    LibmanTheme {
        DashboardScreen(
            state = DashboardUiState(
                totalTitles = 12,
                totalCopies = 48,
                availableCopies = 35,
                checkedOutCopies = 13,
                isEmpty = false
            ),
            onNavigateToBooks = {},
            onNavigateToStudents = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenEmptyPreview() {
    LibmanTheme {
        DashboardScreen(
            state = DashboardUiState(),
            onNavigateToBooks = {},
            onNavigateToStudents = {}
        )
    }
}
