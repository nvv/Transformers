package com.vnamashko.transformers

import com.vnamashko.transformers.fight.Battle
import com.vnamashko.transformers.fight.Battle.Companion.DRAW
import com.vnamashko.transformers.fight.Battle.Companion.WIN_WIN
import com.vnamashko.transformers.network.model.Transformer
import com.vnamashko.transformers.network.model.Transformer.Companion.TEAM_AUTOBOT
import com.vnamashko.transformers.network.model.Transformer.Companion.TEAM_DECEPTICON
import org.junit.Assert
import org.junit.Test
import kotlin.math.min

/**
 * @author Vlad Namashko
 */
class BattleTest {

    private val optimusPrime = Transformer("Optimus Prime", 10, TEAM_AUTOBOT)
    private val predaking = Transformer("PREDAKING", 10, TEAM_DECEPTICON)

    private val any = Transformer("Any", 5, TEAM_DECEPTICON)

    private val minAutobot = Transformer("Min Autobot", 1, TEAM_AUTOBOT)
    private val minDecepticon = Transformer("Min Autobot", 1, TEAM_DECEPTICON)
    private val maxDecepticon = Transformer("Max Deception", 10, TEAM_DECEPTICON)

    @Test
    fun `test check rating`() {
        Assert.assertEquals(optimusPrime.rating(), 50)
        Assert.assertEquals(any.rating(), 25)
        Assert.assertEquals(minAutobot.rating(), 5)
    }

    @Test
    fun `test is unbeatable`() {
        Assert.assertEquals(optimusPrime.isUnbeatable(), true)
        Assert.assertEquals(predaking.isUnbeatable(), true)
        Assert.assertEquals(any.isUnbeatable(), false)
    }

    @Test
    fun `test regular battle`() {
        Assert.assertEquals(Battle.getWinner(maxDecepticon, minAutobot), TEAM_DECEPTICON)
        Assert.assertEquals(Battle.getWinner(optimusPrime, any), TEAM_AUTOBOT)
        Assert.assertEquals(Battle.getWinner(predaking, optimusPrime), WIN_WIN)
        Assert.assertEquals(Battle.getWinner(minDecepticon, minAutobot), DRAW)
        Assert.assertEquals(Battle.getWinner(any, minAutobot), TEAM_DECEPTICON)
    }

    @Test
    fun `test intimidating`() {
        Assert.assertEquals(Battle.checkIntimidating(optimusPrime, minAutobot), optimusPrime)
        Assert.assertEquals(Battle.checkIntimidating(predaking, any), predaking)
        Assert.assertEquals(Battle.checkIntimidating(minAutobot, any), any)
        Assert.assertEquals(Battle.checkIntimidating(minAutobot, minAutobot), null)
    }

    @Test
    fun `test skilled`() {
        Assert.assertEquals(Battle.checkSkill(optimusPrime, any), optimusPrime)
        Assert.assertEquals(Battle.checkSkill(any, minAutobot), any)
        Assert.assertEquals(Battle.checkSkill(any, any), null)
    }

    @Test
    fun `test better`() {
        Assert.assertEquals(Battle.checkRating(any, predaking), predaking)
        Assert.assertEquals(Battle.checkRating(minAutobot, any), any)
        Assert.assertEquals(Battle.checkRating(predaking, optimusPrime), null)
    }

}