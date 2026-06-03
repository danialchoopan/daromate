package ir.danialchoopan.medimate.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.danialchoopan.medimate.presentation.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val timelineItems by viewModel.timelineState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Daily Timeline") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(timelineItems) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = item.medicine.name, style = MaterialTheme.typography.titleLarge)
                        Text(text = "Time: ${item.reminder.nextReminderTime}")
                        Text(text = "Dosage: ${item.medicine.dosage}")
                    }
                }
            }
        }
    }
}
