package com.example.touchprint

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.max

class MainActivity : Activity() {
    private data class Contact(val x: Float, val y: Float, val rx: Float, val ry: Float, val angle: Float)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        setContentView(TouchView())
    }
    private inner class TouchView : View(this) {
        private val contacts = mutableMapOf<Int, Contact>()
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
        private val density = resources.displayMetrics.density
        override fun onDraw(canvas: Canvas) {
            canvas.drawColor(Color.BLACK)
            for (c in contacts.values) {
                canvas.save()
                canvas.rotate(c.angle, c.x, c.y)
                canvas.drawOval(c.x-c.rx, c.y-c.ry, c.x+c.rx, c.y+c.ry, paint)
                canvas.restore()
            }
        }
        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (event.actionMasked == MotionEvent.ACTION_CANCEL) {
                contacts.clear()
            } else {
                contacts.clear()
                for (i in 0 until event.pointerCount) {
                    if ((event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_POINTER_UP) && i == event.actionIndex) continue
                    val major = event.getTouchMajor(i)
                    val minor = event.getTouchMinor(i)
                    val fallback = max(12f * density, event.getSize(i) * 45f * density)
                    val rx = if (major > 0f) major / 2f else fallback / 2f
                    val ry = if (minor > 0f) minor / 2f else fallback / 2f
                    contacts[event.getPointerId(i)] = Contact(event.getX(i), event.getY(i), rx, ry, event.getOrientation(i) * 180f / Math.PI.toFloat())
                }
            }
            invalidate()
            return true
        }
    }
}
