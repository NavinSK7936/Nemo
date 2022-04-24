package com.spacenine.nemo

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt


class ShakeDetector(private var onShakeListener: OnShakeListener? = null) : SensorEventListener {
    private var mShakeTimestamp: Long = 0
    private var mShakeCount = 0

    companion object {
        /*
     * The gForce that is necessary to register as shake.
     * Must be greater than 1G (one earth gravity unit).
     * You can install "G-Force", by Blake La Pierre
     * from the Google Play Store and run it to see how
     *  many G's it takes to register a shake
     */
        private const val SHAKE_THRESHOLD_GRAVITY = 2.7f
        private const val SHAKE_SLOP_TIME_MS = 500
        private const val SHAKE_COUNT_RESET_TIME_MS = 3000
    }

    interface OnShakeListener {
        fun onShake(count: Int)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // ignore
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (onShakeListener != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            // gForce will be close to 1 when there is no movement.
            val f = gX * gX + gY * gY + gZ * gZ
            val d = sqrt(f.toDouble())
            val gForce = d.toFloat()

            Log.e("WTF", "$gX|$gY|$gZ||$gForce")

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                // ignore shake events too close to each other (500ms)

                Log.e("OKKKK", "${mShakeTimestamp + SHAKE_SLOP_TIME_MS}-$now")

                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }

                Log.e("OKKKKBYEEEEE", "${mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS}-$now")

                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0
                }
                mShakeTimestamp = now
                mShakeCount++

                Log.e("OKKKKBYEEEEEOKKKKKK", "$mShakeCount")

                onShakeListener!!.onShake(mShakeCount)
            }
        }
    }
}