package ir.danialchoopan.medimate.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.medimate.presentation.components.EmptyStateView
import ir.danialchoopan.medimate.presentation.components.MedicineCard
import ir.danialchoopan.medimate.presentation.components.Spacing
import ir.danialchoopan.medimate.presentation.viewmodel.DashboardViewModel
import ir.danialchoopan.medimate.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel, navController: androidx.navigation.NavController) {
    val timelineItems by viewModel.timelineState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("برنامه روزانه")
                        Text(
                            text = "$dayName ${today.third} $monthName",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = timelineItems.isEmpty(),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "dashboardContent"
        ) { isEmpty ->
            if (isEmpty) {
                EmptyStateView(
                    icon = Icons.Filled.DateRange,
                    title = "هنوز یادآوری برایتان نیست",
                    message = "برای اضافه اولین داروی خود روی + کلیک کنید.",
                    modifier = Modifier.padding(padding)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
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
