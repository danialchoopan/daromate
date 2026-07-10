package ir.danialchoopan.medimate.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class ButtonStyle { Primary, Secondary, Text }

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "buttonScale"
    )

    val buttonContent: @Composable () -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = when (style) {
                    ButtonStyle.Primary -> MaterialTheme.colorScheme.onPrimary
                    ButtonStyle.Secondary -> MaterialTheme.colorScheme.primary
                    ButtonStyle.Text -> MaterialTheme.colorScheme.primary
                }
            )
        } else {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            }
            Text(text = text)
        }
    }

    when (style) {
        ButtonStyle.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier.scale(scale),
                enabled = enabled && !loading,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                content = buttonContent
            )
        }
        ButtonStyle.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.scale(scale),
                enabled = enabled && !loading,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                content = buttonContent
            )
        }
        ButtonStyle.Text -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.scale(scale),
                enabled = enabled && !loading,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                content = buttonContent
            )
        }
    }
}
