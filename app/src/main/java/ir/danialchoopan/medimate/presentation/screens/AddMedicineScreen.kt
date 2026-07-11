package ir.danialchoopan.medimate.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ir.danialchoopan.medimate.domain.model.Inventory
import ir.danialchoopan.medimate.domain.model.IntervalType
import ir.danialchoopan.medimate.domain.model.Medicine
import ir.danialchoopan.medimate.domain.model.Reminder
import ir.danialchoopan.medimate.presentation.components.AppButton
import ir.danialchoopan.medimate.presentation.components.ErrorCard
import ir.danialchoopan.medimate.presentation.components.Spacing
import ir.danialchoopan.medimate.presentation.components.WarningRow
import ir.danialchoopan.medimate.presentation.viewmodel.AddMedicineViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddMedicineScreen(viewModel: AddMedicineViewModel, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instruction by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var selectedForm by remember { mutableStateOf("قرص") }
    var currentStock by remember { mutableStateOf("0") }
    var selectedInterval by remember { mutableStateOf(IntervalType.DAYS) }
    var nameError by remember { mutableStateOf(false) }
    var dosageError by remember { mutableStateOf(false) }

    val interactions by viewModel.interactions.collectAsState()
    val forms = listOf("قرص", "کپسول", "شربت", "تزریق")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("افزودن دارو") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = Spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(Spacing.sm))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                    viewModel.checkInteractions(it)
                },
                label = { Text("نام دارو") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = if (nameError) {{ Text("نام الزامی است") }} else null,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(Spacing.sm))

            AnimatedVisibility(
                visible = interactions.isNotEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                ErrorCard(isVisible = true) {
                    interactions.forEach { interaction ->
                        WarningRow(
                            text = "${interaction.severity.name}: ${interaction.description}"
                        )
                        if (interaction != interactions.last()) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
            if (interactions.isNotEmpty()) Spacer(modifier = Modifier.height(Spacing.sm))

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("توضیحات (اختیاری)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(Spacing.sm))

            OutlinedTextField(
                value = reason, onValueChange = { reason = it },
                label = { Text("دلیل مصرف (اختیاری)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(Spacing.md))

            Text("شکل", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.sm))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                forms.forEach { form ->
                    FilterChip(
                        selected = selectedForm == form,
                        onClick = { selectedForm = form },
                        label = { Text(form) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it; dosageError = false },
                label = { Text("دوز") },
                modifier = Modifier.fillMaxWidth(),
                isError = dosageError,
                supportingText = if (dosageError) {{ Text("دوز الزامی است") }} else null,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Spacing.md))
            Text("موجودی", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.sm))
            OutlinedTextField(
                value = currentStock, onValueChange = { currentStock = it },
                label = { Text("تعداد کنونی") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Spacing.md))
            Text("زمانبندی", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.sm))
            IntervalType.entries.forEach { interval ->
                val label = when(interval) {
                    IntervalType.MINUTES -> "دقیقه"
                    IntervalType.HOURS -> "ساعتی"
                    IntervalType.DAYS -> "روزانه"
                    IntervalType.WEEKS -> "هفتگی"
                    IntervalType.EVEN_DAYS -> "روزهای زوج"
                    IntervalType.ODD_DAYS -> "روزهای فرد"
                    IntervalType.CYCLE -> "چرخه‌ای"
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedInterval = interval }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedInterval == interval,
                        onClick = { selectedInterval = interval }
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))
            AppButton(
                text = "ذخیره دارو",
                onClick = {
                    nameError = name.isBlank()
                    dosageError = dosage.isBlank()
                    if (nameError || dosageError) return@AppButton

                    val medicine = Medicine(
                        name = name.trim(), description = description.trim(), dosage = dosage.trim(),
                        form = selectedForm, instruction = instruction.trim(), reason = reason.trim(),
                        color = 0xFF4CAF50.toInt()
                    )
                    val reminder = Reminder(
                        medicineId = 0,
                        intervalType = selectedInterval,
                        intervalValue = 1,
                        nextReminderTime = System.currentTimeMillis() + 60000
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
            )
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}
