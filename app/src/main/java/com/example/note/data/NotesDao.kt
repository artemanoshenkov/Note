package com.example.note.data

import ContentItemDbModel
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao // обьект доступа к базе
interface NotesDao {

    @Query("SELECT * FROM notes ORDER BY updateAt DESC")
    fun getAllNotes(): Flow<List<NoteWithContentDbModel>>

    @Query("SELECT * FROM notes WHERE id == :noteId")
    suspend fun getNote(noteId: Int): NoteWithContentDbModel
    @Query("""SELECT DISTINCT notes.* FROM notes JOIN content ON content.noteId == notes.id
    WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' 
    ORDER BY updateAt DESC""")
    fun searchNotes(query: String): Flow<List<NoteDbModel>>

    @Query("DELETE FROM notes WHERE id == :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId")
    suspend fun switchPinnedStatus(noteId: Int)

    @Insert(onConflict = REPLACE)
    suspend fun addNote(noteDbModel: NoteDbModel)

    @Insert(onConflict = REPLACE)
    suspend fun addNoteContent(content: List<ContentItemDbModel>)

    @Query("DELETE FROM content WHERE noteId == :noteId")
    suspend fun deleteNoteContent(noteId: Int)

}