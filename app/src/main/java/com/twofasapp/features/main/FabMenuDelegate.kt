package com.twofasapp.features.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.twofasapp.extensions.makeGone
import com.twofasapp.extensions.makeInvisible
import com.twofasapp.extensions.makeVisible
import com.twofasapp.resources.R
import com.twofasapp.databinding.ActivityMainBinding
import com.twofasapp.features.fabscroll.FabScrollListener

class FabMenuDelegate(
    private val context: Context,
    private val binding: ActivityMainBinding,
) {
    private var isOpen = false
    private var lastToggleClick = System.currentTimeMillis()
    private val scrollListener = FabScrollListener(binding.fab)

    fun init(onAddManuallyClick: () -> Unit, onAddQrClick: () -> Unit, recycler: RecyclerView? = null) {
        recycler?.addOnScrollListener(scrollListener)

        binding.fab.setOnClickListener { toggle() }
        binding.fabMenuMask.setOnClickListener { toggle() }
        binding.fabLayout1.setOnClickListener {
            onAddManuallyClick()
            Handler().postDelayed({ closeFabMenu() }, 500)
        }
        binding.fabLayout2.setOnClickListener {
            onAddQrClick()
            Handler().postDelayed({ closeFabMenu() }, 500)
        }
    }

    private fun toggle() {
        if (System.currentTimeMillis() - lastToggleClick > 300) {
            if (isOpen) closeFabMenu() else openFabMenu()
            lastToggleClick = System.currentTimeMillis()
        }
    }

    fun openFabMenu() {
        isOpen = true
        binding.fab.contentDescription = "Cancel"

        binding.fabLayout1.makeVisible()
        binding.fabLayout2.makeVisible()

        binding.fabMenuMask.makeVisible()
        binding.fabMenuMask.alpha = 0f
        binding.fabMenuMask.animate().alpha(1f).setListener(null).start()

        binding.fab.animate().setDuration(150).rotationBy(45f)

        binding.fab1.startAnimation(animationFabOpen(90))
        binding.fab1Label.startAnimation(animationLabelOpen(binding.fab1Label, 90))

        binding.fab2.startAnimation(animationFabOpen(60))
        binding.fab2Label.startAnimation(animationLabelOpen(binding.fab2Label, 60))
    }

    fun closeFabMenu() {
        isOpen = false
        binding.fab.contentDescription = "Add"

        binding.fabMenuMask.alpha = 1f
        binding.fabMenuMask.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.fabMenuMask.makeGone()
            }
        }).start()


        binding.fab.animate().setDuration(150).rotationBy(-45f)

        binding.fab1.startAnimation(animationFabClose(binding.fabLayout1, 30))
        binding.fab1Label.startAnimation(animationLabelClose(binding.fab1Label, 30))

        binding.fab2.startAnimation(animationFabClose(binding.fabLayout2, 60))
        binding.fab2Label.startAnimation(animationLabelClose(binding.fab2Label, 60))
    }

    private fun animationFabOpen(delay: Long): Animation {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fab_scale_up)
        animation.startOffset = delay
        return animation
    }

    private fun animationLabelOpen(view: View, delay: Long): Animation {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fab_slide_in)
        animation.startOffset = delay
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(a: Animation) {
                view.makeVisible()
            }

            override fun onAnimationEnd(a: Animation) {}
            override fun onAnimationRepeat(a: Animation) {}
        })
        return animation
    }


    private fun animationFabClose(view: View, delay: Long): Animation {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fab_scale_down)
        animation.startOffset = delay
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(a: Animation) {}
            override fun onAnimationEnd(a: Animation) {
                view.makeGone()
            }

            override fun onAnimationRepeat(a: Animation) {}
        })
        return animation
    }

    private fun animationLabelClose(view: View, delay: Long): Animation {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fab_slide_out)
        animation.startOffset = delay
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(a: Animation) {}
            override fun onAnimationEnd(a: Animation) {
                view.makeInvisible()
            }

            override fun onAnimationRepeat(a: Animation) {}
        })
        return animation
    }

    private fun resetFabStatus() {
        closeFabMenu()
        binding.fab.animate().setDuration(0).rotationBy(-binding.fab.rotation)
    }

    fun showFab() {
        scrollListener.isForceHidden = false
        binding.fab.show()
    }

    fun hideFab() {
        scrollListener.isForceHidden = true
        resetFabStatus()
        binding.fab.hide()
    }

    fun isFabOpen() = isOpen
}