package com.turkcell.libraryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.libraryapp.data.model.BorrowRecord
import com.turkcell.libraryapp.data.repository.BorrowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class BorrowState {
    object Idle : BorrowState()
    object Loading : BorrowState()
    object Success : BorrowState()
    data class Error(val message: String) : BorrowState()
}

class BorrowViewModel : ViewModel() {
    private val repository = BorrowRepository()

    private val _borrowState = MutableStateFlow<BorrowState>(BorrowState.Idle)
    val borrowState: StateFlow<BorrowState> = _borrowState

    private val _borrows = MutableStateFlow<List<BorrowRecord>>(emptyList())
    val borrows: StateFlow<List<BorrowRecord>> = _borrows

    fun borrowBook(studentId: String, bookId: String, days: Int) {
        viewModelScope.launch {
            _borrowState.value = BorrowState.Loading
            repository.borrowBook(studentId, bookId, days)
                .onSuccess { _borrowState.value = BorrowState.Success }
                .onFailure { ex -> _borrowState.value = BorrowState.Error(ex.message ?: "Kiralama başarısız") }
        }
    }

    fun loadBorrows(studentId: String) {
        viewModelScope.launch {
            _borrowState.value = BorrowState.Loading
            repository.getBorrowsByStudent(studentId)
                .onSuccess { records ->
                    _borrows.value = records
                    _borrowState.value = BorrowState.Idle
                }
                .onFailure { ex -> _borrowState.value = BorrowState.Error(ex.message ?: "Yüklenemedi") }
        }
    }

    fun returnBook(recordId: String, studentId: String) {
        viewModelScope.launch {
            _borrowState.value = BorrowState.Loading
            repository.returnBook(recordId)
                .onSuccess {
                    loadBorrows(studentId)
                }
                .onFailure { ex -> _borrowState.value = BorrowState.Error(ex.message ?: "İade başarısız") }
        }
    }

    fun resetState() {
        _borrowState.value = BorrowState.Idle
    }
}