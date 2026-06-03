package ir.danialchoopan.medimate.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ir.danialchoopan.medimate.domain.model.*
import ir.danialchoopan.medimate.presentation.viewmodel.AddMedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(viewModel: AddMedicineViewModel, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instruction by remember { mutableStateOf("") }
    var selectedForm by remember { mutableStateOf("Tablet") }
    var currentStock by remember { mutableStateOf("0") }
    var selectedInterval by remember { mutableStateOf(IntervalType.DAYS) }

    val forms = listOf("Tablet", "Capsule", "Syrup", "Injection")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medicine") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Medicine Name") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("Form", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(forms) { form ->
                    FilterChip(
                        selected = selectedForm == form,
                        onClick = { selectedForm = form },
                        label = { Text(form) }
                    )
                }
            }

            OutlinedTextField(
                value = dosage, onValueChange = { dosage = it },
                label = { Text("Dosage") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Pill Tracker", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = currentStock, onValueChange = { currentStock = it },
                label = { Text("Current Stock") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Scheduling", style = MaterialTheme.typography.titleMedium)
            IntervalType.values().forEach { interval ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable { selectedInterval = interval }
                ) {
                    RadioButton(selected = selectedInterval == interval, onClick = { selectedInterval = interval })
                    Text(interval.name)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val medicine = Medicine(
                        name = name, description = "", dosage = dosage,
                        form = selectedForm, instruction = instruction, reason = "",
                        color = 0xFF4CAF50.toInt()
                    )
                    val reminder = Reminder(
                        medicineId = 0,
                        intervalType = selectedInterval,
                        intervalValue = 1,
                        nextReminderTime = System.currentTimeMillis() + 60000 // 1 min from now for testing
                    )
                    val inventory = Inventory(
                        medicineId = 0,
                        currentStock = currentStock.toIntOrNull() ?: 0,
                        lowStockThreshold = 5
                    )
                    viewModel.addMedicine(medicine, listOf(reminder), inventory)
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Medicine")
            }
        }
    }
}
