package ir.danialchoopan.medimate.presentation.screens

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ir.danialchoopan.medimate.domain.model.IntervalType
import ir.danialchoopan.medimate.presentation.components.AppButton
import ir.danialchoopan.medimate.presentation.components.ButtonStyle
import ir.danialchoopan.medimate.presentation.components.LoadingOverlay
import ir.danialchoopan.medimate.presentation.components.Spacing
import ir.danialchoopan.medimate.presentation.viewmodel.AddMedicineViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditMedicineScreen(
    medicineId: Int,
    viewModel: AddMedicineViewModel,
    navController: NavController
) {
    val medicine by viewModel.getMedicineById(medicineId).collectAsState()
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instruction by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var selectedForm by remember { mutableStateOf("قرص") }
    var selectedInterval by remember { mutableStateOf(IntervalType.DAYS) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val forms = listOf("قرص", "کپسول", "شربت", "تزریق")

    LaunchedEffect(medicine) {
        medicine?.let {
            name = it.name
            dosage = it.dosage
            instruction = it.instruction
            description = it.description
            reason = it.reason
            selectedForm = it.form
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ویرایش دارو") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "حذف",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        LoadingOverlay(isLoading = medicine == null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = Spacing.md)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(Spacing.sm))

                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("نام دارو") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(Spacing.sm))

                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("توضیحات") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(Spacing.sm))

                OutlinedTextField(
                    value = reason, onValueChange = { reason = it },
                    label = { Text("دلیل مصرف") },
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
                    value = dosage, onValueChange = { dosage = it },
                    label = { Text("دوز") },
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
                    text = "به‌روزرسانی",
                    onClick = {
                        val updated = medicine!!.copy(
                            name = name, description = description, dosage = dosage,
                            form = selectedForm, instruction = instruction, reason = reason
                        )
                        viewModel.updateMedicine(updated)
                        navController.navigateUp()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.sm))
                AppButton(
                    text = "حذف دارو",
                    onClick = { showDeleteDialog = true },
                    style = ButtonStyle.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف دارو") },
            text = { Text("آیا از حذف ${medicine?.name} مطمئنید؟ این عمل قابل بازگشت نیست.") },
            confirmButton = {
                TextButton(onClick = {
                    medicine?.let { viewModel.deleteMedicine(it) }
                    showDeleteDialog = false
                    navController.navigateUp()
                }) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}
