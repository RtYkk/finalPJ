package jlu.kemiko.libman.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jlu.kemiko.libman.common.validation.Validators
import jlu.kemiko.libman.ui.AuthSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LoginEvent {
    data class Success(val session: AuthSession) : LoginEvent
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>(replay = 0, extraBufferCapacity = 1)
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun onRoleSelected(role: LoginRole) {
        _uiState.update {
            val base = it.copy(
                selectedRole = role,
                credentialsError = null,
                studentIdError = null
            )
            when (role) {
                LoginRole.ADMIN -> base.copy(
                    username = base.username.ifBlank { DEFAULT_ADMIN_USERNAME },
                    password = base.password.ifBlank { DEFAULT_ADMIN_PASSWORD }
                )
                LoginRole.STUDENT -> base
            }
        }
    }

    fun onUsernameChange(value: String) {
        _uiState.update {
            if (it.selectedRole == LoginRole.ADMIN) it.copy(username = value, credentialsError = null) else it
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            if (it.selectedRole == LoginRole.ADMIN) it.copy(password = value, credentialsError = null) else it
        }
    }

    fun onStudentIdChange(value: String) {
        _uiState.update {
            if (it.selectedRole == LoginRole.STUDENT) it.copy(studentId = value, studentIdError = null) else it
        }
    }

    fun submit() {
        val state = _uiState.value
        when (state.selectedRole) {
            LoginRole.ADMIN -> handleAdminLogin(state)
            LoginRole.STUDENT -> handleStudentLogin(state)
        }
    }

    private fun handleAdminLogin(state: LoginUiState) {
        val username = state.username.trim()
        val password = state.password
        if (username == DEFAULT_ADMIN_USERNAME && password == DEFAULT_ADMIN_PASSWORD) {
            emitSuccess(AuthSession.Admin)
        } else {
            _uiState.update { it.copy(credentialsError = "Invalid admin credentials") }
        }
    }

    private fun handleStudentLogin(state: LoginUiState) {
        val studentId = state.studentId.trim()
        if (!Validators.isValidStudentId(studentId)) {
            _uiState.update { it.copy(studentIdError = "Student ID must be exactly 8 digits") }
            return
        }
        emitSuccess(AuthSession.Student(studentId))
    }

    private fun emitSuccess(session: AuthSession) {
        viewModelScope.launch {
            _events.emit(LoginEvent.Success(session))
            _uiState.value = LoginUiState()
        }
    }
}
