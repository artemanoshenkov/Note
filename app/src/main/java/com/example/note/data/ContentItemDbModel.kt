import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.note.data.NoteDbModel


@Entity(
    tableName = "content",
    /*состовной первичный ключ, состоит из 2 полей*/
    primaryKeys = ["noteId", "order"],
    /*внешний ключ*/
    foreignKeys = [
        ForeignKey(
            /*сущность на которую ссылается*/
            entity = NoteDbModel::class,
            /*поле ключа родителя*/
            parentColumns = ["id"],
            /*поле ключа зависимости*/
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ContentItemDbModel(
    val noteId: Int,
    val contentType: ContentType,
    val content: String,
    val order: Int
)

enum class ContentType() {
    TEXT, IMAGE
}



/*
package com.example.note.data

import kotlinx.serialization.Serializable

*/
/*Для получения возможности преобразования коллекций обьектов ContentItemDbModel в строку json и обратно
* используем анотаццию Serializable на все*//*

@Serializable */
/*Для того что бы обазначить что он сериализуемый*//*

sealed interface ContentItemDbModel {
    @Serializable
    data class Text(val content: String): ContentItemDbModel
    @Serializable
    data class Image(val url: String): ContentItemDbModel
}*/
