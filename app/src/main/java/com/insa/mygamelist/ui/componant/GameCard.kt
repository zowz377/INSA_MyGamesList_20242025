package com.insa.mygamelist.ui.componant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import coil3.compose.AsyncImage
import com.insa.mygamelist.MainActivity
import com.insa.mygamelist.data.Games
import com.insa.mygamelist.data.IGDB

@Composable
fun GameCard(game : Games,
             navController: NavController,
             favoriteGames : MutableState<Set<Long>>,
){
    // Mise en forme de 3 blocs les uns à côté des autres : la couverture du jeu, ses infos et le boutton favori
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(5.dp)
            .height(100.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(229,224,232))
            .clickable { navController.navigate(MainActivity.GameRoute(game.id)) }
    ) {
        // Affichage de la couverture du jeu
        AsyncImage(
            model = "https:"+ IGDB.covers.find{game.cover==it.id}?.url,
            contentDescription = "Couverture de "+game.name,
            modifier = Modifier.padding(15.dp)
        )
        // Affichage des infos du jeu
        Column(modifier = Modifier.padding(10.dp).width(230.dp)){
            Text(   // Affichage du titre du jeu
                game.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
            // Récupération des genres et mise en forme des genres du jeu
            val mygenres = "Genres : " + IGDB.genres.filter{ it.id in game.genres }.joinToString(", ") { it.name }
            Text(   // Affichage des genres du jeu
                text = mygenres,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
        // Affichage du boutton favori
        FavoriteButton(game, favoriteGames)
    }
}
