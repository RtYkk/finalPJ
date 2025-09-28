package jlu.kemiko.libman.ui.loans

import androidx.lifecycle.ViewModel
import jlu.kemiko.libman.scan.ScannerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoanScannerViewModel : ViewModel() {

    private val _state = MutableStateFlow<ScannerState>(ScannerState.Idle)
    val state: StateFlow<ScannerState> = _state.asStateFlow()

    fun beginScanning() {
        _state.value = ScannerState.Scanning
    }

    fun onIsbnDetected(isbn13: String) {
        _state.value = ScannerState.Success(isbn13)
    }

    fun onError(message: String, cause: Throwable? = null) {
        _state.value = ScannerState.Error(message, cause)
    }

    fun reset() {
        _state.value = ScannerState.Scanning
    }

    fun isScanning(): Boolean = _state.value is ScannerState.Scanning
}
