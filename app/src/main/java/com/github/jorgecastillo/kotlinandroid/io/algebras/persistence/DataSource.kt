package com.github.jorgecastillo.kotlinandroid.io.algebras.persistence

import arrow.Kind
import arrow.effects.typeclasses.suspended.concurrent.Fx
import com.github.jorgecastillo.kotlinandroid.BuildConfig
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery
import kotlinx.coroutines.Dispatchers

private val apiClient
    get() = CharacterApiClient(
            MarvelApiConfig.Builder(
                    BuildConfig.MARVEL_PUBLIC_KEY,
                    BuildConfig.MARVEL_PRIVATE_KEY
            ).debug().build()
    )

private fun fetchHeroesQuery(): CharactersQuery =
        CharactersQuery.Builder.create().withOffset(0).withLimit(50).build()

private fun fetchHero(heroId: String) =
        apiClient.getCharacter(heroId).response

private fun fetchHeroes(query: CharactersQuery): List<CharacterDto> =
        apiClient.getAll(query).response.characters

fun <F> Fx<F>.fetchAllHeroes(): Kind<F, List<CharacterDto>> = fx {
    val query = fetchHeroesQuery()
    val heroes = !NonBlocking.effect { fetchHeroes(query) }
    continueOn(Dispatchers.Main)
    heroes
}

fun <F> Fx<F>.fetchHeroDetails(heroId: String): Kind<F, CharacterDto> = fx {
    val hero = !NonBlocking.effect { fetchHero(heroId) }
    continueOn(Dispatchers.Main)
    hero
}
