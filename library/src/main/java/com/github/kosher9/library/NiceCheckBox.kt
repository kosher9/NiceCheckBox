package com.github.kosher9.library

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Checkable
import kotlin.math.pow

class NiceCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr), Checkable{

    private var mCenter = Point()

    private val mTickPoint1 = Point()
    private val mTickPoint2 = Point()
    private val mTickPoint3 = Point()
    private val mTickPoints = arrayOf(mTickPoint1, mTickPoint2, mTickPoint3)

    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = TICK_COLOR
        style = Paint.Style.STROKE
        strokeWidth = 7.5f
        strokeCap = Paint.Cap.ROUND
    }

    private val mTickPathLeft = Path()
    private val mTickPathRight = Path()

    private var mTickDrawing = true

    private var mLeftDistance: Float = 0f
    private var mRightDistance: Float = 0f
    private var mLeftDrewDistance: Float = 0f
    private var mRightDrewDistance: Float = 0f

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var backgroundCanvas: Canvas
    private var foregroundCanvas: Canvas

    private var checkBitmap: Bitmap
    private var borderBitmap: Bitmap

    private var checkEraser: Paint
    private var checkPaint: Paint
    private var borderEraser: Paint
    private var borderPaint: Paint

    private var restoreCheckSatate = true

    private var backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#08000000")
    }

    private var mScale = 1f

    private var checked = false

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
        if (!this.checked){
            animateMe()
        } else {
            animateBack()
        }
        restoreCheckSatate = false
        this.checked = !this.checked
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvas?.drawColor(Color.parseColor("#F4E7D4"))
        canvas?.drawCircle(mCenter.x.toFloat(), mCenter.y.toFloat(), mCenter.x.toFloat() - 5, backgroundPaint)
        backgroundCanvas.drawCircle(mCenter.x.toFloat(), mCenter.y.toFloat(), mCenter.x.toFloat(), borderPaint)
        backgroundCanvas.drawCircle(mCenter.x.toFloat(), mCenter.y.toFloat(), mCenter.x.toFloat() - 5, borderEraser)
        canvas?.drawBitmap(borderBitmap, 0f, 0f, null)
        foregroundCanvas.drawCircle(mCenter.x.toFloat(), mCenter.y.toFloat(), mCenter.x.toFloat() - 6, checkPaint)
        if (checked){
            if (restoreCheckSatate){
                foregroundCanvas.drawCircle(mCenter.x.toFloat(), mCenter.y.toFloat(), mCenter.x.toFloat() * 0 -5, checkEraser)
            } else {
                foregroundCanvas.drawCircle(mCenter.x.toFloat(), mCenter.y.toFloat(), mCenter.x.toFloat() * mScale -5, checkEraser)
            }
        } else{
            foregroundCanvas.drawCircle(mCenter.x.toFloat(), mCenter.y.toFloat(), mCenter.x.toFloat() * mScale -5, checkEraser)
        }
        canvas?.drawBitmap(checkBitmap, 0f, 0f, null)
        drawTick(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(widthMeasureSpec))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mWidth = measuredWidth
        mHeight = measuredHeight
        mCenter.x = mWidth / 2
        mCenter.y = mHeight / 2
        mTickPoints[0].x = mCenter.x / 2
        mTickPoints[0].y = mCenter.y
        mTickPoints[1].x = mCenter.x - mCenter.x / 4
        mTickPoints[1].y = mCenter.y + mCenter.y / 4
        mTickPoints[2].x = mCenter.x + mCenter.x * 3 / 8
        mTickPoints[2].y = mCenter.y * 6 / 8

        mLeftDistance = kotlin.math.sqrt(
            (mTickPoints[1].x - mTickPoints[0].x).toFloat()
                .pow(2) + (mTickPoints[1].y - mTickPoints[0].y).toFloat().pow(2)
        )

        mRightDistance = kotlin.math.sqrt(
            (mTickPoints[2].x - mTickPoints[1].x).toFloat()
                .pow(2) + (mTickPoints[2].y - mTickPoints[1].y).toFloat().pow(2)
        )
    }

    private fun measureSize(measureSpec: Int): Int{
        val defSize = CompatUtils.dp2px(context, DEF_DRAW_SIZE)
        val specSize = MeasureSpec.getSize(measureSpec)
        val specMode = MeasureSpec.getMode(measureSpec)

        var result = 0
        when(specMode){
            MeasureSpec.AT_MOST -> {
                result = kotlin.math.min(defSize, specSize)
            }
            MeasureSpec.EXACTLY -> {
                result = specSize
            }
            MeasureSpec.UNSPECIFIED -> {
                return result
            }
        }
        return result
    }

    private fun drawTick(canvas: Canvas?) {
        if (checked && mTickDrawing) {
            mTickPathLeft.moveTo(mTickPoints[1].x.toFloat(), mTickPoints[1].y.toFloat())
            mTickPathLeft.lineTo(mTickPoints[0].x.toFloat(), mTickPoints[0].y.toFloat())
            canvas?.drawPath(mTickPathLeft, tickPaint)
            mTickPathLeft.reset()
            mTickPathRight.moveTo(mTickPoints[1].x.toFloat(), mTickPoints[1].y.toFloat())
            mTickPathRight.lineTo(mTickPoints[2].x.toFloat(), mTickPoints[2].y.toFloat())
            canvas?.drawPath(mTickPathRight, tickPaint)
            mTickPathRight.reset()

            /*postDelayed({
                postInvalidate()
            }, 10)*/
        }

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

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putBoolean(KEY_INSTANCE_STATE, checked)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle){
            val bundle = state
            val isChecked = bundle.getBoolean(KEY_INSTANCE_STATE)
            setChecked(isChecked)
            super.onRestoreInstanceState(bundle.getParcelable(KEY_INSTANCE_STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun isChecked(): Boolean {
        return this.checked
    }

    override fun toggle() {
        isChecked = !checked
        invalidate()
    }

    override fun setChecked(checked: Boolean) {
        this.checked = checked
        restoreCheckSatate = checked
        invalidate()
    }

    companion object{
        const val TICK_COLOR = Color.WHITE
        const val DEF_DRAW_SIZE = 25f
        const val KEY_INSTANCE_STATE = "InstanceState"
    }
}
