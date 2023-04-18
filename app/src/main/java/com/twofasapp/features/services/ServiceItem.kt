package com.twofasapp.features.services

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.view.animation.AccelerateInterpolator
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.binding.ModelAbstractBindingItem
import com.mikepenz.fastadapter.drag.IDraggable
import com.twofasapp.databinding.ItemServiceBinding
import com.twofasapp.entity.ServiceModel
import com.twofasapp.extensions.context
import com.twofasapp.extensions.getColorFromRes
import com.twofasapp.extensions.setBackgroundTint
import com.twofasapp.extensions.splitForSpelling
import com.twofasapp.extensions.visible
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.model.toColor
import com.twofasapp.resources.R

class ServiceItem(
    model: ServiceModel,
    val isInEditMode: Boolean,
    val isInSearchMode: Boolean,
    val isAnyNoticeVisible: Boolean,
    private val onClick: (model: ServiceModel) -> Unit = {},
    private val onLongClick: (model: ServiceModel) -> Unit = {},
    private val onDragTouch: (viewHolder: RecyclerView.ViewHolder) -> Unit = {},
    private val onRefreshCounterClick: (model: ServiceModel) -> Unit = {},
) : ModelAbstractBindingItem<ServiceModel, ItemServiceBinding>(model), IDraggable {

    private var codeColorNormal: Int? = null
    private var codeColorExpiring: Int? = null

    override var identifier = model.service.id
    override val type = R.id.item_service
    override val isDraggable: Boolean = true

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemServiceBinding.inflate(inflater, parent, false)

    @SuppressLint("ClickableViewAccessibility", "MissingSuperCall")
    override fun bindView(holder: BindingViewHolder<ItemServiceBinding>, payloads: List<Any>) {
        super.bindView(holder, payloads)
        with(holder.binding) {

            root.setOnLongClickListener {
                if (isInEditMode) {
                    false
                } else {
                    onLongClick(model)
                    true
                }
            }

            root.setOnClickListener {
                if (model.service.authType == ServiceDto.AuthType.HOTP && model.isHotpCodeVisible.not() && isInEditMode.not()) {
                    return@setOnClickListener
                }

                onClick(model)
            }

            actionRefreshCounter.setOnClickListener { onRefreshCounterClick(model) }

            name.text = model.service.name
            account.text = if (model.service.name.equals(model.service.otpAccount, true)) "" else model.service.otpAccount
            actionEdit.contentDescription = "Edit ${name.text} service"

            if (model.service.authType == ServiceDto.AuthType.HOTP && model.isHotpCodeVisible.not()) {
                code.text = model.formatCodeInvisible()
            } else {
                code.text = model.formatCode()
            }

            code.pivotX = 0f
            serviceBadge.setBackgroundTint(model.service.badge?.color.toColor(holder.context))

            iconLayout.updateIcon(model.service)

            counter.updateProgress(
                progress = model.counter,
                max = model.service.getPeriod()
            )

            updateCodeColor(holder, model)
            updateStateMode(holder, isInEditMode)
            updateAccessibility(holder, model)
            updateDividers(holder, isAnyNoticeVisible)

            dragHandle.alpha = if (isInSearchMode) .3f else 1f
            dragHandle.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    if (isInSearchMode.not()) {
                        onDragTouch.invoke(holder)
                    }
                }
                return@setOnTouchListener true
            }

            nextCode.pivotX = 0f
            nextCode.text = model.formatNextCode()

            if (model.shouldShowNextToken.not() || isInEditMode || model.service.authType == ServiceDto.AuthType.HOTP) {
                showAccount(this)
                return
            }

            if (model.counter == 5) {
                code.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).setStartDelay(150).start()

                if (account.alpha == 1f) {
                    account.animate()
                        .setDuration(200)
                        .alpha(0f)
                        .translationY(50f)
                        .setInterpolator(AccelerateInterpolator())
                        .withEndAction { }.start()
                }

                if (nextCode.alpha == 0f) {
                    nextCode.animate()
                        .setDuration(200)
                        .alpha(1f)
                        .translationY(0f)
                        .setStartDelay(150)
                        .start()
                }
            } else if (model.counter == (model.service.getPeriod())) {
                code.animate().scaleX(1f).scaleY(1f).setDuration(300).setStartDelay(150).start()

                if (nextCode.alpha == 1f) {
                    nextCode.animate()
                        .setDuration(200)
                        .alpha(0f)
                        .translationY(50f)
                        .setInterpolator(AccelerateInterpolator())
                        .withEndAction { }.start()
                }

                if (account.alpha == 0f) {
                    account.animate()
                        .setDuration(300)
                        .alpha(1f)
                        .translationY(0f)
                        .setStartDelay(150)
                        .start()
                }
            } else {
                if (model.counter > 0 && model.counter < 5) {
                    showNextToken(this)
                } else {
                    showAccount(this)
                }
            }
        }
    }

    private fun showNextToken(binding: ItemServiceBinding) {
        with(binding) {
            code.scaleX = 0.9f
            code.scaleY = 0.9f
            account.alpha = 0f
            nextCode.alpha = 1f
            nextCode.translationY = 0f
        }
    }

    private fun showAccount(binding: ItemServiceBinding) {
        with(binding) {
            code.scaleX = 1.0f
            code.scaleY = 1.0f
            account.alpha = 1f
            account.translationY = 0f
            nextCode.alpha = 0f
        }
    }

    private fun updateCodeColor(holder: BindingViewHolder<ItemServiceBinding>, model: ServiceModel) {
        if (model.service.authType == ServiceDto.AuthType.HOTP) {
            codeColorNormal = holder.context.getColorFromRes(R.color.serviceCodeNormal)
            holder.binding.code.setTextColor(codeColorNormal!!)
            return
        }

        if (codeColorNormal == null) {
            codeColorNormal = holder.context.getColorFromRes(R.color.serviceCodeNormal)
        }

        if (codeColorExpiring == null) {
            codeColorExpiring = holder.context.getColorFromRes(R.color.serviceCodeExpiring)
        }

        val colorTo = if (model.counter in 0..5) codeColorExpiring!! else codeColorNormal!!

        if (holder.binding.code.currentTextColor == colorTo) {
            return
        }

        val animator = ValueAnimator.ofInt(holder.binding.code.currentTextColor, colorTo)
        animator.addUpdateListener {
            holder.binding.code.setTextColor(it.animatedValue as Int)
        }
        animator.setEvaluator(ArgbEvaluator())
        animator.setDuration(500)
        animator.start()
    }

    private fun updateStateMode(holder: BindingViewHolder<ItemServiceBinding>, isInEditMode: Boolean) {
        with(holder.binding) {
            placeholder.isInvisible = isInEditMode.not()
            code.isInvisible = isInEditMode
            dragHandle.visible(isInEditMode)
            actionEdit.visible(isInEditMode)

            when (model.service.authType) {
                ServiceDto.AuthType.TOTP -> {
                    counter.isInvisible = isInEditMode
                    actionRefreshCounter.isVisible = false
                }

                ServiceDto.AuthType.HOTP -> {
                    counter.isVisible = false
                    actionRefreshCounter.isInvisible = isInEditMode
                    actionRefreshCounter.isEnabled = model.isHotpRefreshEnabled
                    actionRefreshCounter.alpha = if (model.isHotpRefreshEnabled) 1f else .3f
                }
            }

            if (isInEditMode) {
                code.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            } else {
                code.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            }
        }
    }

    private fun updateAccessibility(holder: BindingViewHolder<ItemServiceBinding>, model: ServiceModel) {
        holder.binding.counter.accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.text = "${model.counter} seconds left"
            }
        }

        holder.binding.code.accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.text = model.code.splitForSpelling()
            }
        }
    }

    private fun updateDividers(holder: BindingViewHolder<ItemServiceBinding>, isRateAppVisible: Boolean) {
        when {
            isPhone(holder) -> setDividersForPhone(holder, isRateAppVisible)
            isTabletLandscape(holder) -> setDividersForTablet(holder, isRateAppVisible, 3)
            isTablet(holder) -> setDividersForTablet(holder, isRateAppVisible, 2)
        }
    }

    private fun setDividers(holder: BindingViewHolder<ItemServiceBinding>, top: Boolean, bottom: Boolean, start: Boolean, end: Boolean) {
        holder.binding.topDivider.isVisible = top
        holder.binding.bottomDivider.isVisible = bottom
        holder.binding.endDivider.isVisible = end
    }

    private fun setDividersForPhone(holder: BindingViewHolder<ItemServiceBinding>, isRateAppVisible: Boolean) {
        if (isRateAppVisible && holder.adapterPosition == 1) {
            setDividers(holder, top = true, bottom = true, start = false, end = false)
        } else {
            setDividers(holder, top = false, bottom = true, start = false, end = false)
        }
    }

    private fun setDividersForTablet(holder: BindingViewHolder<ItemServiceBinding>, isRateAppVisible: Boolean, spanCount: Int) {
        if (isRateAppVisible) {
            if (holder.adapterPosition <= spanCount) {
                setDividers(holder, top = true, bottom = true, start = false, end = true)
            } else {
                setDividers(holder, top = false, bottom = true, start = false, end = true)
            }

        } else {
            setDividers(holder, top = false, bottom = true, start = false, end = true)
        }
    }


    private fun isPhone(holder: BindingViewHolder<ItemServiceBinding>) = holder.context.resources.getBoolean(R.bool.isPhone)
    private fun isTablet(holder: BindingViewHolder<ItemServiceBinding>) = holder.context.resources.getBoolean(R.bool.isTablet)
    private fun isTabletLandscape(holder: BindingViewHolder<ItemServiceBinding>) = holder.context.resources.getBoolean(R.bool.isTabletLandscape)
}
