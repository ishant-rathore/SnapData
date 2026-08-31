package com.example.snapdata.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.data.DocumentEntity
import com.example.snapdata.model.DocumentType
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.components.SnapDataDocumentCard
import com.example.snapdata.ui.components.SnapDataEmptyState
import com.example.snapdata.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: SnapDataViewModel) {
    val documents by viewModel.savedDocuments.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<DocumentType?>(null) }
    var docToDelete by remember { mutableStateOf<DocumentEntity?>(null) }
    val context = LocalContext.current

    val filteredDocs = documents.filter { doc ->
        val matchesQuery = searchQuery.isBlank() ||
                doc.title.contains(searchQuery, ignoreCase = true) ||
                doc.summary.contains(searchQuery, ignoreCase = true) ||
                doc.rawOcrText.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == null || doc.getTypedDocType() == selectedFilter
        matchesQuery && matchesFilter
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }

    Scaffold(
        containerColor = WarmCreamBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Document Archive",
                        fontWeight = FontWeight.Bold,
                        color = SnapDataBlack,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmCreamBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Input Box
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x06000000))
                        .testTag("history_search_input"),
                    placeholder = { Text("Search title, OCR text, or fields...", fontSize = 13.sp, color = TextSecondary) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, contentDescription = "Search", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite,
                        focusedBorderColor = SnapDataRed,
                        unfocusedBorderColor = LightBorder
                    ),
                    singleLine = true
                )
            }

            // Category Filter Pills
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterPill(
                            label = "All (${documents.size})",
                            isSelected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            testTag = "filter_all"
                        )
                    }
                    items(DocumentType.entries) { type ->
                        val count = documents.count { it.getTypedDocType() == type }
                        if (count > 0 || documents.isEmpty()) {
                            FilterPill(
                                label = "${type.displayName} ($count)",
                                isSelected = selectedFilter == type,
                                onClick = { selectedFilter = if (selectedFilter == type) null else type },
                                testTag = "filter_${type.name.lowercase()}"
                            )
                        }
                    }
                }
            }

            // Content List or Empty State
            if (filteredDocs.isEmpty()) {
                item {
                    SnapDataEmptyState(
                        title = if (searchQuery.isNotEmpty()) "No matching documents" else "No saved documents yet",
                        subtitle = if (searchQuery.isNotEmpty()) "Try searching for a different keyword or removing category filters." else "Scan paper documents, receipts, or invoices to start building your archive.",
                        actionLabel = "Scan Document",
                        onAction = { viewModel.navigateTo(AppScreen.ACQUISITION) }
                    )
                }
            } else {
                item {
                    Text(
                        text = "Saved Records (${filteredDocs.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SnapDataBlack,
                        fontSize = 15.sp
                    )
                }

                items(filteredDocs, key = { it.id }) { doc ->
                    var showItemMenu by remember { mutableStateOf(false) }

                    Box {
                        SnapDataDocumentCard(
                            title = doc.title,
                            dateFormatted = dateFormatter.format(Date(doc.createdAt)),
                            docType = doc.getTypedDocType(),
                            onClick = {
                                viewModel.reopenDocument(doc)
                            },
                            onMenuClick = { showItemMenu = true },
                            testTag = "history_doc_card_${doc.id}"
                        )

                        DropdownMenu(
                            expanded = showItemMenu,
                            onDismissRequest = { showItemMenu = false },
                            modifier = Modifier.background(CardWhite)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Open & Edit", color = SnapDataBlack, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null, tint = SnapDataBlack, modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    showItemMenu = false
                                    viewModel.reopenDocument(doc)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export Document", color = SnapDataBlack, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = SnapDataBlack, modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    showItemMenu = false
                                    viewModel.reopenDocument(doc)
                                    viewModel.navigateTo(AppScreen.EXPORT)
                                }
                            )
                            Divider(color = SubtleBorder)
                            DropdownMenuItem(
                                text = { Text("Delete", color = SnapDataRed, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = SnapDataRed, modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    showItemMenu = false
                                    docToDelete = doc
                                }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Delete Confirmation Dialog
    if (docToDelete != null) {
        AlertDialog(
            onDismissRequest = { docToDelete = null },
            title = { Text("Delete Document?", fontWeight = FontWeight.Bold, color = SnapDataBlack) },
            text = { Text("Are you sure you want to permanently delete \"${docToDelete?.title}\"? This action cannot be undone.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        val toRemove = docToDelete
                        docToDelete = null
                        if (toRemove != null) {
                            viewModel.deleteDocument(toRemove)
                            Toast.makeText(context, "Document deleted", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed)
                ) {
                    Text("Delete", color = CardWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { docToDelete = null }) {
                    Text("Cancel", color = TextDark)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) SnapDataRed else CardWhite)
            .border(1.dp, if (isSelected) SnapDataRed else LightBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) CardWhite else TextDark
        )
    }
}
