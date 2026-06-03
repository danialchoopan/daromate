package ir.danialchoopan.medimate.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.danialchoopan.medimate.presentation.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val report by viewModel.adherenceReport.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Adherence History") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Adherence Rate: ${report.adherenceRate}%", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Total Expected: ${report.totalExpected}")
            Text("Total Taken: ${report.totalTaken}")
        }
    }
}
