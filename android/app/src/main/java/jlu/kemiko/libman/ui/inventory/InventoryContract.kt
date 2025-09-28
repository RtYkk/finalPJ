package jlu.kemiko.libman.ui.inventory

/**
 * UI state for the inventory management screen.
 */
data class InventoryUiState(
    val isbnInput: String = "",
    val errorMessage: String? = null,
    val recentlySavedIsbn: String? = null
)
