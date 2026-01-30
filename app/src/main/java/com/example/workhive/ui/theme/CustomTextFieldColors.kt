package com.example.workhive.ui

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun customTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFFE8E8E8),
    unfocusedContainerColor = Color(0xFFE8E8E8),
    disabledContainerColor = Color(0xFFE8E8E8),

    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    disabledTextColor = Color.DarkGray,

    cursorColor = Color(0xFF8E57FF),

    focusedIndicatorColor = Color(0xFF8E57FF),
    unfocusedIndicatorColor = Color(0xFF707070),
    disabledIndicatorColor = Color.DarkGray
)
