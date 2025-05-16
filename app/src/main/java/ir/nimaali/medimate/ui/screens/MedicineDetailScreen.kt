package ir.nimaali.medimate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ir.nimaali.medimate.data.dao.MedicineDao
import ir.nimaali.medimate.data.dao.ReminderDao
import ir.nimaali.medimate.data.table.IntervalType
import ir.nimaali.medimate.data.table.Reminder
import ir.nimaali.medimate.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    medicineId: Int,
    medicineDao: MedicineDao,
    reminderDao: ReminderDao,
    navController: NavController
) {
    val medicine by medicineDao.getMedicineById(medicineId).collectAsState(initial = null)
    val reminders by reminderDao.getRemindersForMedicine(medicineId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(medicine?.name ?: "جزئیات دارو") },
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
        if (medicine == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("دارو یافت نشد")
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
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = medicine!!.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "شروع از: ${DateTimeUtils.timestampToPersianDate(medicine!!.startDate)}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Divider()
                }

                item {
                    Text(
                        text = "یادآوری‌ها",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                items(reminders) { reminder ->
                    ReminderItem(reminder = reminder)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder) {
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
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "یادآوری بعدی: ${DateTimeUtils.timestampToPersianDateTime(reminder.nextReminderTime)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Switch(
                    checked = reminder.isActive,
                    onCheckedChange = { /* TODO: Update reminder status */ }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (reminder.isActive) "فعال" else "غیرفعال",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}