package com.vnamashko.transformers.ui.adapter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vnamashko.transformers.R
import com.vnamashko.transformers.fight.Battle
import com.vnamashko.transformers.network.model.Transformer
import com.vnamashko.transformers.network.model.Transformer.Companion.TEAM_AUTOBOT
import com.vnamashko.transformers.ui.view.SkillGraphView
import kotlinx.android.synthetic.main.battle_header_list_view.view.*
import kotlinx.android.synthetic.main.transformer_item_list_view.view.*


/**
 * @author Vlad Namashko
 */
class TransformerListAdapter(private val listener: ClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Transformer>()
    private var isBattlePossible = false

    fun setItems(transformers: List<Transformer>) {
        items.clear()
        items.addAll(transformers)

        isBattlePossible = Battle(items).isBattlePossible()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) =
            when (position) {
                0 -> if (isBattlePossible) TYPE_BATTLE else TYPE_HEADER
                else -> TYPE_ITEM
            }


    override fun onCreateViewHolder(parent: ViewGroup, type: Int) =
            when (type) {
                TYPE_HEADER -> EmpyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.empty_header_list_view, parent, false))
                TYPE_BATTLE -> BattleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.battle_header_list_view, parent, false))
                else -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.transformer_item_list_view, parent, false))
            }

    override fun getItemCount() = items.size + 1

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, pos: Int) {

        when (viewHolder) {
            is EmpyViewHolder -> {
            }
            is BattleViewHolder -> {
                viewHolder.bind(listener)
            }
            else -> {
                val holder = viewHolder as ViewHolder
                val item = items[pos - 1]
                holder.bind(item)
                holder.view.setOnClickListener {
                    listener.onClick(item)
                }
            }
        }

    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Transformer) {
            view.name.text = item.name
            view.rating.text = item.rating().toString()

            val isAutoBot = item.team == TEAM_AUTOBOT
            view.team_logo.setImageResource(if (isAutoBot) R.drawable.ic_autobot else R.drawable.ic_decepticon)
            view.team_logo.setBackgroundResource(if (isAutoBot) R.drawable.team_logo_autobot_round_bg else R.drawable.team_logo_decepticon_round_bg)

            val color = generateColor(item.rating(), 5, 50)
            view.rating_graph.setBackgroundColor(color)

            view.skills.removeAllViews()

            item.skills.set.forEachIndexed { index, skill ->
                view.skills.addView(SkillGraphView(view.context, index, skill, generateColor(skill, 1, 10)))
            }

            view.expand.setOnClickListener {
                val showDetails = view.skills.visibility == View.GONE

                view.skills.visibility = if (showDetails) View.VISIBLE else View.GONE
                ObjectAnimator.ofFloat(view.expand, "rotation", if (showDetails) 0F else 180F,
                        if (showDetails) 180F else 0F).setDuration(ROTATION_ANIM_DURATION).start()

            }
        }

        private fun generateColor(item: Int, min: Int, max: Int) =
                ArgbEvaluator().evaluate((max - item) / (max - min).toFloat(),
                        ContextCompat.getColor(itemView.context, R.color.red_dark),
                        ContextCompat.getColor(itemView.context, R.color.green)) as Int
    }

    class EmpyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class BattleViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(listener: ClickListener) {
            view.setOnClickListener {

                view.title.visibility = View.GONE
                view.container.postDelayed({
                    listener.beginBattle()
                    view.title.postDelayed({
                        view.title.visibility = View.VISIBLE
                    }, 1000)
                }, 750)

                // TODO: later - by some devices "endTransition" invoked before animation is finished
                /*
                view.container.layoutTransition.addTransitionListener(object : LayoutTransition.TransitionListener {
                    override fun startTransition(transition: LayoutTransition?, container: ViewGroup?, view: View?, transitionType: Int) {
                    }

                    override fun endTransition(transition: LayoutTransition?, container: ViewGroup?, view1: View?, transitionType: Int) {
                        view.container.layoutTransition.removeTransitionListener(this)
                        listener.beginBattle()
                        view.title.postDelayed({
                            view.title.visibility = View.VISIBLE
                        }, 1000)
                    }

                })
                view.title.visibility = View.GONE
                */
            }
        }
    }

    interface ClickListener {
        fun onClick(transformer: Transformer)

        fun beginBattle()
    }

    companion object {
        const val ROTATION_ANIM_DURATION = 250L

        const val TYPE_HEADER = 0
        const val TYPE_BATTLE = 1
        const val TYPE_ITEM = 2
    }
}