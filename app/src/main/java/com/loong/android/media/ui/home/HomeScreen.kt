package com.loong.android.media.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.loong.android.media.ui.model.Route

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier, routes: List<Route>) {
    Scaffold {
        LazyColumn(modifier.padding(it)) {
            items(routes, { item -> item.toString() }) { route ->
                Text(
                    route.toString(),
                    Modifier
                        .padding(8.dp)
                        .clickable { navController.navigate(route) }
                )
            }
        }
    }
}