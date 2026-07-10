package ir.danialchoopan.medimate.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.danialchoopan.medimate.domain.model.InteractionSeverity
import ir.danialchoopan.medimate.domain.model.LogStatus
import ir.danialchoopan.medimate.presentation.theme.SeverityMild
import ir.danialchoopan.medimate.presentation.theme.SeverityModerate
import ir.danialchoopan.medimate.presentation.theme.SeveritySevere
import ir.danialchoopan.medimate.presentation.theme.StatusMissed
import ir.danialchoopan.medimate.presentation.theme.StatusSnoozed
import ir.danialchoopan.medimate.presentation.theme.StatusTaken

@Composable
fun SeverityBadge(
    severity: InteractionSeverity,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (severity) {
        InteractionSeverity.SEVERE -> SeveritySevere.copy(alpha = 0.12f) to SeveritySevere
        InteractionSeverity.MODERATE -> SeverityModerate.copy(alpha = 0.12f) to SeverityModerate
        InteractionSeverity.MILD -> SeverityMild.copy(alpha = 0.12f) to SeverityMild
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = severity.name,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
fun StatusBadge(
    status: LogStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        LogStatus.TAKEN -> StatusTaken.copy(alpha = 0.12f) to StatusTaken
        LogStatus.MISSED -> StatusMissed.copy(alpha = 0.12f) to StatusMissed
        LogStatus.SNOOZED -> StatusSnoozed.copy(alpha = 0.12f) to StatusSnoozed
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
