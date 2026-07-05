package com.chatflow.presentation.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CodeBlockView(
    code: String,
    language: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color(0xFF1E1E1E),
                RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF333333),
                    RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp
                    )
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = language.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray,
                fontSize = 11.sp
            )

            IconButton(
                modifier = Modifier.size(24.dp),
                onClick = {
                    val clipboard = context.getSystemService(
                        Context.CLIPBOARD_SERVICE
                    ) as ClipboardManager

                    clipboard.setPrimaryClip(
                        ClipData.newPlainText("code", code)
                    )

                    copied = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = if (copied) Color.Green else Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = code,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                lineHeight = 18.sp
            ),
            color = Color(0xFFD4D4D4)
        )
    }
}