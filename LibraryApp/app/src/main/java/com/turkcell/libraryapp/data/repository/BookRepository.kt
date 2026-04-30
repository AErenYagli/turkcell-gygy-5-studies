package com.turkcell.libraryapp.data.repository

import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.data.supabase.supabase
import io.github.jan.supabase.postgrest.postgrest

class BookRepository {
    suspend fun getAllBooks(): Result<List<Book>> = runCatching {
        supabase.postgrest["books"]
            .select()
            .decodeList<Book>()
    }

    suspend fun getBookById(id:String): Result<Book> = runCatching {
        supabase.postgrest["books"]
            .select { filter { eq("id",id) } }
            .decodeSingle<Book>()
    }

    suspend fun addBook(book: Book): Result<Unit> = runCatching {
        supabase.postgrest["books"].insert(book)
    }

    // ÖDEV 2: BookRepository Güncelleme, silme, arama fonksiyonlarını tanımla.
    suspend fun updateBook(book: Book): Result<Unit> = runCatching {
        supabase.postgrest["books"]
            .update(book) { filter { eq("id", book.id)}  }
    }

    suspend fun deleteBook(id: String): Result<Unit> = runCatching {
        supabase.postgrest["books"]
            .delete { filter { eq("id", id) } }
    }

    suspend fun searchBooks(
        query: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<Book>> {


        val trimmed = query.trim()
        if (trimmed.isBlank()) return Result.success(emptyList())

        return runCatching {
            supabase.postgrest["books"]
                .select {
                    filter {
                        or {
                            ilike("title", "%$trimmed%")
                            ilike("author", "%$trimmed%")
                            ilike("isbn", "%$trimmed%")
                            ilike("category", "%$trimmed%")
                        }
                    }
                    limit(limit.toLong())
                    range(offset.toLong(), (offset + limit - 1).toLong())
                }
                .decodeList<Book>()
        }
    }
 }