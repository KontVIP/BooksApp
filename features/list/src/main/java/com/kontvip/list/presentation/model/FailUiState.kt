package com.kontvip.list.presentation.model

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.kontvip.list.domain.core.ListScreenUiState

data class FailUiState(private val errorMessage: String) : ListScreenUiState {
    @Composable
    override fun UiDisplay(onBookSelected: (bookId: String) -> Unit) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = errorMessage
        )
    }

    override fun isFail(): Boolean = true
}