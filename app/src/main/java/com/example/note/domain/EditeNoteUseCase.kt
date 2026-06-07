package com.example.note.domain

class EditeNoteUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(note: Note) {
        repository.editNote(note)
    }
}