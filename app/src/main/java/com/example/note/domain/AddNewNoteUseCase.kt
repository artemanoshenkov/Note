package com.example.note.domain

import javax.inject.Inject

class AddNewNoteUseCase @Inject constructor (
    private val repository: NotesRepository
) {

    suspend operator fun invoke(
         title: String,
         content: String
    ) {
        repository.addNewNote(
            title=title,
            content=content,
            isPinned = false,
            updateAt = System.currentTimeMillis())
    }
}