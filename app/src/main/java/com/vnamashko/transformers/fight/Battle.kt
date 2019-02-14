package com.vnamashko.transformers.fight

import android.support.annotation.VisibleForTesting
import com.vnamashko.transformers.network.model.Transformer
import com.vnamashko.transformers.network.model.Transformer.Companion.COURAGE
import com.vnamashko.transformers.network.model.Transformer.Companion.RANK
import com.vnamashko.transformers.network.model.Transformer.Companion.SKILL
import com.vnamashko.transformers.network.model.Transformer.Companion.STRENGTH
import com.vnamashko.transformers.network.model.Transformer.Companion.TEAM_AUTOBOT
import com.vnamashko.transformers.network.model.Transformer.Companion.TEAM_DECEPTICON

/**
 * @author Vlad Namashko
 */
class Battle(transformers: List<Transformer>) {

    private val autobots = transformers.filter { it.isAutobot }.sortedByDescending { it[RANK] }
    private val decepticons = transformers.filter { it.isDecepticon }.sortedByDescending { it[RANK] }

    fun isBattlePossible() = autobots.isNotEmpty() && decepticons.isNotEmpty()

    lateinit var victor: String
    val results = ArrayList<Result>()

    fun simulateBattle() {

        val battles = Math.min(autobots.size, decepticons.size)

        var autobotWins = 0
        var decepticonWins = 0
        var massDestruction = false
        for (i in 0 until battles) {
            val autobot = autobots[i]
            val decepticon = decepticons[i]

            val res = getWinner(autobot, decepticon)
            results.add(Result(autobot, decepticon, res))

            if (res == WIN_WIN) {
                massDestruction = true
                break
            }

            when (res) {
                TEAM_AUTOBOT -> autobotWins++
                TEAM_DECEPTICON -> decepticonWins++
            }
        }

        victor = when {
            massDestruction -> WIN_WIN
            autobotWins > decepticonWins -> TEAM_AUTOBOT
            autobotWins < decepticonWins -> TEAM_DECEPTICON
            else -> DRAW
        }

    }


    companion object {

        const val DRAW = "DRAW"
        const val WIN_WIN = "WIN_WIN"

        @VisibleForTesting
        fun getWinner(autobot: Transformer, decepticon: Transformer): String {
            if (autobot.isUnbeatable() || decepticon.isUnbeatable()) {
                return if (autobot.isUnbeatable() && decepticon.isUnbeatable()) {
                    WIN_WIN
                } else {
                    if (autobot.isUnbeatable()) TEAM_AUTOBOT else TEAM_DECEPTICON
                }
            }

            val betterStrength = checkStrength(autobot, decepticon)
            val betterCourage = checkCourage(autobot, decepticon)

            // opponent ran away
            if (betterStrength != null && betterStrength == betterCourage) {
                return betterStrength.team
            }

            // more skilled one
            checkSkill(autobot, decepticon)?.let {
                return it.team
            }

            // better one
            checkRating(autobot, decepticon)?.let {
                return it.team
            }

            return DRAW
        }


        @VisibleForTesting
        fun checkStrength(t1: Transformer, t2: Transformer) : Transformer? =
            when {
                t1[STRENGTH] >= t2[STRENGTH] + 3 -> t1
                t2[STRENGTH] >= t1[STRENGTH] + 3 -> t2
                else -> null
            }

        @VisibleForTesting
        fun checkCourage(t1: Transformer, t2: Transformer) : Transformer? =
            when {
                t1[COURAGE] >= t2[COURAGE] + 4 -> t1
                t2[COURAGE] >= t1[COURAGE] + 4 -> t2
                else -> null
            }

        @VisibleForTesting
        fun checkSkill(t1: Transformer, t2: Transformer) : Transformer? =
            when {
                t1[SKILL] >= t2[SKILL] + 3 -> t1
                t2[SKILL] >= t1[SKILL] + 3 -> t2
                else -> null
            }

        @VisibleForTesting
        fun checkRating(t1: Transformer, t2: Transformer) : Transformer? =
            when {
                t1.rating() > t2.rating() -> t1
                t1.rating() < t2.rating() -> t2
                else -> null
            }

        @VisibleForTesting
        fun checkIntimidating(t1: Transformer, t2: Transformer): Transformer? {
            val betterStrength = checkStrength(t1, t2)
            val betterCourage = checkCourage(t1, t2)

            // opponent ran away
            return if (betterStrength != null && betterStrength == betterCourage) {
                betterStrength
            } else {
                null
            }
        }
    }

    data class Result(val autobot: Transformer, val deceptiocon: Transformer, var result: String)
}