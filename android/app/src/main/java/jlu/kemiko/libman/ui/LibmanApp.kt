package jlu.kemiko.libman.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jlu.kemiko.libman.ui.navigation.LibmanNavHost
import jlu.kemiko.libman.ui.theme.LibmanTheme
import jlu.kemiko.libman.ui.components.LibmanTopAppBar

/**
 * Top-level root composable hosting the application theme and navigation graph.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibmanApp() {
    LibmanTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                LibmanTopAppBar(title = "Libman Library")
            }
        ) { innerPadding ->
            LibmanNavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}
