package com.vnamashko.transformers.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vnamashko.transformers.MainActivity
import com.vnamashko.transformers.R
import com.vnamashko.transformers.core.DependencyLocator
import com.vnamashko.transformers.network.model.Transformer
import com.vnamashko.transformers.network.service.ApiService
import com.vnamashko.transformers.ui.adapter.TransformerListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.transformer_list_view.*
import javax.inject.Inject
import android.graphics.Rect
import com.vnamashko.transformers.fight.Battle
import com.vnamashko.transformers.network.model.Transformers
import com.vnamashko.transformers.ui.view.RxProgressDialog
import com.vnamashko.transformers.ui.vm.TransformerDetailsVM
import com.vnamashko.transformers.ui.vm.TransformerListVM
import com.vnamashko.transformers.utils.ViewUtils


/**
 * @author Vlad Namashko
 */
class TransformerListFragment : BaseFragment() {

    private lateinit var model: TransformerListVM

    private val adapter = TransformerListAdapter(object : TransformerListAdapter.ClickListener {
        override fun beginBattle() {
            (activity as MainActivity).startBattle()
        }

        override fun onClick(transformer: Transformer) {
            (activity as MainActivity).showDetails(transformer)
        }
    })

    @set:Inject
    lateinit var api: ApiService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        DependencyLocator.getInstance().coreComponent.inject(this)
        return inflater.inflate(R.layout.transformer_list_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProviders.of(requireActivity()).get(TransformerListVM::class.java)

        items.layoutManager = LinearLayoutManager(context)
        items.addItemDecoration(object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = ViewUtils.getDimensionSize(5).toInt()
                }
            }
        })

        disposable.add(model.observer.subscribe {
            adapter.setItems(model.items())
        })

        disposable.add(api.getAll()
                .subscribeOn(Schedulers.io())
                .compose(RxProgressDialog<Transformers>(requireActivity()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    model.setTransformers(it.transformers)

                    // set adapter after first portion is ready
                    items.adapter = adapter
                }, {

                }))

        add.setOnClickListener {
            // add new transformer
            (activity as MainActivity).showDetails(null)
        }
    }

    override fun getTitle() = DependencyLocator.getInstance().context.getString(R.string.roster)

    override fun hasBackNavigation() = false

    override fun hasToolbar() = false

    override fun canChangeStatusBarColor() = false

    companion object {

        fun newInstance(): TransformerListFragment {
            return TransformerListFragment()
        }

        val TAG = "TransformerListFragment"
    }
}