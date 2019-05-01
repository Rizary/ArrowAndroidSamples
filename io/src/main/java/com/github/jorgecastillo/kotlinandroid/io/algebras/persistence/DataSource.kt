package com.github.jorgecastillo.kotlinandroid.io.algebras.persistence

import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx
import com.github.jorgecastillo.kotlinandroid.BuildConfig
import com.karumi.marvelapiclient.CharacterApiClient
import com.karumi.marvelapiclient.MarvelApiConfig
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.CharactersQuery

object DataSource {

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

    fun fetchAllHeroes(): IO<List<CharacterDto>> = fx {
        val query = fetchHeroesQuery()
        !NonBlocking.effect { fetchHeroes(query) }
    }

    fun fetchHeroDetails(heroId: String): IO<CharacterDto> = fx {
        !NonBlocking.effect { fetchHero(heroId) }
    }
}
