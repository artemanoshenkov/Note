package com.example.note.data

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class ImageFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /*свойство в Android, которое возвращает объект File,
    указывающий на приватную директорию приложения для хранения внутренних файлов.*/
    private val imagesFile: File = context.filesDir

    /*Копирование файла из хранилеща устройства в хранилище приложения*/
    suspend fun copyImageToInternalStorage(url: String): String {
        /*Создание имени файла*/
        val fileName = "IMAGE_${UUID.randomUUID()}.jpg"
        /*Создается путь до файла для дальнецйшей записи в него*/
        val file = File(imagesFile, fileName)
        /*открываем поток ввода забирая обьект, указываем путь к файлу который будет взят в поток ввода для дальнейшего чтения байтов*//*
        val inputStream = context.contentResolver.openInputStream(url.toUri())
        *//*открываем поток вывода указывая путь*//*
        val outputStream = file.outputStream()
        *//*копируем поток байтов в поток вывода*//*
        inputStream?.copyTo(outputStream)
        *//*Обязательно закрываем потоки после импользхования*//*
        inputStream?.close()
        outputStream.close()*/

        withContext(Dispatchers.IO) {
            /*Использование use под капотом обрачивает в try {} catch {} finally {} закрывая соединеие*/
            context.contentResolver.openInputStream(url.toUri()).use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
        }


        return file.absolutePath
    }

    /*Метод удаления файла из хранилеща приложения Internal Storage*/
    suspend fun deteImage(url: String) {
        val file = File(url)
        withContext(Dispatchers.IO) {
            if(file.exists() && isInternalStorage(file.absolutePath)) {
                file.delete()
            }
        }
    }

    /*Метод для проверки на то что файл находится в Internal Storage*/
    fun isInternalStorage(path: String): Boolean {
        return path.startsWith(imagesFile.absolutePath)
    }
}