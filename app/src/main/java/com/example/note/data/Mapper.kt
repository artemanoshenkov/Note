/** функции расширения для преобразования одного обьекта в другой **/

package com.example.note.data
import com.example.note.domain.Note

fun Note.toDbModel(): NoteDbModel {
    return NoteDbModel(
        id,
        title,
        content,
        updateAt,
        isPinned
    )
}

fun NoteDbModel.toEntity(): Note {
    return Note(
        id,
        title,
        content,
        updateAt,
        isPinned
    )
}

fun List<NoteDbModel>.toEntities(): List<Note> {
    return this.map {
        it.toEntity()
    }
}