package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.effects.IO
import arrow.effects.extensions.io.applicativeError.handleError
import arrow.effects.extensions.io.fx.fx
import arrow.unsafe
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.HeroesUseCases
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.CharacterError
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.SuperHeroViewModel
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

    private fun displayErrors(view: SuperHeroesView, t: Throwable): IO<Unit> = unsafe {
        fx {
            !effect {
                when (CharacterError.fromThrowable(t)) {
                    is CharacterError.NotFoundError -> view.showNotFoundError()
                    is CharacterError.UnknownServerError -> view.showGenericError()
                    is CharacterError.AuthenticationError -> view.showAuthenticationError()
                }
            }
        }
    }

    fun getAllHeroes(view: SuperHeroesListView): IO<Unit> = fx {
        val result = !HeroesUseCases.getHeroes()
        !effect {
            view.drawHeroes(result.map {
                SuperHeroViewModel(
                        it.id,
                        it.name,
                        it.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
                        it.description
                )
            })
        }
    }.handleError {
        displayErrors(view, it)
    }

    fun drawSuperHeroDetails(heroId: String, view: SuperHeroDetailView): IO<Unit> = fx {
        val result = !HeroesUseCases.getHeroDetails(heroId)
        !effect {
            view.drawHero(
                    SuperHeroViewModel(
                            result.id,
                            result.name,
                            result.thumbnail.getImageUrl(PORTRAIT_UNCANNY),
                            result.description
                    )
            )
        }
    }.handleError {
        displayErrors(view, it)
    }
}
