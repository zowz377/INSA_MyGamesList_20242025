package com.insa.mygamelist.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.insa.mygamelist.data.Games
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.componant.FavoriteButton
import com.insa.mygamelist.ui.componant.GameDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(id: Long,
               navController: NavController,
               favoriteGames: MutableState<Set<Long>>,
               displayList: List<Games>)
{
    // Définition de tous les éléments utilisés dans la page
    val game = IGDB.games.find {id==it.id}

    val initialPage = displayList.indexOf(game)
    val pagerState = rememberPagerState(initialPage, pageCount = { displayList.size })


    Scaffold(
        // Paramètrage de la top bar
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color(138, 239, 110, 255),
                    titleContentColor = Color.Black,),
                title = { Text(displayList[pagerState.currentPage].name) },
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
        HorizontalPager(state = pagerState, modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                page -> GameDetails(displayList[page].id)     //permet de changer le GameDetails lorsqu'on swipe
        }
    }
}