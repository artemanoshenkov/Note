package com.example.note.data

import android.content.Context
import com.example.note.domain.ContentItem
import com.example.note.domain.Note
import com.example.note.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao
) : NotesRepository {




    override suspend fun addNewNote(
        title: String,
        content: List<ContentItem>,
        updateAt: Long,
        isPinned: Boolean
    ) {
        val newNote = Note(
            id = 0,
            title = title,
            content = content,
            updateAt = updateAt,
            isPinned = isPinned
        )
        notesDao.addNote(newNote.toDbModel())
    }

    override suspend fun deleteNote(noteId: Int) {
        notesDao.deleteNote(noteId)
    }

    override suspend fun editNote(note: Note) {
        notesDao.addNote(note.toDbModel())
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { it.toEntities() }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }

//    companion object {
//
//        var instance: NotesRepositoryImpl? = null
//        private val LOC = Any()
//
//        fun getInstance(notesDatabase: NotesDatabase): NotesRepositoryImpl {
//            instance?.let { return it }
//
//            synchronized(LOC) {
//                instance?.let { return it }
//
//                return NotesRepositoryImpl().also {instance = it}
//            }
//        }
//
//    }
}