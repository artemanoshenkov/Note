package com.example.note.data
/**создание базы данных*/
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = arrayOf(NoteDbModel::class),
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase: RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {
        private var instance: NotesDatabase? = null

        private val LOCK = Any()

        fun getInstance(context: Context): NotesDatabase {

            if(instance != null) {
                return instance!!
            }

            synchronized(LOCK) {
                if(instance != null) {
                    return instance!!
                }

                return Room.databaseBuilder(
                    context = context,
                    klass = NotesDatabase::class.java,
                    name = "notes.db"
                ).build().also { instance = it }
            }

        }
    }
}