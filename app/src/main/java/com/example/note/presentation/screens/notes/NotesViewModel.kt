@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.note.presentation.screens.notes

import androidx.lifecycle.ViewModel
import com.example.note.data.TestNotesRepositoryImpl
import com.example.note.domain.AddNewNoteUseCase
import com.example.note.domain.DeleteNoteUseCase
import com.example.note.domain.EditeNoteUseCase
import com.example.note.domain.GetAllNotesUseCase
import com.example.note.domain.GetNoteUseCase
import com.example.note.domain.Note
import com.example.note.domain.SearchNotesUseCase
import com.example.note.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class NotesViewModel : ViewModel() {
    /** Работа с репазиторием при получении команд */
    private val repository = TestNotesRepositoryImpl

    private val addNewNote = AddNewNoteUseCase(repository)
    private val deleteNote = DeleteNoteUseCase(repository)
    private val editeNoteUseCase = EditeNoteUseCase(repository)
    private val getAllNotes = GetAllNotesUseCase(repository)
    private val getNote = GetNoteUseCase(repository)
    private val searchNote = SearchNotesUseCase(repository)
    private val switchPinnedStatus = SwitchPinnedStatusUseCase(repository)
    /** query поток данных для поиска Note */
    private val query = MutableStateFlow("")

    /** _state поток текущего состояни экрана NotesScreenState который содержит Notes
     * и обновляется при выполнеии определенной команды*/
    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

    //TODO:тестовый метод для добавления тестовых данных
    private fun addTestNotes(){
        repeat(50) {
            addNewNote(title = "Title№ $it", content = "Content№ $it")
        }
    }
    /** Метод processCommands для обработки команд которые будут передаваться при нажатии на кнопки
     * вызывая методы у репозитория и передавая необходимые данные*/
    fun processCommands(command: NotesCommand) {
        when (command) {
            is NotesCommand.DeleteNote -> {
                deleteNote(command.noteId)
            }

            is NotesCommand.EditeNote -> {
                val note = getNote(command.note.id)
                editeNoteUseCase(note.copy(title = note.title + " edited"))
            }

            is NotesCommand.InputSearchQuery -> {
                query.update { command.query.trim() }
            }

            is NotesCommand.SwitchPinnedStatus -> {
                switchPinnedStatus(command.nodeId)
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    /** Инициализация NotesViewModel при первом запуске
     * и при пустом поиске (query) отображаютя текущие Notes в своиз состоянях*/
    init {

        addTestNotes() //TODO: тестовые данные позже удаляться
        query
            .onEach {input->
                _state.update { it.copy(query = input) }
            }
            /** flatMapLatest позваляет переключать поток на новый при поиске
             * а все предыдущие будут отменены у нас будет новый Flow и подписка будет на нем
             * в отличии от map который остается на старом потоке и нам бы пришлось заново подписываться и отменять старые потоки в ручную*/
            .flatMapLatest {
                if (it.isBlank()) {
                    getAllNotes()/**Flow со списком всех заметок*/
                } else {
                    searchNote(it)/**Flow со списком заметок которые ищет пользователь по query*/
                }
            }.onEach {listNotes ->
                val pinnedNotes = listNotes.filter { it.isPinned }
                val othersNotes = listNotes.filter { !it.isPinned }

                _state.update {
                    it.copy(pinnedNotes = pinnedNotes, othersNotes = othersNotes)
                }
            }
            .launchIn(scope)
    }


}
/** Перечень команд которые будут обрабатываться с помощью метода processCommands*/
sealed interface NotesCommand {

    data class InputSearchQuery(val query: String) : NotesCommand
    data class SwitchPinnedStatus(val nodeId: Int) : NotesCommand

    data class DeleteNote(val noteId: Int) : NotesCommand
    data class EditeNote(val note: Note) : NotesCommand
}

/** Состояние экран с информацией о Note и полем поиска который отображается на экране*/
data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val othersNotes: List<Note> = listOf()
)