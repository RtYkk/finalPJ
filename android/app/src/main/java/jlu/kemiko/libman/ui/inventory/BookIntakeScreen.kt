package jlu.kemiko.libman.ui.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import jlu.kemiko.libman.LibmanApplication
import jlu.kemiko.libman.data.repository.InventoryRepository
import jlu.kemiko.libman.network.IsbnLookupService
import jlu.kemiko.libman.ui.components.LibmanPrimaryButton
import jlu.kemiko.libman.ui.components.LibmanSecondaryButton
import jlu.kemiko.libman.ui.components.LibmanSurfaceCard
import jlu.kemiko.libman.ui.theme.LibmanTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BookIntakeRoute(
    isbn13: String,
    onSaved: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookIntakeViewModel = viewModel(factory = rememberBookIntakeViewModelFactory(isbn13))
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is BookIntakeEvent.Saved -> {
                    onSaved(event.isbn13)
                    onBack()
                }
            }
        }
    }

    BookIntakeScreen(
        state = uiState,
        onTitleChange = viewModel::onTitleChange,
        onAuthorChange = viewModel::onAuthorChange,
        onCopyCountChange = viewModel::onCopyCountChange,
        onAvailableCountChange = viewModel::onAvailableCountChange,
        onFetchMetadata = viewModel::fetchMetadata,
        onSave = viewModel::save,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun rememberBookIntakeViewModelFactory(isbn13: String): ViewModelProvider.Factory {
    val context = LocalContext.current.applicationContext
    val app = context as? LibmanApplication
        ?: error("BookIntakeRoute must be hosted in LibmanApplication context")
    val repository = app.container.inventoryRepository
    val lookupService = app.container.isbnLookupService
    return remember(isbn13, repository, lookupService) {
        BookIntakeViewModelFactory(
            isbn13 = isbn13,
            inventoryRepository = repository,
            isbnLookupService = lookupService
        )
    }
}

private class BookIntakeViewModelFactory(
    private val isbn13: String,
    private val inventoryRepository: InventoryRepository,
    private val isbnLookupService: IsbnLookupService
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookIntakeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookIntakeViewModel(isbn13, inventoryRepository, isbnLookupService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}

@Composable
private fun BookIntakeScreen(
    state: BookIntakeUiState,
    onTitleChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onCopyCountChange: (String) -> Unit,
    onAvailableCountChange: (String) -> Unit,
    onFetchMetadata: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LibmanTheme.spacing
    val scrollState = rememberScrollState()
    val canSave = state.title.isNotBlank() &&
        state.copyCountInput.isNotBlank() &&
        state.availableCountInput.isNotBlank() &&
        !state.isSaving

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = spacing.large, vertical = spacing.large),
        verticalArrangement = Arrangement.spacedBy(spacing.large)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text(
                text = "图书录入",
                style = LibmanTheme.typography.headlineMedium
            )
            Text(
                text = "ISBN ${state.isbn13}",
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (state.isExistingBook) {
                Text(
                    text = "该 ISBN 已存在，将更新馆藏信息。",
                    style = LibmanTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        LibmanSurfaceCard(
            modifier = Modifier.fillMaxWidth(),
            tonal = false
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    LibmanSecondaryButton(
                        text = if (state.isFetchInProgress) "正在获取" else "从 Google Books 获取",
                        onClick = onFetchMetadata,
                        enabled = !state.isFetchInProgress,
                        modifier = Modifier.weight(1f)
                    )
                        if (state.isFetchInProgress) {
                            CircularProgressIndicator(modifier = Modifier.size(spacing.large))
                    }
                }
                if (state.fetchError != null) {
                    Text(
                        text = state.fetchError,
                        style = LibmanTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (state.infoMessage != null) {
                    Text(
                        text = state.infoMessage,
                        style = LibmanTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        LibmanSurfaceCard(
            modifier = Modifier.fillMaxWidth(),
            tonal = false
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    label = { Text("书名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.author,
                    onValueChange = onAuthorChange,
                    label = { Text("作者") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    OutlinedTextField(
                        value = state.copyCountInput,
                        onValueChange = onCopyCountChange,
                        label = { Text("馆藏册数") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = state.availableCountInput,
                        onValueChange = onAvailableCountChange,
                        label = { Text("可借册数") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (state.formError != null) {
                    Text(
                        text = state.formError,
                        style = LibmanTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
            modifier = Modifier.fillMaxWidth()
        ) {
            LibmanSecondaryButton(
                text = "返回",
                onClick = onBack,
                modifier = Modifier.weight(1f)
            )
            LibmanPrimaryButton(
                text = if (state.isSaving) "保存中" else "保存到馆藏",
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookIntakeScreenPreview() {
    LibmanTheme {
        BookIntakeScreen(
            state = BookIntakeUiState(
                isbn13 = "9781234567890",
                title = "Nineteen Eighty-Four",
                author = "George Orwell",
                copyCountInput = "3",
                availableCountInput = "2",
                infoMessage = "已从 Google Books 填充图书信息。"
            ),
            onTitleChange = {},
            onAuthorChange = {},
            onCopyCountChange = {},
            onAvailableCountChange = {},
            onFetchMetadata = {},
            onSave = {},
            onBack = {}
        )
    }
}
