package etec.com.tcc.palmslibras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.database.DatabaseHelper;
import etec.com.tcc.palmslibras.models.User;
import etec.com.tcc.palmslibras.utils.SessionManager;

public class AuthActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private boolean isLoginView = true; // Controla se a tela está em modo login ou cadastro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Inicialmente, a tela é de cadastro, mas o botão principal é "Criar Conta"
        updateUIForViewMode();

        registerButton.setOnClickListener(v -> {
            if (isLoginView) { // Se está em modo login, o botão secundário vira "Criar Conta"
                isLoginView = false;
                updateUIForViewMode();
            } else { // Se está em modo cadastro, executa o registro
                registerUser();
            }
        });

        loginButton.setOnClickListener(v -> {
            if(isLoginView){ // Se está em modo login, executa o login
                loginUser();
            } else { // Se está em modo cadastro, o botão principal vira o de login
                isLoginView = true;
                updateUIForViewMode();
            }
        });
    }

    private void updateUIForViewMode() {
        if (isLoginView) {
            nameEditText.setVisibility(View.GONE);
            loginButton.setText("Entrar");
            registerButton.setText("Criar uma conta");
        } else {
            nameEditText.setVisibility(View.VISIBLE);
            loginButton.setText("Já tenho uma conta");
            registerButton.setText("Criar Conta");
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

        long userId = dbHelper.addUser(name, email, password);
        if (userId != -1) {
            Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            sessionManager.createLoginSession(userId);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Erro ao cadastrar. O email pode já estar em uso.", Toast.LENGTH_SHORT).show();
        }
    }
}