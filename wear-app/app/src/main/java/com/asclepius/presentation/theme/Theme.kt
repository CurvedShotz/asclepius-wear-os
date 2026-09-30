package com.asclepius.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Composable
fun AsclepiusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}
