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
    val username: String = DEFAULT_ADMIN_USERNAME,
    val password: String = DEFAULT_ADMIN_PASSWORD,
    val studentId: String = "",
    val studentIdError: String? = null,
    val credentialsError: String? = null,
    val isSubmitting: Boolean = false
)

internal const val DEFAULT_ADMIN_USERNAME = "admin"
internal const val DEFAULT_ADMIN_PASSWORD = "admin"
