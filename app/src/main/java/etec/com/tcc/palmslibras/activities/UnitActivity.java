package etec.com.tcc.palmslibras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.fragments.MemoryFragment;
import etec.com.tcc.palmslibras.fragments.QaFragment;
import etec.com.tcc.palmslibras.models.Gesture;
import etec.com.tcc.palmslibras.models.Exercices;
import etec.com.tcc.palmslibras.utils.GestureManager;
import etec.com.tcc.palmslibras.utils.OnLessonCompleteListener;

public class UnitActivity extends AppCompatActivity implements OnLessonCompleteListener {

    private ProgressBar progressBar;
    private LinearLayout feedbackContainer;
    private TextView tvFeedback;
    private ImageButton closeButton;
    private List<ImageView> hearts = new ArrayList<>();

    private List<Exercices> exercicesQueue = new ArrayList<>();
    private int currentLessonIndex = 0;
    private int lives = 5;
    private final int MAX_LIVES = 5;
    private int xpGained = 0;
    private int correctAnswers = 0;

    // Handler para agendar a próxima lição
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        GestureManager.loadGestures(getApplicationContext());

        initializeViews();
        updateLivesDisplay();
        loadUnitLessons();
        loadCurrentLesson();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        feedbackContainer = findViewById(R.id.feedbackContainer);
        tvFeedback = findViewById(R.id.tvFeedback);
        closeButton = findViewById(R.id.closeButton);

        hearts.add(findViewById(R.id.heart1));
        hearts.add(findViewById(R.id.heart2));
        hearts.add(findViewById(R.id.heart3));
        hearts.add(findViewById(R.id.heart4));
        hearts.add(findViewById(R.id.heart5));

        closeButton.setOnClickListener(v -> finish());
        // A linha abaixo foi removida para desativar o clique no feedback
        // feedbackContainer.setOnClickListener(v -> proceedToNextLesson());
    }

    private void updateLivesDisplay() {
        for (int i = 0; i < MAX_LIVES; i++) {
            if (i < lives) {
                hearts.get(i).setVisibility(View.VISIBLE);
            } else {
                // Usar GONE em vez de INVISIBLE para não ocupar espaço
                hearts.get(i).setVisibility(View.GONE);
            }
        }
    }


    private void loadUnitLessons() {
        List<Gesture> unitGestures = GestureManager.getGesturesForUnit(1);
        Collections.shuffle(unitGestures);

        for (int i = 0; i < unitGestures.size(); i++) {
            Gesture currentGesture = unitGestures.get(i);

            if (i > 0 && i % 3 == 0) {
                Collections.shuffle(unitGestures);
                List<Gesture> memoryPairs = new ArrayList<>(unitGestures.subList(0, 4));
                exercicesQueue.add(Exercices.createMemoryLesson(memoryPairs));
            }

            List<Gesture> options = GestureManager.getRandomGestures(currentGesture, 3);
            options.add(currentGesture);
            Collections.shuffle(options);
            exercicesQueue.add(Exercices.createQaLesson(options, currentGesture));
        }

        progressBar.setMax(exercicesQueue.size());
        progressBar.setProgress(0);
    }

    private void loadCurrentLesson() {
        if (currentLessonIndex >= exercicesQueue.size()) {
            showUnitResults();
            return;
        }

        Exercices exercices = exercicesQueue.get(currentLessonIndex);
        Fragment lessonFragment;

        switch (exercices.getType()) {
            case MEMORY_GAME:
                lessonFragment = new MemoryFragment();
                break;
            case QUESTION_ANSWER:
            default:
                lessonFragment = new QaFragment();
                break;
        }

        Bundle args = new Bundle();
        args.putSerializable("lesson_data", exercices);
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
            updateLivesDisplay();
            showFeedback(false);
            // A verificação de "sem vidas" foi movida para proceedToNextLesson
            // para que o feedback de erro seja exibido antes de a atividade terminar.
        }
    }

    private void showFeedback(boolean isCorrect) {
        feedbackContainer.setVisibility(View.VISIBLE);
        if (isCorrect) {
            feedbackContainer.setBackgroundResource(R.drawable.button_primary_background);
            tvFeedback.setText(getString(R.string.feedback_correct_xp, 10));
        } else {
            feedbackContainer.setBackgroundResource(R.drawable.button_incorrect_background);
            tvFeedback.setText(R.string.feedback_incorrect);
        }

        // Agenda a chamada para a próxima lição após 3 segundos
        handler.postDelayed(() -> {
            if (!isFinishing()) { // Garante que a atividade ainda está ativa
                proceedToNextLesson();
            }
        }, 3000);
    }

    private void proceedToNextLesson(){
        // Esconde o feedback antes de carregar a próxima lição
        feedbackContainer.setVisibility(View.GONE);

        // Verifica se o jogador ainda tem vidas
        if (lives <= 0) {
            Toast.makeText(this, getString(R.string.no_more_lives_toast), Toast.LENGTH_LONG).show();
            finish();
            return; // Interrompe a execução para não carregar a próxima lição
        }

        currentLessonIndex++;
        progressBar.setProgress(currentLessonIndex);
        loadCurrentLesson();
    }

    private void showUnitResults() {
        Intent intent = new Intent(this, UnitResultsActivity.class);
        intent.putExtra("XP_GAINED", xpGained);
        intent.putExtra("LESSONS_COMPLETED", exercicesQueue.size());
        intent.putExtra("CORRECT_ANSWERS", correctAnswers);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove quaisquer callbacks pendentes para evitar memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}