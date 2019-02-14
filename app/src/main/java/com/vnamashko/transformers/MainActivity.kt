package com.vnamashko.transformers

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.vnamashko.transformers.core.DependencyLocator
import com.vnamashko.transformers.core.LocalStorage
import com.vnamashko.transformers.network.model.Transformer
import com.vnamashko.transformers.network.service.ApiService
import com.vnamashko.transformers.ui.BaseFragment
import com.vnamashko.transformers.ui.BattleFragment
import com.vnamashko.transformers.ui.TransformerDetailsFragment
import com.vnamashko.transformers.ui.TransformerListFragment
import com.vnamashko.transformers.ui.view.RxProgressDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * @author Vlad Namashko
 */
class MainActivity : AppCompatActivity() {

    @set:Inject
    lateinit var storage: LocalStorage

    @set:Inject
    lateinit var api: ApiService

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DependencyLocator.initInstance(this)
        DependencyLocator.getInstance().coreComponent.inject(this)

        setSupportActionBar(toolbar)

        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.frame)
            if (fragment != null) {
                onFragmentChanged(fragment as BaseFragment)
            } else {
                finish()
            }
        }

        if (storage.isNewUser) {
            disposable.add(api.getToken()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(RxProgressDialog<String>(this))
                    .subscribe({
                        storage.token = it
                        openList()
                    }, {
                        AlertDialog.Builder(this@MainActivity).setMessage(R.string.error_get_token)
                                .setPositiveButton(R.string.btn_ok)
                                { dialog, id ->
                                    finish();
                                }.create().show()
                        it.printStackTrace()
                    })
            )

        } else {
            openList()
        }
    }

    private fun openList() {
        val fragment = TransformerListFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .add(R.id.frame, fragment, TransformerListFragment.TAG)
                .addToBackStack(TransformerListFragment.TAG)
                .commit()

        onFragmentChanged(fragment)
    }

    fun showDetails(transformer: Transformer? = null) {
        val fragment = TransformerDetailsFragment.newInstance(transformer)
        supportFragmentManager.beginTransaction()
                .add(R.id.frame, fragment, TransformerDetailsFragment.TAG)
                .addToBackStack(TransformerDetailsFragment.TAG)
                .commit()
        onFragmentChanged(fragment)
    }

    fun startBattle() {
        val fragment = BattleFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame, fragment, BattleFragment.TAG)
            .addToBackStack(BattleFragment.TAG)
            .commit()
        onFragmentChanged(fragment)
    }

    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount == 1 -> finish()
            else -> super.onBackPressed()
        }
    }

    private fun onFragmentChanged(top: BaseFragment) {
        title = top.getTitle()
        setBackVisibility(top.hasBackNavigation())
        toolbar.visibility = if (top.hasToolbar()) View.GONE else View.VISIBLE
        if (!top.canChangeStatusBarColor()) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
    }

    private fun setBackVisibility(visible: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(visible)
        supportActionBar?.setDisplayShowHomeEnabled(visible)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
