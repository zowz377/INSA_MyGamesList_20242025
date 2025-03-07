package com.insa.mygamelist

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.insa.mygamelist.data.Platforms
import com.insa.mygamelist.ui.theme.MyGamesListTheme
import kotlinx.serialization.Serializable

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
            // Création du nav controller
            val navController = rememberNavController()

            // Création d'une liste des favoris qui est passée en paramètre de toutes nos pages
            val favoriteGames = remember { mutableStateOf(setOf<Long>()) }

            MyGamesListTheme {
                NavHost(navController, startDestination = HomeRoute) {  // Chemin init = HomeRoute
                    composable<HomeRoute> {     // La HomeRoute nous emmène sur le HomeScreen
                        HomeScreen(navController, favoriteGames)
                    }
                    composable<GameRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<GameRoute>()
                        GameScreen(route.id, navController, favoriteGames)
                    }
                }
            }
        }
    }
}

// #################### Définition des composables inclus dans les pages ####################
// ---------------------------- Définition du composable DisplayLogo ----------------------------
@Composable
fun DisplayLogo(platform : Platforms){
    AsyncImage(
        model = "https:"+IGDB.platform_logos.find{platform.platform_logo == it.id}?.url,
        contentDescription = "image",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.padding(7.dp).size(70.dp)
    )
}

// --------------------------- Définition du composable FavoriteButton ---------------------------
@Composable
fun FavoriteButton(game : Games, favoriteGames: MutableState<Set<Long>>){
    // Définition de l'état du boutton favori
    val isFavorite = favoriteGames.value.contains(game.id)
    IconButton(onClick = {
        if(isFavorite){
            favoriteGames.value -= game.id
        }else{
            favoriteGames.value += game.id
        }
    })
    {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Mettre en favori / Enlever des favoris"
        )
    }
}

// ------------------------------ Définition du composable GameCard ------------------------------
@Composable
fun GameCard(game : Games, navController: NavController, favoriteGames : MutableState<Set<Long>>){

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(15.dp)
            .height(100.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Color(184, 184, 187, 255))
            .clickable { navController.navigate(MainActivity.GameRoute(game.id)) }
    ) {
        AsyncImage(
            model = "https:"+IGDB.covers.find{game.cover==it.id}?.url,
            contentDescription = "image",
            modifier = Modifier.padding(15.dp))
        Column(modifier = Modifier.padding(10.dp).width(230.dp)){
            Text(
                game.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
            val mygenres = "Genres : " + IGDB.genres.filter{ it.id in game.genres }.joinToString(", ") { it.name }
            Text(
                text = mygenres,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
        FavoriteButton(game, favoriteGames)
    }
}

// ############################## Définition des différentes pages ##############################
// ----------------------------- Définition du composable HomeScreen -----------------------------
@SuppressLint("UseOfNonLambdaOffsetOverload")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, favoriteGames: MutableState<Set<Long>>) {
    // Définition des les états des bouttons
    var searchText by rememberSaveable { mutableStateOf("") }
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }

    // Filtre de recherche par nom, genre et plateforme compatible
    val filteredGames = IGDB.games.filter { game ->
            game.name.contains(searchText, ignoreCase = true) ||
                    IGDB.genres
                        .filter {game.genres.contains(it.id)}
                        .find {it.name.contains(searchText, ignoreCase = true)} != null ||
                    IGDB.platforms
                        .filter { game.platforms.contains(it.id)}
                        .find {it.name.contains(searchText, ignoreCase = true)} != null
    }

    Scaffold(topBar = {
        // Paramètrage de la top bar
        TopAppBar(
            colors = topAppBarColors(
                containerColor = Color(138, 239, 110, 255),
                titleContentColor = Color.Black,),
            title = { Text("My Games List") },
            actions = {
                IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                    Icon(
                        imageVector = if (isSearchVisible) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = "Afficher/Cacher la recherche"
                    )
                }
            }
        )
    }, modifier = Modifier.fillMaxSize()) { innerPadding ->
        // Affichage conditionnel de la barre de recherche
        if (isSearchVisible) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Rechercher...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            )
            if (filteredGames.isNotEmpty()) {
                // Permet l'affichage en liste scrollable des games cards filtrées
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .offset(y = if (isSearchVisible) 60.dp else 0.dp)
                )
                { // Fait un for each auto
                    items(filteredGames.size) { // Calcule le nombre de jeux qui correspondent à la recherche
                            index -> // Incrémente jusqu'au nombre précédant
                        val game = filteredGames[index]
                        GameCard(game, navController, favoriteGames)
                    }
                }
            }else{
                Text(
                    text = "No match :(",
                    modifier = Modifier
                        .padding(innerPadding)
                        .offset(y = 350.dp)
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }else{
            // Permet l'affichage en liste scrollable de toutes les games cards
            LazyColumn (modifier = Modifier.padding(innerPadding)){
                items(IGDB.games.size){
                        index ->
                    val game =IGDB.games[index]
                    GameCard(game, navController, favoriteGames)
                }
            }
        }
    }
}

// ----------------------------- Définition du composable GameScreen -----------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(id: Long, navController: NavController, favoriteGames: MutableState<Set<Long>>) {
    // Définition de tous les éléments utilisés dans la page
    val game = IGDB.games.find{id==it.id}
    val title = game?.name ?: "Unfound"
    val cover = "https:"+IGDB.covers.find{game?.cover==it.id}?.url
    val genre = IGDB.genres.filter{ it.id in (game?.genres ?: listOf(String))}.joinToString(", ") {it.name}
    val platforms = IGDB.platforms.filter{ it.id in game?.platforms!!}
    val summary = game?.summary ?: ""

    Scaffold(
        // Paramètrage de la top bar
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                containerColor = Color(138, 239, 110, 255),
                titleContentColor = Color.Black,),
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = {navController.navigateUp()}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = { IGDB.games.find{id==it.id}?.let { FavoriteButton(it, favoriteGames) } }
            )
        },
        modifier = Modifier.fillMaxSize())
    { innerPadding ->
    // Paramètrage du reste de la page
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            // Affichage du titre du jeu
            Text(text = title,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(15.dp)
                    .width(250.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )

            // Affichage de la couverture du jeu
            AsyncImage(
                model = cover,
                contentDescription = "image",
                modifier = Modifier.size(250.dp)
            )
            // Affichage du(des) genre(s) du jeu
            Text(
                text = genre,
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic
            )
            // Affichage de toutes les plateformes compatibles avec le jeu
            LazyRow {
                items(platforms.size){ // Calcule le nombre de jeux
                    index -> // Incrémente jusqu'au nombre précédant
                    val platform = platforms[index]
                    DisplayLogo(platform)
                }
            }
            // Affichage du résumé du jeu
            Text(
                text = summary,
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Left,
                fontSize = 20.sp
            )
        }
    }
}