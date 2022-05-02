package com.spacenine.nemo.magnet

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class MagnetDetector(private val magneticListener: MagneticListener) : SensorEventListener {

    interface MagneticListener {
        fun onHit(mX: Float, mY: Float, mZ: Float)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        magneticListener.onHit(x, y, z)
    }


}