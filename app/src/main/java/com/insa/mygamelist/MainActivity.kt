package com.insa.mygamelist

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
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
import com.insa.mygamelist.ui.screen.GameScreen
import com.insa.mygamelist.ui.screen.HomeScreen
import com.insa.mygamelist.ui.theme.MyGamesListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            // Stockés via leurs id
            val favoriteGames = remember { mutableStateOf(setOf<Long>()) }

            var searchText by rememberSaveable { mutableStateOf("") }

            var isSearchVisible by rememberSaveable { mutableStateOf(false) }

            var isFavoriteSelected by rememberSaveable { mutableStateOf(false) }

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

            // Sélection des jeux à afficher selon les filtres recherche et favoris
            val displayList = when {
                isSearchVisible && searchText.isNotEmpty() && isFavoriteSelected ->
                    filteredGames.filter { it.id in favoriteGames.value } // Filtre favoris + Recherche
                isSearchVisible && searchText.isNotEmpty() ->
                    filteredGames // Pas de filtre favoris + Recherche
                isFavoriteSelected ->
                    IGDB.games.filter { it.id in favoriteGames.value } // Filtre favoris + Pas Recherche
                else ->
                    IGDB.games // Pas de filtre favoris + Pas Recherche
            }

            MyGamesListTheme {
                NavHost(navController, startDestination = HomeRoute) {  // Chemin init = HomeRoute
                    composable<HomeRoute> {     // La HomeRoute nous emmène sur le HomeScreen
                        HomeScreen(navController = navController,
                            favoriteGames = favoriteGames,
                            searchText = searchText,
                            setSearchText = {newSearchText -> searchText = newSearchText},
                            isFavoriteSelected = isFavoriteSelected,
                            toggleIsFavoriteSelected = { isFavoriteSelected = !isFavoriteSelected },
                            displayList = displayList,
                            isSearchVisible = isSearchVisible,
                            toggleIsSearchVisible = { isSearchVisible = !isSearchVisible }
                            )
                    }
                    composable<GameRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<GameRoute>()
                        GameScreen(route.id,
                            navController,
                            favoriteGames,
                            displayList)
                    }
                }
            }
        }
    }
}