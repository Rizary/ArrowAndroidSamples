package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx
import com.github.jorgecastillo.kotlinandroid.io.runtime.ui.SuperHeroDetailActivity

object Navigation {

    fun goToHeroDetailsPage(ctx: Context, heroId: String): IO<Unit> = fx {
        !effect { SuperHeroDetailActivity.launch(ctx, heroId) }
    }
}
