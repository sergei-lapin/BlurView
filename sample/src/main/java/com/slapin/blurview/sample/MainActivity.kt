package com.slapin.blurview.sample

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var animator: ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val img = findViewById<View>(R.id.img)
        animator = ValueAnimator.ofFloat(1f, 2f).apply {
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Float
                img.scaleX = animatedValue
                img.scaleY = animatedValue
            }
            duration = 3000L
            repeatCount = INFINITE
            repeatMode = REVERSE
        }
        animator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        animator.cancel()
    }
}