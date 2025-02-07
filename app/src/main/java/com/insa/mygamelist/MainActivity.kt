package com.insa.mygamelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.Games
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.theme.MyGamesListTheme
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    @Serializable
    object HomeRoute        // Création de l'objet HomeRoute = chemin pout aller au HomeScreen
    @Serializable
    data class GameRoute(val id : Long) // idem sauf pour aller au GameScreen qui correspond à l'id


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        IGDB.load(this)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()     // Création du nav controller

            MyGamesListTheme {
                NavHost(navController, startDestination = HomeRoute) {  // Chemin init = HomeRoute
                    composable<HomeRoute> {     // La HomeRoute nou emmène sur le HomeScreen
                        HomePage(navController)
                    }
                    composable<GameRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<GameRoute>()
                        GameScreen(route.id, navController)
                    }
                }
            }
        }
    }
}


// Define the HomeScreen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavHostController) {
    Scaffold(topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = Color(138, 239, 110, 255),
            titleContentColor = Color.Black,),
            title = { Text("My Games List") })
    }, modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn (modifier = Modifier.padding(innerPadding)){
            items(IGDB.games.size){
                index ->
                    val game =IGDB.games[index]
                    GameCard(game, navController)
            }
        }
    }
}

// Define the GameScreen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(id: Long, navController: NavController) {
    var game = IGDB.games.find{id==it.id}

    Scaffold(topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = Color(138, 239, 110, 255),
            titleContentColor = Color.Black,),
            title = { Text(game?.name ?: "Unfound") },
            navigationIcon = {
                IconButton(onClick = {navController.navigateUp()}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            },)
    }, modifier = Modifier.fillMaxSize()) { innerPadding ->
         Text(text = game?.id.toString(), modifier = Modifier.padding(innerPadding))
    }
}


@Composable
fun GameCard(game : Games, navController: NavController){
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(15.dp)
            .height(100.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Color(184, 184, 187, 255))
            .clickable { navController.navigate(MainActivity.GameRoute(game.id)) }
        ) {
            AsyncImage(model = "https:"+IGDB.covers.find({game.cover==it.id})?.url,
                contentDescription = "image",
                modifier = Modifier.padding(15.dp))
            Column(){
                Text(game.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )

                var mygenres = "Genres : " + IGDB.genres.filter{ it.id in game.genres }.joinToString(", ") { it.name }
                Text(text = mygenres,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
        }
}