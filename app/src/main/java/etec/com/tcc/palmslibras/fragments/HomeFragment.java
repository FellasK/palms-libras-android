package etec.com.tcc.palmslibras.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.activities.UnitActivity;
import etec.com.tcc.palmslibras.database.DatabaseHelper;
import etec.com.tcc.palmslibras.models.User;
import etec.com.tcc.palmslibras.utils.SessionManager;

public class HomeFragment extends Fragment {

    private TextView greetingTextView;
    private TextView tvLevel;
    private TextView tvXp;
    private TextView tvStreak;
    private ProgressBar progressModule1;
    private ProgressBar progressModule2;
    private ProgressBar progressModule3;
    private Button btnContinueCourse;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        greetingTextView = view.findViewById(R.id.greetingTextView);
        tvLevel = view.findViewById(R.id.tvLevel);
        tvXp = view.findViewById(R.id.tvXp);
        tvStreak = view.findViewById(R.id.tvStreak);
        progressModule1 = view.findViewById(R.id.progressModule1);
        progressModule2 = view.findViewById(R.id.progressModule2);
        progressModule3 = view.findViewById(R.id.progressModule3);
        btnContinueCourse = view.findViewById(R.id.btnContinueCourse);
        sessionManager = new SessionManager(getContext());
        dbHelper = new DatabaseHelper(getContext());

        loadUserData();

        btnContinueCourse.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UnitActivity.class);
            intent.putExtra("LESSON_ID", 1);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserData() {
        long userId = sessionManager.getUserId();
        User user = dbHelper.getUserById(userId);
        String name = "Usuário";
        if (user != null) {
            name = user.getName();
        }

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Bom dia";
        } else if (timeOfDay >= 12 && timeOfDay < 18) {
            greeting = "Boa tarde";
        } else {
            greeting = "Boa noite";
        }

        greetingTextView.setText(greeting + ", " + name + "!");

        if (user != null) {
            tvLevel.setText("Nível " + user.getLevel());
            tvXp.setText(user.getXp() + " XP");
            tvStreak.setText(user.getStreak() + " dias de sequência 🔥");

            int xp = user.getXp();
            progressModule1.setProgress(Math.min(100, (xp % 100)));
            progressModule2.setProgress(Math.min(100, (xp / 4) % 100));
            progressModule3.setProgress(Math.min(100, (xp / 7) % 100));
        }
    }
}