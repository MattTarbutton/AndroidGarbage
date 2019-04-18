package com.example.test

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable

data class ExpandingCircle(private val centerX: Float, private val centerY: Float, private val maxRadius: Float, private val color: Int) : Parcelable {

    var currentRadius = 1f

    constructor(parcel: Parcel, color: Int) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        color) {
        currentRadius = parcel.readFloat()
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = color
        paint.apply { alpha = ((maxRadius - currentRadius) / maxRadius * MAX_ALPHA).toInt() }
        canvas.drawCircle(centerX, centerY, currentRadius, paint)
        currentRadius += 4
    }

    fun isValid() = currentRadius < maxRadius

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(centerX)
        parcel.writeFloat(centerY)
        parcel.writeFloat(maxRadius)
        parcel.writeFloat(currentRadius)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExpandingCircle> {
        private const val MAX_ALPHA = 255

        override fun createFromParcel(parcel: Parcel): ExpandingCircle {
            return ExpandingCircle(parcel, Color.MAGENTA)
        }

        override fun newArray(size: Int): Array<ExpandingCircle?> {
            return arrayOfNulls(size)
        }
    }

}