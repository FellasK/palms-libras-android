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
import java.util.Random;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.fragments.ConnectGameFragment;
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
    private int unitNumber = 1;

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

        closeButton.setOnClickListener(v -> {
            int lessonsCompleted = currentLessonIndex;
            int totalLessons = exercicesQueue != null ? exercicesQueue.size() : 0;
            boolean shouldConfirm = lessonsCompleted >= 1;

            if (!shouldConfirm) {
                finish();
                return;
            }

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setMessage(getString(R.string.exit_activity_confirmation))
                    .setNegativeButton("Continuar", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Sair", (dialog, which) -> finish())
                    .show();
        });
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
        unitNumber = getIntent().getIntExtra("UNIT_NUMBER", 1);
        List<Gesture> novidadesQueue = GestureManager.getGesturesForUnit(unitNumber);
        List<Gesture> unlockedPool = new ArrayList<>();
        Random random = new Random();

        while (!novidadesQueue.isEmpty()) {
            Gesture next = novidadesQueue.remove(0);
            exercicesQueue.add(Exercices.createInstruction(next));
            unlockedPool.add(next);

            if (unlockedPool.size() >= 4) {
                int testsToSchedule = 1 + random.nextInt(2);
                for (int i = 0; i < testsToSchedule; i++) {
                    addRandomTestUsingPool(unlockedPool, random);
                }
            }
        }

        int finalTests = Math.min(3, Math.max(1, unlockedPool.size() / 2));
        for (int i = 0; i < finalTests; i++) addRandomTestUsingPool(unlockedPool, random);

        progressBar.setMax(exercicesQueue.size());
        progressBar.setProgress(0);
    }

    private void addRandomTestUsingPool(List<Gesture> pool, Random random) {
        if (pool == null || pool.size() < 2) return;
        List<Exercices.ActivityDataType> candidates = new ArrayList<>();
        candidates.add(Exercices.ActivityDataType.CAMERA_EXERCISE);
        if (pool.size() >= 4) {
            candidates.add(Exercices.ActivityDataType.MEMORY_GAME);
            candidates.add(Exercices.ActivityDataType.CONNECT_GAME);
        }
        if (pool.size() >= 4) {
            candidates.add(Exercices.ActivityDataType.QUESTION_ANSWER);
        }
        Collections.shuffle(candidates, random);
        Exercices.ActivityDataType type = candidates.get(0);

        switch (type) {
            case QUESTION_ANSWER: {
                Gesture correct = pool.get(random.nextInt(pool.size()));
                List<Gesture> options = new ArrayList<>();
                options.add(correct);
                // escolhe 3 distintos do pool
                List<Gesture> others = new ArrayList<>(pool);
                others.remove(correct);
                Collections.shuffle(others, random);
                int needed = Math.min(3, others.size());
                options.addAll(others.subList(0, needed));
                // se por algum motivo não alcançou 4, duplica alguns existentes (mantém regra de só usar pool)
                while (options.size() < 4 && !others.isEmpty()) {
                    options.add(others.get(random.nextInt(others.size())));
                }
                Collections.shuffle(options, random);
                exercicesQueue.add(Exercices.createQaLesson(options, correct));
                break;
            }
            case MEMORY_GAME: {
                List<Gesture> copy = new ArrayList<>(pool);
                Collections.shuffle(copy, random);
                exercicesQueue.add(Exercices.createMemoryLesson(copy.subList(0, 4)));
                break;
            }
            case CONNECT_GAME: {
                List<Gesture> copy = new ArrayList<>(pool);
                Collections.shuffle(copy, random);
                exercicesQueue.add(Exercices.createConnectLesson(copy.subList(0, 4)));
                break;
            }
            case CAMERA_EXERCISE:
            default: {
                Gesture g = pool.get(random.nextInt(pool.size()));
                exercicesQueue.add(Exercices.createCameraExercise(g));
                break;
            }
        }
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
            case INSTRUCTION:
                lessonFragment = new etec.com.tcc.palmslibras.fragments.InstructionFragment();
                break;
            case CAMERA_EXERCISE:
                lessonFragment = new etec.com.tcc.palmslibras.fragments.CameraExerciseFragment();
                break;
            case CONNECT_GAME: // Adicione este case
                lessonFragment = new ConnectGameFragment();
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
        Exercices current = exercicesQueue.get(currentLessonIndex);
        if (current.getType() == Exercices.ActivityDataType.INSTRUCTION
                || current.getType() == Exercices.ActivityDataType.CAMERA_EXERCISE) {
            proceedToNextLesson();
            return;
        }
        if (current.getType() == Exercices.ActivityDataType.MEMORY_GAME) {
            if (isCorrect) {
                xpGained += 10;
                correctAnswers++;
                showFeedback(true);
            } else {
                lives--;
                updateLivesDisplay();
                if (lives <= 0) {
                    Toast.makeText(this, getString(R.string.no_more_lives_toast), Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
            return;
        }
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

    private void proceedToNextLesson() {
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
        int totalAssessable = 0;
        for (Exercices ex : exercicesQueue) {
            if (ex.getType() != Exercices.ActivityDataType.INSTRUCTION) totalAssessable++;
        }
        int maxXp = totalAssessable * 10;

        etec.com.tcc.palmslibras.utils.SessionManager sm = new etec.com.tcc.palmslibras.utils.SessionManager(this);
        etec.com.tcc.palmslibras.database.DatabaseHelper db = new etec.com.tcc.palmslibras.database.DatabaseHelper(this);
        long userId = sm.getUserId();
        if (userId != -1) {
            db.updateUserStats(userId, xpGained, correctAnswers, exercicesQueue.size());
        }
        if (unitNumber == 1) {
            sm.setUnit1Completed(true);
            sm.setUnit2Unlocked(true);
        }

        etec.com.tcc.palmslibras.fragments.UnitCompleteDialogFragment dialog =
                etec.com.tcc.palmslibras.fragments.UnitCompleteDialogFragment.newInstance(xpGained, maxXp, correctAnswers, totalAssessable);
        dialog.show(getSupportFragmentManager(), "unit_complete_dialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove quaisquer callbacks pendentes para evitar memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}