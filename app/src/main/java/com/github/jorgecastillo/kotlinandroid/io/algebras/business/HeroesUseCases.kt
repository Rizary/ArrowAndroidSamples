package com.github.jorgecastillo.kotlinandroid.io.algebras.business

import arrow.Kind
import arrow.effects.typeclasses.suspended.concurrent.Fx
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.CachePolicy
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.getHeroDetailsWithCachePolicy
import com.github.jorgecastillo.kotlinandroid.io.algebras.persistence.getHeroesWithCachePolicy
import com.karumi.marvelapiclient.model.CharacterDto

fun <F> Fx<F>.getHeroes(): Kind<F, List<CharacterDto>> =
        getHeroesWithCachePolicy(CachePolicy.NetworkOnly)

fun <F> Fx<F>.getHeroDetails(heroId: String): Kind<F, CharacterDto> =
        getHeroDetailsWithCachePolicy(CachePolicy.NetworkOnly, heroId)
