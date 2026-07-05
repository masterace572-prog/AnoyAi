package com.chatflow.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chatflow.domain.model.AiModel

@Composable
fun ModelPicker(
    models: List<AiModel>,
    selectedModel: AiModel?,
    onModelSelected: (AiModel) -> Unit,
    freeOnly: Boolean,
    onFreeOnlyChanged: (Boolean) -> Unit
) {
    var selectedTab by remember { mutableStateOf("All") }
    val providers = listOf("All", "gemini", "groq", "deepseek", "openrouter")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Select Model", style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Free Only", style = MaterialTheme.typography.bodySmall)
                Switch(checked = freeOnly, onCheckedChange = onFreeOnlyChanged)
            }
        }

        ScrollableTabRow(
            selectedTabIndex = providers.indexOf(selectedTab),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            providers.forEach { p ->
                Tab(
                    selected = (selectedTab == p),
                    onClick = { selectedTab = p },
                    text = { Text(p.replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        val filteredModels = models.filter { model ->
            val matchesTab = (selectedTab == "All" || model.providerId == selectedTab)
            val matchesFree = if (freeOnly) model.isFree else true
            matchesTab && matchesFree
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredModels) { model ->
                ModelItem(model, isSelected = (model == selectedModel), onClick = { onModelSelected(model) })
            }
        }
    }
}

@Composable
fun ModelItem(model: AiModel, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = model.displayName, style = MaterialTheme.typography.titleMedium)
                Text(text = "${model.providerId} • ${model.contextLength} tokens", style = MaterialTheme.typography.bodySmall)
            }
            if (model.isFree) {
                Badge { Text("Free") }
            }
        }
    }
}
