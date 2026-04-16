package com.panelsandwich.checklist.vistas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import java.io.ByteArrayOutputStream

class VistaFirma(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val path = Path()
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var trazos = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        canvas?.drawColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap != null) {
            canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        }
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                canvas?.drawPath(path, paint)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                canvas?.drawPath(path, paint)
                trazos++
                path.reset()
                invalidate()
                return true
            }
        }
        return false
    }

    fun limpiar() {
        bitmap?.let {
            canvas?.drawColor(Color.WHITE)
            trazos = 0
            invalidate()
        }
    }

    fun estaVacia(): Boolean = trazos == 0

    fun obtenerFirmaBase64(): String {
        if (bitmap == null) return ""
        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun obtenerFirma(): Bitmap? = bitmap
}
