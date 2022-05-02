package com.spacenine.nemo.gravity

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

fun getNetForce(gX: Double, gY: Double, gZ: Double): Double =
    sqrt(gX * gX + gY * gY + gZ * gZ)

class ForceDetector(private val onForceListener: ForceListener) : SensorEventListener {

    interface ForceListener {
        fun onHit(gX: Float, gY: Float, gZ: Float)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

//        val gX = x / SensorManager.GRAVITY_EARTH
//        val gY = y / SensorManager.GRAVITY_EARTH
//        val gZ = z / SensorManager.GRAVITY_EARTH

        onForceListener.onHit(x, y, z)
//        onForceListener.onHit(gX, gY, gZ)
    }


}