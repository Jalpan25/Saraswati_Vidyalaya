//package com.example.saraswatividyalaya;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class login extends AppCompatActivity {
//
//    EditText emailEditText, passwordEditText;
//    Button loginButton;
//    FirebaseDatabase db;
//    DatabaseReference reference;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        emailEditText = findViewById(R.id.usernameEditText);
//        passwordEditText = findViewById(R.id.passwordEditText);
//        loginButton = findViewById(R.id.loginButton);
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String enteredEmail = emailEditText.getText().toString();
//                String enteredPassword = passwordEditText.getText().toString();
//
//                if (!enteredEmail.isEmpty() && !enteredPassword.isEmpty()) {
//                    db = FirebaseDatabase.getInstance();
//                    reference = db.getReference("Students");
//
//                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            boolean isValidUser = false;
//
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                String storedEmail = snapshot.child("email1").getValue(String.class);
//                                String storedPassword = snapshot.child("password1").getValue(String.class);
//
//                                if (enteredEmail.equals(storedEmail) && enteredPassword.equals(storedPassword)) {
//                                    isValidUser = true;
//                                    break;
//                                }
//                            }
//
//                            if (isValidUser) {
//                                Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
//                                // Navigate to the next activity after successful login
//                                Intent intent = new Intent(login.this, home_page.class);
//                                startActivity(intent);
//                                finish();
//                            } else {
//                                Toast.makeText(login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            Toast.makeText(login.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    Toast.makeText(login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//}



//successfully logged in without retrive
//package com.example.saraswatividyalaya;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//        public class login extends AppCompatActivity {
//
//            EditText emailEditText, passwordEditText;
//            Button loginButton, teacherPanelButton;
//
//            FirebaseDatabase db;
//            DatabaseReference reference;
//
//            @Override
//            protected void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                setContentView(R.layout.activity_login);
//
//                // Initialize the views
//                emailEditText = findViewById(R.id.usernameEditText);
//                passwordEditText = findViewById(R.id.passwordEditText);
//                loginButton = findViewById(R.id.loginButton);
//                teacherPanelButton = findViewById(R.id.teacherPanelButton);
//
//                // Initialize Firebase database reference
//                db = FirebaseDatabase.getInstance();
//                reference = db.getReference("Students");
//
//                // Set onClickListener for the login button
//                loginButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String email = emailEditText.getText().toString().trim();
//                        String password = passwordEditText.getText().toString().trim();
//
//                        if (TextUtils.isEmpty(email)) {
//                            emailEditText.setError("Enter your email");
//                            return;
//                        }
//
//                        if (TextUtils.isEmpty(password)) {
//                            passwordEditText.setError("Enter your password");
//                            return;
//                        }
//
//                        // Check if email and password match
//                        checkLoginCredentials(email, password);
//                    }
//                });
//
//                // Set onClickListener for the teacher panel button
//                teacherPanelButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Redirect to teacher login
//                        Intent intent = new Intent(login.this, teacher_login.class);
//                        startActivity(intent);
//                    }
//                });
//            }
//
//            private void checkLoginCredentials(String email, String password) {
//                reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        boolean isValid = false;
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            String dbEmail = snapshot.child("email1").getValue(String.class);
//                            String dbPassword = snapshot.child("password1").getValue(String.class);
//
//                            if (email.equals(dbEmail) && password.equals(dbPassword)) {
//                                isValid = true;
//                                break;
//                            }
//                        }
//
//                        if (isValid) {
//                            Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                            // Redirect to the home page or dashboard
//                             Intent intent = new Intent(login.this, home_page.class);
//                             startActivity(intent);
//                        } else {
//                            Toast.makeText(login.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(login.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }




// retrieve data successfully without teacher panel
//package com.example.saraswatividyalaya;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//public class login extends AppCompatActivity {
//
//    private EditText emailEditText, passwordEditText;
//    private Button loginButton;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
//        emailEditText = findViewById(R.id.usernameEditText);
//        passwordEditText = findViewById(R.id.passwordEditText);
//        loginButton = findViewById(R.id.loginButton);
//
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = emailEditText.getText().toString().trim();
//                String password = passwordEditText.getText().toString().trim();
//
//                if (TextUtils.isEmpty(email)) {
//                    emailEditText.setError("Email is required.");
//                    return;
//                }
//
//                if (TextUtils.isEmpty(password)) {
//                    passwordEditText.setError("Password is required.");
//                    return;
//                }
//
//                loginUser(email, password);
//            }
//        });
//    }
//
//
//    private void loginUser(String email, String password) {
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
//
//                            // Redirect to the home page
//                            Intent intent = new Intent(login.this, home_page.class);
//                            intent.putExtra("userEmail", user.getEmail());
//                            startActivity(intent);
//                            finish(); // Prevent back navigation to login
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(login.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//}


//successfully data retrive with teacher panel
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

    // Method to log in the user
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Redirect to home page
                            Intent intent = new Intent(login.this, home_page.class);
                            intent.putExtra("userEmail", user.getEmail());
                            startActivity(intent);
                            finish(); // Close login activity
                        } else {
                            // If sign-in fails, display a message to the user
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(login.this, "Authentication Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                            Log.e("LoginError", "Login Failed: " + errorMessage);
                        }
                    }
                });
    }
}
