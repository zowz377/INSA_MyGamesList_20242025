package com.insa.mygamelist.ui.componant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.IGDB

@Composable
fun GameDetails(id: Long) {
    // Définition de tous les éléments utilisés
    val game = IGDB.games.find { id == it.id }
    val name = game?.name ?: "Unfound"
    val cover = "https:" + IGDB.covers.find { game?.cover == it.id }?.url
    val genre = IGDB.genres.filter { it.id in (game?.genres ?: listOf(String)) }.joinToString(", ") { it.name }
    val platforms = IGDB.platforms.filter { it.id in game?.platforms!! }
    val summary = game?.summary ?: ""

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())){
        // Affichage du titre du jeu
        Text(
            text = name,
            modifier = Modifier
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
            contentDescription = "Couverture de "+name,
            modifier = Modifier
                .size(250.dp)
                .padding(10.dp)
        )
        // Affichage du(des) genre(s) du jeu
        Text(
            text = genre,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic
        )
        // Affichage de toutes les plateformes compatibles avec le jeu
        LazyRow {
            items(platforms.size){ // Calcule le nombre de jeux
                    index -> // Incrémente jusqu'au nombre nombre de jeux
                val platform = platforms[index]
                DisplayLogo(platform)
            }
        }
        // Affichage du résumé du jeu
        Text(
            text = summary,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Left,
            fontSize = 20.sp
        )
    }
}
