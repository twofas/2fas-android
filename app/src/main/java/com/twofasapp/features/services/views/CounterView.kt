package com.twofasapp.features.services.views

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.twofasapp.extensions.getColorFromRes
import com.twofasapp.resources.R
import com.twofasapp.databinding.ViewCounterBinding

class CounterView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = ViewCounterBinding.inflate(LayoutInflater.from(context), this)
    private val codeColorNormal: Int by lazy { context.getColorFromRes(R.color.serviceCodeNormal) }
    private val codeColorExpiring: Int by lazy { context.getColorFromRes(R.color.serviceCodeExpiring) }

    init {
        binding.counterProgress.progressDrawable.mutate()
    }

    fun updateProgress(progress: Int, max: Int) {
        with(binding) {
            counterProgress.max = max
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                counterProgress.setProgress(progress, true)
            } else {
                counterProgress.progress = progress
            }
            counterText.text = progress.toString()

            val colorTo = if (progress in 0..5) codeColorExpiring else codeColorNormal

            if (counterText.currentTextColor == colorTo) {
                return
            }

            val animator = ValueAnimator.ofInt(counterText.currentTextColor, colorTo)
            animator.addUpdateListener {
                counterText.setTextColor(it.animatedValue as Int)
                counterProgress.progressDrawable.setColorFilter(it.animatedValue as Int, PorterDuff.Mode.SRC_IN)
            }
            animator.setEvaluator(ArgbEvaluator())
            animator.setDuration(500)
            animator.start()
        }
    }
}