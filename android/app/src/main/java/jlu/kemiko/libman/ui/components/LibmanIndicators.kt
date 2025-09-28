package jlu.kemiko.libman.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import jlu.kemiko.libman.ui.theme.LibmanTheme

enum class LibmanStatusStyle {
    Info,
    Success,
    Warning,
    Error
}

@Composable
fun LibmanStatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: LibmanStatusStyle = LibmanStatusStyle.Info,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val extended = LibmanTheme.extendedColors
    val (containerColor, contentColor) = when (style) {
        LibmanStatusStyle.Info -> extended.infoContainer to extended.onInfoContainer
        LibmanStatusStyle.Success -> extended.successContainer to extended.onSuccessContainer
        LibmanStatusStyle.Warning -> extended.warningContainer to extended.onWarningContainer
        LibmanStatusStyle.Error -> colorScheme.errorContainer to colorScheme.onErrorContainer
    }

    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(percent = 50),
        tonalElevation = LibmanTheme.elevations.level1
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = LibmanTheme.spacing.small,
                vertical = LibmanTheme.spacing.micro
            ),
            horizontalArrangement = Arrangement.spacedBy(LibmanTheme.spacing.tiny),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
            }
            Text(
                text = text,
                style = LibmanTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
