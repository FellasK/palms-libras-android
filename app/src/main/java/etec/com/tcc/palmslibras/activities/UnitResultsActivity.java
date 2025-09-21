package etec.com.tcc.palmslibras.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import etec.com.tcc.palmslibras.R;

public class UnitResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_results);

        TextView tvTotalXp = findViewById(R.id.tvTotalXp);
        TextView tvAccuracy = findViewById(R.id.tvAccuracy);
        Button btnFinish = findViewById(R.id.btnFinish);

        int xpGained = getIntent().getIntExtra("XP_GAINED", 0);
        int lessonsCompleted = getIntent().getIntExtra("LESSONS_COMPLETED", 1);
        int correctAnswers = getIntent().getIntExtra("CORRECT_ANSWERS", 0);

        tvTotalXp.setText(getString(R.string.results_xp_format, xpGained));

        if (lessonsCompleted > 0) {
            float accuracy = ((float) correctAnswers / lessonsCompleted) * 100;
            tvAccuracy.setText(String.format(Locale.US, getString(R.string.results_accuracy_format), accuracy));
        } else {
            tvAccuracy.setText(String.format(Locale.US, getString(R.string.results_accuracy_format), 0f));
        }

        btnFinish.setOnClickListener(v -> finish());
    }
}