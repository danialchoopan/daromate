package ir.nimaali.medimate.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gmail.hamedvakhide.compose_jalali_datepicker.JalaliDatePickerDialog
import ir.huri.jcal.JalaliCalendar
import ir.nimaali.medimate.R
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.ui.theme.vazirFontFamily
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

    val m_context= LocalContext.current

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
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_local_hospital_24),
                            contentDescription = "toolbar logo",
                            modifier = Modifier
                                .size(44.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "افزودن داروی جدید",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = vazirFontFamily
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
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
                label = { Text("نام دارو",
                    fontFamily = vazirFontFamily) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("توضیحات",
                    fontFamily = vazirFontFamily) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("زمان شروع:", style = MaterialTheme.typography.labelLarge, fontFamily = vazirFontFamily)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = startOption == StartOption.NOW,
                    onClick = { startOption = StartOption.NOW },
                    label = { Text("همین الان",
                        fontFamily = vazirFontFamily) }
                )

                FilterChip(
                    selected = startOption == StartOption.LATER,
                    onClick = { startOption = StartOption.LATER },
                    label = { Text("تاریخ و زمان مشخص",
                        fontFamily = vazirFontFamily) }
                )
            }

            if (startOption == StartOption.LATER) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDatePicker.value = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("انتخاب تاریخ: ${DateTimeUtils.timestampToPersianDate(selectedDate)}",
                        fontFamily = vazirFontFamily)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("انتخاب زمان: ${"%02d".format(selectedHour)}:${"%02d".format(selectedMinute)}",
                        fontFamily = vazirFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("بازه زمانی یادآوری:", style = MaterialTheme.typography.labelLarge,
                fontFamily = vazirFontFamily)

            Spacer(modifier = Modifier.height(8.dp))

            intervalOptions.forEach { (label, interval) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp
                        )
                ) {
                    RadioButton(
                        selected = selectedInterval == interval,
                        onClick = { selectedInterval = interval }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        modifier = Modifier.clickable { selectedInterval = interval },
                        fontFamily = vazirFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if(name.trim().isNotEmpty() || description.trim().isNotEmpty()){


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
                    }else{
                        Toast.makeText(m_context,"نام و توضیحات را لطفا وارد کنید ",Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("ذخیره دارو",
                    fontFamily = vazirFontFamily)
            }
        }
    }

    if (showDatePicker.value) {
        JalaliDatePickerDialog(
            openDialog = showDatePicker,
            onSelectDay = { jalaliDate ->
                val calendar = JalaliCalendar(jalaliDate.year, jalaliDate.month, jalaliDate.day)
                selectedDate = calendar.toGregorian().timeInMillis
            },
            onConfirm = { jalaliDate ->
                val calendar = JalaliCalendar(jalaliDate.year, jalaliDate.month, jalaliDate.day)
                selectedDate = calendar.toGregorian().timeInMillis
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
        title = { Text("انتخاب زمان",
            fontFamily = vazirFontFamily) },
        text = {
            Column {
                Text("ساعت:", style = MaterialTheme.typography.labelMedium,
                    fontFamily = vazirFontFamily)
                Slider(
                    value = selectedHour.toFloat(),
                    onValueChange = { selectedHour = it.toInt() },
                    valueRange = 0f..23f,
                    steps = 22
                )
                Text("$selectedHour:00", style = MaterialTheme.typography.bodyLarge,
                    fontFamily = vazirFontFamily)

                Spacer(modifier = Modifier.height(16.dp))

                Text("دقیقه:", style = MaterialTheme.typography.labelMedium,
                    fontFamily = vazirFontFamily)
                Slider(
                    value = selectedMinute.toFloat(),
                    onValueChange = { selectedMinute = it.toInt() },
                    valueRange = 0f..59f,
                    steps = 58
                )
                Text("00:$selectedMinute", style = MaterialTheme.typography.bodyLarge,
                    fontFamily = vazirFontFamily)
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedHour, selectedMinute) }) {
                Text("تأیید",
                    fontFamily = vazirFontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("انصراف",
                    fontFamily = vazirFontFamily)
            }
        }
    )
}

enum class StartOption {
    NOW, LATER
}