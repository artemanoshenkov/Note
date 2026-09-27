package com.example.note.presentation.screens.creations

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note.domain.AddNewNoteUseCase
import com.example.note.domain.ContentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNewNoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreateNoteCommand) {
        when (command) {
            CreateNoteCommand.Back -> {
                _state.update { CreateNoteState.Finished }
            }

            is CreateNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        val newContent = previousState.content.mapIndexed { index, content ->
                            if (index == command.index && content is ContentItem.Text) {
                                content.copy(content = command.content)
                            } else {
                                content
                            }
                        }
                        previousState.copy(
                            content = newContent,

                        )
                    } else {
                        previousState
                    }

                }

            }

            is CreateNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.copy(
                            title = command.title,
                        )
                    } else {
                        previousState
                    }

                }
            }

            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is CreateNoteState.Creation) {
                            val title = previousState.title
                            val content = previousState.content.filter {contentItem ->
                                contentItem !is ContentItem.Text || contentItem.content.isNotBlank()
                            }
                            addNoteUseCase(title = title, content)
                            CreateNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }
/*Если последним элеметом было дабавление изображения то надо добавить ContentItem.Text("")
если перед добавлением изображения был ContentItem.Text("") его нужно удалить
после добавить ContentItem.Image("kjkopbdkab") и добавить ContentItem.Text("")*/
            is CreateNoteCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        val newItems = previousState.content.toMutableList()
                        val lastItem = newItems.last()
                        if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                            newItems.removeAt(newItems.lastIndex)
                        }
                        newItems.add(ContentItem.Image(url = command.uri.toString()))
                        newItems.add(ContentItem.Text(""))
                        previousState.copy(content = newItems)
                    } else {
                        previousState
                    }

                }
            }
        }
    }
}

sealed interface CreateNoteCommand {
    data class InputTitle(val title: String) : CreateNoteCommand
    data class InputContent(val content: String, val index: Int) : CreateNoteCommand
    data object Save : CreateNoteCommand
    data object Back : CreateNoteCommand
    data class AddImage(val uri: Uri): CreateNoteCommand
}

sealed interface CreateNoteState {
    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.Text("")),
        val index: Int = 0

    ) : CreateNoteState {
        val isSaveEnable: Boolean
            get()  {
                return when {
                    title.isBlank() -> false
                    content.isEmpty() -> false
                    else -> {
                        content.any {
                            it !is ContentItem.Text || it.content.isNotBlank() /*Если в коллекции
                          контента есть хотябы картинка ContentItem.Image или контент не пустой*/
                        }
                    }
                }
            }
    }

    data object Finished : CreateNoteState
}