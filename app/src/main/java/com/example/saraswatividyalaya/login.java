package com.example.saraswatividyalaya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, teacherPanelButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, redirect to the home page
            Intent intent = new Intent(login.this, home_page.class);
            intent.putExtra("userEmail", currentUser.getEmail());
            startActivity(intent);
            finish(); // Close the login activity
        }

        // Initialize views
        emailEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        teacherPanelButton = findViewById(R.id.teacherPanelButton);

        // Login button onClickListener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email is required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required.");
                    return;
                }

                // Proceed with login
                loginUser(email, password);
            }
        });

        // Teacher Panel button onClickListener
        teacherPanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to teacher login
                Intent intent = new Intent(login.this, teacher_login.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        Log.d("LoginDebug", "Attempting login with email: " + email);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("LoginDebug", "Login successful for email: " + email);

                            String userEmail = user.getEmail();

                            // Fetch Roll Number based on the email
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Students");
                            dbRef.orderByChild("email1").equalTo(userEmail)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Log.d("LoginDebug", "Snapshot exists: " + snapshot.exists());
                                            if (snapshot.exists()) {
                                                for (DataSnapshot data : snapshot.getChildren()) {
                                                    Log.d("LoginDebug", "Data found: " + data.getValue());
                                                    String rollNo = data.child("rollno1").getValue(String.class);
                                                    Log.d("LoginDebug", "Roll number found: " + rollNo);

                                                    // Save roll number in SharedPreferences
                                                    getSharedPreferences("userDetails", MODE_PRIVATE)
                                                            .edit()
                                                            .putString("rollNo", rollNo)
                                                            .apply();

                                                    // Redirect to home_page and pass roll number
                                                    Intent intent = new Intent(login.this, home_page.class);
                                                    intent.putExtra("rollNo", rollNo);
                                                    Log.d("LoginDebug", "Roll number to pass: " + rollNo);

                                                    startActivity(intent);
                                                    finish(); // Close login activity
                                                }
                                            } else {
                                                Log.e("LoginDebug", "No student data found for email: " + userEmail);
                                                Toast.makeText(login.this, "No student record found", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("LoginDebug", "Database error: " + error.getMessage());
                                        }
                                    });
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Log.e("LoginDebug", "Authentication failed: " + errorMessage);
                            Toast.makeText(login.this, "Authentication Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
