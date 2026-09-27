@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.note.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note.domain.GetAllNotesUseCase
import com.example.note.domain.Note
import com.example.note.domain.SearchNotesUseCase
import com.example.note.domain.SwitchPinnedStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotes: GetAllNotesUseCase,
    private val searchNote: SearchNotesUseCase,
    private val switchPinnedStatus: SwitchPinnedStatusUseCase
) : ViewModel() {
    /** Работа с репазиторием при получении команд */

    /** query поток данных для поиска Note */
    private val query = MutableStateFlow("")

    /** _state поток текущего состояни экрана NotesScreenState который содержит Notes
     * и обновляется при выполнеии определенной команды*/
    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()
    /** Для ViewModel был разработан скоуп viewModelScope
     * благодаря которому не надо переопределять метод onCleared он автоматически отменяет все корутины*/
//    private val scope = CoroutineScope(Dispatchers.IO)

    /** Инициализация NotesViewModel при первом запуске
     * и при пустом поиске (query) отображаютя текущие Notes в своиз состоянях*/
    init {

        query
            .onEach { input ->
                _state.update { it.copy(query = input) }
            }
            /** flatMapLatest позваляет переключать поток на новый при поиске
             * а все предыдущие будут отменены у нас будет новый Flow и подписка будет на нем
             * в отличии от map который остается на старом потоке и нам бы пришлось заново подписываться и отменять старые потоки в ручную*/
            .flatMapLatest {
                if (it.isBlank()) {
                    getAllNotes()
                    /**Flow со списком всех заметок*/
                } else {
                    searchNote(it)
                    /**Flow со списком заметок которые ищет пользователь по query*/
                }
            }.onEach { listNotes ->
                val pinnedNotes = listNotes.filter { it.isPinned }
                val othersNotes = listNotes.filter { !it.isPinned }

                _state.update {
                    it.copy(pinnedNotes = pinnedNotes, othersNotes = othersNotes)
                }
            }
            .launchIn(viewModelScope)

    }


    /** Метод processCommands для обработки команд которые будут передаваться при нажатии на кнопки
     * вызывая методы у репозитория и передавая необходимые данные*/
    fun processCommands(command: NotesCommand) {
        viewModelScope.launch {
            when (command) {


                is NotesCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }

                is NotesCommand.SwitchPinnedStatus -> {
                    switchPinnedStatus(command.nodeId)
                }
            }
        }

    }
    /** Для прекращения жизненного цикла корутин при уничтожении
     * ViewModel уничтожаются все корутины */
//    override fun onCleared() {
//        super.onCleared()
//        scope.cancel()
//    }
}

/** Перечень команд которые будут обрабатываться с помощью метода processCommands*/
sealed interface NotesCommand {

    data class InputSearchQuery(val query: String) : NotesCommand
    data class SwitchPinnedStatus(val nodeId: Int) : NotesCommand

}

/** Состояние экран с информацией о Note и полем поиска который отображается на экране*/
data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val othersNotes: List<Note> = listOf()
)