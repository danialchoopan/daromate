package ir.nimaali.medimate.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ir.nimaali.medimate.R
import ir.nimaali.medimate.data.dao.MedicineDao
import ir.nimaali.medimate.data.dao.ReminderDao
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.data.table.Reminder
import ir.nimaali.medimate.ui.ReminderScheduler
import ir.nimaali.medimate.ui.theme.vazirFontFamily
import ir.nimaali.medimate.util.DateTimeUtils
import ir.nimaali.medimate.viewmodel.MedicineViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    viewModel: MedicineViewModel,
    medicineId: Int,
    medicineDao: MedicineDao,
    reminderDao: ReminderDao,
    navController: NavController,
) {
    val medicine by medicineDao.getMedicineById(medicineId).collectAsState(initial = null)
    val reminders by reminderDao.getRemindersForMedicine(medicineId)
        .collectAsState(initial = emptyList())


    var showDeleteConfirmDialog by remember {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()

    val m_context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = medicine?.name + " جزئیات دارو ",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = vazirFontFamily
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showDeleteConfirmDialog = true

                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف دارو",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addReminder/${medicine!!.id}") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "افزودن یادآوری")
            }
        }
    ) { padding ->
        //حذف دارو
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("حذف دارو") },
                text = { Text("آیا از حذف این دارو مطمئن هستید؟") },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.deleteMedicineAndReminders(medicineId)
                        }
                        showDeleteConfirmDialog = false

                        navController.popBackStack()
                    }) {
                        Text("حذف")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("لغو")
                    }
                }
            )
        }

        if (medicine == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "دارو یافت نشد",
                    fontFamily = vazirFontFamily
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = medicine!!.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = vazirFontFamily
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = medicine!!.description,
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = vazirFontFamily
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "شروع از: ${DateTimeUtils.timestampToPersianDate(medicine!!.startDate)}",
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = vazirFontFamily
                        )
                    }
                    Divider()
                }

                item {
                    Text(
                        text = "یادآوری‌ها",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp),
                        fontFamily = vazirFontFamily
                    )
                }

                if (reminders.isEmpty()) {
                    item {
                        Text(
                            "یادآوری برای نمایش موجود نیست",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            fontFamily = vazirFontFamily
                        )
                    }
                } else {

                    items(reminders) { reminder ->
                        ReminderItem(m_context, reminder, medicine!!, viewModel = viewModel)
                        Divider()
                    }
                }

            }
        }
    }
}

@Composable
fun ReminderItem(
    m_context: Context,
    reminder: Reminder,
    medicine: Medicine,
    viewModel: MedicineViewModel,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = when (reminder.intervalType) {
                    IntervalType.MINUTES -> "هر ${reminder.intervalValue} دقیقه"
                    IntervalType.HOURS -> "هر ${reminder.intervalValue} ساعت"
                    IntervalType.DAYS -> "هر ${reminder.intervalValue} روز"
                    IntervalType.WEEKS -> "هر ${reminder.intervalValue} هفته"
                },
                style = MaterialTheme.typography.titleLarge,
                fontFamily = vazirFontFamily
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "یادآوری بعدی: ${DateTimeUtils.timestampToPersianDateTime(reminder.nextReminderTime)}",
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = vazirFontFamily
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceAround) {

                Switch(
                    checked = reminder.isActive,
                    onCheckedChange = {
                        if (reminder.isActive) {
                            viewModel.cancelReminderById(reminder.id)
                            Toast.makeText(m_context, "یادآوری غیرفعال شد", Toast.LENGTH_SHORT)
                                .show()

                        } else {
                            viewModel.enableReminderById(reminder.id)
                            Toast.makeText(m_context, "یادآوری فعال شد", Toast.LENGTH_SHORT)
                                .show()

                        }

                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (reminder.isActive) "فعال" else "غیرفعال",
                        modifier = Modifier.align(Alignment.CenterVertically),
                    fontFamily = vazirFontFamily
                )


                Spacer(modifier = Modifier.width(20.dp))

                IconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = { viewModel.deleteReminderById(reminder.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = Color.Red,
                        modifier = Modifier.size(32.dp)
                    )
                }


            }
        }

    }
}