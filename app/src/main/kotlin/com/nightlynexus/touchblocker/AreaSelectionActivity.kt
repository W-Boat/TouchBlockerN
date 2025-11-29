package com.nightlynexus.touchblocker

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View

class AreaSelectionActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(SelectionView(this))
  }

  private inner class SelectionView(context: Context) : View(context) {
    private val paint = Paint().apply {
      color = Color.argb(128, 255, 0, 0)
      style = Paint.Style.FILL
    }
    private val borderPaint = Paint().apply {
      color = Color.RED
      style = Paint.Style.STROKE
      strokeWidth = 5f
    }
    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var drawing = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          startX = event.x
          startY = event.y
          drawing = true
        }
        MotionEvent.ACTION_MOVE -> {
          endX = event.x
          endY = event.y
          invalidate()
        }
        MotionEvent.ACTION_UP -> {
          endX = event.x
          endY = event.y
          drawing = false
          saveSelection()
          this@AreaSelectionActivity.finish()
        }
      }
      return true
    }

    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      if (drawing) {
        val rect = RectF(
          minOf(startX, endX),
          minOf(startY, endY),
          maxOf(startX, endX),
          maxOf(startY, endY)
        )
        canvas.drawRect(rect, paint)
        canvas.drawRect(rect, borderPaint)
      }
    }

    private fun saveSelection() {
      val prefs = this@AreaSelectionActivity.getSharedPreferences("touch_blocker", Context.MODE_PRIVATE)
      prefs.edit().apply {
        putFloat("block_left", minOf(startX, endX))
        putFloat("block_top", minOf(startY, endY))
        putFloat("block_right", maxOf(startX, endX))
        putFloat("block_bottom", maxOf(startY, endY))
        apply()
      }
    }
  }
}
