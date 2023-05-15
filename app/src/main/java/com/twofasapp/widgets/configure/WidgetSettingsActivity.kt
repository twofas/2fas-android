package com.twofasapp.widgets.configure

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.BaseActivityPresenter
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.databinding.ActivityWidgetSettingsBinding
import com.twofasapp.design.theme.ThemeState
import com.twofasapp.extensions.clicksThrottled
import com.twofasapp.extensions.makeGone
import com.twofasapp.extensions.makeVisible
import com.twofasapp.extensions.makeWindowSecure
import com.twofasapp.extensions.navigationClicksThrottled
import com.twofasapp.views.ModelDiffUtilCallback
import com.twofasapp.widgets.broadcast.WidgetBroadcaster
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class WidgetSettingsActivity : BaseActivityPresenter<ActivityWidgetSettingsBinding>(), WidgetSettingsContract.View, AuthAware {

    private val presenter: WidgetSettingsContract.Presenter by injectThis()
    private val authTracker: AuthTracker by inject()
    private val fastAdapter = FastItemAdapter<IItem<*>>()
    private var saveMenuButton: TextView? = null
    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeState.applyTheme(settingsRepository.getAppSettings().selectedTheme)

        authTracker.onWidgetSettingsScreen()
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            settingsRepository.observeAppSettings().collect {
                makeWindowSecure(allow = it.allowScreenshots)
            }
        }
        setContentView(ActivityWidgetSettingsBinding::inflate)
        setPresenter(presenter)
        viewBinding.recycler.adapter = fastAdapter
        viewBinding.recycler.itemAnimator = DefaultItemAnimator()
        saveMenuButton = viewBinding.toolbar.menu.findItem(com.twofasapp.R.id.menu_save).actionView!!.findViewById(com.twofasapp.R.id.saveMenuButton)
        saveMenuButton?.isEnabled = true
    }

    override fun toolbarBackClicks() = viewBinding.toolbar.navigationClicksThrottled()

    override fun saveClicks() = saveMenuButton!!.clicksThrottled()

    override fun cancelClicks() = viewBinding.cancel.clicksThrottled()

    override fun agreeClicks() = viewBinding.agree.clicksThrottled()

    override fun getAppWidgetId(): Int {
        return intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    override fun isNew() =
        intent?.extras?.getBoolean(WidgetBroadcaster.EXTRA_IS_NEW, true) ?: true

    override fun showContent() {
        viewBinding.contentLayout.makeVisible()
        viewBinding.warningLayout.makeGone()
    }

    override fun setItems(items: List<IItem<*>>) {
        FastAdapterDiffUtil.set(fastAdapter.itemAdapter, items, ModelDiffUtilCallback())
    }

    override fun finishAddingWidget() {
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getAppWidgetId())
        }
        setResult(RESULT_OK, resultValue)
        finish()
    }

    override fun close() {
        finish()
        moveTaskToBack(true)
    }

    override fun onBackPressed() {
        finish()
        moveTaskToBack(true)
    }

    override fun onAuthenticated() = Unit
}