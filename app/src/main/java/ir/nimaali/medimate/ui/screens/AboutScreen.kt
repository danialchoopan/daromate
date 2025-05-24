package ir.nimaali.medimate.ui.screens


import android.app.Activity
import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ir.nimaali.medimate.R
import ir.nimaali.medimate.data.table.Medicine
import ir.nimaali.medimate.ui.theme.vazirFontFamily
import ir.nimaali.medimate.util.DateTimeUtils
import ir.nimaali.medimate.viewmodel.MedicineViewModel
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "درباره ما",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = vazirFontFamily
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بازگشت",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_medical_services_24_black),
                contentDescription = "لوگو",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "دارویار",
                fontSize = 42.sp,
                color = Color.Black,
                fontFamily = vazirFontFamily,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "درباره سرویس ما",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = vazirFontFamily,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = """
                    دارویار یک اپلیکیشن ساده و کاربردی برای مدیریت داروها و یادآوری مصرف آن‌هاست.
                    این برنامه برای افرادی طراحی شده که می‌خواهند داروهای خود یا عزیزانشان را به‌موقع و بدون فراموشی مصرف کنند.

                    هدف ما کمک به بهبود سلامت شما از طریق یک رابط کاربری ساده و تجربه‌ای روان است.
                """.trimIndent(),
                fontSize = 16.sp,
                fontFamily = vazirFontFamily,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ساخته شده توسط: نیماعلی آبادی",
                fontSize = 18.sp,
                fontFamily = vazirFontFamily
            )
        }
    }
}
