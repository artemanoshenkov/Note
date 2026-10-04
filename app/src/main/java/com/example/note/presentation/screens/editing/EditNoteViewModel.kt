package com.example.note.presentation.screens.editing

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note.domain.ContentItem
import com.example.note.domain.ContentItem.Image
import com.example.note.domain.ContentItem.Text
import com.example.note.domain.DeleteNoteUseCase
import com.example.note.domain.EditeNoteUseCase
import com.example.note.domain.GetNoteUseCase
import com.example.note.domain.Note
import com.example.note.presentation.screens.creations.CreateNoteCommand
import com.example.note.presentation.screens.creations.CreateNoteState
import com.example.note.presentation.screens.notes.NotesCommand
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    private val editNoteUseCase: EditeNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    @Assisted("idNote") private val idNote: Int,
) : ViewModel() {

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                val note = getNoteUseCase(idNote)
                val content = if (note.content.lastOrNull() !is ContentItem.Text) {
                    note.content + ContentItem.Text("")
                } else {
                    note.content
                }
                EditNoteState.Editing(note.copy(content = content))
            }

        }
    }

    fun processCommand(command: EditNoteCommand) {
        when (command) {
            EditNoteCommand.Back -> {
                _state.update { EditNoteState.Finished }
            }

            is EditNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newContent = previousState.note.content.mapIndexed { index, content ->
                            if (index == command.index && content is ContentItem.Text) {
                                content.copy(content = command.content)
                            } else {
                                content
                            }
                        }
                        val newNote = previousState.note.copy(content = newContent)
                        previousState.copy(
                            note = newNote
                        )
                    } else {
                        previousState
                    }

                }
            }

            is EditNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newNote = previousState.note.copy(title = command.title)
                        previousState.copy(note = newNote)
                    } else {
                        previousState
                    }

                }
            }

            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            editNoteUseCase(note)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }

            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            deleteNoteUseCase(note.id)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }

            is EditNoteCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        previousState.note.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            previousState.copy(note = previousState.note.copy(content = it))
                        }
                    } else {
                        previousState
                    }

                }
            }

            is EditNoteCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newItems = previousState.note.content.toMutableList()
                        val lastItem = newItems.last()
                        if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                            newItems.removeAt(newItems.lastIndex)
                        }
                        newItems.add(Image(url = command.uri.toString()))
                        newItems.add(Text(""))
                        previousState.copy(note = previousState.note.copy(content = newItems))
                    } else {
                        previousState
                    }

                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("idNote") idNote: Int
        ): EditNoteViewModel

    }
}

sealed interface EditNoteCommand {
    data class InputTitle(val title: String) : EditNoteCommand
    data class InputContent(val content: String, val index: Int) : EditNoteCommand
    data object Save : EditNoteCommand
    data object Back : EditNoteCommand

    data object Delete : EditNoteCommand
    data class DeleteImage(val index: Int) : EditNoteCommand
    data class AddImage(val uri: Uri) : EditNoteCommand
}

sealed interface EditNoteState {

    data object Initial : EditNoteState
    data class Editing(
        val note: Note,
    ) : EditNoteState {
        val isSaveEnable: Boolean
            get() {
                return when {
                    note.title.isBlank() -> false
                    note.content.isEmpty() -> false
                    else -> {
                        note.content.any {
                            it !is ContentItem.Text || it.content.isNotBlank() /*Если в коллекции
                          контента есть хотябы картинка ContentItem.Image или контент не пустой*/
                        }
                    }
                }
            }
    }

    data object Finished : EditNoteState
}