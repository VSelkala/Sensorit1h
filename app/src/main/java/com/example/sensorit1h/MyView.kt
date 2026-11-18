
package com.example.sensorit1h

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class MyView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var x1 = 0f
    private var y1 = 0f
    private var firstPoint = true
    val currentX: Float
        get() = x1

    val currentY: Float
        get() = y1

    // Tallennetaan aikaisempi piirtojälki
    private val path = Path()

    // Mustan viivan asetukset
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    // Punaisen pallon asetukset
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Piirretään aikaisempi viiva
        canvas.drawPath(path, linePaint)

        // Piirretään pallo nykyiseen sijaintiin
        canvas.drawCircle(x1, y1, 10f, circlePaint)
    }

    fun setXY(x: Float, y: Float) {

        // Rajoitetaan pallon liike piirtoalueen sisälle
        x1 = x.coerceIn(10f, (width - 10f).coerceAtLeast(10f))
        y1 = y.coerceIn(10f, (height - 10f).coerceAtLeast(10f))

        // Aloitetaan viiva ensimmäisestä pisteestä
        if (firstPoint) {
            path.moveTo(x1, y1)
            firstPoint = false
        } else {
            path.lineTo(x1, y1)
        }

        invalidate()
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (oldw == 0 || oldh == 0) {
            x1 = w / 2f
            y1 = h / 2f
        }
    }

    // Haluttaessa piirtojäljen voi tyhjentää
    fun clearTrail() {
        path.reset()
        firstPoint = true
        invalidate()
    }
}
