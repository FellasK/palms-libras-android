package etec.com.tcc.palmslibras.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.adapters.CourseAdapter;
import etec.com.tcc.palmslibras.models.Course;
import etec.com.tcc.palmslibras.database.DatabaseHelper;
import etec.com.tcc.palmslibras.models.User;
import etec.com.tcc.palmslibras.utils.SessionManager;

public class CourseFragment extends Fragment {

    private RecyclerView coursesRecyclerView;
    private android.widget.TextView greetingTextView;
    private android.widget.TextView tvLevel;
    private android.widget.TextView tvXp;
    private android.widget.TextView tvStreak;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        greetingTextView = view.findViewById(R.id.greetingTextView);
        tvLevel = view.findViewById(R.id.tvLevel);
        tvXp = view.findViewById(R.id.tvXp);
        tvStreak = view.findViewById(R.id.tvStreak);
        sessionManager = new SessionManager(getContext());
        dbHelper = new DatabaseHelper(getContext());

        coursesRecyclerView = view.findViewById(R.id.coursesRecyclerView);
        coursesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), calculateSpanCount()));
        coursesRecyclerView.setHasFixedSize(true);

        // Criando uma lista estática de cursos para exibição
        List<Course> courseList = new ArrayList<>();
        courseList.add(new Course(getString(R.string.course_alphabet_title), getString(R.string.course_alphabet_description), R.drawable.ic_abc_placeholder, 1, true, R.color.primary_purple));
        courseList.add(new Course(getString(R.string.numbers_title), getString(R.string.numbers_description), 0, 2, false, R.color.primary_blue));
        courseList.add(new Course(getString(R.string.colors_title), getString(R.string.colors_description), R.drawable.ic_activities, 3, false, R.color.primary_red));
        //lugar para adicionar mais cursos
        CourseAdapter adapter = new CourseAdapter(courseList, getContext());
        coursesRecyclerView.setAdapter(adapter);

        loadUserData();

        return view;
    }

    private void loadUserData() {
        long userId = sessionManager.getUserId();
        User user = dbHelper.getUserById(userId);
        String name = "Usuário";
        if (user != null) name = user.getName();
        Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY);
        String greeting = h < 12 ? "Bom dia" : h < 18 ? "Boa tarde" : "Boa noite";
        greetingTextView.setText(greeting + ", " + name + "!");
        if (user != null) {
            tvLevel.setText("Nível " + user.getLevel());
            tvXp.setText(user.getXp() + " XP");
            tvStreak.setText(user.getStreak() + " dias de sequência 🔥");
        }
    }

    private int calculateSpanCount() {
        if (getContext() == null) return 2;
        float density = getContext().getResources().getDisplayMetrics().density;
        int widthPx = getContext().getResources().getDisplayMetrics().widthPixels;
        float widthDp = widthPx / density;
        if (widthDp >= 600) return 3;
        return 2;
    }
}