package com.spacenine.nemo.gravity

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.spacenine.nemo.SensorService.MAX_NET_G_VALUE
import com.spacenine.nemo.R
import com.spacenine.nemo.gravity.ForceDetector.ForceListener
import kotlinx.android.synthetic.main.activity_gravity.*

class GravityActivity : AppCompatActivity() {

    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor

    private val mForceDetector = ForceDetector(object : ForceListener {
        override fun onHit(gX: Float, gY: Float, gZ: Float) {
            val gNet = getNetForce(gX.toDouble(), gY.toDouble(), gZ.toDouble())

            netForceTextView.text = gNet.toString()
            netForceProgressBar.progress = gNet.toInt()

            forceOnX.text = "$gX"
            forceOnY.text = "$gY"
            forceOnZ.text = "$gZ"

        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gravity)

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        maxForceTextView.text = maxForceTextView.text.toString() + "$MAX_NET_G_VALUE" + " m/s^2"

        // register the listener
        mSensorManager.registerListener(mForceDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI)

    }

    override fun onDestroy() {
        super.onDestroy()

        mSensorManager.unregisterListener(mForceDetector, mAccelerometer)
    }
}