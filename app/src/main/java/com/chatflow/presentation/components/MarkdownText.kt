package com.chatflow.presentation.components

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                Markwon.create(context).setMarkdown(this, text)
            }
        },
        update = { textView ->
            Markwon.create(textView.context).setMarkdown(textView, text)
        }
    )
}