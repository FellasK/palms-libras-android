package etec.com.tcc.palmslibras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.fragments.MemoryFragment;
import etec.com.tcc.palmslibras.fragments.QaFragment;
import etec.com.tcc.palmslibras.models.Gesture;
import etec.com.tcc.palmslibras.models.Lesson;
import etec.com.tcc.palmslibras.utils.GestureManager;
import etec.com.tcc.palmslibras.utils.OnLessonCompleteListener;

public class UnitActivity extends AppCompatActivity implements OnLessonCompleteListener {

    private ProgressBar progressBar;
    private TextView tvLives;
    private LinearLayout feedbackContainer;
    private TextView tvFeedback;
    private Button btnContinue;
    private ImageButton closeButton;

    private List<Lesson> lessonQueue = new ArrayList<>();
    private int currentLessonIndex = 0;
    private int lives = 3;
    private int xpGained = 0;
    private int correctAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        GestureManager.loadGestures(getApplicationContext());

        initializeViews();
        loadUnitLessons();
        loadCurrentLesson();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        tvLives = findViewById(R.id.tvLives);
        feedbackContainer = findViewById(R.id.feedbackContainer);
        tvFeedback = findViewById(R.id.tvFeedback);
        btnContinue = findViewById(R.id.btnContinue);
        closeButton = findViewById(R.id.closeButton);

        tvLives.setText(String.valueOf(lives));
        closeButton.setOnClickListener(v -> finish());
        btnContinue.setOnClickListener(v -> proceedToNextLesson());
    }

    private void loadUnitLessons() {
        List<Gesture> unitGestures = GestureManager.getGesturesForUnit(1);
        Collections.shuffle(unitGestures);

        for (int i = 0; i < unitGestures.size(); i++) {
            Gesture currentGesture = unitGestures.get(i);

            if (i > 0 && i % 3 == 0) {
                Collections.shuffle(unitGestures);
                List<Gesture> memoryPairs = new ArrayList<>(unitGestures.subList(0, 4));
                lessonQueue.add(Lesson.createMemoryLesson(memoryPairs));
            }

            List<Gesture> options = GestureManager.getRandomGestures(currentGesture, 3);
            options.add(currentGesture);
            Collections.shuffle(options);
            lessonQueue.add(Lesson.createQaLesson(options, currentGesture));
        }

        progressBar.setMax(lessonQueue.size());
        progressBar.setProgress(0);
    }

    private void loadCurrentLesson() {
        if (currentLessonIndex >= lessonQueue.size()) {
            showUnitResults();
            return;
        }

        Lesson lesson = lessonQueue.get(currentLessonIndex);
        Fragment lessonFragment;

        switch (lesson.getType()) {
            case MEMORY_GAME:
                lessonFragment = new MemoryFragment();
                break;
            case QUESTION_ANSWER:
            default:
                lessonFragment = new QaFragment();
                break;
        }

        Bundle args = new Bundle();
        args.putSerializable("lesson_data", lesson);
        lessonFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.lesson_fragment_container, lessonFragment);
        transaction.commit();
    }

    @Override
    public void onLessonCompleted(boolean isCorrect) {
        if (isCorrect) {
            xpGained += 10;
            correctAnswers++;
            showFeedback(true);
        } else {
            lives--;
            tvLives.setText(String.valueOf(lives));
            showFeedback(false);
            if (lives <= 0) {
                Toast.makeText(this, getString(R.string.no_more_lives_toast), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void showFeedback(boolean isCorrect) {
        feedbackContainer.setVisibility(View.VISIBLE);
        if (isCorrect) {
            feedbackContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.feedback_correct_background));
            tvFeedback.setText(R.string.feedback_correct);
            btnContinue.setTextColor(ContextCompat.getColor(this, R.color.feedback_correct_background));
        } else {
            feedbackContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.feedback_incorrect_background));
            tvFeedback.setText(R.string.feedback_incorrect);
            btnContinue.setTextColor(ContextCompat.getColor(this, R.color.feedback_incorrect_background));
        }
    }

    private void proceedToNextLesson(){
        currentLessonIndex++;
        progressBar.setProgress(currentLessonIndex);
        feedbackContainer.setVisibility(View.GONE);
        loadCurrentLesson();
    }

    private void showUnitResults() {
        Intent intent = new Intent(this, UnitResultsActivity.class);
        intent.putExtra("XP_GAINED", xpGained);
        intent.putExtra("LESSONS_COMPLETED", lessonQueue.size());
        intent.putExtra("CORRECT_ANSWERS", correctAnswers);
        startActivity(intent);
        finish();
    }
}