package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.HeroesUseCases
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.HeroViewState
import com.karumi.marvelapiclient.model.CharacterDto
import com.karumi.marvelapiclient.model.MarvelImage.Size.PORTRAIT_UNCANNY

interface SuperHeroesView {

    fun showLoading(): Unit

    fun hideLoading(): Unit

    fun showNotFoundError(): Unit

    fun showGenericError(): Unit

    fun showAuthenticationError(): Unit
}

interface SuperHeroesListView : SuperHeroesView {

    fun drawHeroes(heroes: List<HeroViewState>): Unit
}

interface SuperHeroDetailView : SuperHeroesView {

    fun drawHero(hero: HeroViewState)
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

    private fun displayErrors(view: SuperHeroesView, t: Throwable): Unit {
        when (CharacterError.fromThrowable(t)) {
            is CharacterError.NotFoundError -> view.showNotFoundError()
            is CharacterError.UnknownServerError -> view.showGenericError()
            is CharacterError.AuthenticationError -> view.showAuthenticationError()
        }
    }

    fun getAllHeroes(view: SuperHeroesListView): IO<Unit> = fx {
        !effect { view.showLoading() }
        val maybeHeroes = !HeroesUseCases.getHeroes().attempt()
        !effect { view.hideLoading() }
        !effect {
            maybeHeroes.fold(
                    ifLeft = { displayErrors(view, it) },
                    ifRight = { view.drawHeroes(it.map { heroDto -> heroDto.toViewState() }) }
            )
        }
    }

    fun drawSuperHeroDetails(heroId: String, view: SuperHeroDetailView): IO<Unit> = fx {
        !effect { view.showLoading() }
        val maybeHero = !HeroesUseCases.getHeroDetails(heroId).attempt()
        !effect { view.hideLoading() }
        !effect {
            maybeHero.fold(
                    ifLeft = { displayErrors(view, it) },
                    ifRight = { heroDto -> view.drawHero(heroDto.toViewState()) }
            )
        }
    }
}

fun CharacterDto.toViewState() = HeroViewState(
        id,
        name,
        thumbnail.getImageUrl(PORTRAIT_UNCANNY),
        description
)
