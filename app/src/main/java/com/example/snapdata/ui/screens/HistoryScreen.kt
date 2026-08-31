package com.example.snapdata.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.data.DocumentEntity
import com.example.snapdata.model.DocumentType
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.theme.AccentGreen
import com.example.snapdata.ui.theme.AccentRed
import com.example.snapdata.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val documents by viewModel.savedDocuments.collectAsState()
    var searchInput by remember { mutableStateOf(uiState.searchQuery) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Document Archive", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_history")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.ACQUISITION) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("history_new_scan_btn")
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "New Scan")
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            val isTabletWide = maxWidth >= 650.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = {
                        searchInput = it
                        viewModel.setSearchQuery(it)
                    },
                    placeholder = { Text("Search documents, extracted fields, text...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchInput.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchInput = ""
                                    viewModel.setSearchQuery("")
                                    focusManager.clearFocus()
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("history_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Category Filter Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedDocTypeFilter == null,
                            onClick = { viewModel.setDocTypeFilter(null) },
                            label = { Text("All (${documents.size})") },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                    }
                    items(DocumentType.values()) { type ->
                        FilterChip(
                            selected = uiState.selectedDocTypeFilter == type,
                            onClick = {
                                if (uiState.selectedDocTypeFilter == type) {
                                    viewModel.setDocTypeFilter(null)
                                } else {
                                    viewModel.setDocTypeFilter(type)
                                }
                            },
                            label = { Text(type.displayName) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                    }
                }

                // Document Content / Empty States
                if (documents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (searchInput.isNotBlank()) Icons.Outlined.SearchOff else Icons.Outlined.FolderOff,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchInput.isNotBlank()) "No matching documents found" else "No documents archived yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (searchInput.isNotBlank()) "No documents match '$searchInput'. Try adjusting your search query." else "Scan or import a document to extract structured fields into your local database.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (searchInput.isBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(AppScreen.ACQUISITION) },
                                    modifier = Modifier.heightIn(min = 48.dp).testTag("history_empty_scan_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(Icons.Default.DocumentScanner, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Scan Document")
                                }
                            }
                        }
                    }
                } else if (isTabletWide) {
                    // 2-Column Responsive Grid on Tablets
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(documents) { doc ->
                            DocumentHistoryCard(
                                doc = doc,
                                onOpen = { viewModel.reopenDocument(doc) },
                                onDelete = { viewModel.deleteDocument(doc) }
                            )
                        }
                    }
                } else {
                    // Compact Vertical List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(documents) { doc ->
                            DocumentHistoryCard(
                                doc = doc,
                                onOpen = { viewModel.reopenDocument(doc) },
                                onDelete = { viewModel.deleteDocument(doc) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(40.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentHistoryCard(
    doc: DocumentEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("history_item_${doc.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = doc.docType,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = AccentGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${(doc.overallConfidence * 100).toInt()}% conf",
                            color = AccentGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("delete_doc_btn_${doc.id}")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Document", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = doc.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (doc.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = doc.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(doc.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )

                Text(
                    text = "${doc.getFieldsList().size} fields, ${doc.getTablesList().size} table(s)",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryBlue
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Document?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${doc.title}' from local storage? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    modifier = Modifier.heightIn(min = 48.dp).testTag("confirm_delete_btn_${doc.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
