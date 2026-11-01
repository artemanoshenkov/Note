package com.example.note.data

import android.content.Context
import com.example.note.domain.ContentItem
import com.example.note.domain.Note
import com.example.note.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val imageFileManager: ImageFileManager
) : NotesRepository {


    override suspend fun addNewNote(
        title: String,
        content: List<ContentItem>,
        updateAt: Long,
        isPinned: Boolean
    ) {
        val processedContent = content.processForStorage()
        val noteDbModel = NoteDbModel(
            id = 0,
            title = title,
            /*Обрабатываем content для сохранения изображений в Internal Storage*/
            updateAt = updateAt,
            isPinned = isPinned
        )

/*        val noteId = notesDao.addNote(noteDbModel).toInt()
        val contentItems = processedContent.toContentItemDbModel(noteId = noteId)
        notesDao.addNoteContent(contentItems)*/
        notesDao.addNoteWithContent(noteDbModel, content)

    }

    /*Добавление удаления картинок из Internal Storage при удалении заметки*/
    override suspend fun deleteNote(noteId: Int) {
        val note = notesDao.getNote(noteId).toEntity()
        notesDao.deleteNote(noteId)

        note.content.filterIsInstance<ContentItem.Image>()
            .map {
                it.url
            }
            .forEach {
            imageFileManager.deteImage(it)
        }
    }

    /*Обновляем функцию добавления редактирования заметки*/
    override suspend fun editNote(note: Note) {
        /*получаем старую заметку из базы данных*/
        val oldNote = notesDao.getNote(note.id).toEntity()
        /*из старой замеки получаем список путей до изображений*/
        val oldListUrls = oldNote.content.filterIsInstance<ContentItem.Image>().map { it.url }
        /*из новой замеки получаем список путей до изображений*/
        val newListUrls = note.content.filterIsInstance<ContentItem.Image>().map { it.url }
        /*возвращает новую коллекцию, содержащую элементы из oldListUrls, которых нет в newListUrls*/
        val removeUrls = oldListUrls - newListUrls
        /*удаляем изображения их Internal Storage*/
        removeUrls.forEach {
            imageFileManager.deteImage(it)
        }
        /*Загружаем новые изображения из External в Internal Storage*/
        val processedContent = note.content.processForStorage()
        /*Обновляем контент в редактируемой заметки*/
        val processNote = note.copy(content = processedContent)

        /*notesDao.addNote(processNote.toDbModel())
        notesDao.deleteNoteContent(note.id)
        notesDao.addNoteContent(processedContent.toContentItemDbModel(note.id))*/
        notesDao.updateNote(
            noteDbModel = processNote.toDbModel(),
            content = processedContent.toContentItemDbModel(processNote.id)
        )
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { it.toEntities() }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }

    /*Добавляем функцию(расширение) для обработки изображения
    * если ссылка на него есть в Internal storage то ничего не делаем если ее там нет то
    * копируем изоброжение из External в Internal Storage*/
    private suspend fun List<ContentItem>.processForStorage(): List<ContentItem> {
        return map { contentItems ->
            when (contentItems) {
                is ContentItem.Image -> {
                    if (imageFileManager.isInternalStorage(path = contentItems.url)) {
                        contentItems
                    } else {
                        val internalPath =
                            imageFileManager.copyImageToInternalStorage(contentItems.url)
                        ContentItem.Image(internalPath)
                    }
                }

                is ContentItem.Text -> contentItems
            }
        }
    }

    /*    companion object {

            var instance: NotesRepositoryImpl? = null
            private val LOC = Any()

            fun getInstance(notesDatabase: NotesDatabase): NotesRepositoryImpl {
                instance?.let { return it }

                synchronized(LOC) {
                    instance?.let { return it }

                    return NotesRepositoryImpl().also {instance = it}
                }
            }

        }*/
}