@file:Suppress("unused")

package io.jerryc05.pd_detector.ui

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

class MyApp : Application(), Application.ActivityLifecycleCallbacks {
  lateinit var weakActivity: WeakReference<Activity>

  override fun onCreate() {
    super.onCreate()
    registerActivityLifecycleCallbacks(this)
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    weakActivity = WeakReference(activity)
  }

  override fun onActivityStarted(activity: Activity) {
  }

  override fun onActivityResumed(activity: Activity) {
  }

  override fun onActivityPaused(activity: Activity) {
  }

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
  }

  override fun onActivityStopped(activity: Activity) {
  }

  override fun onActivityDestroyed(activity: Activity) {
  }
}