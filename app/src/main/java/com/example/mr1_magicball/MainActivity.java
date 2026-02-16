package com.example.mr1_magicball;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button askButton;
    private String[] answers;
    private Random random;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Vibrator vibrator;

    private long lastShakeTime = 0;
    private long lastUpdate = 0;
    private float lastX, lastY, lastZ;

    // Константы для определения встряхивания
    private static final int SHAKE_THRESHOLD = 800; // Минимальная сила встряхивания
    private static final int SHAKE_TIMEOUT = 1000; // Минимальное время между встряхиваниями (мс)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Инициализация массива ответов
        answers = new String[]{
                "Бесспорно",
                "Предрешено",
                "Никаких сомнений",
                "Определённо да",
                "Можешь быть уверен в этом",
                "Мне кажется — да",
                "Вероятнее всего",
                "Хорошие перспективы",
                "Знаки говорят — да",
                "Да",
                "Пока не ясно, попробуй снова",
                "Спроси позже",
                "Лучше не рассказывать",
                "Сейчас нельзя предсказать",
                "Сконцентрируйся и спроси опять",
                "Даже не думай",
                "Мой ответ — нет",
                "По моим данным — нет",
                "Перспективы не очень хорошие",
                "Весьма сомнительно"
        };

        random = new Random();

        askButton = findViewById(R.id.askButton);

        vibrator  = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (!vibrator.hasVibrator()) {
            Toast.makeText(this, "Вибромотора нет", Toast.LENGTH_SHORT).show();
        }

        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMagicAnswer();
                startVibration();
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

    }

    private void showMagicAnswer() {
        int index = random.nextInt(answers.length);
        String answer = answers[index];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ответ магического шара")
                .setMessage(answer)
                .setPositiveButton("Спросить снова", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();

            // Проверяем, прошло ли достаточно времени с последнего обновления
            if ((currentTime - lastUpdate) > 100) {
                long timeDifference = currentTime - lastUpdate;
                lastUpdate = currentTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // Вычисляем разницу с предыдущими значениями
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / timeDifference * 10000;

                // Если скорость превышает порог и прошло достаточно времени с последнего встряхивания
                if (speed > SHAKE_THRESHOLD && (currentTime - lastShakeTime) > SHAKE_TIMEOUT) {
                    lastShakeTime = currentTime;

                    startVibration();

                    // Запускаем в UI потоке
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMagicAnswer();
                        }
                    });
                }

                // Сохраняем текущие значения для следующего сравнения
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    private void startVibration() {
        if (vibrator == null || !vibrator.hasVibrator()) return;

        int duration = 500;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            VibrationEffect effect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(duration);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}