package com.example.note.data

import ContentItemDbModel
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.example.note.domain.ContentItem
import kotlinx.coroutines.flow.Flow


@Dao // обьект доступа к базе
interface NotesDao {
    @Transaction /*так как при этом запросе происходит обращение к 2 таблицам*/
    @Query("SELECT * FROM notes ORDER BY updateAt DESC")
    fun getAllNotes(): Flow<List<NoteWithContentDbModel>>

    @Query("SELECT * FROM notes WHERE id == :noteId")
    suspend fun getNote(noteId: Int): NoteWithContentDbModel

    @Query(
        """SELECT DISTINCT notes.* FROM notes JOIN content ON content.noteId == notes.id
    WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' 
    ORDER BY updateAt DESC"""
    )
    fun searchNotes(query: String): Flow<List<NoteWithContentDbModel>>

    @Transaction
    @Query("DELETE FROM notes WHERE id == :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId")
    suspend fun switchPinnedStatus(noteId: Int)

    @Insert(onConflict = REPLACE)
    suspend fun addNote(noteDbModel: NoteDbModel): Long /*база будет возвращать сгенерированный id*/

    @Insert(onConflict = REPLACE)
    suspend fun addNoteContent(content: List<ContentItemDbModel>)

    @Query("DELETE FROM content WHERE noteId == :noteId")
    suspend fun deleteNoteContent(noteId: Int)

    @Transaction/*для выполнения действий в рамках одной транзакции*/
    suspend fun addNoteWithContent(
        noteDbModel: NoteDbModel,
        content: List<ContentItem>
    ) {
        val noteId = addNote(noteDbModel).toInt()

        val contentItems = content.toContentItemDbModel(noteId = noteId)
        addNoteContent(contentItems)
    }

    @Transaction
    suspend fun updateNote(
        noteDbModel: NoteDbModel,
        content: List<ContentItemDbModel>
    ) {
        addNote(noteDbModel)
        deleteNoteContent(noteDbModel.id)
        addNoteContent(content)
    }

}