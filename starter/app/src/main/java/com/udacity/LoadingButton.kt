package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import java.util.*

private const val TAG = "LoadingButton"

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var progressWidth = 0
    private var progressArcAngle = 0

    private var buttonAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    var buttonState: ButtonState = ButtonState.Completed
        set(value) {
            field = value
            when (field) {
                ButtonState.Clicked -> clicked()
                ButtonState.Loading -> loading()
                ButtonState.Completed -> completed()
            }
        }

    private var buttonText = ""
    private var buttonTextColor = 0
    private var buttonBackground = 0

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonText = getString(R.styleable.LoadingButton_text) ?: ""
            buttonTextColor = getInt(R.styleable.LoadingButton_textColor, 0)
            buttonBackground = getInt(R.styleable.LoadingButton_buttonBackground, 0)
        }
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = buttonTextColor
        textSize = context.resources.getDimension(R.dimen.default_text_size)
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorAccent)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawButton(canvas)
        drawAnimatingButton(canvas)
        drawCircleLoader(canvas)
        drawText(canvas)
    }

    private fun drawButton(canvas: Canvas) {
        backgroundPaint.color = buttonBackground
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), backgroundPaint)
    }

    private fun drawAnimatingButton(canvas: Canvas) {
        backgroundPaint.color = if (progressWidth == 0) buttonBackground else ContextCompat.getColor(context, R.color.colorPrimaryDark)
        canvas.drawRect(0f, 0f, widthSize.toFloat() * progressWidth / 100, heightSize.toFloat(), backgroundPaint)
    }

    private fun drawCircleLoader(canvas: Canvas) {
        val left = widthSize - 160f
        val right = widthSize - 60f

        val top = (heightSize - 100) / 2f
        val bottom = top + 100
        canvas.drawArc(left, top, right, bottom, 0F, progressArcAngle.toFloat(), true, circlePaint)
    }

    private fun drawText(canvas: Canvas) {
        val label = if (buttonState == ButtonState.Loading) {
            R.string.button_loading
        } else {
            R.string.button_download
        }
        buttonText = context.getString(label)
        val posX = width / 2f
        val posY = height / 2f + textPaint.textSize / 2
        canvas.drawText(buttonText.toUpperCase(Locale.ROOT), posX, posY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun clicked() {
        Log.d(TAG, "clicked")
    }

    private fun loading() {
        Log.d(TAG, "loading")
        buttonAnimator = ValueAnimator.ofInt(0, widthSize)
        buttonAnimator.duration = 3000
        buttonAnimator.addUpdateListener {
            progressWidth = it.animatedValue as Int
            invalidate()
        }
        buttonAnimator.start()

        circleAnimator = ValueAnimator.ofInt(0, 360)
        circleAnimator.duration = 3000
        circleAnimator.addUpdateListener {
            progressArcAngle = it.animatedValue as Int
            invalidate()
        }
        circleAnimator.start()
    }

    private fun completed() {
        Log.d(TAG, "completed")
        try {
            buttonAnimator.end()
            circleAnimator.end()
        } catch (e: Exception) {
        }

        progressWidth = 0
        progressArcAngle = 0
        invalidate()
    }
}