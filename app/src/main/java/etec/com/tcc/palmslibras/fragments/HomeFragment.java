package etec.com.tcc.palmslibras.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.database.DatabaseHelper;
import etec.com.tcc.palmslibras.models.User;
import etec.com.tcc.palmslibras.utils.SessionManager;

public class HomeFragment extends Fragment {

    private TextView greetingTextView;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        greetingTextView = view.findViewById(R.id.greetingTextView);
        sessionManager = new SessionManager(getContext());
        dbHelper = new DatabaseHelper(getContext());

        loadUserData();

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
    }
}