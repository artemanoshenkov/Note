package com.example.note.domain


/*Для добавления в базу данных коллекции обьектов(контента изображений....) */
sealed interface Content {

    data class Text(val content: String)

    data class Image(val url: String)

}