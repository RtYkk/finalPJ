package jlu.kemiko.libman.ui.login

/**
 * Login screen state and supporting types.
 */
enum class LoginRole {
    ADMIN,
    STUDENT
}

data class LoginUiState(
    val selectedRole: LoginRole = LoginRole.ADMIN,
    val username: String = "",
    val password: String = "",
    val studentId: String = "",
    val studentIdError: String? = null,
    val credentialsError: String? = null,
    val isSubmitting: Boolean = false
)
