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


class GameOfLifeView(context: Context, screenWidth: Int, screenHeight: Int) : View(context) {

    private var paint: Paint = Paint()
    private var grid: MutableList<MutableList<GridSquare>> = mutableListOf()
    private val birthLimit: Int = 3
    private val deathLimit: Int = 2
    private val gridCellWidth: Float = 20f
    private val gridCellHeight: Float = 20f
    private var fingerUp: Boolean = true
    private var aliveCells: MutableList<GridSquare> = mutableListOf()
    private val simulationFrequency: Float = 0.05f
    private val sampleRate: Int = 44100
    private var track: AudioTrack = AudioTrack(
        AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_DEFAULT,
        AudioFormat.ENCODING_PCM_8BIT, (sampleRate * .1f).toInt(), AudioTrack.MODE_STREAM)
    private val maxRadius by lazy {
        context.resources.getDimension(R.dimen.maxRadius)
    }

    init {
        // create the Paint and set its color
        //paint.color = Color.GRAY
        //paint.setColor(Color.GRAY)

        val height = (screenHeight / gridCellWidth).toInt()
        val width = (screenWidth / gridCellHeight).toInt()

        for (i in 0 until width)
        {
            grid.add(mutableListOf())
            for (j in 0 until height)
            {
                grid[i].add(GridSquare(i * gridCellWidth, j * gridCellHeight, (i + 1) * gridCellWidth, (j + 1) * gridCellHeight, 20 * (j + 5)))
            }
        }

        //var timer = Timer()
        //timer.scheduleAtFixedRate(TimerTask(){::doSimulationStep}, 0, 17)
        //Timer("simulationStep", false).scheduleAtFixedRate(0, 50){
        Timer("simulationStep", false).scheduleAtFixedRate(0, (simulationFrequency * 1000).toLong()){
            doSimulationStep()
        }

        //generateSoundWave(440, .17f)
        //generateSoundWave(.1f)
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
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        grid.forEach {
            it.forEach { gs: GridSquare ->
                if (gs.isAlive()) {
                    gs.draw(canvas, paint)
                    invalidate()
                }
            }
        }

        /*if (fingerUp) {
            doSimulationStep()
        }*/
        //generateSoundWave(.1f)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerIndex = event.actionIndex
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> return true
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                /*circleList += ExpandingCircle(
                    event.getX(pointerIndex),
                    event.getY(pointerIndex), maxRadius, colors[Random.nextInt(0, colors.size - 1)])
                invalidate()*/
                fingerUp = true
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val x = (event.getX(pointerIndex) / gridCellWidth).toInt()
                val y = (event.getY(pointerIndex) / gridCellHeight).toInt()

                if (x >= 0 && x < grid.size ) {
                    if (y >= 0 && y < grid[x].size) {
                        grid[x][y].startDrawing(Color.BLACK)
                    }
                }
                fingerUp = false
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun doSimulationStep() {
        if (!fingerUp)
            return

        var newAliveCells: MutableList<Point> = mutableListOf()
        var killCells: MutableList<Point> = mutableListOf()

        for (i in 0 until grid.size)
        {
            for (j in 0 until grid[0].size)
            {
                var neighbors = countAliveNeighbors(i, j)

                if (!grid[i][j].isAlive()) {
                    if (neighbors == birthLimit){
                        newAliveCells.add(Point(i, j))
                    }
                }
                else
                {
                    aliveCells.add(grid[i][j])
                    if (neighbors < deathLimit){
                        killCells.add(Point(i, j))
                    }
                    else if (neighbors > birthLimit){
                        killCells.add(Point(i, j))
                    }
                }
            }
        }

        newAliveCells.forEach { p: Point ->
            grid[p.x][p.y].startDrawing(Color.BLACK)
        }

        killCells.forEach {p: Point ->
            grid[p.x][p.y].stopDrawing()
        }

        //generateSoundWave(simulationFrequency * 10)
        //generateSoundWave(.1f)
    }

    private fun countAliveNeighbors(x: Int, y: Int): Int {

        var count = 0

        for (i in -1..1) {
            for (j in -1..1){

                var checkX = x + i
                var checkY = y + j

                if (i == 0 && j == 0) continue
                else if (checkX < 0 || checkX >= grid.size || checkY < 0 || checkY >= grid[0].size) continue
                else count += if (grid[checkX][checkY].isAlive()) 1 else 0
            }
        }

        return count
    }

    private fun generateSoundWave(duration: Float) {
        //if (aliveCells.isEmpty())
        //return

        var soundData = ByteArray((duration * sampleRate).toInt())

        for (i in 0 until soundData.size) {
            var sample = 0.0
            var count = 0

            /*aliveCells.forEach{
                sample += it.percent() * Math.sin(2 *  Math.PI * it.frequency * i / sampleRate) * 128
                count++
            }*/

            if (count == 0) count++

            sample += Math.sin(2 *  Math.PI * 440 * i / sampleRate) * 128
            //var sample: Byte = (Math.sin(2 *  Math.PI * frequency * i / sampleRate) * 128).toByte()
            soundData[i] = (sample / count).toByte()
        }

        //var track = AudioTrack(AudioAttributes.CONTENT_TYPE_MUSIC, AudioFormat.CHANNEL_OUT_DEFAULT, soundData.size, AudioTrack.MODE_STATIC, AudioManager.AUDIO_SESSION_ID_GENERATE)

        //var track = AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_DEFAULT,
        //AudioFormat.ENCODING_PCM_8BIT, soundData.size, AudioTrack.MODE_STREAM)

        if (track.state == AudioTrack.STATE_INITIALIZED) {
            track.write(soundData, 0, soundData.size)
            track.play()
        }

        aliveCells.clear()
    }
}