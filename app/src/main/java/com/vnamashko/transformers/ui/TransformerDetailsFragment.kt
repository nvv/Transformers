package com.vnamashko.transformers.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import com.vnamashko.transformers.R
import com.vnamashko.transformers.core.DependencyLocator
import com.vnamashko.transformers.network.model.Transformer
import com.vnamashko.transformers.network.model.Transformer.Companion.TEAM_AUTOBOT
import com.vnamashko.transformers.network.model.Transformer.Companion.TEAM_DECEPTICON
import com.vnamashko.transformers.network.service.ApiService
import com.vnamashko.transformers.ui.view.RxProgressDialog
import com.vnamashko.transformers.ui.view.SkillView
import com.vnamashko.transformers.ui.vm.TransformerDetailsVM
import com.vnamashko.transformers.ui.vm.TransformerListVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.skill_view.view.*
import kotlinx.android.synthetic.main.transformer_details_view.*
import javax.inject.Inject


/**
 * @author Vlad Namashko
 */
class TransformerDetailsFragment : BaseFragment() {

    private lateinit var model: TransformerDetailsVM
    private lateinit var listModel: TransformerListVM

    @set:Inject
    lateinit var api: ApiService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.transformer_details_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DependencyLocator.getInstance().coreComponent.inject(this)

        val transformer = arguments?.getParcelable<Transformer>(ARG_TRANSFORMER)
        model = ViewModelProviders.of(this, object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TransformerDetailsVM(transformer) as T
            }
        }).get(TransformerDetailsVM::class.java)

        listModel = ViewModelProviders.of(requireActivity()).get(TransformerListVM::class.java)

        back.setOnClickListener {
            hideSoftKeyboard()
            activity?.onBackPressed()
        }

        title.text = DependencyLocator.getInstance().context.getString(
                if (model.isEdit) R.string.edit_transformer else R.string.create_transformer)

        save.setOnClickListener {
            disposable.add((if (model.isEdit) api.update(model.transformer) else api.add(model.transformer))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(RxProgressDialog<Transformer>(requireActivity()))
                    .subscribe({
                        hideSoftKeyboard()
                        listModel.addUpdateTransformer(it)
                        activity?.onBackPressed()
                    }, { th -> handleError(th) }))
        }

        delete.visibility = if (model.isEdit) View.VISIBLE else View.GONE
        transformer?.id?.let { id ->
            delete.setOnClickListener {
                val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
                builder.setTitle(R.string.confirm)
                    .setMessage(R.string.confirm_message)
                    .setPositiveButton(R.string.btn_ok) { dialog, which ->
                        run {
                            dialog.dismiss()
                            delete(id)
                        }
                    }
                    .setNegativeButton(R.string.btn_cancel) { dialog, which -> dialog.dismiss() }

                builder.create().show()
            }
        }

        model.transformer.skills.set.forEachIndexed { index, skill ->
            val view = SkillView(context, index, skill)

            view.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                    val value = Math.max(Transformer.SKILLS_MIN, value)
                    model.transformer.skills.set[index] = value
                    view.setValue(value)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            skills.addView(view)
        }

        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                model.transformer.name = s.toString()
                s?.let {
                    val isEnabled = !it.isEmpty()
                    save.isEnabled = isEnabled
                    syncSaveEnabled(isEnabled)
                }

            }
        })

        icon_decepticon.setOnClickListener {
            if (model.transformer.team != TEAM_DECEPTICON) {
                selectTeam(TEAM_DECEPTICON)
            }
        }

        icon_autobot.setOnClickListener {
            if (model.transformer.team != TEAM_AUTOBOT) {
                selectTeam(TEAM_AUTOBOT)
            }
        }

        transformer?.let {
            selectTeam(it.team, false)
            name.setText(it.name)
        } ?: run {
            selectTeam(TEAM_AUTOBOT, false)
        }

        val isSaveEnavled = !model.transformer.name.isEmpty()
        syncSaveEnabled(isSaveEnavled)
        save.isEnabled = isSaveEnavled

        name_layout.clearFocus()
    }

    private fun delete(id: String) {
        disposable.add(api.delete(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(RxProgressDialog<Unit>(requireActivity()))
            .subscribe({
                hideSoftKeyboard()
                listModel.deleteTransformer(id)
                activity?.onBackPressed()
            }, { th -> handleError(th) })
        )
    }

    private fun handleError(throwable: Throwable) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setTitle(R.string.error)
                .setMessage(R.string.error_edit_item)
                .setPositiveButton(R.string.btn_ok) { dialog, which -> dialog.dismiss() }

        builder.create().show()
    }

    fun hideSoftKeyboard() {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.let {
            imm.hideSoftInputFromWindow(name.windowToken, 0)
        }
    }

    private fun syncSaveEnabled(isEnabled: Boolean) {
        save.setColorFilter(name.context.resources.getColor(if (isEnabled) R.color.white else R.color.grey))
    }

    private fun selectTeam(selectedTeam: String, animateTeamLogo: Boolean = true) {

        // update model
        model.transformer.team = selectedTeam

        // icons

        val selected = if (selectedTeam == TEAM_AUTOBOT) icon_autobot else icon_decepticon
        val unselected = if (selectedTeam == TEAM_AUTOBOT) icon_decepticon else icon_autobot

        val size = resources.getDimension(R.dimen.icon_size).toInt()
        val smallSize = resources.getDimension(R.dimen.icon_size_small).toInt()

        val selectedParams = selected.layoutParams
        val unselectedParams = unselected.layoutParams

        if (animateTeamLogo) {
            val anim = ValueAnimator.ofInt(smallSize, size)
            anim.addUpdateListener { animation ->
                val value = animation?.animatedValue as Int
                selectedParams.width = value
                selectedParams.height = value

                unselectedParams.width = size - (value - smallSize)
                unselectedParams.height = size - (value - smallSize)

                selected.layoutParams = selectedParams
                unselected.layoutParams = unselectedParams
            }
            anim.duration = ICON_TRANSITION_ANIM_DUR
            anim.start()
        } else {
            selectedParams.width = size
            selectedParams.height = size

            unselectedParams.width = smallSize
            unselectedParams.height = smallSize
            unselectedParams.height = smallSize

            selected.layoutParams = selectedParams
            unselected.layoutParams = unselectedParams
        }

        // background
        val color = if (selectedTeam == TEAM_AUTOBOT) R.color.autobot else R.color.decepticon

        context?.let {
            val colorFrom = (container.background as ColorDrawable).color
            val colorTo = ContextCompat.getColor(it, color)
            val animation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            animation.duration = BACKGROUND_TRANSITION_ANIM_DUR
            animation.addUpdateListener { animator ->
                container.setBackgroundColor(animator.animatedValue as Int)
                toolbar.setBackgroundColor(animator.animatedValue as Int)
                activity?.window?.statusBarColor = colorTo
            }
            animation.start()
        }

        // team name
        val teamName = getString(if (selectedTeam == TEAM_AUTOBOT) R.string.autobots else R.string.decepticons)
        team.text = getString(R.string.team, teamName)
    }

    override fun canChangeStatusBarColor() = true

    override fun getTitle() = DependencyLocator.getInstance().context.getString(R.string.create_transformer)

    override fun hasBackNavigation(): Boolean = true

    override fun hasToolbar(): Boolean = true

    companion object {

        private val ARG_TRANSFORMER = "arg_transformer"
        private const val BACKGROUND_TRANSITION_ANIM_DUR = 250L
        private const val ICON_TRANSITION_ANIM_DUR = 500L

        fun newInstance(transformer: Transformer?): TransformerDetailsFragment {
            val fragment = TransformerDetailsFragment()
            fragment.arguments = Bundle()
            fragment.arguments?.putParcelable(ARG_TRANSFORMER, transformer)
            return fragment
        }

        val TAG = "TransformerDetailsFragment"
    }

}