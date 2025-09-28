package jlu.kemiko.libman.ui.students

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jlu.kemiko.libman.ui.components.LibmanSurfaceCard
import jlu.kemiko.libman.ui.theme.LibmanTheme

@Composable
fun StudentsManagementRoute(
    modifier: Modifier = Modifier
) {
    StudentsManagementScreen(modifier = modifier)
}

@Composable
private fun StudentsManagementScreen(
    modifier: Modifier = Modifier
) {
    val spacing = LibmanTheme.spacing
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.large, vertical = spacing.large),
        verticalArrangement = Arrangement.spacedBy(spacing.large)
    ) {
        Text(
            text = "学生管理",
            style = LibmanTheme.typography.headlineMedium
        )

        LibmanSurfaceCard(
            modifier = Modifier.fillMaxWidth(),
            tonal = true
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                Text(
                    text = "功能即将上线",
                    style = LibmanTheme.typography.titleMedium
                )
                Text(
                    text = "在这里维护学生资料、借阅资格和关联的学业信息。",
                    style = LibmanTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentsManagementPreview() {
    LibmanTheme {
        StudentsManagementScreen()
    }
}
