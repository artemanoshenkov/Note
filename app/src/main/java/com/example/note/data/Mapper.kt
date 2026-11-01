/** функции расширения для преобразования одного обьекта в другой **/

package com.example.note.data

import ContentItemDbModel
import ContentType
import com.example.note.domain.ContentItem
import com.example.note.domain.Note
import kotlinx.serialization.json.Json

fun Note.toDbModel(): NoteDbModel {
    /*преобразования коллекции обьектов в строку*/
    /*val contentAsString = Json.encodeToString(this.content.toContentItemDbModel())*/
    return NoteDbModel(
        id,
        title,
        updateAt,
        isPinned
    )
}

/*Сериализация списка с интерфейсом ContentItem в json строку для передачи ее в NoteDbModel
* который в качестве контента должен содержать Sting для возможности хранения в бд*/
fun List<ContentItem>.toContentItemDbModel(noteId: Int): List<ContentItemDbModel> {
    return mapIndexed { index, contentItem ->
        when (contentItem) {
            is ContentItem.Text -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.TEXT,
                    content = contentItem.content,
                    order = index
                )
            }

            is ContentItem.Image -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.IMAGE,
                    content = contentItem.url,
                    order = index
                )
            }
        }
    }
}

fun NoteWithContentDbModel.toEntity(): Note {
    /*Получение коллекции обьектов из строки полученной из БД*/
/*    val contentItemDbModel = Json.decodeFromString<List<ContentItemDbModel>>(content)*/
    return Note(
        noteDbModel.id,
        noteDbModel.title,
        content.toContentItems(),
        noteDbModel.updateAt,
        noteDbModel.isPinned
    )
}

/*Сериализация обьектов ContentItemDbModel полученных из строки полученной из БД
в коллекцию обьектов ContentItem для того что бы domain слой оставался независимым*/
fun List<ContentItemDbModel>.toContentItems(): List<ContentItem> {
    return map { contentItem ->
        when (contentItem.contentType) {
            ContentType.TEXT -> {
                ContentItem.Text(contentItem.content)
            }
            ContentType.IMAGE -> {
                ContentItem.Image(contentItem.content)
            }
        }
    }
}


fun List<NoteWithContentDbModel>.toEntities(): List<Note> {
    return this.map {
        it.toEntity()
    }
}