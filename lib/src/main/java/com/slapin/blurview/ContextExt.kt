package com.slapin.blurview

import android.app.Activity
import android.content.Context
import android.view.View

internal inline val Context.activityView: View?
    get() = (this as? Activity)?.window?.decorView?.findViewById(android.R.id.content)