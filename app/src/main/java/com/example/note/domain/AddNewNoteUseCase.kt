package com.example.note.domain

class AddNewNoteUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(
         title: String,
         content: String
    ) {
        repository.addNewNote(title, content)
    }
}