package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel
import com.turkcell.libraryapp.ui.viewmodel.BookViewModel
import com.turkcell.libraryapp.ui.viewmodel.BorrowState
import com.turkcell.libraryapp.ui.viewmodel.BorrowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel,
    borrowViewModel: BorrowViewModel,
    onNavigateToBorrows: () -> Unit
) {
    val profileState by authViewModel.profile.collectAsState()
    val books by bookViewModel.books.collectAsState()
    val isLoading by bookViewModel.isLoading.collectAsState()
    val borrowState by borrowViewModel.borrowState.collectAsState()
    val borrows by borrowViewModel.borrows.collectAsState()

    var selectedBookForBorrow by remember { mutableStateOf<Book?>(null) }
    var selectedDays by remember { mutableStateOf(3) }
    val snackbarHostState = remember { SnackbarHostState() }

    val borrowedBookIds = borrows
        .filter { it.returnedAt == null }
        .map { it.bookId }
        .toSet()

    LaunchedEffect(profileState?.userId) {
        profileState?.userId?.let { borrowViewModel.loadBorrows(it) }
    }

    LaunchedEffect(borrowState) {
        when (borrowState) {
            is BorrowState.Success -> {
                snackbarHostState.showSnackbar("Kitap başarıyla ödünç alındı! 📚")
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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 40.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "Kütüphane",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = onNavigateToBorrows,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Kiralamalarım")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                books.isEmpty() -> Text("Kitaplar yüklenemedi.")
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books, key = { it.id }) { book ->
                        BookCard(
                            book = book,
                            isBorrowed = book.id in borrowedBookIds,
                            onBorrowClick = { selectedBook ->
                                selectedBookForBorrow = selectedBook
                                selectedDays = 3
                            }
                        )
                    }
                }
            }
        }
    }

    selectedBookForBorrow?.let { book ->
        AlertDialog(
            onDismissRequest = {
                selectedBookForBorrow = null
                borrowViewModel.resetState()
            },
            title = { Text("Ödünç Al") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = book.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = book.author,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Divider()
                    Text("Kaç gün ödünç almak istiyorsunuz?")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { day ->
                            FilterChip(
                                selected = selectedDays == day,
                                onClick = { selectedDays = day },
                                label = { Text("$day gün") }
                            )
                        }
                    }
                    if (borrowState is BorrowState.Error) {
                        Text(
                            text = (borrowState as BorrowState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val studentId = profileState?.userId ?: return@Button
                        borrowViewModel.borrowBook(studentId, book.id, selectedDays)
                        selectedBookForBorrow = null
                    },
                    enabled = borrowState !is BorrowState.Loading
                ) {
                    if (borrowState is BorrowState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Onayla")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedBookForBorrow = null
                    borrowViewModel.resetState()
                }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun BookCard(
    book: Book,
    isBorrowed: Boolean,
    onBorrowClick: (Book) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = book.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = book.author,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (book.category.isNotEmpty()) {
                            BookChip(
                                text = book.category,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        BookChip(
                            text = if (book.avaiableCopies > 0) "Mevcut" else "Tükendi",
                            containerColor = if (book.avaiableCopies > 0)
                                MaterialTheme.colorScheme.tertiaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer,
                            textColor = if (book.avaiableCopies > 0)
                                MaterialTheme.colorScheme.onTertiaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${book.pageCount}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "sayfa",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (book.avaiableCopies > 0) {
                    Button(
                        onClick = { onBorrowClick(book) },
                        enabled = !isBorrowed,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isBorrowed) "Ödünç Alındı" else "Ödünç Al",
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "Stokta Yok",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookChip(
    text: String,
    containerColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 11.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}