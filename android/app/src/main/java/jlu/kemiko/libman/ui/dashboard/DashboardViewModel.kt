package jlu.kemiko.libman.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import jlu.kemiko.libman.data.repository.InventoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = inventoryRepository
        .observeBooks()
        .map { books ->
            val totalTitles = books.size
            val totalCopies = books.sumOf { it.copyCount }
            val availableCopies = books.sumOf { it.availableCount }
            val checkedOutCopies = (totalCopies - availableCopies).coerceAtLeast(0)
            DashboardUiState(
                totalTitles = totalTitles,
                totalCopies = totalCopies,
                availableCopies = availableCopies,
                checkedOutCopies = checkedOutCopies,
                isEmpty = books.isEmpty()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = DashboardUiState()
        )

    companion object {
        fun factory(inventoryRepository: InventoryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                        "Unknown ViewModel class ${modelClass.name}"
                    }
                    return DashboardViewModel(inventoryRepository) as T
                }
            }
    }
}
