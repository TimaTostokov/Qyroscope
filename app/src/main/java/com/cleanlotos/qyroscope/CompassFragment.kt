package com.cleanlotos.qyroscope

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cleanlotos.qyroscope.databinding.FragmentCompassBinding

class CompassFragment : Fragment(R.layout.fragment_compass), SensorEventListener {

    private val binding by viewBinding(FragmentCompassBinding::bind)

    private var currentDegree = 0f

    private var sensorManager: SensorManager? = null

    private var tvHeading: TextView? = null

    private var image: ImageView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        tvHeading = view.findViewById(R.id.tv_header)
        image = view.findViewById(R.id.img_compass)
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val degree = Math.round(event!!.values[0])
        tvHeading?.text = buildString {
            append("Heading ")
            append(degree)
            append(" degree")
        }

        val rotation = RotateAnimation(
            currentDegree, (-degree).toFloat(), Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotation.duration = 210
        rotation.fillAfter = true
        image?.startAnimation(rotation)
        currentDegree = -degree.toFloat()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

}