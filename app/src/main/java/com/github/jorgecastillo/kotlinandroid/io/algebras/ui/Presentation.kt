package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.core.Either
import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.HeroesUseCases
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.SuperHeroViewModel
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage.Size.PORTRAIT_UNCANNY

interface SuperHeroesView {

    fun showNotFoundError(): Unit

    fun showGenericError(): Unit

    fun showAuthenticationError(): Unit
}

interface SuperHeroesListView : SuperHeroesView {

    fun drawHeroes(heroes: List<SuperHeroViewModel>): Unit
}

interface SuperHeroDetailView : SuperHeroesView {

    fun drawHero(hero: SuperHeroViewModel)
}

/**
 * On tagless-final module we built this operations over abstract behaviors defined on top of an F
 * type. This is equivalent, but already fixing the type F to IO, for simplicity. Sometimes you're
 * okay fixing the type to some concrete type you know will fulfill your needs for all the cases.
 * But remember: you're losing polymorphism on your program when doing this.
 */
object Presentation {

    fun onHeroListItemClick(ctx: Context, heroId: String): IO<Unit> =
            Navigation.goToHeroDetailsPage(ctx, heroId)

    private fun displayErrors(view: SuperHeroesView, t: Throwable) {
        when (CharacterError.fromThrowable(t)) {
            is CharacterError.NotFoundError -> view.showNotFoundError()
            is CharacterError.UnknownServerError -> view.showGenericError()
            is CharacterError.AuthenticationError -> view.showAuthenticationError()
        }
    }

    fun getAllHeroes(): IO<List<CharacterDto>> = fx {
        !HeroesUseCases.getHeroes()
    }

    fun handleHeroesListResult(
            view: SuperHeroesListView,
            result: Either<Throwable, List<CharacterDto>>): Unit {

        result.fold(
                ifLeft = { displayErrors(view, it) },
                ifRight = {
                    view.drawHeroes(it.map { heroDto ->
                        SuperHeroViewModel(
                                heroDto.id,
                                heroDto.name,
                                heroDto.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
                                heroDto.description
                        )
                    })
                }
        )
    }

    fun drawSuperHeroDetails(heroId: String): IO<CharacterDto> = fx {
        !HeroesUseCases.getHeroDetails(heroId)
    }

    fun handleDetailsResult(view: SuperHeroDetailView,
                            result: Either<Throwable, CharacterDto>): Unit {
        result.fold(
                ifLeft = { displayErrors(view, it) },
                ifRight = { heroDto ->
                    view.drawHero(
                            SuperHeroViewModel(
                                    heroDto.id,
                                    heroDto.name,
                                    heroDto.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
                                    heroDto.description
                            )
                    )
                }
        )
    }
}
