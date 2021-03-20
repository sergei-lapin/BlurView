package com.slapin.blurview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.renderscript.RenderScript
import android.util.AttributeSet
import android.view.Choreographer
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.res.use
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

private const val DEFAULT_BLUR_RADIUS = 12f
private const val SAMPLE_SCALE_FACTOR = 1 / 12f

class BlurView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @Px
    var cornerRadius: Int = 0

    var blurRadius: Float = DEFAULT_BLUR_RADIUS

    @ColorInt
    var tintColor: Int = Color.WHITE

    private val renderScript by lazy { RenderScript.create(context) }

    private var contentViewWeakRef = WeakReference<View>(null)

    private var prevSample: Bitmap? = null

    private val blurLoop = object : Choreographer.FrameCallback {

        override fun doFrame(frameTimeNanos: Long) {
            invalidate()
            choreographer.postFrameCallback(this)
        }
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.BlurView).use { typedArray ->
            cornerRadius = typedArray.getDimensionPixelSize(R.styleable.BlurView_bvCornerRadius, 0)
            blurRadius = typedArray.getFloat(R.styleable.BlurView_bvBlurRadius, DEFAULT_BLUR_RADIUS)
                .coerceIn(0.001f, 25f)
            tintColor = typedArray.getColor(R.styleable.BlurView_bvTint, Color.WHITE)
        }
    }

    override fun invalidate() {
        blur()?.let(this::setBackground)
        super.invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        choreographer.postFrameCallback(blurLoop)
    }

    override fun onDetachedFromWindow() {
        choreographer.removeFrameCallback(blurLoop)
        super.onDetachedFromWindow()
    }

    private fun blur(): Drawable? {
        val contentView = contentViewWeakRef.get() ?: setupContentViewWeakRef() ?: return null

        val location = intArrayOf(0, 0)
        getLocationOnScreen(location)

        if (width <= 0 || height <= 0) return null

        alpha = 0f
        val sample = contentView.sample().withinBounds()
        if (sample.sameAs(prevSample)) {
            alpha = 1f
            return null
        }
        prevSample = Bitmap.createBitmap(sample)
        return sample
            .withBlur(renderScript, blurRadius)
            .asRoundedDrawable
            .applyColor()
            .also { alpha = 1f }
    }

    private fun setupContentViewWeakRef(): View? {
        val activityView = context.activityView
        contentViewWeakRef = WeakReference(activityView)
        return activityView
    }

    private fun View.sample(): Bitmap {
        return Bitmap.createBitmap(width.sampleScaled, height.sampleScaled, Bitmap.Config.ARGB_8888)
            .applyCanvas {
                translate(-scrollX.toFloat(), -scrollY.toFloat())
                scale(SAMPLE_SCALE_FACTOR, SAMPLE_SCALE_FACTOR)
                draw(this)
            }
    }

    private fun Bitmap.withinBounds(): Bitmap {
        return Bitmap.createBitmap(
            this,
            x.toInt().sampleScaled,
            y.toInt().sampleScaled,
            this@BlurView.width.sampleScaled,
            this@BlurView.height.sampleScaled
        )
    }

    private fun Drawable.applyColor(): Drawable {
        mutate()
        colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.MULTIPLY)
        return this
    }

    private inline val Int.sampleScaled: Int
        get() = (this * SAMPLE_SCALE_FACTOR).roundToInt()

    private inline val choreographer: Choreographer
        get() = Choreographer.getInstance()

    private inline val Bitmap.asRoundedDrawable: Drawable
        get() = RoundedBitmapDrawableFactory.create(resources, this).apply {
            cornerRadius = this@BlurView.cornerRadius.toFloat()
        }
}