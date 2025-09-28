package jlu.kemiko.libman.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.Modifier
import jlu.kemiko.libman.ui.components.LibmanTopAppBar
import jlu.kemiko.libman.ui.login.LoginRoute
import jlu.kemiko.libman.ui.navigation.LibmanNavHost
import jlu.kemiko.libman.ui.student.StudentDashboard
import jlu.kemiko.libman.ui.theme.LibmanTheme

/**
 * Top-level root composable hosting the application theme and navigation graph.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibmanApp() {
    LibmanTheme {
        var session by rememberSaveable(stateSaver = authSessionSaver()) {
            mutableStateOf<AuthSession?>(null)
        }

        if (session == null) {
            LoginRoute(
                onAuthenticated = { session = it },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AuthenticatedShell(
                session = session!!,
                onLogout = { session = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedShell(
    session: AuthSession,
    onLogout: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LibmanTopAppBar(
                title = when (session) {
                    AuthSession.Admin -> "Libman Library"
                    is AuthSession.Student -> "Student Portal"
                },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text(text = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (session) {
            AuthSession.Admin -> {
                LibmanNavHost(modifier = contentModifier)
            }

            is AuthSession.Student -> {
                StudentDashboard(
                    studentId = session.studentId,
                    modifier = contentModifier
                )
            }
        }
    }
}

private fun authSessionSaver() = mapSaver<AuthSession?>(
    save = { session ->
        when (session) {
            null -> emptyMap()
            AuthSession.Admin -> mapOf("role" to "admin")
            is AuthSession.Student -> mapOf(
                "role" to "student",
                "studentId" to session.studentId
            )
        }
    },
    restore = { map ->
        when (map["role"]) {
            "admin" -> AuthSession.Admin
            "student" -> (map["studentId"] as? String)?.let(AuthSession::Student)
            else -> null
        }
    }
)
