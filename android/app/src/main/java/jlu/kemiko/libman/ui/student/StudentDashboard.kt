package jlu.kemiko.libman.ui.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import jlu.kemiko.libman.ui.components.LibmanSurfaceCard
import jlu.kemiko.libman.ui.theme.LibmanTheme

@Composable
fun StudentDashboard(
    studentId: String,
    modifier: Modifier = Modifier
) {
    val spacing = LibmanTheme.spacing
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.large, vertical = spacing.xLarge),
        verticalArrangement = Arrangement.spacedBy(spacing.large, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Student Portal",
            style = LibmanTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Welcome, student $studentId. Loan tracking and history will appear here soon.",
            style = LibmanTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        LibmanSurfaceCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.small),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Coming soon",
                    style = LibmanTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "You will be able to view current loans, due dates, and return reminders once the student dashboard is completed.",
                    style = LibmanTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentDashboardPreview() {
    LibmanTheme {
        StudentDashboard(studentId = "12345678")
    }
}
