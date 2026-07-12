package ir.danialchoopan.medimate.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.medimate.util.DateTimeUtils

data class CalendarDay(
    val day: Int,
    val taken: Int,
    val expected: Int
)

@Composable
fun JalaliCalendar(
    dailyData: Map<Int, Pair<Int, Int>>, // day -> (taken, expected)
    modifier: Modifier = Modifier
) {
    var currentYear by remember { mutableIntStateOf(DateTimeUtils.getCurrentPersianDate().first) }
    var currentMonth by remember { mutableIntStateOf(DateTimeUtils.getCurrentPersianDate().second) }

    val daysInMonth = DateTimeUtils.getDaysInPersianMonth(currentYear, currentMonth)
    val monthName = DateTimeUtils.getPersianMonthName(currentMonth)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Month header with navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (currentMonth > 1) currentMonth-- else {
                        currentMonth = 12
                        currentYear--
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "ماه قبل")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$currentYear",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = {
                    if (currentMonth < 12) currentMonth++ else {
                        currentMonth = 1
                        currentYear++
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "ماه بعد")
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Day of week headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("ش", "ی", "د", "س", "چ", "پ", "ج").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Calendar grid
            val firstDayOfWeek = getFirstDayOfWeek(currentYear, currentMonth)
            val totalCells = firstDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height((rows * 48).dp)
            ) {
                items(totalCells) { index ->
                    if (index < firstDayOfWeek) {
                        // Empty cell before first day
                        Box(modifier = Modifier.aspectRatio(1f))
                    } else {
                        val day = index - firstDayOfWeek + 1
                        val (taken, expected) = dailyData[day] ?: Pair(0, 0)
                        CalendarDayCell(
                            day = day,
                            taken = taken,
                            expected = expected
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color(0xFF4CAF50), label = "مصرف شده")
                LegendItem(color = Color(0xFFF57C00), label = "نیمه مصرف")
                LegendItem(color = Color(0xFFD32F2F), label = "مصرف نشده")
                LegendItem(color = MaterialTheme.colorScheme.surfaceVariant, label = "بدون داده")
            }
        }
    }
}

@Composable
private fun CalendarDayCell(day: Int, taken: Int, expected: Int) {
    val backgroundColor = when {
        taken == 0 && expected == 0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        taken >= expected -> Color(0xFF4CAF50).copy(alpha = 0.2f)
        taken > 0 -> Color(0xFFF57C00).copy(alpha = 0.2f)
        else -> Color(0xFFD32F2F).copy(alpha = 0.1f)
    }

    val textColor = when {
        taken == 0 && expected == 0 -> MaterialTheme.colorScheme.onSurfaceVariant
        taken >= expected -> Color(0xFF2E7D32)
        taken > 0 -> Color(0xFFE65100)
        else -> Color(0xFFC62828)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { /* Show day details */ },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$day",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            if (expected > 0) {
                Text(
                    text = "$taken/$expected",
                    fontSize = 8.sp,
                    color = textColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.5f))
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getFirstDayOfWeek(year: Int, month: Int): Int {
    var totalDays = 0
    for (y in 1 until year) {
        totalDays += 365
        totalDays += y / 28
    }
    for (m in 1 until month) {
        totalDays += when {
            m <= 6 -> 31
            m <= 11 -> 30
            else -> if (DateTimeUtils.isPersianLeapYear(year)) 30 else 29
        }
    }
    return (totalDays + 1) % 7
}
