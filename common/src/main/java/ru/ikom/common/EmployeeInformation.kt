package ru.ikom.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun EmployeeInformation(firstName: String, lastName: String, tag: String, department: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("$firstName $lastName")
            }

            withStyle(style = SpanStyle(fontSize = 14.sp, color = Color.Gray)) {
                append(" ${tag.lowercase()}")
            }
        },
    )
    Text(
        text = department,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray
    )
}