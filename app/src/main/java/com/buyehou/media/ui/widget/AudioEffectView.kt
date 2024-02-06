package com.buyehou.media.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.buyehou.media.ui.helper.CircleDrawHelper
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * 音频动效view
 *
 * @author Rosen
 */
class AudioEffectView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        fun dp2px(context: Context, value: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics
            )
        }

        fun isUiThread(): Boolean {
            return Thread.currentThread() == Looper.getMainLooper().thread
        }

        private const val DEFAULT_POINT_COUNT = 20

        private val EMPTY = FloatArray(DEFAULT_POINT_COUNT)
    }

    /**
     * 线条数量
     */
    private var lineCount = 8

    /**
     * 封面半径
     */
    private var coverRadius = dp2px(context, 248f)

    /**
     * 动效和封面的间距
     */
    private var interval = dp2px(context, 23f)

    /**
     * 动效线条宽度
     */
    private var effectWidth = dp2px(context, 2f)

    private val paint = Paint()

    // 曲线顶点相关
    private val pointCount = DEFAULT_POINT_COUNT
    private val points: Array<PointF> = Array(pointCount) { PointF() }
    private val circleDrawHelper: CircleDrawHelper = CircleDrawHelper(pointCount)

    var data: FloatArray = EMPTY
        set(value) {
            field = value
            if (isUiThread()) {
                invalidate()
            } else {
                postInvalidate()
            }
        }

    init {
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = effectWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawWare(canvas)
    }

    private fun drawWare(canvas: Canvas) {
        paint.color = Color.RED
        canvas.drawCircle(
            (width / 2).toFloat(), (height / 2).toFloat(), coverRadius + interval, paint
        )
        paint.color = Color.BLACK
        for (j in 0 until lineCount) {
            for (i in points.indices) {
                // 数据选取
                val remain = min(width, height) / 2 - coverRadius - interval
                val value = remain * (data[i] / 127) * (j / lineCount.toFloat() + 1)
                val r = coverRadius + interval + value

                // 索引的点位
                val index = i * (360.0 / pointCount)
                val x = sin(Math.toRadians(index)).toFloat() * r + canvas.width / 2
                val y = cos(Math.toRadians(index)).toFloat() * r + canvas.height / 2
                points[i].set(x, y)
            }
            circleDrawHelper.calculate(points, 0.8)
            circleDrawHelper.drawBezierCurve(canvas, points, paint)
        }
    }

}