package jlu.kemiko.libman.ui.inventory

import androidx.lifecycle.ViewModel
import jlu.kemiko.libman.common.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InventoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

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
            current.copy(
                isbnInput = "",
                errorMessage = null,
                lastAddedIsbn = isbn
            )
        }
        // TODO: Wire into repository to persist book metadata.
    }

    fun clearSuccessMessage() {
        _uiState.update { current ->
            current.copy(lastAddedIsbn = null)
        }
    }
}
