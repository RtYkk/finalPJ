package jlu.kemiko.libman.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jlu.kemiko.libman.domain.usecase.BorrowBookUseCase
import jlu.kemiko.libman.domain.usecase.ReturnBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Orchestrates top-level UI state and triggers domain use cases.
 */
class AppViewModel(
    private val borrowBook: BorrowBookUseCase,
    private val returnBook: ReturnBookUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState

    fun borrow(isbn13: String, studentId: String) {
        viewModelScope.launch {
            borrowBook(isbn13, studentId)
            // TODO: update state with success/error feedback
        }
    }

    fun returnBook(loanId: Long) {
        viewModelScope.launch {
            returnBook(loanId)
            // TODO: update state with success/error feedback
        }
    }
}

data class AppUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
