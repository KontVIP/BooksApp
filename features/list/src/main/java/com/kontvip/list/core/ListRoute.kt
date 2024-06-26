package com.kontvip.list.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.kontvip.common.navigation.Route
import com.kontvip.list.presentation.ListScreen
import kotlinx.serialization.Serializable

@Serializable
data object ListRoute : Route {
    @Composable
    override fun Content(navController: NavController) {
        ListScreen(navController = navController)
    }
}