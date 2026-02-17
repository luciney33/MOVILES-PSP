package com.example.navigationcompose.ui.screens.dragonBall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.domain.model.DragonBallCharacter

@Composable
fun DragonBallListScreen(
    viewModel: ListaViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    text = Constantes.TEXT_PERSONAJES_DRAGON_BALL,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(state.characters, key = { it.id }) { character ->
                CharacterItem(character = character)
            }
        }
    }
}

@Composable
fun CharacterItem(character: DragonBallCharacter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${Constantes.TEXT_RAZA}${character.race}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${Constantes.TEXT_KI}${character.ki}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DragonBallListPreview() {
    val mockList = listOf(
        DragonBallCharacter(
            id = 1,
            name = Constantes.TEXT_GOKU,
            ki = Constantes.TEXT_KI_GOKU,
            race = Constantes.TEXT_SAIYAN,
            description = Constantes.TEXT_HERO,
            image = Constantes.URL_EXAMPLE_GOKU_GIF
        ),
        DragonBallCharacter(
            id = 2,
            name = Constantes.TEXT_FREEZER,
            ki = Constantes.TEXT_KI_FREEZER,
            race = Constantes.TEXT_FRIEZA_RACE,
            description = Constantes.TEXT_VILLAIN,
            image = Constantes.URL_EXAMPLE_FREEZER_GIF
        )
    )

    MaterialTheme {
        Surface {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockList) { character ->
                    CharacterItem(character = character)
                }
            }
        }
    }
}