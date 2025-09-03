package etec.com.tcc.palmslibras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);

            Intent intent;
            if (sessionManager.isLoggedIn()) {
                // Se o usuário já está logado, vai para a tela principal
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Se não, vai para a tela de autenticação
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            finish();
        }, 3000); // 3 segundos de espera
    }
}