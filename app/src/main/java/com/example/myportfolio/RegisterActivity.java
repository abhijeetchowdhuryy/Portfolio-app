package com.example.myportfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout name, email, password, confirmPassword;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    MaterialCardView login;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        login = findViewById(R.id.login);
        auth = FirebaseAuth.getInstance();
        login.setOnClickListener(v -> {
            String nameText = name.getEditText().getText().toString();
            String emailText = email.getEditText().getText().toString();
            String passwordText = password.getEditText().getText().toString();
            String confirmPasswordText = confirmPassword.getEditText().getText().toString();

            if(TextUtils.isEmpty(nameText) || TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(confirmPasswordText))
            {
                name.setError("Please fill all the fields");
                email.setError("Please fill all the fields");
                password.setError("Please fill all the fields");
                confirmPassword.setError("Please fill all the fields");
            } else if(!passwordText.equals(confirmPasswordText))
            {
                confirmPassword.setError("Password does not match");
            } else if (passwordText.length() < 6)
            {
                password.setError("Password must be at least 6 characters");
            } else {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                login.setEnabled(false);
                registerUser(nameText, emailText, passwordText);
            }
        });
    }

    private void registerUser(String nameText, String emailText, String passwordText) {
        auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                DatabaseReference reference = FirebaseDatabase.getInstance("https://my-portfolio-5ef40-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(auth.getCurrentUser().getUid());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", auth.getCurrentUser().getUid());
                hashMap.put("name", nameText);
                hashMap.put("email", emailText);
                hashMap.put("password", passwordText);
                hashMap.put("imageURL", "default");
                hashMap.put("phone", "");
                hashMap.put("address", "");
                hashMap.put("institution", "");
                hashMap.put("job", "");
                hashMap.put("github", "");
                hashMap.put("linkdein", "");
                reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                });
            } else {
                progressDialog.dismiss();
                login.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}