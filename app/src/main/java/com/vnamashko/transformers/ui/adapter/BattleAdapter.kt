package com.vnamashko.transformers.ui.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vnamashko.transformers.R
import com.vnamashko.transformers.fight.Battle
import com.vnamashko.transformers.fight.Battle.Companion.DRAW
import com.vnamashko.transformers.fight.Battle.Companion.WIN_WIN
import com.vnamashko.transformers.network.model.Transformer.Companion.TEAM_AUTOBOT
import com.vnamashko.transformers.network.model.Transformer.Companion.TEAM_DECEPTICON
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.battle_item_header.view.*
import kotlinx.android.synthetic.main.battle_item_list_view.view.*
import java.util.concurrent.TimeUnit

/**
 * @author Vlad Namashko
 */
class BattleAdapter(val context: Context?, val disposable: CompositeDisposable, val battle: Battle) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val battleResults = mutableListOf<Battle.Result>()
    var currentAutobotWins = 0
    var currentDecepticonWins = 0
    var totalResult: String? = null

    init {
        battle.simulateBattle()

        disposable.add(Observable
                .interval(1, TimeUnit.SECONDS)
                .map { i -> battle.results[i.toInt()] }
                .take(battle.results.size.toLong())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    battleResults.add(it)
                    notifyItemInserted(battleResults.size)

                    when (it.result) {
                        TEAM_AUTOBOT -> currentAutobotWins++
                        TEAM_DECEPTICON -> currentDecepticonWins++
                        WIN_WIN -> {}
                        else -> {
                            currentAutobotWins++
                            currentDecepticonWins++
                        }
                    }

                    if (battleResults.size == battle.results.size) {
                        totalResult = battle.victor
                    }

                    notifyItemChanged(0)
                })

    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int) =
            when (type) {
                TransformerListAdapter.TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.battle_item_header, parent, false))
                else -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.battle_item_list_view, parent, false))
            }


    override fun getItemViewType(position: Int) =
            when (position) {
                0 -> BattleAdapter.TYPE_HEADER
                else -> BattleAdapter.TYPE_ITEM
            }

    override fun getItemCount() = battleResults.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.itemViewType == TYPE_ITEM) {
            val result = battleResults[position - 1]

            val span = when (result.result) {
                WIN_WIN -> getMassDestructionText(result)
                DRAW -> getDrawText(result)
                else -> getWinnerText(result)
            }

            holder.itemView.result_line.text = span
        } else {
            // only after first row been inserted
            if (battleResults.size == 1) {
                holder.itemView.container.post {
                    holder.itemView.vs.visibility = View.VISIBLE
                }
            } else {
                holder.itemView.vs.visibility = if (battleResults.size > 0) View.VISIBLE else View.GONE
            }

            holder.itemView.autobot_wins.text = currentAutobotWins.toString()
            holder.itemView.decepticon_wins.text = currentDecepticonWins.toString()

            totalResult?.let {
                holder.itemView.total_result.text = context?.getString(when (totalResult) {
                    TEAM_DECEPTICON -> R.string.result_decepticon_wins
                    TEAM_AUTOBOT -> R.string.result_autobot_wins
                    DRAW -> R.string.result_draw
                    else -> R.string.result_total_destruction
                })
                holder.itemView.total_result.visibility = View.VISIBLE

                val smallSize = holder.itemView.resources.getDimension(R.dimen.icon_size_small).toInt()
                val size = holder.itemView.resources.getDimension(R.dimen.icon_size).toInt()

                val autobot = holder.itemView.autobot_logo.layoutParams
                val decepticon = holder.itemView.decepticon_logo.layoutParams

                val anim = ValueAnimator.ofInt(smallSize, size)
                anim.addUpdateListener { animation ->
                    val value = animation?.animatedValue as Int
                    autobot.width = value
                    autobot.height = value

                    decepticon.width = value
                    decepticon.height = value

                    holder.itemView.autobot_logo.layoutParams = autobot
                    holder.itemView.decepticon_logo.layoutParams = decepticon
                }
                anim.duration = ICON_TRANSITION_ANIM_DUR
                anim.start()
            }
        }

    }

    private fun getMassDestructionText(result: Battle.Result): SpannableString {
        val autobotName = result.autobot.name
        val decepticonName = result.deceptiocon.name

        val text = context?.getString(R.string.win_win_case, autobotName, decepticonName)

        val span = SpannableString(text)
        setSpan(span, R.color.autobot, autobotName)
        setSpan(span, R.color.decepticon, decepticonName)
        return span
    }

    private fun getDrawText(result: Battle.Result): SpannableString {
        val autobotName = result.autobot.name
        val decepticonName = result.deceptiocon.name

        val text = context?.getString(R.string.draw_case, autobotName, decepticonName)

        val span = SpannableString(text)
        setSpan(span, R.color.autobot, autobotName)
        setSpan(span, R.color.decepticon, decepticonName)
        return span
    }

    private fun getWinnerText(result: Battle.Result): SpannableString {
        val autobotName = result.autobot.name
        val decepticonName = result.deceptiocon.name

        val autobotWins = result.result == TEAM_AUTOBOT
        val name1 = if (autobotWins) result.autobot.name else result.deceptiocon.name
        val name2 = if (autobotWins) result.deceptiocon.name else result.autobot.name

        val text = context?.getString(R.string.win_case, name1, name2)

        val span = SpannableString(text)
        setSpan(span, R.color.autobot, autobotName)
        setSpan(span, R.color.decepticon, decepticonName)
        return span
    }

    private fun setSpan(span: SpannableString, color: Int, text: String) {
        val start = span.indexOf(text)
        context?.let {
            span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, color)), start, start + text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1

        const val ICON_TRANSITION_ANIM_DUR = 500L
    }
}