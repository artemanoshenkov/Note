package com.example.note.data

import android.content.Context
import com.example.note.domain.Note
import com.example.note.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl private constructor(context: Context) : NotesRepository {

    private val notesDatabase = NotesDatabase.getInstance(context = context)
    private val notesDao = notesDatabase.notesDao()

    override suspend fun addNewNote(
        title: String,
        content: String,
        updateAt: Long,
        isPinned: Boolean
    ) {
        val newNote = NoteDbModel(
            id = 0,
            title = title,
            content = content,
            updateAt = updateAt,
            isPinned = isPinned
        )
        notesDao.addNote(newNote)
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

    companion object {

        var instance: NotesRepositoryImpl? = null
        private val LOC = Any()

        fun getInstance(content: Context): NotesRepositoryImpl {
            instance?.let { return it }

            synchronized(LOC) {
                instance?.let { return it }

                return NotesRepositoryImpl(content).also {instance = it}
            }
        }

    }
}