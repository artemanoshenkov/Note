package com.example.note.domain

/*Для того что бы Domain слой оставался независимым а мы могли хранить в БД различные обьекты
* в Data слое будет происходить преобразования коллкций ContentItem в строку json и передачи ее в NoteDbModel
* для дальнейшей записи в БД и наоборот строка из БД будет преобразованна
* в коллекцию обьектов ContentItemDbModel data слоя
* которые будут преобразованны в коллекцию обьектов ContentItem domain слоя*/
sealed interface ContentItem {

    data class Text(val content: String): ContentItem

    data class Image(val url: String): ContentItem
}