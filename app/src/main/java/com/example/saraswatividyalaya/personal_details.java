package com.example.saraswatividyalaya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class personal_details extends AppCompatActivity {

    private TextView profileName, email1, dob1, gender1, GRNO1, medium1, std_div1;
    private ImageView profileImage;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Students");
        storageReference = FirebaseStorage.getInstance().getReference();

        // Link XML views with Java objects
        profileName = findViewById(R.id.profileName);
        email1 = findViewById(R.id.email1);
        GRNO1 = findViewById(R.id.GRNO1);
        medium1 = findViewById(R.id.medium1);
        std_div1 = findViewById(R.id.std_div1);
        dob1 = findViewById(R.id.dob1);
        gender1 = findViewById(R.id.Gender1);
        profileImage = findViewById(R.id.profileImg);  // Your ImageView

        // Fetch and display student details
        fetchStudentDetails();
    }

    @Override
    public void onBackPressed() {
        // Navigate to the home page when the back button is pressed
        Intent intent = new Intent(personal_details.this, home_page.class);
        startActivity(intent);
        finish(); // Finish this activity so it doesn't remain in the back stack
    }

    private void fetchStudentDetails() {
        // Get the current logged-in user
        String userEmail = mAuth.getCurrentUser().getEmail();

        if (userEmail == null) {
            // Redirect to login if user is not logged in
            Toast.makeText(personal_details.this, "Please log in first", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(personal_details.this, login.class);
            startActivity(intent);
            finish();
            return;
        }

        // Query the database to find the user by email
        databaseReference.orderByChild("email1").equalTo(userEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                // Assuming student data has the same structure as added in the add_student file
                                String name = snapshot.child("name1").getValue(String.class);
                                String email = snapshot.child("email1").getValue(String.class);
                                String grno = snapshot.child("rollno1").getValue(String.class);
                                String medium = snapshot.child("medium1").getValue(String.class);
                                String standard = snapshot.child("standard1").getValue(String.class);
                                String dob = snapshot.child("dob1").getValue(String.class);
                                String gender = snapshot.child("gender1").getValue(String.class);

                                // Set the data to the respective TextViews
                                profileName.setText(name);
                                email1.setText(email);
                                GRNO1.setText(grno);
                                medium1.setText(medium);
                                std_div1.setText(standard);
                                dob1.setText(dob);
                                gender1.setText(gender);

                                // Fetch and display the profile image from Firebase Storage
                                loadProfileImage(grno);  // Assume the image is stored with the student's GRNO as the filename
                            }
                        } else {
                            Toast.makeText(personal_details.this, "No data found for the user", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(personal_details.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to load profile image from Firebase Storage
    private void loadProfileImage(String grno) {
        // Reference to the student's profile image in Firebase Storage
        StorageReference profileImageRef = storageReference.child("student_images/" + grno + ".jpg");

        // Use Glide to load the image into the ImageView
        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(personal_details.this)
                    .load(uri)
                    .placeholder(R.drawable.placeholder)  // Set a placeholder while the image loads
                    .into(profileImage);
        }).addOnFailureListener(e -> {
            Toast.makeText(personal_details.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
        });
    }
}
