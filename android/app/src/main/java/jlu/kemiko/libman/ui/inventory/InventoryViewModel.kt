package jlu.kemiko.libman.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jlu.kemiko.libman.common.validation.Validators
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InventoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<InventoryEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<InventoryEvent> = _events.asSharedFlow()

    fun onIsbnChange(value: String) {
        _uiState.update { current ->
            current.copy(
                isbnInput = value.filterNot(Char::isWhitespace),
                errorMessage = null
            )
        }
    }

    fun applyScannedIsbn(isbn13: String) {
        _uiState.update { current ->
            current.copy(
                isbnInput = isbn13,
                errorMessage = null
            )
        }
    }

    fun submit() {
        val isbn = _uiState.value.isbnInput.trim()
        if (!Validators.isValidIsbn13(isbn)) {
            _uiState.update { current ->
                current.copy(errorMessage = "请输入有效的 13 位 ISBN 号")
            }
            return
        }

        _uiState.update { current ->
            current.copy(errorMessage = null)
        }
        viewModelScope.launch {
            _events.emit(InventoryEvent.NavigateToIntake(isbn))
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { current ->
            current.copy(recentlySavedIsbn = null)
        }
    }

    fun showBookSaved(isbn13: String) {
        _uiState.update { current ->
            current.copy(recentlySavedIsbn = isbn13, isbnInput = "")
        }
    }
}

sealed interface InventoryEvent {
    data class NavigateToIntake(val isbn13: String) : InventoryEvent
}
