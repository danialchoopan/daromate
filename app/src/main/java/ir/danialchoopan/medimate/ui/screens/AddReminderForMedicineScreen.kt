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
import ir.nimaali.medimate.data.dao.MedicineDao
import ir.nimaali.medimate.data.dao.ReminderDao
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.ui.theme.vazirFontFamily
import ir.nimaali.medimate.util.DateTimeUtils
import ir.nimaali.medimate.viewmodel.MedicineViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderForMedicineScreen(
    medicineId: Int,
    medicineDao: MedicineDao,
    reminderDao: ReminderDao,
    viewModel: MedicineViewModel,
    navController: NavController
) {
    val medicine by medicineDao.getMedicineById(medicineId).collectAsState(initial = null)

    val m_context = LocalContext.current

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
                            text = "افزودن یادآوری جدید",
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
            if (medicine != null) {
                OutlinedTextField(
                    value = medicine!!.name,
                    onValueChange = { },
                    label = { Text("نام دارو", fontFamily = vazirFontFamily) },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = medicine!!.description,
                    onValueChange = {  },
                    label = { Text("توضیحات", fontFamily = vazirFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    maxLines = 3
                )
            } else {
                Text("دارو یافت نشد")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("زمان شروع:", style = MaterialTheme.typography.labelLarge, fontFamily = vazirFontFamily)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = startOption == StartOption.NOW,
                    onClick = { startOption = StartOption.NOW },
                    label = { Text("همین الان", fontFamily = vazirFontFamily) }
                )

                FilterChip(
                    selected = startOption == StartOption.LATER,
                    onClick = { startOption = StartOption.LATER },
                    label = { Text("تاریخ و زمان مشخص", fontFamily = vazirFontFamily) }
                )
            }

            if (startOption == StartOption.LATER) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDatePicker.value = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("انتخاب تاریخ: ${DateTimeUtils.timestampToPersianDate(selectedDate)}", fontFamily = vazirFontFamily)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("انتخاب زمان: ${"%02d".format(selectedHour)}:${"%02d".format(selectedMinute)}", fontFamily = vazirFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("بازه زمانی یادآوری:", style = MaterialTheme.typography.labelLarge, fontFamily = vazirFontFamily)

            Spacer(modifier = Modifier.height(8.dp))

            intervalOptions.forEach { (label, interval) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
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
                        modifier = Modifier.clickable { selectedInterval = interval },
                        fontFamily = vazirFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (medicine != null) {
                        viewModel.addReminderForMedicine(medicine!!.id, selectedInterval)
                        navController.popBackStack()
                    } else {
                        Toast.makeText(m_context, "نام و توضیحات را لطفا وارد کنید", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = medicine != null
            ) {
                Text("ذخیره یادآوری", fontFamily = vazirFontFamily)
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