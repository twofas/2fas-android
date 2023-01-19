package com.twofasapp.start.ui.onboarding

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.twofasapp.base.BaseActivity
import com.twofasapp.extensions.childViews
import com.twofasapp.extensions.openBrowserApp
import com.twofasapp.navigation.StartDirections
import com.twofasapp.navigation.StartRouter
import com.twofasapp.resources.R
import com.twofasapp.start.databinding.ActivityOnboardingBinding
import com.twofasapp.start.ui.onboarding.step.OnboardingStepFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>() {

    private val viewModel: OnboardingViewModel by viewModel()
    private val router: StartRouter by injectThis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityOnboardingBinding::inflate)

        viewBinding.viewPager.adapter = ScreenSlidePagerAdapter(this)
        viewBinding.viewPager.offscreenPageLimit = 1
        viewBinding.skip.setOnClickListener {
            viewModel.onSkipClicked()
            router.navigate(StartDirections.Main)
            finish()
        }

        viewBinding.terms.setOnClickListener {
            viewModel.onTermsClicked()
            openBrowserApp(url = "https://2fas.com/terms-of-service")
        }

        viewBinding.next.setOnClickListener {
            viewModel.onNextClicked(viewBinding.viewPager.currentItem)

            if (viewBinding.viewPager.currentItem == 3) {
                viewModel.onStartUsingClicked()
                router.navigate(StartDirections.Main)
                finish()
            } else {
                viewBinding.viewPager.currentItem = viewBinding.viewPager.currentItem + 1
            }
        }

        viewBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewBinding.terms.isVisible = position == 0
                viewBinding.skip.isVisible = position != 0
                viewBinding.next.text = when (position) {
                    0 -> getString(R.string.commons__continue)
                    3 -> getString(R.string.introduction__title)
                    else -> getString(R.string.commons__next)
                }
                viewBinding.dots.isVisible = position != 0
                viewBinding.dots.childViews.forEach { it.isSelected = false }

                when (position) {
                    1 -> viewBinding.dot1.isSelected = true
                    2 -> viewBinding.dot2.isSelected = true
                    3 -> viewBinding.dot3.isSelected = true
                }
            }
        })
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            val params = when (position) {
                0 -> OnboardingStepFragment.Params(
                    title = getString(R.string.introduction__page_1_title),
                    description = getString(R.string.introduction__page_1_content),
                    smallImageRes = R.drawable.onboarding_step_one,
                )

                1 -> OnboardingStepFragment.Params(
                    title = getString(R.string.introduction__page_2_title),
                    description = getString(R.string.introduction__page_2_content),
                    imageRes = R.drawable.onboarding_step_two,
                )

                2 -> OnboardingStepFragment.Params(
                    title = getString(R.string.introduction__page_3_title),
                    description = getString(R.string.introduction__page_3_content),
                    imageRes = R.drawable.onboarding_step_three,
                )

                3 -> OnboardingStepFragment.Params(
                    title = getString(R.string.introduction__page_4_title),
                    description = getString(R.string.introduction__page_4_content_android),
                    imageRes = R.drawable.onboarding_step_four,
                )

                else -> OnboardingStepFragment.Params(
                    title = "",
                    description = "",
                    imageRes = R.drawable.logo_2fas,
                )
            }

            return OnboardingStepFragment.newInstance(params)
        }
    }
}