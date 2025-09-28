package jlu.kemiko.libman.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import jlu.kemiko.libman.ui.theme.LibmanTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibmanSurfaceCard(
    modifier: Modifier = Modifier,
    tonal: Boolean = false,
    shape: Shape? = null,
    contentPadding: PaddingValues? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val resolvedShape = shape ?: MaterialTheme.shapes.large
    val colors = MaterialTheme.colorScheme
    val containerColor = if (tonal) colors.secondaryContainer else colors.surface
    val contentColor = if (tonal) colors.onSecondaryContainer else colors.onSurface
    val shadowElevation = if (tonal) LibmanTheme.elevations.level1 else LibmanTheme.elevations.level2
    val padding = contentPadding ?: PaddingValues(LibmanTheme.spacing.medium)

    if (onClick != null) {
        Surface(
            modifier = modifier,
            shape = resolvedShape,
            tonalElevation = if (tonal) LibmanTheme.elevations.level1 else 0.dp,
            shadowElevation = shadowElevation,
            color = containerColor,
            contentColor = contentColor,
            onClick = onClick
        ) {
            Column(modifier = Modifier.padding(padding)) {
                content()
            }
        }
    } else {
        Surface(
            modifier = modifier,
            shape = resolvedShape,
            tonalElevation = if (tonal) LibmanTheme.elevations.level1 else 0.dp,
            shadowElevation = shadowElevation,
            color = containerColor,
            contentColor = contentColor
        ) {
            Column(modifier = Modifier.padding(padding)) {
                content()
            }
        }
    }
}
