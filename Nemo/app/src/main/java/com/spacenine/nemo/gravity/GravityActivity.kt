package com.spacenine.nemo.gravity

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.spacenine.nemo.MAX_NET_G_VALUE
import com.spacenine.nemo.R
import com.spacenine.nemo.gravity.ForceDetector.ForceListener
import kotlinx.android.synthetic.main.activity_gravity.*

class GravityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gravity)

        maxForceTextView.text = maxForceTextView.text.toString() + "$MAX_NET_G_VALUE" + " m/s^2"

        val mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val mForceDetector = ForceDetector(object : ForceListener {
            override fun onHit(gX: Float, gY: Float, gZ: Float) {
                val gNet = getNetForce(gX.toDouble(), gY.toDouble(), gZ.toDouble())

                netForceTextView.text = "$gNet"
                netForceProgressBar.progress = gNet.toInt()

                forceOnX.text = "$gX"
                forceOnY.text = "$gY"
                forceOnZ.text = "$gZ"

            }
        })

        // register the listener
        mSensorManager.registerListener(mForceDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI)

    }
}