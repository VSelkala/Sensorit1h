
package com.example.sensorit1h

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.Surface

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null
    private lateinit var textView: TextView
    private lateinit var myView: MyView
    private var lastTimestamp = 0L
    private var velocityX = 0f
    private var velocityY = 0f
    private val sensitivity = 250f
    private val damping = 0.90f
    private fun getScreenRotation(): Int {
        return windowManager.defaultDisplay.rotation
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val clearButton = findViewById<Button>(R.id.clearButton)
        clearButton.setOnClickListener {
            myView.clearTrail()
        }
        textView = findViewById(R.id.textView)
        myView = findViewById(R.id.myView)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    }

    override fun onResume() {
        super.onResume()
        lastTimestamp = 0L
        gyroscope?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_GYROSCOPE) return

        if (myView.width == 0 || myView.height == 0) return

        val timestamp = event.timestamp

        if (lastTimestamp == 0L) {
            lastTimestamp = timestamp
            return
        }

        val deltaTime = (timestamp - lastTimestamp) / 1_000_000_000f
        lastTimestamp = timestamp

        val gyroX: Float
        val gyroY: Float

        when (getScreenRotation()) {
            Surface.ROTATION_0 -> {
                gyroX = event.values[0]
                gyroY = event.values[1]
            }

            Surface.ROTATION_90 -> {
                gyroX = -event.values[1]
                gyroY = event.values[0]
            }

            Surface.ROTATION_180 -> {
                gyroX = -event.values[0]
                gyroY = -event.values[1]
            }

            Surface.ROTATION_270 -> {
                gyroX = event.values[1]
                gyroY = -event.values[0]
            }

            else -> {
                gyroX = event.values[0]
                gyroY = event.values[1]
            }
        }

        velocityX += gyroY * sensitivity * deltaTime
        velocityY += gyroX * sensitivity * deltaTime

        velocityX *= damping
        velocityY *= damping

        val newX = myView.currentX + velocityX
        val newY = myView.currentY + velocityY

        myView.setXY(newX, newY)

        textView.text = String.format(
            "Gyro X: %.3f\nGyro Y: %.3f\nPisteen X: %.1f\nPisteen Y: %.1f",
            gyroX,
            gyroY,
            myView.currentX,
            myView.currentY
        )

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}
