package com.twofasapp.features.services.addedservice

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.twofasapp.databinding.DialogAddedServiceBinding
import com.twofasapp.entity.ServiceModel
import com.twofasapp.extensions.clicksThrottled
import com.twofasapp.extensions.copyToClipboard
import com.twofasapp.extensions.getColorFromRes
import com.twofasapp.extensions.removeWhiteCharacters
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.resources.R
import com.twofasapp.services.ui.ServiceActivity
import io.reactivex.Flowable
import org.koin.android.ext.android.inject

class AddedServiceBottomSheet : com.twofasapp.base.BaseBottomSheet<DialogAddedServiceBinding>(),
    AddedServiceContract.View {

    companion object {
        private val ARG_SERVICE = "service"

        fun newInstance(service: ServiceDto) = AddedServiceBottomSheet().apply {
            arguments = bundleOf(ARG_SERVICE to service)
        }
    }

    private val codeColorNormal: Int by lazy { requireContext().getColorFromRes(R.color.serviceCodeNormal) }
    private val codeColorExpiring: Int by lazy { requireContext().getColorFromRes(R.color.serviceCodeExpiring) }
    private var animator: ValueAnimator? = null
    private val presenter: AddedServiceContract.Presenter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        super.onCreateView(
            inflater,
            container,
            savedInstanceState,
            DialogAddedServiceBinding::inflate
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPresenter(presenter)

        binding.copyCode.setOnClickListener {
            requireContext().copyToClipboard(
                text = binding.code.text.toString().removeWhiteCharacters(),
                label = "Token",
                toast = getString(R.string.tokens__copied_clipboard)
            )
        }
    }

    override fun getServiceExtra(): ServiceDto {
        return requireArguments().getParcelable(ServiceActivity.ARG_SERVICE)!!
    }

    override fun customizeClicks(): Flowable<Unit> =
        binding.customize.clicksThrottled()

    override fun refreshCounterClicks(): Flowable<Unit> =
        binding.actionRefreshCounter.clicksThrottled()

    override fun editIconClicks(): Flowable<Unit> =
        binding.editIcon.clicksThrottled()

    override fun setTitle(text: String) {
        binding.title.text = text
    }

    override fun setSubtitle(text: String) {
        binding.subtitle.text = text
        binding.subtitle.isVisible = text.isNotEmpty()
    }

    override fun updateService(model: ServiceModel) {
        binding.editIcon.isVisible =
            model.service.iconCollectionId.isBlank() ||
                    model.service.iconCollectionId == ServiceIcons.defaultCollectionId ||
                    model.service.selectedImageType == ServiceDto.ImageType.Label

        binding.code.text = model.formatCode()
        binding.iconLayout.updateIcon(model.service)

        binding.counter.updateProgress(
            progress = model.counter,
            max = model.service.getPeriod()
        )

        when (model.service.authType) {
            ServiceDto.AuthType.TOTP -> {
                binding.counter.isVisible = true
                binding.actionRefreshCounter.isVisible = false
            }

            ServiceDto.AuthType.HOTP -> {
                binding.counter.isVisible = false
                binding.actionRefreshCounter.isVisible = true
                binding.actionRefreshCounter.isEnabled = model.isHotpRefreshEnabled
                binding.actionRefreshCounter.alpha = if (model.isHotpRefreshEnabled) 1f else .3f
            }
        }

        val colorTo =
            if (model.counter in 0..5 && model.service.authType == ServiceDto.AuthType.TOTP) codeColorExpiring else codeColorNormal

        if (binding.code.currentTextColor == colorTo) {
            return
        }

        animator?.removeAllUpdateListeners()
        animator?.cancel()
        animator = ValueAnimator.ofInt(binding.code.currentTextColor, colorTo)
        animator?.addUpdateListener {
            binding.code.setTextColor(it.animatedValue as Int)
        }
        animator?.setEvaluator(ArgbEvaluator())
        animator?.setDuration(500)
        animator?.start()
    }

    override fun closeBottomSheet() {
        dismiss()
    }

    override fun onDestroyView() {
        animator?.removeAllUpdateListeners()
        animator?.cancel()
        super.onDestroyView()
    }
}