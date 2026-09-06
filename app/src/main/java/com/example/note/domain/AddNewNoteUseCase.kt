package com.example.note.domain

class AddNewNoteUseCase(
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