package etec.com.tcc.palmslibras.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.adapters.CourseAdapter;
import etec.com.tcc.palmslibras.models.Course;

public class CourseFragment extends Fragment {

    private RecyclerView coursesRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        coursesRecyclerView = view.findViewById(R.id.coursesRecyclerView);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Criando uma lista estática de cursos para exibição
        List<Course> courseList = new ArrayList<>();
        courseList.add(new Course("Alfabeto", "Aprenda o alfabeto do A ao Z", R.drawable.ic_activities, 1));
        courseList.add(new Course("Números", "Aprenda os números de 1 a 10", R.drawable.ic_activities, 2));
        // Adicione mais cursos/lições aqui

        CourseAdapter adapter = new CourseAdapter(courseList, getContext());
        coursesRecyclerView.setAdapter(adapter);

        return view;
    }
}