package com.chatflow.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.TextView
import io.noties.markwon.Markwon
import io.noties.markwon.syntax.highlight.SyntaxHighlightPlugin

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                // Initialize Markwon with Syntax Highlighting
                val markwon = Markwon.builder(context)
                    .usePlugin(SyntaxHighlightPlugin.create())
                    .build()
                markwon.setMarkdown(this, text)
            }
        },
        update = { textView ->
            val markwon = Markwon.create(textView.context)
            markwon.setMarkdown(textView, text)
        }
    )
}
