package jlu.kemiko.libman.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import jlu.kemiko.libman.ui.AuthSession
import jlu.kemiko.libman.ui.components.LibmanPrimaryButton
import jlu.kemiko.libman.ui.components.LibmanSurfaceCard
import jlu.kemiko.libman.ui.login.LoginRole.ADMIN
import jlu.kemiko.libman.ui.login.LoginRole.STUDENT
import jlu.kemiko.libman.ui.theme.LibmanTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginRoute(
    onAuthenticated: (AuthSession) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoginEvent.Success -> onAuthenticated(event.session)
            }
        }
    }

    LoginScreen(
        state = uiState,
        modifier = modifier,
        onRoleSelect = viewModel::onRoleSelected,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onStudentIdChange = viewModel::onStudentIdChange,
        onSubmit = viewModel::submit
    )
}

@Composable
private fun LoginScreen(
    state: LoginUiState,
    modifier: Modifier = Modifier,
    onRoleSelect: (LoginRole) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onStudentIdChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val spacing = LibmanTheme.spacing
    val submitEnabled = when (state.selectedRole) {
        ADMIN -> state.username.isNotBlank() && state.password.isNotBlank()
        STUDENT -> state.studentId.isNotBlank()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.large, vertical = spacing.xLarge),
        verticalArrangement = Arrangement.spacedBy(spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome to Libman",
                style = LibmanTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = "Log in as an administrator or student to continue.",
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        LibmanSurfaceCard(
            modifier = Modifier.fillMaxWidth(),
            tonal = false,
            contentPadding = PaddingValues(all = spacing.large)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                Text(
                    text = "Login role",
                    style = LibmanTheme.typography.titleMedium
                )

                RoleToggle(
                    selectedRole = state.selectedRole,
                    onSelect = onRoleSelect
                )

                when (state.selectedRole) {
                    ADMIN -> AdminCredentialsSection(
                        state = state,
                        onUsernameChange = onUsernameChange,
                        onPasswordChange = onPasswordChange
                    )

                    STUDENT -> StudentCredentialsSection(
                        state = state,
                        onStudentIdChange = onStudentIdChange
                    )
                }

                CredentialsHint(state = state)

                LibmanPrimaryButton(
                    text = "Continue",
                    onClick = onSubmit,
                    enabled = submitEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RoleToggle(
    selectedRole: LoginRole,
    onSelect: (LoginRole) -> Unit
) {
    val spacing = LibmanTheme.spacing
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        RoleOption(
            label = "Administrator",
            role = ADMIN,
            selected = selectedRole == ADMIN,
            onSelect = onSelect
        )
        RoleOption(
            label = "Student",
            role = STUDENT,
            selected = selectedRole == STUDENT,
            onSelect = onSelect
        )
    }
}

@Composable
private fun RoleOption(
    label: String,
    role: LoginRole,
    selected: Boolean,
    onSelect: (LoginRole) -> Unit
) {
    val spacing = LibmanTheme.spacing
    LibmanSurfaceCard(
        modifier = Modifier.fillMaxWidth(),
        tonal = selected,
        onClick = { onSelect(role) },
        contentPadding = PaddingValues(horizontal = spacing.medium, vertical = spacing.small)
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = LibmanTheme.typography.bodyLarge
            )
            RadioButton(
                selected = selected,
                onClick = null
            )
        }
    }
}

@Composable
private fun AdminCredentialsSection(
    state: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    val spacing = LibmanTheme.spacing
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StudentCredentialsSection(
    state: LoginUiState,
    onStudentIdChange: (String) -> Unit
) {
    val spacing = LibmanTheme.spacing
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        OutlinedTextField(
            value = state.studentId,
            onValueChange = onStudentIdChange,
            label = { Text("Student ID") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Student ID must be exactly 8 digits.",
            style = LibmanTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CredentialsHint(state: LoginUiState) {
    val errorColor = MaterialTheme.colorScheme.error
    state.credentialsError?.let { error ->
        Text(
            text = error,
            color = errorColor,
            style = LibmanTheme.typography.bodyMedium
        )
    }
    state.studentIdError?.let { error ->
        Text(
            text = error,
            color = errorColor,
            style = LibmanTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LibmanTheme {
        LoginScreen(
            state = LoginUiState(),
            onRoleSelect = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onStudentIdChange = {},
            onSubmit = {}
        )
    }
}
