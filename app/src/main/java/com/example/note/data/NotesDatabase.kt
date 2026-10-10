package com.example.note.data

/**создание базы данных*/
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = arrayOf(NoteDbModel::class, ContentItemDbModel::class),
    version = 2 /*при обновлении структуры базы данных указываем новую версию "2"
    а при создании базы данных Room.databaseBuilder указываем стратегию (что делать при обновлении версии БД)*/,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {
        private var instance: NotesDatabase? = null

        private val LOCK = Any()

        fun getInstance(context: Context): NotesDatabase {

            if (instance != null) {
                return instance!!
            }

            synchronized(LOCK) {
                if (instance != null) {
                    return instance!!
                }

                return Room.databaseBuilder(
                    context = context,
                    klass = NotesDatabase::class.java,
                    name = "notes.db"

                ).fallbackToDestructiveMigration(dropAllTables = true)
                    /*.fallbackToDestructiveMigration(dropAllTables = true) - миграции не нужно делать просто удалить старую БД и создать новую*/
                    .build().also { instance = it }
            }

        }
    }
}