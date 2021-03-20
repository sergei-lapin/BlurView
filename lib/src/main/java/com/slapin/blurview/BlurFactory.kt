package com.slapin.blurview

import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

fun Bitmap.withBlur(renderScript: RenderScript, blurRadius: Float): Bitmap {
    val input = Allocation.createFromBitmap(renderScript, this)
    val output = Allocation.createTyped(renderScript, input.type)
    val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

    with(script) {
        setRadius(blurRadius)
        setInput(input)
        forEach(output)
    }

    output.copyTo(this)
    return this
}