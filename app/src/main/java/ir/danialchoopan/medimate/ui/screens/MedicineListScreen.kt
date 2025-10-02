package ir.nimaali.medimate.ui.screens

import android.app.Activity
import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.ui.theme.vazirFontFamily
import ir.nimaali.medimate.util.DateTimeUtils
import ir.nimaali.medimate.viewmodel.MedicineViewModel
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineListScreen(
    viewModel: MedicineViewModel,
    navController: NavController,
) {
    val medicines = viewModel.medicines.collectAsState().value
    var expanded by remember {
        mutableStateOf(false)
    }

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
                            painter = painterResource(id = R.drawable.baseline_medical_information_24), // your logo in drawable
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "مدیریت داروها",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            fontFamily = vazirFontFamily
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "منو",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("درباره من", fontFamily = vazirFontFamily) },
                            onClick = {
                                expanded = false
                                navController.navigate("about")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("خروج", fontFamily = vazirFontFamily) },
                            onClick = {
                                expanded = false
                                navController.popBackStack()
                                exitProcess(0)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addMedicine") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "افزودن دارو")
            }
        },
    ) { padding ->
        if (medicines.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    "دارو برای یادآوری موجود نیست",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontFamily = vazirFontFamily
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(medicines) { medicine ->
                    MedicineItem(
                        medicine = medicine,
                        onClick = { navController.navigate("medicineDetail/${medicine.id}") }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun MedicineItem(medicine: Medicine, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(9.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = vazirFontFamily
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "شروع از: ${DateTimeUtils.timestampToPersianDate(medicine.startDate)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = vazirFontFamily
                )
            }

            Image(
                painter = painterResource(id = R.drawable.baseline_medical_services_24_black), // your logo in drawable
                contentDescription = "Logo",
                modifier = Modifier
                    .size(58.dp)
                    .padding(end = 10.dp)
            )

        }
    }
}