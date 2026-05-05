package com.turkcell.libraryapp.data.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class BorrowRecord(
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val id: String? = null,

    @SerialName("student_id") val studentId: String,
    @SerialName("book_id") val bookId: String,

    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("borrowed_at") val borrowedAt: String = "",

    @SerialName("due_date") val dueDate: String = "",
    @SerialName("returned_at") val returnedAt: String? = null
)
