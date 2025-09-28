package jlu.kemiko.libman.ui.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import jlu.kemiko.libman.LibmanApplication
import jlu.kemiko.libman.ui.components.LibmanPrimaryButton
import jlu.kemiko.libman.ui.components.LibmanSecondaryButton
import jlu.kemiko.libman.ui.components.LibmanSurfaceCard
import jlu.kemiko.libman.ui.theme.LibmanTheme

@Composable
fun BookCatalogRoute(
    onBack: () -> Unit,
    onAddNew: () -> Unit,
    onEditBook: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookCatalogViewModel = viewModel(factory = rememberBookCatalogViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    BookCatalogScreen(
        state = uiState,
        onBack = onBack,
        onAddNew = onAddNew,
        onEditBook = onEditBook,
        modifier = modifier
    )
}

@Composable
private fun rememberBookCatalogViewModelFactory(): ViewModelProvider.Factory {
    val context = LocalContext.current.applicationContext
    val app = context as? LibmanApplication
        ?: error("BookCatalogRoute must be hosted in LibmanApplication context")
    val repository = app.container.inventoryRepository
    return remember(repository) {
        BookCatalogViewModelFactory(repository)
    }
}

private class BookCatalogViewModelFactory(
    private val repository: jlu.kemiko.libman.data.repository.InventoryRepository
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookCatalogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookCatalogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}

@Composable
private fun BookCatalogScreen(
    state: BookCatalogUiState,
    onBack: () -> Unit,
    onAddNew: () -> Unit,
    onEditBook: (String) -> Unit,
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
                text = "馆藏列表",
                style = LibmanTheme.typography.headlineMedium
            )
            Text(
                text = "浏览当前馆藏，并可返回继续录入新图书。",
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        RowActions(onBack = onBack, onAddNew = onAddNew)

        if (state.isLoading) {
            LoadingPlaceholder()
        } else if (state.books.isEmpty()) {
            EmptyPlaceholder(onAddNew = onAddNew)
        } else {
            BookList(state = state, onEditBook = onEditBook)
        }
    }
}

@Composable
private fun RowActions(
    onBack: () -> Unit,
    onAddNew: () -> Unit
) {
    val spacing = LibmanTheme.spacing
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        LibmanSecondaryButton(
            text = "返回",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
        LibmanPrimaryButton(
            text = "录入新图书",
            onClick = onAddNew,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LoadingPlaceholder() {
    LibmanSurfaceCard(tonal = true) {
        val spacing = LibmanTheme.spacing
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Text(
                text = "加载馆藏信息…",
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyPlaceholder(onAddNew: () -> Unit) {
    LibmanSurfaceCard(tonal = true) {
        val spacing = LibmanTheme.spacing
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Text(
                text = "暂无馆藏记录",
                style = LibmanTheme.typography.titleMedium
            )
            Text(
                text = "通过扫码或手动录入 ISBN，建立您的馆藏库。",
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LibmanPrimaryButton(
                text = "马上录入",
                onClick = onAddNew,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BookList(
    state: BookCatalogUiState,
    onEditBook: (String) -> Unit
) {
    val spacing = LibmanTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        contentPadding = PaddingValues(bottom = spacing.xLarge)
    ) {
        items(state.books, key = { it.isbn13 }) { item ->
            BookRow(item = item, onEditBook = onEditBook)
        }
    }
}

@Composable
private fun BookRow(
    item: BookCatalogItem,
    onEditBook: (String) -> Unit
) {
    val spacing = LibmanTheme.spacing
    LibmanSurfaceCard(tonal = false, onClick = { onEditBook(item.isbn13) }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Text(
                text = item.title,
                style = LibmanTheme.typography.titleMedium
            )
            Text(
                text = "作者：${item.authorLabel}",
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.large)
            ) {
                Column {
                    Text(
                        text = "馆藏册数",
                        style = LibmanTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.copyCount.toString(),
                        style = LibmanTheme.typography.titleMedium
                    )
                }
                Column {
                    Text(
                        text = "可借册数",
                        style = LibmanTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.availableCount.toString(),
                        style = LibmanTheme.typography.titleMedium
                    )
                }
            }
            Text(
                text = "ISBN：${item.isbn13}",
                style = LibmanTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookCatalogScreenEmptyPreview() {
    LibmanTheme {
        BookCatalogScreen(
            state = BookCatalogUiState(isLoading = false),
            onBack = {},
            onAddNew = {},
            onEditBook = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookCatalogScreenPreview() {
    LibmanTheme {
        BookCatalogScreen(
            state = BookCatalogUiState(
                isLoading = false,
                books = listOf(
                    BookCatalogItem(
                        isbn13 = "9781234567890",
                        title = "Nineteen Eighty-Four",
                        authorLabel = "George Orwell",
                        copyCount = 3,
                        availableCount = 1
                    ),
                    BookCatalogItem(
                        isbn13 = "9787020002207",
                        title = "百年孤独",
                        authorLabel = "加西亚·马尔克斯",
                        copyCount = 2,
                        availableCount = 2
                    )
                )
            ),
            onBack = {},
            onAddNew = {},
            onEditBook = {}
        )
    }
}
