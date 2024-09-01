package com.cleanlotos.qyroscope

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cleanlotos.qyroscope.databinding.FragmentStepCounterBinding

class StepCounterFragment : Fragment(R.layout.fragment_step_counter), SensorEventListener {

    private val binding by viewBinding(FragmentStepCounterBinding::bind)

    private var sensorManager: SensorManager? = null

    private var running = false

    private var totalSteps = 0f

    private var previousTotalSteps = 0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
        resetSteps()
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(
                requireContext(),
                "No sensor detected on this device",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            binding.tvStepsTaken.text = currentSteps.toString()
            binding.circularProgressBar.apply {
                setProgressWithAnimation(currentSteps.toFloat())
            }
        }
    }

    private fun resetSteps() {
        binding.tvStepsTaken.setOnClickListener {
            Toast.makeText(requireContext(), "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        binding.tvStepsTaken.setOnLongClickListener {
            previousTotalSteps = totalSteps
            binding.tvStepsTaken.text = 0.toString()
            saveData()
            true
        }
    }

    private fun getSharedPrefs(): SharedPreferences {
        return getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
    }

    private fun saveData() {
        with(getSharedPrefs().edit()) {
            putFloat("key1", previousTotalSteps)
            apply()
        }
    }

    private fun loadData() {
        previousTotalSteps = getSharedPrefs().getFloat("key1", 0f)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onStop() {
        super.onStop()
        running = false
        sensorManager?.unregisterListener(this)
    }

}