package ir.nimaali.medimate.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ir.nimaali.medimate.R


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val vazirFontFamily = FontFamily(
    Font(R.font.vazir)
)

val vazirTypography = Typography(
    // Customize the specific text styles you need, e.g.,
    titleLarge = TextStyle(
        fontFamily = vazirFontFamily,
        // Other style properties like fontSize, fontWeight, etc.
    ),
    bodyLarge = TextStyle(
        fontFamily = vazirFontFamily,
        // Other style properties
    ),
    // ... other text styles
)