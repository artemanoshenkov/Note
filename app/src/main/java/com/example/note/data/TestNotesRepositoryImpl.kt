package com.example.note.data

import com.example.note.domain.Note
import com.example.note.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object TestNotesRepositoryImpl : NotesRepository {

    private val notesListFlow: MutableStateFlow<List<Note>> = MutableStateFlow(listOf())



    override fun addNewNote(title: String, content: String) {
//        val newListNote = notesListFlow.value.toMutableList()
//        newListNote.add(newNote)
//        notesListFlow.value = newListNote


        notesListFlow.update {oldList->

            val newNote = Note(id = oldList.size,
                title = title,
                content = content,
                updateAt = System.currentTimeMillis(),
                isPinned = false
            )

            oldList + newNote
        }
    }

    override fun deleteNote(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.toMutableList().apply {
                removeIf {
                    it.id == noteId
                }
            }
        }
    }

    override fun editNote(note: Note) {
        notesListFlow.update {oldList->
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

    override fun getNote(noteId: Int): Note {
        return notesListFlow.value.first { it.id == noteId }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesListFlow.map {currentList ->
            currentList.filter {
                it.content.contains(query) || it.title.contains(query)
            }
        }
    }

    override fun switchPinnedStatus(noteId: Int) {
        notesListFlow.update { oldList->
            oldList.map {
                if(it.id == noteId) {
                    it.copy(isPinned = !it.isPinned)
                } else {
                    it
                }
            }
        }
    }
}