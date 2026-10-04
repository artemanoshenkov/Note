package com.example.note.presentation.modules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.note.domain.ContentItem


/*Выносим функцию с контентом отдельно*/
@Composable
fun Content(
    modifier: Modifier,
    content: List<ContentItem>,
    onDeleteImageClick: (Int) -> Unit,
    onTextChanged: (Int, String) -> Unit
) {
    /*Для возможности скролла контента добави LazyColumn*/
    LazyColumn(
        modifier = modifier
    ) {
        content.forEachIndexed { index, contentItem ->
            item(key = index) {
                when (contentItem) {
                    is ContentItem.Image -> {
                        val isAlreadyDisplay = index > 0 && content[index - 1] is ContentItem.Image
                        /*Если придыдущий обьект не ихображение то продалжаем,
                        * для того что бы не отображать то что уже было*/
                        content.takeIf {
                            !isAlreadyDisplay
                        }
                            /*Отбросили все обьекты до index*/
                            ?.drop(index)
                            /*Брать пока ContentItem является Image*/
                            ?.takeWhile {
                                it is ContentItem.Image
                                /*получаем url изображения использую даункаст*/
                            }?.map { (it as ContentItem.Image).url }
                            ?.let { urls ->
                                ImageGroup(
                                    modifier = Modifier.padding(horizontal = 24.dp),
                                    urlsImages = urls,
                                    onDeleteImageClick = {imageIndex ->
                                        onDeleteImageClick(index + imageIndex)
                                    }
                                )
                            }
                    }

                    is ContentItem.Text -> {
                        TextContent(
                            text = contentItem.content,
                            onTextChanged = {
                                onTextChanged(index, it)
                            }
                        )
                    }

                }
            }
        }
    }
}


/*Функция для отображения нескольких изображений в строгу (группировки)*/
@Composable
fun ImageGroup(
    modifier: Modifier = Modifier,
    urlsImages: List<String>,
    onDeleteImageClick: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        urlsImages.forEachIndexed { index, url ->
            ImageContent(
                modifier = Modifier.weight(1f),
                imageUri = url,
                onDeleteImageClick = {
                    onDeleteImageClick(index)
                }
            )
        }

    }
}

/*Вынос функции отображения изображения при создании заметки*/
@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    imageUri: String,
    onDeleteImageClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        /*Загрузка изображения из галлереи
* AsyncImage принимает model указывающую на нужный обьект*/
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            model = imageUri,
            contentDescription = "Image from gallery",
            /*Отвечает за расположение и маштабирование картинки в контейнере
            по умолчанию подстраивается под контейнер*/
            contentScale = ContentScale.FillWidth
        )

        Icon(
            modifier = Modifier
                /*Для того что бы иконка находилась поверх изображения
                * работает в Box*/
                .align(alignment = Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .clickable {
                    onDeleteImageClick()
                },
            imageVector = Icons.Default.Close,
            contentDescription = "Icon for delete image",
            tint = Color.Black
        )
    }

}

/*Вынос поля TextField для удобной реализации и читаймости кода*/
@Composable
fun TextContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),

        value = text,
        onValueChange = onTextChanged,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        ),
        placeholder = {
            Text(
                text = "Введите текст...",
                fontSize = 24.sp,
//                                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        }
    )
}
