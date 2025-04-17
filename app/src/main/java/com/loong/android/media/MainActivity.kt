package com.loong.android.media

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.loong.android.media.ui.event.EventScreen
import com.loong.android.media.ui.home.HomeScreen
import com.loong.android.media.ui.model.Route
import com.loong.android.media.ui.theme.ComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, Route.Home) {
        composable<Route.Home> {
            HomeScreen(navController)
        }
        composable<Route.Event> {
            EventScreen(navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AppNavHost()
}