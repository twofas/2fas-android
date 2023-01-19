package com.twofasapp.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import org.koin.core.scope.Scope
import timber.log.Timber

class ActivityProvider : Application.ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null

    fun provide(): Activity {
        return currentActivity ?: throw RuntimeException("ActivityProvider: Could not provide Activity")
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Timber.d("${activity.javaClass.simpleName} :: onActivityCreated")
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.d("${activity.javaClass.simpleName} :: onActivityResumed")
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.d("${activity.javaClass.simpleName} :: onActivityPaused")
        currentActivity = null
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.d("${activity.javaClass.simpleName} :: onActivityStarted")
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.d("${activity.javaClass.simpleName} :: onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Timber.d("${activity.javaClass.simpleName} :: onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.d("${activity.javaClass.simpleName} :: onActivityDestroyed")
    }

    companion object {
        fun Scope.activityContext(): Activity =
            get<ActivityProvider>().provide()
    }
}

