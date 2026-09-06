/** Создания модели для БД **/

package com.example.note.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes") //для преобразования data class в таблицу
data class NoteDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val content: String,
    val updateAt: Long,
    val isPinned: Boolean
)
