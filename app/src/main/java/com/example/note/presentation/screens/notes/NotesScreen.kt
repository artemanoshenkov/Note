package com.example.note.presentation.screens.notes

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.note.domain.Note
import kotlin.coroutines.coroutineContext


@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    /** viewModel() - переживает рекомпозицию и смену конфигурации
     * она хранит данные которые необходимо передать пользователю
     * для импорта необходимо добавить зависимости в gradle..
     * https://developer.android.com/develop/ui/compose/libraries#viewmodel*/
    viewModel: NotesViewModel = viewModel()
) {

    /** С помощью делегата by мы можем в короткой форме у state получить текущее значение value..
     * для этого его необходимо ипортировать "import androidx.compose.runtime.getValue"
     * что эквивалентно val currentState = state.value */
    val state by viewModel.state.collectAsState()
//    val currentState = state.value

    /** Функция remember позваляет обьекту пережить рекомпозицию и
     * сохраить его старое значение только при рекомпозиции но не при пересоздании активити (при смене конфигурации)
     * в ней можно производить вычисления и создавать обьекты*/
    val scrollState = remember<ScrollState> { ScrollState(0) }

    /** Для последовательной прогрузки элементов которые видны на экране используется  LazyColumn
     * в него уже встроенн скролл.
     * В нутри нее мы не можем вызывать Composable функции только внутри item {}- в него необходимо передавать элементы отдельно
     * иначе все отрисуется сразу и смысла применения не будет все тоже самое и для LazyRow()*/
    LazyColumn(

        modifier = Modifier
            .padding(top = 48.dp),

        /** verticalScroll(ScrollState(0)) позваляет делать скролл передается стартавая позиция
         * есть встроенные функции rememberS.... они сохраняют состояния не только при рекомпозиции
         * но и при пересоздании активити */
//            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
//                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

//                items(state.pinnedNotes) {note->
//                    NoteCard(
//                        note = note,
//                        onNoteClick = {
//                            viewModel.processCommands(NotesCommand.SwitchPinnedStatus(note.id))
//                        }
//                    )
//                }
                state.pinnedNotes.forEach { note ->
                    /** Для добвления универсальности создадим свою Composable функцию NoteCard
                     * она позваляет реализовать нужный функционал при взаимодействии с карточкой*/
                    item {
                        NoteCard(
                            note = note,
                            onNoteClick = {
                                viewModel.processCommands(NotesCommand.SwitchPinnedStatus(note.id))
                            }
                        )
                    }
//                Text(
//                    /** clickable {NotesCommand.....} реакция на кнопку
//                     * (при нажатии вызывается команда метод обработки команды у
//                     * viewModel.processCommands с передачей данных) */
//                    modifier = Modifier.clickable {
//                        viewModel.processCommands(NotesCommand.SwitchPinnedStatus(note.id))
////                    viewModel.processCommands(NotesCommand.EditeNote(note))
//                    },
//                    fontSize = 24.sp,
//                    text = "${note.title} - ${note.content}"
//                )
                }
            }
        }

        /** Укароченный вариант применения item{},
         * функция items() в которую передается коллекция, и уже каждый элемент обрабатывается */

//        items(state.othersNotes) {note->
//            NoteCard(
//                note = note,
//                onNoteClick = {
//                    viewModel.processCommands(NotesCommand.SwitchPinnedStatus(note.id))
//                }
//            )
//
//        }

        state.othersNotes.forEach { note ->
            /** Для добвления универсальности создадим свою Composable функцию NoteCard
             * она позваляет реализовать нужный функционал при взаимодействии с карточкой*/
            item {
                NoteCard(
                    note = note,
                    onNoteClick = {
                        viewModel.processCommands(NotesCommand.SwitchPinnedStatus(note.id))
                    }
                )
            }
//            Text(
//                /** clickable {NotesCommand....} реакция на кнопку
//                 * (при нажатии вызывается команда метод обработки команды у
//                 * viewModel.processCommands с передачей данных) */
//                modifier = Modifier.clickable {
//                    viewModel.processCommands(NotesCommand.SwitchPinnedStatus(note.id))
////                    viewModel.processCommands(NotesCommand.EditeNote(note))
//                },
//                fontSize = 24.sp,
//                text = "${note.title} - ${note.content}"
//            )
        }
    }
}


/** При создании своиз Composable функий нужно придерживаться правил:
 *  - всегда добавлять Modifier что бы снаружи можно было добавить свои настройки
 *  - данные с которыми работает эта функция должны приходить снаружи в качестве параметра (note
 *  - если она должна реагировать на какие то события (клик на кнопку)
 *  то это не указывается явно а сообщается на ружу что действие было выполнено*/
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    /**получаем возможностиь передачи любого действия при клике на карточку*/
    onNoteClick: (note: Note) -> Unit

) {
    Text(
        /** clickable {NotesCommand....} реакция на кнопку
         * (при нажатии вызывается команда метод обработки команды у
         * viewModel.processCommands с передачей данных)
         * или в данном случае анонимная функция в котрую передается значение и какоето действие*/
        modifier = Modifier.clickable {
            onNoteClick(note)
//            viewModel.processCommands(NotesCommand.SwitchPinnedStatus(note.id))
//            viewModel.processCommands(NotesCommand.EditeNote(note))
        },
        fontSize = 24.sp,
        text = "${note.title} - ${note.content}"
    )
}