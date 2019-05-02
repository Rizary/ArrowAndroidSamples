package com.github.jorgecastillo.kotlinandroid.io.algebras.ui

import android.content.Context
import arrow.Kind
import arrow.effects.typeclasses.suspended.concurrent.Fx
import com.github.jorgecastillo.kotlinandroid.io.runtime.ui.SuperHeroDetailActivity

fun <F> Fx<F>.goToHeroDetailsPage(ctx: Context, heroId: String): Kind<F, Unit> = fx {
    !effect { SuperHeroDetailActivity.launch(ctx, heroId) }
}
