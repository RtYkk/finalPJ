package jlu.kemiko.libman.ui.inventory

data class BookIntakeUiState(
    val isbn13: String,
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val coverImageUrl: String = "",
    val copyCountInput: String = "1",
    val availableCountInput: String = "1",
    val isFetchInProgress: Boolean = false,
    val fetchError: String? = null,
    val infoMessage: String? = null,
    val isSaving: Boolean = false,
    val formError: String? = null,
    val isExistingBook: Boolean = false,
    val metadataFetchedAt: Long? = null
)

sealed interface BookIntakeEvent {
    data class Saved(val isbn13: String) : BookIntakeEvent
}
