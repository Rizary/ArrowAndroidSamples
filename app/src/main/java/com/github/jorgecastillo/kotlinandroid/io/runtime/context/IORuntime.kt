package com.github.jorgecastillo.kotlinandroid.io.runtime.context

import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.IOFx
import arrow.effects.typeclasses.suspended.concurrent.Fx
import kotlin.coroutines.CoroutineContext

/**
 * This is the Type class contract for the required dependencies. It can potentially work over any
 * data type F that supports concurrency, or in other words, any data type F that there's an
 * instance of concurrent Fx for.
 */
interface Runtime<F> : Fx<F> {
    val bgDispatcher: CoroutineContext
    val mainDispatcher: CoroutineContext
}

/**
 * This is the instance of the Runtime we are using to run our app. It works over the IO data type.
 */
interface IORuntime : Runtime<ForIO>, IOFx

fun IO.Companion.runtime(ctx: RuntimeContext) = object : IORuntime {
    override val bgDispatcher: CoroutineContext
        get() = ctx.bgDispatcher
    override val mainDispatcher: CoroutineContext
        get() = ctx.mainDispatcher
}

data class RuntimeContext(
        val bgDispatcher: CoroutineContext,
        val mainDispatcher: CoroutineContext
)
