package ir.nimaali.medimate.ui.screens


import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ir.nimaali.medimate.R
import ir.nimaali.medimate.ui.theme.vazirFontFamily
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SplashScreen(navController: NavController) {
    Scaffold(
        topBar = {

        },
        modifier = Modifier.fillMaxSize(),
        content = {
            LaunchedEffect(true) {
                delay(1700)
                navController.navigate("medicineList"){
                    popUpTo(0)
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF4CAF50))
                    .padding(20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_medication_liquid_24),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(140.dp) // You can customize the size
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "دارویار", fontSize = 50.sp, color = Color.White, fontFamily = vazirFontFamily)
                Spacer(modifier = Modifier.height(10.dp))
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(100.dp))
                Text(text = "نیما علی آبادی", fontSize = 14.sp, color = Color.White, fontFamily = vazirFontFamily)


            }
        }
    )
}