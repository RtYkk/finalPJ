package jlu.kemiko.libman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jlu.kemiko.libman.ui.theme.LibmanTheme
import jlu.kemiko.libman.ui.LibmanApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibmanTheme {
                LibmanApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LibmanAppPreview() {
    LibmanTheme {
        LibmanApp()
    }
}