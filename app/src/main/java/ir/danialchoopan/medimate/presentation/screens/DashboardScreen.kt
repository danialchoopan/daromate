package ir.danialchoopan.medimate.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.medimate.presentation.components.EmptyStateView
import ir.danialchoopan.medimate.presentation.components.GradientCard
import ir.danialchoopan.medimate.presentation.components.MedicineCard
import ir.danialchoopan.medimate.presentation.components.Spacing
import ir.danialchoopan.medimate.presentation.theme.GradientEnd
import ir.danialchoopan.medimate.presentation.theme.GradientStart
import ir.danialchoopan.medimate.presentation.viewmodel.DashboardViewModel
import ir.danialchoopan.medimate.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel, navController: androidx.navigation.NavController) {
    val timelineItems by viewModel.timelineState.collectAsState()

    val today = DateTimeUtils.getCurrentPersianDate()
    val monthName = DateTimeUtils.getPersianMonthName(today.second)
    val dayName = DateTimeUtils.getPersianDayName(
        java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK).let {
            when (it) {
                java.util.Calendar.SATURDAY -> 1
                java.util.Calendar.SUNDAY -> 2
                java.util.Calendar.MONDAY -> 3
                java.util.Calendar.TUESDAY -> 4
                java.util.Calendar.WEDNESDAY -> 5
                java.util.Calendar.THURSDAY -> 6
                java.util.Calendar.FRIDAY -> 7
                else -> 1
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "برنامه روزانه",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Text(
                            text = "$dayName ${today.third} $monthName",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GradientStart,
                            GradientEnd,
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 0f,
                        endY = 600f
                    )
                )
        ) {
            AnimatedContent(
                targetState = timelineItems.isEmpty(),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "dashboardContent",
                modifier = Modifier.padding(padding)
            ) { isEmpty ->
                if (isEmpty) {
                    EmptyStateView(
                        icon = Icons.Filled.DateRange,
                        title = "هنوز یادآوری برایتان نیست",
                        message = "برای اضافه اولین داروی خود روی + کلیک کنید.",
                    )
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Stats card
                        GradientCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${timelineItems.size}",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color.White
                                )
                                Text(
                                    text = "یادآوری امروز",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            items(
                                items = timelineItems,
                                key = { it.reminder.id }
                            ) { item ->
                                MedicineCard(
                                    name = item.medicine.name,
                                    subtitle = "${item.medicine.dosage} - ${item.medicine.form}",
                                    trailing = DateTimeUtils.timestampToPersianDate(item.reminder.nextReminderTime),
                                    onClick = {
                                        navController.navigate("edit_medicine/${item.medicine.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
