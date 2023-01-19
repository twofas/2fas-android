package com.twofasapp.start.ui.onboarding.step

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.twofasapp.base.BaseFragment
import com.twofasapp.start.R
import com.twofasapp.start.databinding.FragmentOnboardingStepBinding
import kotlinx.parcelize.Parcelize

internal class OnboardingStepFragment : BaseFragment(R.layout.fragment_onboarding_step) {

    companion object {
        fun newInstance(params: Params) = OnboardingStepFragment().apply {
            arguments = bundleOf("params" to params)
        }
    }

    private val viewBinding by viewBinding(FragmentOnboardingStepBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val params = requireArguments().getParcelable<Params>("params")!!

        viewBinding.title.text = params.title
        viewBinding.description.text = params.description

        if (params.imageRes != null) {
            viewBinding.image.setImageResource(params.imageRes)
            viewBinding.image.isVisible = true
        }

        if (params.smallImageRes != null) {
            viewBinding.smallImage.setImageResource(params.smallImageRes)
            viewBinding.smallImage.isVisible = true
        }
    }

    @Parcelize
    data class Params(
        val title: String,
        val description: String,
        val imageRes: Int? = null,
        val smallImageRes: Int? = null,
    ) : Parcelable
}