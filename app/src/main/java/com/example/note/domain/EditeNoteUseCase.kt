package com.example.note.domain

class EditeNoteUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(note: Note) {
        repository.editNote(
            note.copy(
                updateAt = System.currentTimeMillis()
            )
        )
    }
}