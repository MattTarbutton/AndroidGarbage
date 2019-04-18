package com.example.test

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random
import android.view.Display
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate


class ColorTapView(context: Context) : View(context) {

    private var colors: Array<Int> = arrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK, Color.CYAN, Color.DKGRAY, Color.MAGENTA, Color.LTGRAY)
    private var paint: Paint = Paint()
    private var circleList: List<ExpandingCircle> = listOf()
    private val simulationFrequency: Float = 0.05f
    private val sampleRate: Int = 44100
    private var track: AudioTrack = AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_DEFAULT,
        AudioFormat.ENCODING_PCM_8BIT, (sampleRate * .1f).toInt(), AudioTrack.MODE_STREAM)
    private val maxRadius by lazy {
        context.resources.getDimension(R.dimen.maxRadius)
    }

    init {
        // create the Paint and set its color
        //paint.color = Color.GRAY
        //paint.setColor(Color.GRAY)
    }

    override fun onMeasure(
        widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val desiredWidth = suggestedMinimumWidth +
                paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight +
                paddingTop + paddingBottom
        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        circleList.forEach {
            paint.color = colors[Random.nextInt(0, colors.size - 1)]
            it.draw(canvas, paint)
        }

        // Invalidate before removing circles that are max radius so we draw a blank canvas if there are no more circles
        // Otherwise the last circle stays at like 254 alpha
        if (circleList.isNotEmpty()){// && isAttachedToWindow) {
            invalidate()
        }
        circleList = circleList.filter { it.isValid() }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerIndex = event.actionIndex
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> return true
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                circleList += ExpandingCircle(
                    event.getX(pointerIndex),
                    event.getY(pointerIndex), maxRadius, colors[Random.nextInt(0, colors.size - 1)])
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                circleList += ExpandingCircle(
                    event.getX(pointerIndex),
                    event.getY(pointerIndex), maxRadius, colors[Random.nextInt(0, colors.size - 1)])
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun generateSoundWave(duration: Float) {

        var soundData = ByteArray((duration * sampleRate).toInt())

        for (i in 0 until soundData.size) {
            var sample = 0.0
            var count = 0

            if (count == 0) count++

            sample += Math.sin(2 *  Math.PI * 440 * i / sampleRate) * 128
            //var sample: Byte = (Math.sin(2 *  Math.PI * frequency * i / sampleRate) * 128).toByte()
            soundData[i] = (sample / count).toByte()
        }

        if (track.state == AudioTrack.STATE_INITIALIZED) {
            track.write(soundData, 0, soundData.size)
            track.play()
        }
    }
}