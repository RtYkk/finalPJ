package jlu.kemiko.libman.ui.dashboard

/**
 * Immutable UI model describing high-level library statistics for the admin dashboard.
 */
data class DashboardUiState(
    val totalTitles: Int = 0,
    val totalCopies: Int = 0,
    val availableCopies: Int = 0,
    val checkedOutCopies: Int = 0,
    val isEmpty: Boolean = true
)
