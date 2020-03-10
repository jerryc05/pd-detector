@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "unused")

package io.jerryc05.pd_detector.core.log

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import io.jerryc05.pd_detector.BuildConfig
import io.jerryc05.pd_detector.ui.MyApp.Companion.topActivity


internal fun logA(tag: String, msg: String?, tr: Throwable? = null) {
  if (BuildConfig.DEBUG)
    Log.println(Log.ASSERT, tag, msg +
            if (tr == null) ""
            else "\n```\n${Log.getStackTraceString(tr)}\n```")
}

internal fun logE(tag: String, msg: String?, tr: Throwable? = null) {
  val msg0 = msg ?: ""

  if (BuildConfig.DEBUG)
    Log.e(tag, msg0, tr)

  val trace = if (tr == null) "" else Log.getStackTraceString(tr)
  val errMsg = "$tag: $msg0\n```\n$trace\n```"

  val callback by lazy {
    DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
      when (i) {
        DialogInterface.BUTTON_POSITIVE -> {
          val title = "$tag:${if (msg0.isBlank()) "" else "$msg0 |"} $tr"
          reportGithub(title, errMsg)
        }
        DialogInterface.BUTTON_NEGATIVE -> {
        }
        else -> throw Exception("Invalid button!")
      }
      dialogInterface.dismiss()
    }
  }

  topActivity.get()?.let {
    AlertDialog.Builder(it)
            .setTitle("Error!")
            .setMessage(errMsg)
            .setPositiveButton("Report", callback)
            .setNegativeButton("Cancel", callback)
            .show()
  }
}