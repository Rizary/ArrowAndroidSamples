package com.github.jorgecastillo.kotlinandroid.io.algebras.persistence

import arrow.Kind
import com.github.jorgecastillo.kotlinandroid.BuildConfig
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery

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

fun <F> Runtime<F>.fetchAllHeroes(): Kind<F, List<CharacterDto>> = fx {
    val query = fetchHeroesQuery()
    val heroes = !bgDispatcher.effect { fetchHeroes(query) }
    continueOn(mainDispatcher)
    heroes
}

fun <F> Runtime<F>.fetchHeroDetails(heroId: String): Kind<F, CharacterDto> = fx {
    val hero = !bgDispatcher.effect { fetchHero(heroId) }
    continueOn(mainDispatcher)
    hero
}
