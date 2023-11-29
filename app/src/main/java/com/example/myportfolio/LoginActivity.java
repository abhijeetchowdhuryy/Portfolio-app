package com.example.myportfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout email, password;
    MaterialCardView loggin;
    FirebaseAuth auth;
    TextView forgotPassword;
    ProgressDialog progressDialog;
    TextView signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        forgotPassword = findViewById(R.id.forgotPassword);
        loggin = findViewById(R.id.loggin);
        loggin.setOnClickListener(v -> {
            String emailText = email.getEditText().getText().toString();
            String passwordText = password.getEditText().getText().toString();
            if(TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText))
            {
                Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setMessage("Logging in...");
                progressDialog.show();
                loginUser(emailText, passwordText);
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            }
        });
        signup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
        forgotPassword.setOnClickListener(v -> {
            String emailText = email.getEditText().getText().toString();
            if(TextUtils.isEmpty(emailText))
            {
                Toast.makeText(LoginActivity.this, "Enter Email to reset password", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setMessage("Sending Password Reset Link...");
                progressDialog.show();
                auth.sendPasswordResetEmail(emailText).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Check your Email", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loginUser(String emailText, String passwordText) {
        auth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if(task.isSuccessful())
            {
                Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, PortfolioActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}