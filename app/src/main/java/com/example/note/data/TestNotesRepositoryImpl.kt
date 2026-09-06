package com.example.note.data

import com.example.note.domain.Note
import com.example.note.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object TestNotesRepositoryImpl : NotesRepository {

    private val testDate = mutableListOf<Note>().apply {
        repeat(10) {
            add(Note(it, "Title $it", "Content $it", System.currentTimeMillis(), isPinned = false))
        }
    }
    private val notesListFlow: MutableStateFlow<List<Note>> = MutableStateFlow(testDate)


    override suspend fun addNewNote(
        title: String,
        content: String,
        updateAt: Long,
        isPinned: Boolean
    ) {
//        val newListNote = notesListFlow.value.toMutableList()
//        newListNote.add(newNote)
//        notesListFlow.value = newListNote


        notesListFlow.update { oldList ->

            val newNote = Note(
                id = oldList.size,
                title = title,
                content = content,
                updateAt = updateAt,
                isPinned = isPinned
            )

            oldList + newNote
        }
    }

    override suspend fun deleteNote(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.toMutableList().apply {
                removeIf {
                    it.id == noteId
                }
            }
        }
    }

    override suspend fun editNote(note: Note) {
        notesListFlow.update { oldList ->
            oldList.map {
                if (it.id == note.id) {
                    note
                } else {
                    it
                }
            }
        }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesListFlow.asStateFlow()
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesListFlow.value.first { it.id == noteId }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesListFlow.map { currentList ->
            currentList.filter {
                it.content.contains(query) || it.title.contains(query)
            }
        }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.map {
                if (it.id == noteId) {
                    it.copy(isPinned = !it.isPinned)
                } else {
                    it
                }
            }
        }
    }
}