package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.libraryapp.data.model.BorrowRecord
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel
import com.turkcell.libraryapp.ui.viewmodel.BorrowState
import com.turkcell.libraryapp.ui.viewmodel.BorrowViewModel

@Composable
fun BorrowsScreen(
    authViewModel: AuthViewModel,
    borrowViewModel: BorrowViewModel
) {
    val profileState by authViewModel.profile.collectAsState()
    val borrows by borrowViewModel.borrows.collectAsState()
    val borrowState by borrowViewModel.borrowState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(profileState?.userId) {
        profileState?.userId?.let { borrowViewModel.loadBorrows(it) }
    }

    LaunchedEffect(borrowState) {
        when (borrowState) {
            is BorrowState.Success -> {
                snackbarHostState.showSnackbar("Kitap başarıyla iade edildi!")
                borrowViewModel.resetState()
            }
            is BorrowState.Error -> {
                snackbarHostState.showSnackbar(
                    (borrowState as BorrowState.Error).message
                )
                borrowViewModel.resetState()
            }
            else -> Unit
        }
    }

    val activeBorrows = borrows.filter { it.returnedAt == null }
    val pastBorrows = borrows.filter { it.returnedAt != null }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Aktif (${activeBorrows.size})", "Geçmiş (${pastBorrows.size})")

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Kiralamalarım",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when {
                borrowState is BorrowState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    val currentList = if (selectedTab == 0) activeBorrows else pastBorrows

                    if (currentList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (selectedTab == 0)
                                    "Aktif kiralamanız bulunmuyor."
                                else
                                    "Geçmiş kiralamanız bulunmuyor.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(currentList, key = { it.id!! }) { record ->
                                BorrowCard(
                                    record = record,
                                    isActive = selectedTab == 0,
                                    onReturnClick = {
                                        profileState?.userId?.let { userId ->
                                            borrowViewModel.returnBook(record.id!!, userId)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BorrowCard(
    record: BorrowRecord,
    isActive: Boolean,
    onReturnClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Kitap ID: ${record.bookId}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )


            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DateInfo(label = "Alınma", date = record.borrowedAt)
                DateInfo(label = "Son Teslim", date = record.dueDate)
                if (!isActive && record.returnedAt != null) {
                    DateInfo(label = "İade", date = record.returnedAt)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = if (isActive)
                        MaterialTheme.colorScheme.tertiaryContainer
                    else
                        MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = if (isActive) "Aktif" else "İade Edildi",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isActive)
                            MaterialTheme.colorScheme.onTertiaryContainer
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                if (isActive) {
                    Button(
                        onClick = onReturnClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("İade Et", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DateInfo(label: String, date: String) {
    val formatted = runCatching {
        val parts = date.substring(0, 10).split("-")
        "${parts[2]}.${parts[1]}.${parts[0]}"
    }.getOrDefault(date)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formatted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}