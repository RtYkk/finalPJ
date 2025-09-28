package jlu.kemiko.libman.ui.inventory

data class BookCatalogItem(
    val isbn13: String,
    val title: String,
    val authorLabel: String,
    val copyCount: Int,
    val availableCount: Int
)

data class BookCatalogUiState(
    val isLoading: Boolean = true,
    val books: List<BookCatalogItem> = emptyList()
)
