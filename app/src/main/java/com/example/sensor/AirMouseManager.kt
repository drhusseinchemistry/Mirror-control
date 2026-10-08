package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class AirMouseManager(
    context: Context,
    private val onDelta: (dx: Float, dy: Float) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val gyroSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var isRunning = false
        private set

    var sensitivity: Float = 1.0f

    fun start(): Boolean {
        if (isRunning) return true
        val sensor = gyroSensor ?: accelSensor ?: return false
        val success = sensorManager?.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_GAME
        ) ?: false
        isRunning = success
        return success
    }

    fun stop() {
        if (!isRunning) return
        sensorManager?.unregisterListener(this)
        isRunning = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !isRunning) return

        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            // Gyroscope gives angular speed (rad/s)
            val rawZ = -event.values[2] // Yaw -> horizontal
            val rawX = -event.values[0] // Pitch -> vertical

            val deadband = 0.05f
            val dx = if (abs(rawZ) > deadband) rawZ * 14f * sensitivity else 0f
            val dy = if (abs(rawX) > deadband) rawX * 14f * sensitivity else 0f

            if (dx != 0f || dy != 0f) {
                onDelta(dx, dy)
            }
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Accelerometer fallback tilt
            val tiltX = -event.values[0]
            val tiltY = event.values[1] - 5f // offset typical holding angle

            val deadband = 0.4f
            val dx = if (abs(tiltX) > deadband) tiltX * 3.5f * sensitivity else 0f
            val dy = if (abs(tiltY) > deadband) tiltY * 3.5f * sensitivity else 0f

            if (dx != 0f || dy != 0f) {
                onDelta(dx, dy)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
