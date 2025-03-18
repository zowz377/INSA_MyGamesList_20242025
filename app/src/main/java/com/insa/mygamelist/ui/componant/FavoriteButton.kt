package com.insa.mygamelist.ui.componant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.insa.mygamelist.data.Games

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