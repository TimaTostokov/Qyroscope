package com.cleanlotos.qyroscope;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private SensorManager manager;

    private Sensor sensor;

    private ImageView imageView;

    private TextView textView;

    private SensorEventListener sensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView = findViewById(R.id.tv_textView);
        imageView = findViewById(R.id.imageView);
        Button button = findViewById(R.id.button_next);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        button.setOnClickListener(v -> {

            CompassFragment compassFragment = new CompassFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, compassFragment)
                    .addToBackStack(null)
                    .commit();

            Toast.makeText(MainActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
        });

        if (manager != null) sensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        sensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {

                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, event.values);

                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_Z,
                        SensorManager.AXIS_Y,
                        remappedRotationMatrix);

                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);
                for (int i = 0; i < 3; i++) {
                    orientations[i] = (float) (Math.toDegrees(orientations[i]));
                }

                textView.setText(String.valueOf((int) orientations[2]));
                imageView.setRotation(-orientations[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //TODO
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(sensorEventListener);
    }

}