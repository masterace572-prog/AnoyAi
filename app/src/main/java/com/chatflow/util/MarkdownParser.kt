package com.chatflow.util

data class ChatChunk(
    val text: String,
    val isCode: Boolean = false,
    val language: String = ""
)

object MarkdownParser {
    fun parse(text: String): List<ChatChunk> {
        val chunks = mutableListOf<ChatChunk>()
        val regex = Regex("```(\\w*)\\n?([\\s\\S]*?)```")
        var lastIndex = 0
        
        regex.findAll(text).forEach { match ->
            // Text before the code block
            if (match.range.first > lastIndex) {
                chunks.add(ChatChunk(text.substring(lastIndex, match.range.first)))
            }
            
            // The code block itself
            val lang = match.groupValues[1].ifBlank { "text" }
            val code = match.groupValues[2]
            chunks.add(ChatChunk(code, isCode = true, language = lang))
            
            lastIndex = match.range.last + 1
        }
        
        if (lastIndex < text.length) {
            chunks.add(ChatChunk(text.substring(lastIndex)))
        }
        
        return chunks
    }
}
