package com.vnamashko.transformers.network.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * @author Vlad Namashko
 */
data class Transformer(var id: String? = null, var name: String, var team: String, var icon: String? = null) : Parcelable {

    val skills: Skills = Skills()

    constructor(id: String?, name: String, skills: Skills, team: String, icon: String?): this(id, name, team, icon) {
        for (i in 0 until SKILLS_SIZE) {
            this.skills.set[i] = skills[i]
        }
    }

    // for tests
    constructor(name: String, defaultSkill: Int, team: String): this(null, name, team, null) {
        for (i in 0 until SKILLS_SIZE) {
            this.skills.set[i] = defaultSkill
        }
    }


    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        team = parcel.readString()
        icon = parcel.readString()

        // skills
        skills.set.forEachIndexed { index, i ->
            skills.set[index] = parcel.readInt()
        }
    }

    fun rating() = skills.rating()

    constructor() : this(name = "", team = TEAM_AUTOBOT)

    companion object {

        const val TEAM_AUTOBOT = "A"
        const val TEAM_DECEPTICON = "D"

        const val STRENGTH = 0
        const val INTELLIGENCE = 1
        const val SPEED = 2
        const val ENDURANCE = 3
        const val RANK = 4
        const val COURAGE = 5
        const val FIREPOWER = 6
        const val SKILL = 7

        const val SKILLS_SIZE = 8
        const val SKILLS_MAX = 10
        const val SKILLS_MIN = 1
        private const val SKILLS_DEFAULT = SKILLS_MAX / 2

        @JvmField val CREATOR = object : Parcelable.Creator<Transformer> {

            override fun createFromParcel(parcel: Parcel): Transformer {
                return Transformer(parcel)
            }

            override fun newArray(size: Int): Array<Transformer?> {
                return arrayOfNulls(size)
            }

        }
    }

    fun isUnbeatable(): Boolean {
        return when (name.toLowerCase()) {
            "optimus prime",
            "predaking" -> true
            else -> false
        }
    }

    val isAutobot: Boolean
        get() = team == TEAM_AUTOBOT

    val isDecepticon: Boolean
        get() = team == TEAM_DECEPTICON


    operator fun get(position: Int) = skills[position]

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(team)
        parcel.writeString(icon)

        // skills
        skills.set.forEach {
            parcel.writeInt(it)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    class Skills(strength: Int, intelligence: Int, speed: Int, endurance: Int,
                 rank: Int, courage: Int, firepower: Int, skill: Int) : Serializable {

        constructor(average: Int) : this(average, average, average, average, average, average, average, average)

        constructor() : this(SKILLS_DEFAULT)

        internal val set = Array(SKILLS_SIZE) { SKILLS_DEFAULT }

        internal fun rating() = set[STRENGTH] + set[INTELLIGENCE] + set[SPEED] + set[ENDURANCE] + set[FIREPOWER]

        init {
            set[STRENGTH] = strength
            set[INTELLIGENCE] = intelligence
            set[SPEED] = speed
            set[ENDURANCE] = endurance
            set[RANK] = rank
            set[COURAGE] = courage
            set[FIREPOWER] = firepower
            set[SKILL] = skill
        }

        operator fun get(position: Int) = set[position]

    }

}