package com.spacenine.nemo.magnet

import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.spacenine.nemo.R
import com.spacenine.nemo.gravity.getNetForce
import kotlinx.android.synthetic.main.activity_magnet.*

class MagnetActivity : AppCompatActivity() {

    private lateinit var mSensorManager: SensorManager
    private lateinit var mMagnetometer: Sensor

    private val mMagnetDetector = MagnetDetector(object : MagnetDetector.MagneticListener {
        override fun onHit(mX: Float, mY: Float, mZ: Float) {
            val mNet = getNetForce(mX.toDouble(), mY.toDouble(), mZ.toDouble())

            netForceTextView.text = mNet.toString()
            netForceProgressBar.progress = mNet.toInt()

            forceOnX.text = "$mX"
            forceOnY.text = "$mY"
            forceOnZ.text = "$mZ"
        }

    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magnet)

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        mSensorManager.registerListener(mMagnetDetector, mMagnetometer, SensorManager.SENSOR_DELAY_UI)

    }
}