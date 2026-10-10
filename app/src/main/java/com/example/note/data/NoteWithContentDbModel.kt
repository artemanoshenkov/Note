package com.example.note.data

import ContentItemDbModel
import androidx.room.Embedded
import androidx.room.Relation

/*Класс обобщающий заметку так как у нас две таблицы */
data class NoteWithContentDbModel (
    @Embedded /*говорит room искать по полям модели NoteDbModel а не по полю noteDbModel*/
    val noteDbModel: NoteDbModel,
    /*Вместо написания запроса с join можео использовать Relation передав
    родительское поле первичного ключа и поле связанной сущности указывающее на родителя*/
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val content: List<ContentItemDbModel>

)

