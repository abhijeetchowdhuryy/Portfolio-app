package com.example.myportfolio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PortfolioActivity extends AppCompatActivity {
    MaterialCardView logout, editProfile;
    FirebaseAuth auth;
    TextView name, email, phone, address, institution, job, linkedin, github;
    CircleImageView profile_image;
    String profileID;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        logout = findViewById(R.id.logout);
        editProfile = findViewById(R.id.editProfile);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        institution = findViewById(R.id.institution);
        job = findViewById(R.id.job);
        linkedin = findViewById(R.id.linkedin);
        github = findViewById(R.id.github);
        profile_image = findViewById(R.id.profile_image);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences = PortfolioActivity.this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileID = preferences.getString("profileID", firebaseUser.getUid());
        auth = FirebaseAuth.getInstance();
        logout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(PortfolioActivity.this, LoginActivity.class));
            Objects.requireNonNull(PortfolioActivity.this).finish();
        });

        editProfile.setOnClickListener(v -> {
            startActivity(new Intent(PortfolioActivity.this, EditActivity.class));
            finish();
        });
        getUserData(profileID);
    }

    private void getUserData(String profileID) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://my-portfolio-5ef40-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(profileID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    name.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                    phone.setText(Objects.requireNonNull(snapshot.child("phone").getValue()).toString());
                    address.setText(Objects.requireNonNull(snapshot.child("address").getValue()).toString());
                    institution.setText(Objects.requireNonNull(snapshot.child("institution").getValue()).toString());
                    job.setText(Objects.requireNonNull(snapshot.child("job").getValue()).toString());
                    linkedin.setText(Objects.requireNonNull(snapshot.child("linkedin").getValue()).toString());
                    github.setText(Objects.requireNonNull(snapshot.child("github").getValue()).toString());
//                    name.setText(snapshot.child("name").getValue().toString());
//                    email.setText(snapshot.child("email").getValue().toString());
//                    phone.setText(snapshot.child("phone").getValue().toString());
//                    address.setText(snapshot.child("address").getValue().toString());
//                    institution.setText(snapshot.child("institution").getValue().toString());
//                    job.setText(snapshot.child("job").getValue().toString());
//                    linkedin.setText(snapshot.child("linkedin").getValue().toString());
//                    github.setText(snapshot.child("github").getValue().toString());
                    String imageUrl = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();
                    if (imageUrl.equals("default")) {
                        profile_image.setImageResource(R.drawable.default_profile4);
                    } else {
                        Glide.with(PortfolioActivity.this).load(imageUrl).into(profile_image);
                    }

                } else {
                    Toast.makeText(PortfolioActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}