package etec.com.tcc.palmslibras.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.activities.AuthActivity;
import etec.com.tcc.palmslibras.database.DatabaseHelper;
import etec.com.tcc.palmslibras.models.User;
import etec.com.tcc.palmslibras.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private TextView profileName, profileEmail, profileXP, profileLevel, profileStreak;
    private Button logoutButton;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(getContext());
        dbHelper = new DatabaseHelper(getContext());

        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileXP = view.findViewById(R.id.profileXP);
        profileLevel = view.findViewById(R.id.profileLevel);
        profileStreak = view.findViewById(R.id.profileStreak);
        logoutButton = view.findViewById(R.id.logoutButton);

        loadUserProfile();

        logoutButton.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(getContext(), AuthActivity.class);
            // Flags para limpar as telas anteriores e evitar que o usuário volte com o botão "back"
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

    private void loadUserProfile() {
        long userId = sessionManager.getUserId();
        if (userId != -1) {
            User user = dbHelper.getUserById(userId);
            if (user != null) {
                profileName.setText(user.getName());
                profileEmail.setText(user.getEmail());
                profileXP.setText(String.format(Locale.getDefault(), "XP: %d", user.getXp()));
                profileLevel.setText(String.format(Locale.getDefault(), "Nível: %d", user.getLevel()));
                profileStreak.setText(String.format(Locale.getDefault(), "Streak: %d", user.getStreak()));
            }
        }
    }
}