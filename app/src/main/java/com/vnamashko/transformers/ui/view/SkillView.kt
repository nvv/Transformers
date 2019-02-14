package com.vnamashko.transformers.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.vnamashko.transformers.R
import com.vnamashko.transformers.network.model.Transformer
import kotlinx.android.synthetic.main.skill_view.view.*

/**
 * @author Vlad Namashko
 */
class SkillView(context: Context?, index: Int, value: Int) : LinearLayout(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.skill_view, null, false)

        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val name = when (index) {
            Transformer.STRENGTH -> R.string.strength
            Transformer.INTELLIGENCE -> R.string.intelligence
            Transformer.SPEED -> R.string.speed
            Transformer.ENDURANCE -> R.string.endurance
            Transformer.RANK -> R.string.rank
            Transformer.COURAGE -> R.string.courage
            Transformer.FIREPOWER -> R.string.firepower
            Transformer.SKILL -> R.string.skill
            else -> R.string.err
        }

        view.name.setText(name)
        view.seek.progress = value
        view.progress.progress = value
        view.value.text = value.toString()

        addView(view)
    }

    fun setValue(v: Int) {
        progress.progress = v
        seek.progress = v
        value.text = v.toString()
    }
}