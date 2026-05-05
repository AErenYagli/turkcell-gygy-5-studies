package com.turkcell.libraryapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class BorrowRecord(
    val id: String,
    @SerialName("student_id") val studentId: String,
    @SerialName("book_id") val bookId: String,
    @SerialName("borrowed_at") val borrowedAt: String = "", // Date?
    @SerialName("due_date") val dueDate: String = "",
    @SerialName("returned_at") val returnedAt: String? = null
) {
}