package com.example.myportfolio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

     FirebaseAuth mAuth;
    MaterialCardView register;
    TextView changeProfilePhoto;
    CircleImageView profile_image;
    Uri imageUri;
    StorageTask uploadTask;
    FirebaseUser firebaseUser;
    TextInputLayout name, phone, address, institution, job, linkdein, github;
    StorageReference storageReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        register = findViewById(R.id.register);
        changeProfilePhoto = findViewById(R.id.changeProfilePhoto);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        institution = findViewById(R.id.institution);
        job = findViewById(R.id.job);
        linkdein = findViewById(R.id.linkdein);
//        getUserInfo();
        github = findViewById(R.id.github);
        profile_image = findViewById(R.id.profile_image);
        storageReference = FirebaseStorage.getInstance().getReference("/profile/photos");
        register.setOnClickListener(v -> {
            String nameText = name.getEditText().getText().toString();
            String phoneText = phone.getEditText().getText().toString();
            String addressText = address.getEditText().getText().toString();
            String institutionText = institution.getEditText().getText().toString();
            String jobText = job.getEditText().getText().toString();
            String linkdeinText = linkdein.getEditText().getText().toString();
            String githubText = github.getEditText().getText().toString();

                DatabaseReference reference = FirebaseDatabase.getInstance("https://my-portfolio-5ef40-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(firebaseUser.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", nameText);
                hashMap.put("phone", phoneText);
                hashMap.put("address", addressText);
                hashMap.put("institution", institutionText);
                hashMap.put("job", jobText);
                hashMap.put("linkedin", linkdeinText);
                hashMap.put("github", githubText);
                hashMap.put("id", firebaseUser.getUid());
                hashMap.put("imageUrl", "default");
                reference.updateChildren(hashMap);
                Toast.makeText(MainActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, PortfolioActivity.class));
                finish();
        });

        changeProfilePhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);
        });
    }

//    private void getUserInfo() {
//        DatabaseReference reference = FirebaseDatabase.getInstance("https://my-portfolio-5ef40-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(firebaseUser.getUid());
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Users user = snapshot.getValue(Users.class);
//              //  name.getEditText().setText(user.getName());
//                assert user != null;
//                name.getEditText().setText(user.getName());
//                phone.getEditText().setText(user.getPhone());
//                address.getEditText().setText(user.getAddress());
//                institution.getEditText().setText(user.getInstitution());
//                job.getEditText().setText(user.getJob());
//                linkdein.getEditText().setText(user.getLinkedin());
//                github.getEditText().setText(user.getGithub());
//                if(user.getImageUrl().equals("default"))
//                {
//                    profile_image.setImageResource(R.drawable.default_profile4);
//                } else {
//                    Glide.with(MainActivity.this).load(user.getImageUrl()).into(profile_image);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data != null && data.getData()!=null)
        {
            imageUri = data.getData();
            profile_image.setImageURI(imageUri);
            uploadProfilePicture();
        }
    }

    private void uploadProfilePicture() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(firebaseUser.getUid() + "." + "jpg");
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>)  task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String myUri = downloadUri.toString();
                    saveData(myUri, progressDialog);
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        } else {
            Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void saveData(String myUri, ProgressDialog progressDialog) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://my-portfolio-5ef40-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                assert user != null;
                user.setImageUrl(myUri);
                reference.setValue(user);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
       });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
            preferences.edit().putString("profileID", currentUser.getUid()).apply();
        }
    }
}