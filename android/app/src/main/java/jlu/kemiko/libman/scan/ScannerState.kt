package jlu.kemiko.libman.scan

/**
 * Represents the state of the barcode scanning workflow.
 */
sealed interface ScannerState {
    data object Idle : ScannerState
    data object Scanning : ScannerState
    data class Success(val isbn13: String) : ScannerState
    data class Error(val message: String, val cause: Throwable? = null) : ScannerState
}
