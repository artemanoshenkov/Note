package com.example.note.presentation.screens.notes


import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.note.R
import com.example.note.domain.ContentItem
import com.example.note.domain.Note
import com.example.note.presentation.ui.theme.OtherNotesColors
import com.example.note.presentation.ui.theme.PinnedNotesColors
import com.example.note.presentation.utils.DateFormatter


@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    /** viewModel() - переживает рекомпозицию и смену конфигурации
     * она хранит данные которые необходимо передать пользователю
     * для импорта необходимо добавить зависимости в gradle..
     * https://developer.android.com/develop/ui/compose/libraries#viewmodel*/
    context: Context = LocalContext.current.applicationContext,
    viewModel: NotesViewModel = hiltViewModel(),
    /** Для возможности перехода на другой экран используем колбек*/
    onNoteClick: (note: Note) -> Unit,
    onAddNoteButtonClick: () -> Unit
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

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            /** Кнопка для добавления note */
            FloatingActionButton(
                onClick = onAddNoteButtonClick, //если одинаковые сигнатуры то мы можем присвоить,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_note),
                    contentDescription = "Кнопка добавления заметки"
                )

            }
        }
    ) { innerPadding ->
        /** Для последовательной прогрузки элементов которые видны на экране используется  LazyColumn
         * в него уже встроенн скролл.
         * В нутри нее мы не можем вызывать Composable функции только внутри item {}- в него необходимо передавать элементы отдельно
         * иначе все отрисуется сразу и смысла применения не будет все тоже самое и для LazyRow()*/
        LazyColumn(
            contentPadding = innerPadding
            /** verticalScroll(ScrollState(0)) позваляет делать скролл передается стартавая позиция
             * есть встроенные функции rememberS.... они сохраняют состояния не только при рекомпозиции
             * но и при пересоздании активити */
//            .verticalScroll(rememberScrollState()),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Title(
                    text = "Заметки",
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                SearchBar(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    query = state.query,
                    onQueryChange = {
                        viewModel.processCommands(NotesCommand.InputSearchQuery(it))
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            if (state.pinnedNotes.isNotEmpty()) {
                item {
                    Subtitle(text = "Закрепленные", modifier = Modifier.padding(horizontal = 24.dp))
                }


            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
//                    .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    /** Для добавления отступов у контента в lazy обьектах используетсяя  contentPadding*/
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {

//                items(
//                   items = state.pinnedNotes,
//                    /** Параметр key позваляет связать item с той заметкой котрую он использует */
//                    key = { it.id }
//                ) {note->
//                    NoteCard(
//                        note = note,
//                        onNoteClick = {
//                            viewModel.processCommands(NotesCommand.SwitchPinnedStatus(note.id))
//                        }
//                    )
//                }
                    state.pinnedNotes.forEachIndexed { index, note ->
                        /** Для добвления универсальности создадим свою Composable функцию NoteCard
                         * она позваляет реализовать нужный функционал при взаимодействии с карточкой*/
                        /** Параметр key позваляет связать item с той заметкой котрую он использует для того что бы убрать рипл эффект*/
                        item(note.id) {

                            NoteCard(
                                /** widthIn - устанавливает максимальную и ьинимальную ширину */
                                modifier = Modifier.widthIn(max = 160.dp),
                                note = note,
                                onNoteClick = onNoteClick,

                                onLongClick = {
                                    viewModel.processCommands(NotesCommand.SwitchPinnedStatus(it.id))
                                },
                                backgroundColor = PinnedNotesColors[index % PinnedNotesColors.size],
//                            modifier = Modifier.padding(horizontal = 24.dp)
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

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Subtitle(text = "Все заметки", modifier = Modifier.padding(horizontal = 24.dp))
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            /** Укароченный вариант применения item{},
             * функция items() в которую передается коллекция, и уже каждый элемент обрабатывается
             * (в отличии от обычного он не обрезает контент)*/

//        items(state.othersNotes) {note->
//            NoteCard(
//                note = note,
//                onNoteClick = {
//                    viewModel.processCommands(NotesCommand.SwitchPinnedStatus(note.id))
//                }
//            )
//
//        }

            state.othersNotes.forEachIndexed { index, note ->
                /** Для добвления универсальности создадим свою Composable функцию NoteCard и NoteCardWithImage
                 * она позваляет реализовать нужный функционал при взаимодействии с карточкой*/
                item(key = note.id) {
                    val urlImage = note.content.filterIsInstance<ContentItem.Image>()
                        .map { it.url }
                        .firstOrNull()
                    if (urlImage != null) {

                        NoteCardWithImage(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            note = note,
                            urlImage = urlImage,
                            onNoteClick = onNoteClick,
                            onLongClick = {
                                viewModel.processCommands(NotesCommand.SwitchPinnedStatus(it.id))
                            },
                            backgroundColor = OtherNotesColors[index % OtherNotesColors.size],
                            )
                        Spacer(modifier = Modifier.height(8.dp))
                    } else {
                        NoteCard(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            note = note,
                            onNoteClick = onNoteClick,

                            onLongClick = {
                                viewModel.processCommands(NotesCommand.SwitchPinnedStatus(it.id))
                            },
                            backgroundColor = OtherNotesColors[index % OtherNotesColors.size],

                            )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

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

}

@Composable
fun NoteCardWithImage(
    modifier: Modifier = Modifier,
    note: Note,
    urlImage: String,
    backgroundColor: Color,
    /**получаем возможностиь передачи любого действия при клике на карточку*/
    onNoteClick: (note: Note) -> Unit,
    onLongClick: (note: Note) -> Unit,
) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            /** Для того что бы кнопка могла реагировать на разные нажатия используется  combinedClickable*/
            .combinedClickable(
                onClick = {
                    onNoteClick(note)
                },
                onLongClick = {
                    onLongClick(note)
                },

                )

    ) {
        Box(
            modifier = Modifier
        ) {
            AsyncImage(
                modifier = Modifier
                    /*Задаем максимальную высоту заметки*/
                    .heightIn(max = 120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    ,
                model = urlImage,
                contentDescription = "First image from note",
                /*Отвечает за расположение и маштабирование картинки в контейнере
                по умолчанию подстраивается под контейнер*/
                contentScale = ContentScale.FillWidth
            )
            Column(
                modifier = Modifier.fillMaxWidth().
                padding(16.dp)
                    .align(alignment = Alignment.BottomStart)
            ) {
                Text(

                    text = note.title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    /** overflow - отвечает за стратегию что делать если текст вылез за пределы контейнера
                     * в данном случае добавляет многоточ... */
                    overflow = TextOverflow.Ellipsis
                )
                Text(

                    text = DateFormatter.formateDateToString(note.updateAt),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        note.content
            .filterIsInstance<ContentItem.Text>()
            .filter { it.content.isNotBlank() }
            .map {
                it.content
            }
            .joinToString("\n")
            .takeIf { it.isNotBlank() }
            ?.let {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = it,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3,
                    /** overflow - отвечает за стратегию что делать если текст вылез за пределы контейнера
                     * в данном случае добавляет многоточ... */
                    overflow = TextOverflow.Ellipsis
                )
            }

    }

}


// ========== Composable функции для отображения ===========
/** При создании своиз Composable функий нужно придерживаться правил:
 *  - всегда добавлять Modifier что бы снаружи можно было добавить свои настройки
 *  - данные с которыми работает эта функция должны приходить снаружи в качестве параметра (note
 *  - если она должна реагировать на какие то события (клик на кнопку)
 *  то это не указывается явно а сообщается на ружу что действие было выполнено c помощью колбеков (анонимных функций)*/
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColor: Color,
    /**получаем возможностиь передачи любого действия при клике на карточку*/
    onNoteClick: (note: Note) -> Unit,
    onLongClick: (note: Note) -> Unit,
    ) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            /** Для того что бы кнопка могла реагировать на разные нажатия используется  combinedClickable*/
            .combinedClickable(
                onClick = {
                    onNoteClick(note)
                },
                onLongClick = {
                    onLongClick(note)
                },

                )
            .padding(16.dp)
    ) {

        Text(
            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            /** overflow - отвечает за стратегию что делать если текст вылез за пределы контейнера
             * в данном случае добавляет многоточ... */
            overflow = TextOverflow.Ellipsis
        )
        /** Для добавления необходимого растояния между
         * контентом можно использовать Composable функцию пустышку Spacer*/
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = DateFormatter.formateDateToString(note.updateAt),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )





        note.content
            .filterIsInstance<ContentItem.Text>()
            .filter { it.content.isNotBlank() }
            .map {
                it.content
            }
            .joinToString("\n").let {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = it,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3,
                    /** overflow - отвечает за стратегию что делать если текст вылез за пределы контейнера
                     * в данном случае добавляет многоточ... */
                    overflow = TextOverflow.Ellipsis
                )
            }


//        Text(
//            /** clickable {NotesCommand....} реакция на кнопку
//             * (при нажатии вызывается команда метод обработки команды у
//             * viewModel.processCommands с передачей данных)
//             * или в данном случае анонимная функция в котрую передается значение и какоето действие*/
//            modifier = modifier.clickable {
//                onNoteClick(note)
////            viewModel.processCommands(NotesCommand.SwitchPinnedStatus(note.id))
////            viewModel.processCommands(NotesCommand.EditeNote(note))
//            },
//            fontSize = 24.sp,
//            text = "${note.title} - ${note.content}"
//        )
    }

}

/** Composable функция для отображения Заголовка */
@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

/** Composable функция для отображения поисковой строки */
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(10.dp)
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        value = query,
        onValueChange = onQueryChange,
        /** Slot API применяется для того что бы в это значение можно
         * было положить что угодно а не фиксированный компонет */
        placeholder = {
            Text(
                text = "Поиск...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search note",
                tint = MaterialTheme.colorScheme.onSurface

            )
        },
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun Subtitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}

@Composable
@Preview
fun NoteCardPreview() {
    NoteCardWithImage(
    note = Note(
        id = 1,
        title = "Task 1",
        content = listOf(ContentItem.Text("Create tasc card"), ContentItem.Image("url for image")),

        updateAt = System.currentTimeMillis(),
        isPinned = false
    ),
        backgroundColor = Color.White,
        onNoteClick = {},
        onLongClick = {},
        urlImage = "url for image"
    )
}