package com.insa.mygamelist.ui.componant

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.data.Platforms

@Composable
fun DisplayLogo(platform : Platforms){
    AsyncImage(

        model = "https:"+ (IGDB.platform_logos.find{platform.platform_logo == it.id}?.url?.replace("jpg", "png")?: "//commons.wikimedia.org/wiki/File:No_Image_Available.jpg"),
        contentDescription = "Logos des plateformes supportées",
//        contentScale = ContentScale.FillBounds,
        modifier = Modifier.padding(7.dp).size(70.dp)
    )
}
