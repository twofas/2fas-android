package com.twofasapp.design.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.twofasapp.design.databinding.ViewSimpleEntryBinding
import com.twofasapp.extensions.dpToPx
import com.twofasapp.extensions.getDrawableFromAttr
import com.twofasapp.extensions.getString
import com.twofasapp.extensions.makeGone
import com.twofasapp.extensions.makeGoneIfEmpty
import com.twofasapp.extensions.makeInvisible
import com.twofasapp.extensions.makeVisible
import com.twofasapp.extensions.setTint
import com.twofasapp.extensions.setTintRes

class SimpleEntryView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val viewBinding = ViewSimpleEntryBinding.inflate(LayoutInflater.from(context), this)
    private var lastClickTime: Long = 0
    private var model: SimpleEntry = SimpleEntry()

    init {
        setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
        background = ContextCompat.getDrawable(context, context.getDrawableFromAttr(android.R.attr.selectableItemBackground))
        minHeight = dpToPx(56)
    }

    fun update(action: (SimpleEntry) -> SimpleEntry) {
        model = action.invoke(model)
        updateView()
    }

    private fun updateView() {
        with(viewBinding) {
            val subtitleView = if (model.subtitleGravity == SimpleEntry.Gravity.BOTTOM) entrySubtitle else entrySubtitleEnd

            when (model.subtitleGravity) {
                SimpleEntry.Gravity.BOTTOM -> {
                    entrySubtitle.makeVisible()
                    entrySubtitleEnd.makeGone()
                }
                SimpleEntry.Gravity.END -> {
                    entrySubtitle.makeGone()
                    entrySubtitleEnd.makeVisible()
                }
            }

            entryTitle.text = root.context.getString(model.title, model.titleRes)
            subtitleView.text = root.context.getString(model.subtitle, model.subtitleRes)
            subtitleView.makeGoneIfEmpty()

            isEnabled = model.isEnabled
            entryTitle.animate().alpha(if (model.isEnabled) 1f else .3f).setDuration(150).start()
            subtitleView.animate().alpha(if (model.isEnabled) 1f else .3f).setDuration(150).start()
            entryIcon.animate().alpha(if (model.isEnabled) 1f else .3f).setDuration(150).start()

            if (model.drawableRes == null) {
                entryIcon.makeInvisible()
            } else {
                entryIcon.setImageResource(model.drawableRes!!)

                when {
                    model.drawableTint != null -> entryIcon.setTint(model.drawableTint!!)
                    model.drawableTintRes != null -> entryIcon.setTintRes(model.drawableTintRes!!)
                }

                entryIcon.makeVisible()
            }

            if (model.actionIconRes == null) {
                actionIcon.makeGone()
                actionIcon.setOnClickListener(null)
            } else {
                actionIcon.setImageResource(model.actionIconRes!!)
                actionIcon.setOnClickListener { model.actionIconClick?.invoke(model) }
                actionIcon.makeVisible()
            }

            setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime > 300) {
                    lastClickTime = System.currentTimeMillis()
                    model.clickAction?.invoke(model)
                }
            }
        }
    }
}