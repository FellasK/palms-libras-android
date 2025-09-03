package etec.com.tcc.palmslibras.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.database.DatabaseHelper;
import etec.com.tcc.palmslibras.models.Activity;
import etec.com.tcc.palmslibras.utils.SessionManager;

public class LessonActivity extends AppCompatActivity {

    private TextView questionText;
    private RadioGroup optionsGroup;
    private Button submitButton;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private List<Activity> activityList;
    private int currentActivityIndex = 0;
    private int xpEarned = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitButton = findViewById(R.id.submitButton);

        int lessonId = getIntent().getIntExtra("LESSON_ID", 1);
        activityList = dbHelper.getActivitiesForLesson(lessonId);
        Collections.shuffle(activityList);

        loadActivity();

        submitButton.setOnClickListener(v -> {
            if (submitButton.getText().toString().equalsIgnoreCase("Verificar")) {
                checkAnswer();
            }
        });
    }

    private void loadActivity() {
        if (currentActivityIndex < activityList.size()) {
            Activity currentActivity = activityList.get(currentActivityIndex);
            questionText.setText(currentActivity.getQuestion());

            optionsGroup.clearCheck();
            optionsGroup.removeAllViews();
            submitButton.setText("Verificar");
            submitButton.setOnClickListener(v -> checkAnswer());


            try {
                JSONArray optionsArray = new JSONArray(currentActivity.getOptionsJson());
                List<String> options = new ArrayList<>();
                for (int i = 0; i < optionsArray.length(); i++) {
                    options.add(optionsArray.getString(i));
                }
                Collections.shuffle(options);

                for (String optionText : options) {
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(optionText);
                    radioButton.setTextSize(18f);
                    radioButton.setPadding(32, 32, 32, 32);
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.MATCH_PARENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 0, 24);
                    radioButton.setLayoutParams(params);
                    optionsGroup.addView(radioButton);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            completeLesson();
        }
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Selecione uma opção!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String selectedAnswer = selectedRadioButton.getText().toString();
        Activity currentActivity = activityList.get(currentActivityIndex);

        boolean isCorrect = selectedAnswer.equals(currentActivity.getCorrectAnswer());

        if (isCorrect) {
            xpEarned += currentActivity.getXpReward();
            selectedRadioButton.setBackground(ContextCompat.getDrawable(this, R.drawable.radio_button_correct_background));
            Toast.makeText(this, "Correto! +" + currentActivity.getXpReward() + " XP", Toast.LENGTH_SHORT).show();
        } else {
            selectedRadioButton.setBackground(ContextCompat.getDrawable(this, R.drawable.radio_button_incorrect_background));
            Toast.makeText(this, "Incorreto. A resposta era: " + currentActivity.getCorrectAnswer(), Toast.LENGTH_SHORT).show();
        }

        submitButton.setText("Próximo");
        submitButton.setOnClickListener(v -> {
            currentActivityIndex++;
            loadActivity();
        });
    }

    private void completeLesson() {
        long userId = sessionManager.getUserId();
        if (userId != -1) {
            dbHelper.updateUserXp(userId, xpEarned);
        }

        Toast.makeText(this, "Lição Completa! Você ganhou " + xpEarned + " XP!", Toast.LENGTH_LONG).show();
        finish();
    }
}