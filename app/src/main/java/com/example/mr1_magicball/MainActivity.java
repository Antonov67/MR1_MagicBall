package com.example.mr1_magicball;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button askButton;
    private String[] answers;
    private Random random;

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

        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMagicAnswer();
            }
        });

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
}