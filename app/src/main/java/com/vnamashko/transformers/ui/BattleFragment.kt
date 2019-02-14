package com.vnamashko.transformers.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vnamashko.transformers.R
import com.vnamashko.transformers.core.DependencyLocator
import com.vnamashko.transformers.fight.Battle
import com.vnamashko.transformers.ui.adapter.BattleAdapter
import com.vnamashko.transformers.ui.vm.TransformerListVM
import kotlinx.android.synthetic.main.battle_view.view.*


/**
 * @author Vlad Namashko
 */
class BattleFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.vnamashko.transformers.R.layout.battle_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model = ViewModelProviders.of(requireActivity()).get(TransformerListVM::class.java)

        view.battle_result.layoutManager = LinearLayoutManager(view.context)
        view.battle_result.adapter = BattleAdapter(requireActivity(), disposable, Battle(model.items()))
    }


    override fun getTitle() = DependencyLocator.getInstance().context.getString(R.string.battle)

    override fun hasBackNavigation() = true

    override fun hasToolbar() = false

    override fun canChangeStatusBarColor() = false


    companion object {

        fun newInstance(): BattleFragment {
            return BattleFragment()
        }

        val TAG = "BattleFragment"
    }
}