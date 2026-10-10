/** Создания модели для БД **/

package com.example.note.data

import androidx.core.util.Consumer
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.note.domain.Content

@Entity(tableName = "notes") //для преобразования data class в таблицу
data class NoteDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val updateAt: Long,
    val isPinned: Boolean
)
