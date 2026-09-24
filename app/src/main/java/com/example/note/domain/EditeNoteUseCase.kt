package com.example.note.domain

import javax.inject.Inject

class EditeNoteUseCase @Inject constructor (
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