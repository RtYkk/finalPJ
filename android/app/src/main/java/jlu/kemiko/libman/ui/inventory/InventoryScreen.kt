package jlu.kemiko.libman.ui.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import jlu.kemiko.libman.ui.components.LibmanPrimaryButton
import jlu.kemiko.libman.ui.components.LibmanSecondaryButton
import jlu.kemiko.libman.ui.components.LibmanSurfaceCard
import jlu.kemiko.libman.ui.theme.LibmanTheme

private const val SCAN_RESULT_KEY = "scanned_isbn"

@Composable
fun InventoryRoute(
    savedStateHandle: SavedStateHandle,
    onScanRequested: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scannedIsbnFlow = savedStateHandle.getStateFlow<String?>(SCAN_RESULT_KEY, null)
    val scannedIsbn by scannedIsbnFlow.collectAsState()

    LaunchedEffect(scannedIsbn) {
        scannedIsbn?.let { isbn ->
            viewModel.applyScannedIsbn(isbn)
            savedStateHandle[SCAN_RESULT_KEY] = null
        }
    }

    InventoryScreen(
        state = uiState,
        onIsbnChange = viewModel::onIsbnChange,
        onSubmit = viewModel::submit,
        onScanRequested = onScanRequested,
        onDismissSuccess = viewModel::clearSuccessMessage,
        modifier = modifier
    )
}

@Composable
private fun InventoryScreen(
    state: InventoryUiState,
    onIsbnChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onScanRequested: () -> Unit,
    onDismissSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LibmanTheme.spacing
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.large, vertical = spacing.large),
        verticalArrangement = Arrangement.spacedBy(spacing.large)
    ) {
        Text(
            text = "图书管理",
            style = LibmanTheme.typography.headlineMedium
        )
        Text(
            text = "通过输入或扫码 ISBN 新增馆藏图书。",
            style = LibmanTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LibmanSurfaceCard(
            modifier = Modifier.fillMaxWidth(),
            tonal = false
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                OutlinedTextField(
                    value = state.isbnInput,
                    onValueChange = onIsbnChange,
                    label = { Text("ISBN-13") },
                    placeholder = { Text("例如 9787111122334") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.MenuBook, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = onScanRequested) {
                            Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "扫码录入")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "仅支持 13 位标准 ISBN 号。",
                    style = LibmanTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                state.errorMessage?.let { error ->
                    Text(
                        text = error,
                        style = LibmanTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                LibmanPrimaryButton(
                    text = "添加图书",
                    onClick = {
                        keyboardController?.hide()
                        onSubmit()
                    },
                    enabled = state.isbnInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        state.lastAddedIsbn?.let { isbn ->
            LibmanSurfaceCard(
                modifier = Modifier.fillMaxWidth(),
                tonal = true
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Text(
                        text = "已添加",
                        style = LibmanTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ISBN $isbn 已加入馆藏。本地详情配置即将接入。",
                        style = LibmanTheme.typography.bodyMedium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        LibmanPrimaryButton(
                            text = "继续录入",
                            onClick = onDismissSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        LibmanSecondaryButton(
                            text = "扫码录入",
                            onClick = onScanRequested,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InventoryScreenPreview() {
    LibmanTheme {
        InventoryScreen(
            state = InventoryUiState(isbnInput = "9781234567890"),
            onIsbnChange = {},
            onSubmit = {},
            onScanRequested = {},
            onDismissSuccess = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InventoryScreenSuccessPreview() {
    LibmanTheme {
        InventoryScreen(
            state = InventoryUiState(lastAddedIsbn = "9781234567890"),
            onIsbnChange = {},
            onSubmit = {},
            onScanRequested = {},
            onDismissSuccess = {}
        )
    }
}
