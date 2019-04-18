package com.example.test

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point

data class GridSquare(private val left: Float, private val top: Float, private val right: Float, private val bottom: Float, val frequency: Int){

    private var alphaCounter: Int = 255
    private val maxAlpha: Int = 255
    private var color: Int = 0

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = color
        //paint.apply { alpha = ((1 - alphaCounter / alphaCounterMax) * maxAlpha) }
        paint.apply { alpha = (maxAlpha - alphaCounter) }
        //alphaCounter += 1
        canvas.drawRect(left, top, right, bottom, paint)
    }

    fun isAlive() = alphaCounter < maxAlpha

    fun percent(): Float{
        return alphaCounter.toFloat() / maxAlpha.toFloat()
    }

    fun pointIsInSquare(point: Point): Boolean {
        return point.x > left && point.x < right && point.y < top && point.y > bottom
    }

    fun startDrawing(newColor: Int) {
        color = newColor
        alphaCounter = 0
    }

    fun stopDrawing(){
        alphaCounter = maxAlpha
    }
}