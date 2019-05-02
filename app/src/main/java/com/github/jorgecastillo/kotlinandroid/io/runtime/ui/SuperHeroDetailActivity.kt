package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx
import arrow.effects.extensions.io.unsafeRun.runNonBlocking
import arrow.unsafe
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.R.string
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.SuperHeroDetailView
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.extensions.loadImageAsync
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.getSuperHeroDetails
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.HeroViewState
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detail.*

class SuperHeroDetailActivity : AppCompatActivity(), SuperHeroDetailView {

    companion object {
        const val EXTRA_HERO_ID = "EXTRA_HERO_ID"

        fun launch(source: Context, heroId: String) {
            val intent = Intent(source, SuperHeroDetailActivity::class.java)
            intent.putExtra(EXTRA_HERO_ID, heroId)
            source.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
    }

    override fun onResume() {
        super.onResume()
        intent.extras?.let {
            val heroId = it.getString(EXTRA_HERO_ID)
            if (heroId == null) {
                closeWithError()
            } else {
                unsafe {
                    runNonBlocking({ IO.fx().getSuperHeroDetails(heroId, this@SuperHeroDetailActivity) }, {})
                }
            }
        } ?: closeWithError()
    }

    private fun closeWithError() {
        Toast.makeText(this, string.hero_id_needed, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loader.visibility = View.GONE
    }

    override fun drawHero(hero: HeroViewState) = runOnUiThread {
        collapsingToolbar.title = hero.name
        description.text = hero.description.let { if (it.isNotEmpty()) it else getString(string.empty_description) }
        headerImage.loadImageAsync(hero.photoUrl)
    }

    override fun showNotFoundError() = runOnUiThread {
        Snackbar.make(appBar, string.not_found, Snackbar.LENGTH_SHORT).show()
    }

    override fun showGenericError() = runOnUiThread {
        Snackbar.make(appBar, string.generic, Snackbar.LENGTH_SHORT).show()
    }

    override fun showAuthenticationError() = runOnUiThread {
        Snackbar.make(appBar, string.authentication, Snackbar.LENGTH_SHORT).show()
    }
}
