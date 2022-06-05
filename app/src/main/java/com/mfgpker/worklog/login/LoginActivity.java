package com.mfgpker.worklog.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mfgpker.worklog.MainActivity;
import com.mfgpker.worklog.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        Button register = binding.mainRegister;
        Button login = binding.mainLogin;
        SignInButton signInGoogle = binding.signInGoogleButton;

        if(auth.getCurrentUser() != null) {
            startMainActivity();
        }

        register.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        login.setOnClickListener(view -> {
            String email = binding.loginEmailAddress.getText().toString();
            String password = binding.loginPassword.getText().toString();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Missing email or password", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(LoginActivity.this, "Password is to short", Toast.LENGTH_SHORT).show();
            } else {
                loginUserWithEmail(email, password);
            }
        });

        signInGoogle.setOnClickListener(view -> {

        });
    }

    private void loginUserWithEmail(String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(LoginActivity.this, "Login user successfull!: " + Objects.requireNonNull(auth.getCurrentUser()).getUid(), Toast.LENGTH_SHORT).show();

            startMainActivity();
        }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Login user failed!! :(", Toast.LENGTH_SHORT).show());
/*
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login user successfull!: " + auth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();

                    startMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Login user failed!! :(", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */

    }

    private  void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}