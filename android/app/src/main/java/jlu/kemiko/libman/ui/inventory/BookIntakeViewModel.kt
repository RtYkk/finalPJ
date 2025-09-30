package jlu.kemiko.libman.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jlu.kemiko.libman.data.model.Book
import jlu.kemiko.libman.data.repository.InventoryRepository
import jlu.kemiko.libman.network.IsbnLookupService
import java.time.Clock
import java.time.Instant
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookIntakeViewModel(
    private val isbn13: String,
    private val inventoryRepository: InventoryRepository,
    private val isbnLookupService: IsbnLookupService,
    private val clock: Clock = Clock.systemUTC()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookIntakeUiState(isbn13 = isbn13))
    val uiState: StateFlow<BookIntakeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BookIntakeEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<BookIntakeEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val existing = inventoryRepository.fetchBookByIsbn(isbn13)
            if (existing != null) {
                _uiState.update {
                    it.copy(
                        title = existing.title,
                        author = existing.author.orEmpty(),
                        copyCountInput = existing.copyCount.toString(),
                        availableCountInput = existing.availableCount.toString(),
                        isExistingBook = true,
                        infoMessage = "已加载本地馆藏数据，可继续编辑后保存。",
                        metadataFetchedAt = existing.metadataFetchedAt
                    )
                }
            } else {
                _uiState.update {
                    it.copy(infoMessage = "填写图书信息后保存到馆藏。")
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update {
            it.copy(title = value, formError = null)
        }
    }

    fun onAuthorChange(value: String) {
        _uiState.update {
            it.copy(author = value, formError = null)
        }
    }

    fun onCopyCountChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _uiState.update {
            it.copy(copyCountInput = digitsOnly, formError = null)
        }
    }

    fun onAvailableCountChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _uiState.update {
            it.copy(availableCountInput = digitsOnly, formError = null)
        }
    }

    fun fetchMetadata() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isFetchInProgress = true,
                    fetchError = null,
                    infoMessage = null
                )
            }
            val result = isbnLookupService.fetchMetadata(isbn13)
            result.onSuccess { metadata ->
                val now = Instant.now(clock).toEpochMilli()
                val title = metadata.title.orEmpty()
                val author = metadata.author.orEmpty()
                val message = if (title.isBlank() && author.isBlank()) {
                    "未从 Google Books 获取到字段，请手动填写。"
                } else {
                    "已从 Google Books 填充图书信息，可继续调整后保存。"
                }
                _uiState.update {
                    it.copy(
                        title = if (title.isBlank()) it.title else title,
                        author = if (author.isBlank()) it.author else author,
                        isFetchInProgress = false,
                        fetchError = null,
                        infoMessage = message,
                        metadataFetchedAt = now
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isFetchInProgress = false,
                        fetchError = error.message ?: "未能获取 Google Books 信息，请稍后再试。"
                    )
                }
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            val state = _uiState.value
            val title = state.title.trim()
            if (title.isBlank()) {
                _uiState.update { it.copy(formError = "书名不能为空") }
                return@launch
            }

            val copyCount = state.copyCountInput.toIntOrNull()
            if (copyCount == null || copyCount <= 0) {
                _uiState.update { it.copy(formError = "馆藏册数必须为正整数") }
                return@launch
            }

            val availableCount = state.availableCountInput.toIntOrNull()
            if (availableCount == null || availableCount < 0) {
                _uiState.update { it.copy(formError = "可借册数必须为非负整数") }
                return@launch
            }

            if (availableCount > copyCount) {
                _uiState.update { it.copy(formError = "可借册数不能大于馆藏册数") }
                return@launch
            }

            val author = state.author.trim().ifBlank { null }
            val metadataFetchedAt = state.metadataFetchedAt ?: Instant.now(clock).toEpochMilli()

            _uiState.update { it.copy(isSaving = true, formError = null) }

            val book = Book(
                isbn13 = state.isbn13,
                title = title,
                author = author,
                description = null,
                coverImageUrl = null,
                copyCount = copyCount,
                availableCount = availableCount,
                metadataFetchedAt = metadataFetchedAt
            )

            val result = runCatching {
                inventoryRepository.upsertBooks(book)
            }

            if (result.isSuccess) {
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(BookIntakeEvent.Saved(isbn13 = state.isbn13))
            } else {
                val message = result.exceptionOrNull()?.message ?: "保存失败，请稍后再试。"
                _uiState.update { it.copy(isSaving = false, formError = message) }
            }
        }
    }
}
