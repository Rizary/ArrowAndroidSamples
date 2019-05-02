package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import arrow.effects.extensions.io.unsafeRun.runNonBlocking
import arrow.unsafe
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.Presentation
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.SuperHeroesListView
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.adapter.HeroesCardAdapter
import com.github.jorgecastillo.kotlinandroid.io.algebras.ui.model.HeroViewState
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class SuperHeroListActivity : AppCompatActivity(), SuperHeroesListView {

    private lateinit var adapter: HeroesCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupList()
    }

    private fun setupList() {
        heroesList.setHasFixedSize(true)
        heroesList.layoutManager = LinearLayoutManager(this)
        adapter = HeroesCardAdapter(itemClick = {
            unsafe { runNonBlocking({ Presentation.onHeroListItemClick(this@SuperHeroListActivity, it.heroId) }, {}) }
        })
        heroesList.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        unsafe {
            runNonBlocking({ Presentation.getAllHeroes(this@SuperHeroListActivity) }, {})
        }
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loader.visibility = View.GONE
    }

    override fun drawHeroes(heroes: List<HeroViewState>) {
        adapter.characters = heroes
        adapter.notifyDataSetChanged()
    }

    override fun showNotFoundError() {
        Snackbar.make(heroesList, R.string.not_found, Snackbar.LENGTH_SHORT).show()
    }

    override fun showGenericError() {
        Snackbar.make(heroesList, R.string.generic, Snackbar.LENGTH_SHORT).show()
    }

    override fun showAuthenticationError() {
        Snackbar.make(heroesList, R.string.authentication, Snackbar.LENGTH_SHORT).show()
    }
}

