package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class StartActivity extends AppCompatActivity {

    Button btnRegistration;
    Button btnAuthorization;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnAuthorization = findViewById(R.id.btnGoToAuth);
        btnRegistration = findViewById(R.id.btnGoToReg);

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(StartActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Intent intent = new Intent(StartActivity.this, AuthorizationActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(),
                        "Не удалось войти с помощью биометрии", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegistration.setOnClickListener(view -> {
            Intent intent = new Intent(StartActivity.this, RegistrationActivity.class);
            startActivity(intent);
            finish();
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Авторизация")
                .setSubtitle("Прислоните палец")
                .setNegativeButtonText("Войти с помощью логина и пароля")
                .build();


        btnAuthorization.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });


    }
}