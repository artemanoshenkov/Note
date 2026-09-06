package com.example.note.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.note.presentation.screens.creations.CreateNoteScreen
import com.example.note.presentation.screens.editing.EditNoteScreen
import com.example.note.presentation.screens.notes.NotesScreen

@Composable
fun NavGraph() {
    /** в контроллере будут храниться состояния экранов
     * и у него же будут вызываться методы для определения состояния экрана*/
    val navController = rememberNavController()
    //  за место when
    NavHost(
        navController = navController,
        startDestination = Screen.Notes.route //должно быть стартовое значение навигация происходит по строковым представлениям
    ) {
        composable(Screen.Notes.route) {

            NotesScreen(
                onNoteClick = {
                    navController.navigate(Screen.EditNote.createString(it.id)) // передача параметров происходитчерез "/" edit_note/5
                },
                onAddNoteButtonClick = {
                    navController.navigate(Screen.CreateNote.route) //для переключения на новый экран
                })
        }

        composable(Screen.CreateNote.route) {
            CreateNoteScreen(
                onFinished = {
//                    navController.navigate(Screen.Notes.route)
                    navController.popBackStack() //для корректной работы и возврата к бекстеку
                })
        }

        composable(Screen.EditNote.route) // передача параметров происходитчерез "/" edit_note/5
        {
            EditNoteScreen(
                noteId = it.arguments?.getString("note_id")?.toInt() ?: 0,
                onFinished = {
//                    navController.navigate(Screen.Notes.route)
                    navController.popBackStack() //для корректной работы и возврата к бекстеку
                })
        }

    }

}



//@Composable
//fun CustomNavGraph() {
//    val screen = remember { mutableStateOf<CustomScreen>(CustomScreen.Notes) }
//
//
//    when (val currentScreen = screen.value) {
//        CustomScreen.CreateNote -> {
//            CreateNoteScreen(
//                onFinished = {
//                    screen.value = CustomScreen.Notes
//                })
//        }
//
//        is CustomScreen.EditNote -> {
//            EditNoteScreen(
//                noteId = currentScreen.idNote,
//                onFinished = {
//                    screen.value = CustomScreen.Notes
//                })
//        }
//
//        CustomScreen.Notes -> {
//            NotesScreen(
//                onNoteClick = {
//                    screen.value = CustomScreen.EditNote(it.id)
//                },
//                onAddNoteButtonClick = {
//                    screen.value = CustomScreen.CreateNote
//                })
//        }
//    }
//
//}

sealed class Screen(val route: String) {
    /** Композ навигация работает со строковыми представлениями стейта экрана поэтому добавили свойства класса*/
    data object Notes : Screen("notes")
    data object CreateNote : Screen("create_not")
    data object EditNote : Screen("edit_note/{note_id}") { //Bundle("note_id" - "5")
        fun createString(noteId: Int): String {
            return "edit_note/$noteId"
        }

        fun getNoteId(arguments: Bundle?): Int {
            return arguments?.getString("note_id")?.toInt() ?: 0
        }
    }
}

sealed interface CustomScreen {
    data object Notes : CustomScreen
    data object CreateNote : CustomScreen
    data class EditNote(val idNote: Int) : CustomScreen
}