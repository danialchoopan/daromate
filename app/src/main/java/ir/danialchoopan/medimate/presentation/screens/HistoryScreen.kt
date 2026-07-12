package ir.danialchoopan.medimate.presentation.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import ir.danialchoopan.medimate.domain.model.LogStatus
import ir.danialchoopan.medimate.presentation.components.AppCard
import ir.danialchoopan.medimate.presentation.components.EmptyStateView
import ir.danialchoopan.medimate.presentation.components.JalaliCalendar
import ir.danialchoopan.medimate.presentation.components.Spacing
import ir.danialchoopan.medimate.presentation.components.StatusBadge
import ir.danialchoopan.medimate.presentation.viewmodel.ExportState
import ir.danialchoopan.medimate.presentation.viewmodel.HistoryViewModel
import ir.danialchoopan.medimate.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val report by viewModel.adherenceReport.collectAsState()
    val logs by viewModel.allLogs.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Build calendar data from logs
    val calendarData = remember(logs) {
        val data = mutableMapOf<Int, Pair<Int, Int>>()
        // Group logs by day of month
        logs.groupBy { DateTimeUtils.timestampToPersianDay(it.reminderTime) }
            .forEach { (day, dayLogs) ->
                val taken = dayLogs.count { it.status == LogStatus.TAKEN }
                val expected = dayLogs.size
                data[day] = Pair(taken, expected)
            }
        data
    }

    val shareLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.resetExportState() }

    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportState.Success -> {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, state.uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                shareLauncher.launch(Intent.createChooser(shareIntent, "اشتراک گزارش دارویی"))
                viewModel.resetExportState()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("گزارش پایبندی") },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { viewModel.exportHistory() }) {
                        Icon(Icons.Default.Share, contentDescription = "خروجی")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Adherence summary card
            item {
                AppCard(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    elevation = 0.dp
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            "نرخ پایبندی",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            "%.1f%%".format(report.adherenceRate),
                            style = MaterialTheme.typography.displaySmall
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("مورد", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    "${report.totalExpected}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("مصرف شده", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    "${report.totalTaken}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }

            // Jalali Calendar
            item {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text("تقویم مصرف", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(Spacing.sm))
                JalaliCalendar(dailyData = calendarData)
            }

            // Log history
            item {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text("تاریخچه دارویی", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(Spacing.sm))
            }

            item {
                if (logs.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Filled.Info,
                        title = "هنوز لاگ ثبت نشده است.",
                        message = "تاریخچه اطلاعات دارویی شما در اینجا نمایان خواهد داد.",
                        modifier = Modifier.height(200.dp)
                    )
                }
            }

            items(
                items = logs,
                key = { it.id }
            ) { log ->
                AppCard(
                    elevation = 0.dp,
                    borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = DateTimeUtils.timestampToPersianDate(log.reminderTime),
                                style = MaterialTheme.typography.titleSmall
                            )
                            StatusBadge(status = log.status)
                        }
                        if (log.takenTime != null) {
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = "مصرف شده: ${DateTimeUtils.timestampToPersianDate(log.takenTime)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
