package com.github.jorgecastillo.kotlinandroid.io.algebras.persistence

import arrow.Kind
import arrow.effects.typeclasses.suspended.concurrent.Fx
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.CachePolicy.*
import com.karumi.marvelapiclient.model.CharacterDto

sealed class CachePolicy {
    object NetworkOnly : CachePolicy()
    object NetworkFirst : CachePolicy()
    object LocalOnly : CachePolicy()
    object LocalFirst : CachePolicy()
}

fun <F> Fx<F>.getHeroesWithCachePolicy(policy: CachePolicy): Kind<F, List<CharacterDto>> =
        when (policy) {
            NetworkOnly -> fetchAllHeroes()
            NetworkFirst -> fetchAllHeroes() // TODO change to conditional call
            LocalOnly -> fetchAllHeroes() // TODO change to local only cache call
            LocalFirst -> fetchAllHeroes() // TODO change to conditional call
        }

fun <F> Fx<F>.getHeroDetailsWithCachePolicy(policy: CachePolicy, heroId: String): Kind<F, CharacterDto> =
        when (policy) {
            NetworkOnly -> fetchHeroDetails(heroId)
            NetworkFirst -> fetchHeroDetails(heroId) // TODO change to conditional call
            LocalOnly -> fetchHeroDetails(heroId) // TODO change to local only cache call
            LocalFirst -> fetchHeroDetails(heroId) // TODO change to conditional call
        }
