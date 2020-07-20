package com.github.kosher9.library

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator

class NiceCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr){

    private var backgroundCanvas: Canvas
    private var foregroundCanvas: Canvas

    private var checkBitmap: Bitmap
    private var borderBitmap: Bitmap

    private var checkEraser: Paint
    private var checkPaint: Paint
    private var borderEraser: Paint
    private var borderPaint: Paint

    private var backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x44000000
    }

    private var mScale = 1f

    private var isClicked = false

    init {
        isClickable = true

        borderBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        borderEraser = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        backgroundCanvas = Canvas(borderBitmap)

        checkBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        checkEraser = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        checkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4CAF50")
            style = Paint.Style.FILL
        }
        foregroundCanvas = Canvas(checkBitmap)

    }

    override fun performClick(): Boolean {
        if (!isClicked){
            animateMe()
        } else {
            animateBack()
        }
        isClicked = !isClicked
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#F4E7D4"))
        canvas.drawCircle(100f, 100f, 50f, backgroundPaint)
        backgroundCanvas.drawCircle(100f, 100f, 55f, borderPaint)
        backgroundCanvas.drawCircle(100f, 100f, 50f, borderEraser)
        canvas.drawBitmap(borderBitmap, 0f, 0f, null)
        foregroundCanvas.drawCircle(100f, 100f, 50f - 1, checkPaint)
        foregroundCanvas.drawCircle(100f, 100f, 50f * mScale, checkEraser)
        canvas.drawBitmap(checkBitmap, 0f, 0f, null)
    }

    private fun measureSize(measureSpec: Int){

    }

    private fun animateBack(){
        val animatorBorder = ValueAnimator.ofFloat(0f, 1f)
        animatorBorder.duration = 250
        animatorBorder.interpolator = AccelerateDecelerateInterpolator()
        animatorBorder.addUpdateListener {
            mScale = it.animatedValue as Float
            postInvalidate()
        }
        animatorBorder.start()
    }

    private fun animateMe(){
        val animatorBorder = ValueAnimator.ofFloat(1f, 0f)
        animatorBorder.duration = 250
        animatorBorder.interpolator = AccelerateDecelerateInterpolator()
        animatorBorder.addUpdateListener {
            mScale = it.animatedValue as Float
            postInvalidate()
        }
        animatorBorder.start()
    }
}
