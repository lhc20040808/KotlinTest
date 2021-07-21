package com.marco.kotlintest

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ShadowView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val rec = Rect()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val radius = 20

    init {
        paint.color = Color.GRAY
        paint.style = Paint.Style.FILL
        paint.maskFilter = BlurMaskFilter(radius.toFloat(), BlurMaskFilter.Blur.OUTER)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rec.top = 0 + radius
        rec.left = 0 + radius
        rec.right = w - radius
        rec.bottom = h - radius
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(rec, paint)
        canvas?.drawCircle(width / 2f, height / 2f, 50f, paint)
        canvas?.drawLine(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
}