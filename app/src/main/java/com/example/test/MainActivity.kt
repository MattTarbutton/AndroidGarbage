package com.example.test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.R.attr.y
import android.R.attr.x
import android.graphics.Color
import android.graphics.Point
import android.view.Display
import android.view.KeyEvent
import android.view.View
import android.view.KeyEvent.KEYCODE_BACK
import android.content.Intent






class MainActivity : AppCompatActivity() {

    private lateinit var gameOfLifeView: GameOfLifeView
    private lateinit var colorTapView: ColorTapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        gameOfLifeView = GameOfLifeView(this, width, height)
        colorTapView = ColorTapView(this)
        setContentView(R.layout.activity_main)
        //view.setOnTouchListener{v: View, event: MotionEvent -> handleTouch(m) true}
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
            && keyCode == KeyEvent.KEYCODE_BACK
            && event.getRepeatCount() === 0
        ) {
            onBackPressed()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        /*val setIntent = Intent(Intent.ACTION_MAIN)
        setIntent.addCategory(Intent.CATEGORY_HOME)
        setIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(setIntent)*/
        setContentView(R.layout.activity_main)
    }

    fun onColorTapButtonClicked(view: View) {
        setContentView(colorTapView)
    }

    fun gameOfLifeButtonClicked(view: View){
        setContentView(gameOfLifeView)
    }
}
