package ir.danialchoopan.medimate.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.appwidget.cornerRadius
import androidx.glance.Button
import ir.danialchoopan.medimate.data.local.AppDatabase
import ir.danialchoopan.medimate.domain.model.Reminder
import ir.danialchoopan.medimate.util.DateTimeUtils
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val reminderIdParam = ActionParameters.Key<Int>("reminder_id")

class MediMateWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getDatabase(context)
        val medicines = db.medicineDao().getAllMedicines().first()
        val reminders = db.reminderDao().getAllActiveReminders().first()
        val medicineMap = medicines.associateBy { it.id }

        val timeline = reminders
            .mapNotNull { reminder ->
                medicineMap[reminder.medicineId]?.let { medicine ->
                    Pair(medicine, reminder)
                }
            }
            .sortedBy { it.second.nextReminderTime }
            .take(3)

        provideContent {
            GlanceTheme {
                MediMateWidgetContent(timeline)
            }
        }
    }
}

@Composable
private fun MediMateWidgetContent(timeline: List<Pair<ir.danialchoopan.medimate.data.local.entities.MedicineEntity, Reminder>>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .cornerRadius(16.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "MediMate",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onSurface
            )
        )
        Spacer(GlanceModifier.height(8.dp))

        if (timeline.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No upcoming reminders",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = GlanceTheme.colors.onSurfaceVariant
                    )
                )
            }
        } else {
            timeline.forEach { (medicine, reminder) ->
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = medicine.name,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = GlanceTheme.colors.onSurface
                            )
                        )
                        Text(
                            text = "${medicine.dosage} - ${formatTime(reminder.nextReminderTime)}",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = GlanceTheme.colors.onSurfaceVariant
                            )
                        )
                    }
                    Spacer(GlanceModifier.width(8.dp))
                    Button(
                        text = "Taken",
                        onClick = actionRunCallback<TakenActionCallback>(
                            actionParametersOf(reminderIdParam to reminder.id)
                        )
                    )
                }
                if (reminder != timeline.last().second) {
                    Spacer(GlanceModifier.height(4.dp))
                }
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}
