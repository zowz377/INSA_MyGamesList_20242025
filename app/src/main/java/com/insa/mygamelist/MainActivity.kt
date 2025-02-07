package com.insa.mygamelist

import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.Games
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.theme.MyGamesListTheme
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        IGDB.load(this)

        enableEdgeToEdge()
        setContent {

            MyGamesListTheme {
                Scaffold(topBar = {
                    TopAppBar(colors = topAppBarColors(
                        containerColor = Color(138, 239, 110, 255),
                        titleContentColor = Color.Black,
                    ), title = { Text("My Games List") })
                }, modifier = Modifier.fillMaxSize()) { innerPadding ->

                    LazyColumn (modifier = Modifier.padding(innerPadding)){
                        items(IGDB.games.size){
                            index ->
                                val game =IGDB.games[index]
                                gameCard(game)
                        }
                    }
                    }

                }
            }
        }
        //@Composable
        //fun uploadImage(): String
    }
    @Composable
    fun gameCard(jeu : Games){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(15.dp)
                .height(100.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(Color(184, 184, 187, 255))

        ) {
            AsyncImage(model = "https:"+IGDB.covers.find({jeu.cover==it.id})?.url,
                contentDescription = "image",
                modifier = Modifier.padding(15.dp))
            Column(){
                Text(jeu.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.Underline)
                )
                var deb = "Genre : "
                for(elt in IGDB.games[0].genres){
                    deb += IGDB.genres.find({elt == it.id})?.name + ", "
                }
                deb.dropLast(2)

                var mygenres = "Genres : " + IGDB.genres.filter{ it.id in jeu.genres }.joinToString(", ") { it.name }
                Text(text = mygenres,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }

    }


}