package ir.nimaali.medimate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gmail.hamedvakhide.compose_jalali_datepicker.JalaliDatePickerDialog
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.util.DateTimeUtils
import ir.nimaali.medimate.viewmodel.MedicineViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    viewModel: MedicineViewModel,
    navController: NavController
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // حالت‌های شروع
    var startOption by remember { mutableStateOf(StartOption.NOW) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }

    var showDatePicker = remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val intervalOptions = listOf(
        "2 دقیقه" to Pair(IntervalType.MINUTES, 2),
        "6 ساعت" to Pair(IntervalType.HOURS, 6),
        "8 ساعت" to Pair(IntervalType.HOURS, 8),
        "12 ساعت" to Pair(IntervalType.HOURS, 12),
        "24 ساعت" to Pair(IntervalType.HOURS, 24),
        "48 ساعت" to Pair(IntervalType.HOURS, 48),
        "هفتگی" to Pair(IntervalType.WEEKS, 1)
    )

    var selectedInterval by remember { mutableStateOf(intervalOptions[0].second) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("افزودن داروی جدید") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
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
                value = name,
                onValueChange = { name = it },
                label = { Text("نام دارو") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("توضیحات") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("زمان شروع:", style = MaterialTheme.typography.labelLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = startOption == StartOption.NOW,
                    onClick = { startOption = StartOption.NOW },
                    label = { Text("همین الان") }
                )

                FilterChip(
                    selected = startOption == StartOption.LATER,
                    onClick = { startOption = StartOption.LATER },
                    label = { Text("تاریخ و زمان مشخص") }
                )
            }

            if (startOption == StartOption.LATER) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDatePicker.value = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("انتخاب تاریخ: ${DateTimeUtils.timestampToPersianDate(selectedDate)}")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("انتخاب زمان: ${"%02d".format(selectedHour)}:${"%02d".format(selectedMinute)}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("بازه زمانی یادآوری:", style = MaterialTheme.typography.labelLarge)

            Spacer(modifier = Modifier.height(8.dp))

            intervalOptions.forEach { (label, interval) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedInterval == interval,
                        onClick = { selectedInterval = interval }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        modifier = Modifier.clickable { selectedInterval = interval }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val startTime = if (startOption == StartOption.NOW) {
                        System.currentTimeMillis()
                    } else {
                        // ترکیب تاریخ و زمان انتخاب شده
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = selectedDate
                            set(Calendar.HOUR_OF_DAY, selectedHour)
                            set(Calendar.MINUTE, selectedMinute)
                            set(Calendar.SECOND, 0)
                        }
                        calendar.timeInMillis
                    }

                    val medicine = Medicine(
                        name = name,
                        description = description,
                        startDate = startTime
                    )
                    viewModel.addMedicine(medicine, selectedInterval)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("ذخیره دارو")
            }
        }
    }

    if (showDatePicker.value) {
        JalaliDatePickerDialog(
            openDialog = showDatePicker,
//            onDismissRequest = { showDatePicker.value = false },
            onSelectDay = { jalaliDate ->
                // تبدیل تاریخ شمسی به میلادی
//                val gregorianDate = jalaliDate.toGregorian()
                val calendar = Calendar.getInstance().apply {
                    set(jalaliDate.year, jalaliDate.month - 1, jalaliDate.day)
                }
                selectedDate = calendar.timeInMillis
            },
            onConfirm = { jalaliDate ->
                // تبدیل تاریخ شمسی به میلادی
//                val gregorianDate = jalaliDate.toGregorian()
                val calendar = Calendar.getInstance().apply {
                    set(jalaliDate.year, jalaliDate.month - 1, jalaliDate.day)
                }
                selectedDate = calendar.timeInMillis
                showDatePicker.value = false
            }
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = { hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                showTimePicker = false
            }
        )
    }
}

@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("انتخاب زمان") },
        text = {
            Column {
                Text("ساعت:", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = selectedHour.toFloat(),
                    onValueChange = { selectedHour = it.toInt() },
                    valueRange = 0f..23f,
                    steps = 22
                )
                Text("$selectedHour:00", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Text("دقیقه:", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = selectedMinute.toFloat(),
                    onValueChange = { selectedMinute = it.toInt() },
                    valueRange = 0f..59f,
                    steps = 58
                )
                Text("00:$selectedMinute", style = MaterialTheme.typography.bodyLarge)
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedHour, selectedMinute) }) {
                Text("تأیید")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("انصراف")
            }
        }
    )
}

enum class StartOption {
    NOW, LATER
}