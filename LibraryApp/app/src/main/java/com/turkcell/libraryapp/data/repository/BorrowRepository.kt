package com.turkcell.libraryapp.data.repository

import com.turkcell.libraryapp.data.model.BorrowRecord
import com.turkcell.libraryapp.data.supabase.supabase
import io.github.jan.supabase.postgrest.postgrest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class BorrowRepository {

    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }

    suspend fun borrowBook(
        studentId: String,
        bookId: String,
        days: Int
    ): Result<Unit> = runCatching {
        val now = Date()
        val calendar = Calendar.getInstance().apply {
            time = now
            add(Calendar.DAY_OF_MONTH, days)
        }
        val dueDate = calendar.time

        val record = BorrowRecord(
            id = null,
            studentId = studentId,
            bookId = bookId,
            dueDate = formatDate(dueDate),
            returnedAt = null
        )

        supabase.postgrest["borrow_records"].insert(record)
    }

    suspend fun getBorrowsByStudent(studentId: String): Result<List<BorrowRecord>> = runCatching {
        supabase.postgrest["borrow_records"]
            .select { filter { eq("student_id", studentId) } }
            .decodeList<BorrowRecord>()
    }

    suspend fun returnBook(recordId: String): Result<Unit> = runCatching {
        val now = formatDate(Date())
        supabase.postgrest["borrow_records"]
            .update({ set("returned_at", now) }) {
                filter { eq("id", recordId) }
            }
    }
}