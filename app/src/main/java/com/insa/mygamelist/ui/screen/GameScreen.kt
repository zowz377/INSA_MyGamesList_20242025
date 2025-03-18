package com.insa.mygamelist.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.componant.DisplayLogo
import com.insa.mygamelist.ui.componant.FavoriteButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(id: Long,
               navController: NavController,
               favoriteGames: MutableState<Set<Long>>)
{
    // Définition de tous les éléments utilisés dans la page
    val game = IGDB.games.find{id==it.id}
    val title = game?.name ?: "Unfound"
    val cover = "https:"+ IGDB.covers.find{game?.cover==it.id}?.url
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
                            contentDescription = "Retour à la page précédante"
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
                contentDescription = "Couverture de "+title,
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