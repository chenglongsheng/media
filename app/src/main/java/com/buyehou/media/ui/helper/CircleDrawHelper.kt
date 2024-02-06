package com.buyehou.media.ui.helper

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 三阶贝塞尔曲线画拟合圆
 *
 * 选自[MusicEffect](https://github.com/WYJ0663/MusicEffect)计算部分没有修改，移除了冗余函数，添加自己的理解注释
 *
 * [三阶贝塞尔曲线拟合圆弧](https://blog.csdn.net/nibiewuxuanze/article/details/48165555)
 *
 * [Android音频可视化](https://juejin.cn/post/6893438390507667464#heading-5)
 *
 * @author Rosen
 */
class CircleDrawHelper(count: Int) {
    private val midPoints = Array(count) { PointF() }
    private val ratioPoints = Array(count) { PointF() }
    private val controlPoints = Array(count * 2) { PointF() }
    private val path = Path()
    private val controlPoint1 = PointF()
    private val controlPoint2 = PointF()

    fun calculate(points: Array<PointF>, k: Double) {
        val size = points.size
        // 计算相邻顶点的中点
        for (i in 0 until size) {
            val p1 = points[i]
            val p2 = points[(i + 1) % size]
            midPoints[i].set((p1.x + p2.x) / 2, (p1.y + p2.y) / 2) // 通过向量计算
        }

        // 三阶贝塞尔曲线 公式 B(t) = (1 − t)3 P0 + 3t (1 − t)2P1 + 3t2 (1 − t)P2 + t3 P3 , t ∈[0,1]
        // 计算比例点
        for (i in 0 until size) {
            val p1 = points[i]
            val p2 = points[(i + 1) % size]
            val p3 = points[(i + 2) % size]
            val l1 = distance(p1, p2)
            val l2 = distance(p2, p3)
            val ratio = l1 / (l1 + l2)
            val mp1 = midPoints[i]
            val mp2 = midPoints[(i + 1) % size]
            ratioPointConvert(mp2, mp1, ratio, ratioPoints[i])
        }

        // 移动线段，计算控制点
        var i = 0
        var j = 0
        while (i < size) {
            val ratioPoint = ratioPoints[i]
            val verPoint = points[(i + 1) % size]
            val dx = ratioPoint.x - verPoint.x
            val dy = ratioPoint.y - verPoint.y
            controlPoint1.set(midPoints[i].x - dx, midPoints[i].y - dy)
            controlPoint2.set(midPoints[(i + 1) % size].x - dx, midPoints[(i + 1) % size].y - dy)
            ratioPointConvert(controlPoint1, verPoint, k, controlPoints[j++])
            ratioPointConvert(controlPoint2, verPoint, k, controlPoints[j++])
            i++
        }
    }

    fun drawBezierCurve(canvas: Canvas, points: Array<PointF>, paint: Paint) {
        calculatePath(points)
        canvas.drawPath(path, paint)
    }

    private fun calculatePath(points: Array<PointF>) {
        val size = points.size
        path.reset()
        // 用三阶贝塞尔曲线连接顶点
        for (i in 0 until size) {
            val startPoint = points[i]
            val endPoint = points[(i + 1) % size]
            val controlPoint1 = controlPoints[(i * 2 + controlPoints.size - 1) % controlPoints.size]
            val controlPoint2 = controlPoints[i * 2 % controlPoints.size]
            path.moveTo(startPoint.x, startPoint.y)
            path.cubicTo(
                controlPoint1.x,
                controlPoint1.y,
                controlPoint2.x,
                controlPoint2.y,
                endPoint.x,
                endPoint.y
            )
        }
    }

    /**
     * 计算两点之间的距离
     * 计算公式 d^2 = (x2 - x1)^2 + (y2 - y1)^2
     */
    private fun distance(p1: PointF, p2: PointF): Double {
        return sqrt((p1.x - p2.x).toDouble().pow(2.0) + (p1.y - p2.y).toDouble().pow(2.0))
    }

    /**
     * 比例点转换
     */
    private fun ratioPointConvert(p1: PointF, p2: PointF, ratio: Double, p: PointF) {
        p.x = (ratio * (p1.x - p2.x) + p2.x).toFloat()
        p.y = (ratio * (p1.y - p2.y) + p2.y).toFloat()
    }
}