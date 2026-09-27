/** функции расширения для преобразования одного обьекта в другой **/

package com.example.note.data

import com.example.note.domain.ContentItem
import com.example.note.domain.Note
import kotlinx.serialization.json.Json

fun Note.toDbModel(): NoteDbModel {
    /*преобразования коллекции обьектов в строку*/
    val contentAsString = Json.encodeToString(this.content.toContentItemDbModel())
    return NoteDbModel(
        id,
        title,
        contentAsString,
        updateAt,
        isPinned
    )
}

/*Сериализация списка с интерфейсом ContentItem в json строку для передачи ее в NoteDbModel
* который в качестве контента должен содержать Sting для возможности хранения в бд*/
fun List<ContentItem>.toContentItemDbModel(): List<ContentItemDbModel> {
    return map { contentItem ->
        when (contentItem) {
            is ContentItem.Text -> {
                ContentItemDbModel.Text(contentItem.content)
            }

            is ContentItem.Image -> {
                ContentItemDbModel.Image(contentItem.url)
            }
        }
    }
}

fun NoteDbModel.toEntity(): Note {
    /*Получение коллекции обьектов из строки полученной из БД*/
    val contentItemDbModel = Json.decodeFromString<List<ContentItemDbModel>>(content)
    return Note(
        id,
        title,
        contentItemDbModel.toContentItems(),
        updateAt,
        isPinned
    )
}

/*Сериализация обьектов ContentItemDbModel полученных из строки полученной из БД
в коллекцию обьектов ContentItem для того что бы domain слой оставался независимым*/
fun List<ContentItemDbModel>.toContentItems(): List<ContentItem> {
    return map { contentItem ->
        when (contentItem) {
            is ContentItemDbModel.Text -> {
                ContentItem.Text(contentItem.content)
            }

            is ContentItemDbModel.Image -> {
                ContentItem.Image(contentItem.url)
            }
        }
    }
}


fun List<NoteDbModel>.toEntities(): List<Note> {
    return this.map {
        it.toEntity()
    }
}