package com.example.note.data

import kotlinx.serialization.Serializable

/*Для получения возможности преобразования коллекций обьектов ContentItemDbModel в строку json и обратно
* используем анотаццию Serializable на все*/
@Serializable /*Для того что бы обазначить что он сериализуемый*/
sealed interface ContentItemDbModel {
    @Serializable
    data class Text(val content: String): ContentItemDbModel
    @Serializable
    data class Image(val url: String): ContentItemDbModel
}