package etec.com.tcc.palmslibras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.database.DatabaseHelper;
import etec.com.tcc.palmslibras.models.User;
import etec.com.tcc.palmslibras.utils.SessionManager;

public class AuthActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button mainActionButton, secondaryAuthButton;
    private TextView subtitleText;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private boolean isLoginView = true; // Começa na tela de login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // IDs atualizados para maior clareza
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        mainActionButton = findViewById(R.id.mainActionButton);
        secondaryAuthButton = findViewById(R.id.secondaryAuthButton);
        subtitleText = findViewById(R.id.subtitleText);

        // Configura a UI inicial para o modo de login
        updateUIForViewMode();

        // Listener do botão principal (Roxo)
        mainActionButton.setOnClickListener(v -> {
            if (isLoginView) {
                loginUser();
            } else {
                registerUser();
            }
        });

        secondaryAuthButton.setOnClickListener(v -> {
            // A única função deste botão é alternar a visualização
            isLoginView = !isLoginView; // Inverte o modo (true vira false, false vira true)
            updateUIForViewMode();
        });
    }

    private void updateUIForViewMode() {
        if (isLoginView) {
            // --- MODO LOGIN ---
            subtitleText.setText(getString(R.string.login_subtitle));
            nameEditText.setVisibility(View.GONE);
            mainActionButton.setText(getString(R.string.button_login));
            secondaryAuthButton.setText(getString(R.string.button_create_new_account));
        } else {
            // --- MODO CADASTRO ---
            subtitleText.setText(getString(R.string.signup_subtitle));
            nameEditText.setVisibility(View.VISIBLE);
            mainActionButton.setText(getString(R.string.button_create_account));
            secondaryAuthButton.setText(getString(R.string.button_already_have_account));
        }
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, preencha email e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.getUser(email, password);

        if (user != null) {
            sessionManager.createLoginSession(user.getId());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.checkUserExists(email)) {
            Toast.makeText(this, "Este e-mail já está em uso. Tente outro.", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = dbHelper.addUser(name, email, password);
        if (userId != -1) {
            Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            sessionManager.createLoginSession(userId);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Erro inesperado ao cadastrar.", Toast.LENGTH_SHORT).show();
        }
    }
}