package ru.ikom.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ikom.common.Purple

@Composable
fun DepartmentItem(item: DepartmentUi, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .drawBehind {
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, (size.height * 0.85).toFloat()),
                    end = Offset(size.width, (size.height * 0.85).toFloat()),
                    strokeWidth = 1f
                )
            }
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .widthIn(min = 50.dp)
                .fillMaxHeight()
                .padding(horizontal = 8.dp)
                .clickable(interactionSource = remember {
                    MutableInteractionSource()
                }, indication = null, onClick = onClick)
                .drawBehind {
                    if (item.isSelected) {
                        drawLine(
                            color = Purple,
                            start = Offset(0f, (size.height * 0.85).toFloat()),
                            end = Offset(size.width, (size.height * 0.85).toFloat()),
                            strokeWidth = 4f
                        )
                    }
                },
        ) {
            val color = if (item.isSelected && isSystemInDarkTheme()) Color.White else if (item.isSelected) Color.Black else Color.Gray
            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .widthIn(min = 50.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                text = item.name,
                style = TextStyle(
                    fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp,
                    color = color
                )
            )
        }
    }
}