package com.example.note.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun addNewNote(
        title: String,
        content: String,
        updateAt: Long,
        isPinned: Boolean
    )

    suspend fun deleteNote(noteId: Int)

    suspend fun editNote(note: Note)

    fun getAllNotes(): Flow<List<Note>>

    suspend fun getNote(noteId: Int): Note

    fun searchNotes(query: String): Flow<List<Note>>

    suspend fun switchPinnedStatus(noteId: Int)

}