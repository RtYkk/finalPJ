package jlu.kemiko.libman.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jlu.kemiko.libman.data.repository.InventoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BookCatalogViewModel(
    inventoryRepository: InventoryRepository
) : ViewModel() {

    val uiState: StateFlow<BookCatalogUiState> = inventoryRepository
        .observeBooks()
        .map { books ->
            BookCatalogUiState(
                isLoading = false,
                books = books.map { book ->
                    BookCatalogItem(
                        isbn13 = book.isbn13,
                        title = book.title,
                        authorLabel = book.author ?: "未知作者",
                        copyCount = book.copyCount,
                        availableCount = book.availableCount
                    )
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BookCatalogUiState()
        )
}
