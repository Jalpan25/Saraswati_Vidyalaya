
package com.example.saraswatividyalaya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class add_student extends AppCompatActivity {

    EditText name, email, password, dob, rollno, medium, standard;
    RadioGroup genderGroup;
    RadioButton selectedGender;
    Button buttonOK;
    String name1, email1, password1, dob1, rollno1, medium1, standard1, gender1;
    FirebaseDatabase db;
    DatabaseReference reference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // Initialize the views
        name = findViewById(R.id.usernameEditText);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        dob = findViewById(R.id.editTextDate2);
        rollno = findViewById(R.id.rollNumberEditText);
        medium = findViewById(R.id.mediumEditText);
        standard = findViewById(R.id.standardEditText);
        genderGroup = findViewById(R.id.genderRadioGroup);
        buttonOK = findViewById(R.id.loginButton);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name1 = name.getText().toString();
                email1 = email.getText().toString();
                password1 = password.getText().toString();
                dob1 = dob.getText().toString();
                rollno1 = rollno.getText().toString();
                medium1 = medium.getText().toString();
                standard1 = standard.getText().toString();

                // Get selected gender
                int selectedId = genderGroup.getCheckedRadioButtonId();
                selectedGender = findViewById(selectedId);
                gender1 = selectedGender.getText().toString();

                if (!name1.isEmpty() && !email1.isEmpty() && !password1.isEmpty() && !dob1.isEmpty()
                        && !rollno1.isEmpty() && !medium1.isEmpty() && !standard1.isEmpty() && selectedId != -1) {

                    // Create the user in Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email1, password1)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // User created successfully in Firebase Authentication

                                        // Now store the additional user information in Firebase Realtime Database
                                        Users users = new Users(name1, email1, password1, dob1, rollno1, medium1, standard1, gender1);
                                        db = FirebaseDatabase.getInstance();
                                        reference = db.getReference("Students");
                                        reference.child(rollno1).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                // Clear the input fields
                                                name.setText("");
                                                email.setText("");
                                                password.setText("");
                                                dob.setText("");
                                                rollno.setText("");
                                                medium.setText("");
                                                standard.setText("");
                                                genderGroup.clearCheck();

                                                Toast.makeText(add_student.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        // If user creation in Firebase Authentication fails
                                        Toast.makeText(add_student.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(add_student.this, "Please fill all fields and select gender", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class Users {

        String name1, email1, password1, dob1, rollno1, medium1, standard1, gender1;

        public Users() {
        }

        public Users(String name1, String email1, String password1, String dob1, String rollno1, String medium1, String standard1, String gender1) {
            this.name1 = name1;
            this.email1 = email1;
            this.password1 = password1;
            this.dob1 = dob1;
            this.rollno1 = rollno1;
            this.medium1 = medium1;
            this.standard1 = standard1;
            this.gender1 = gender1;
        }

        // Getters and Setters
        public String getName1() { return name1; }
        public void setName1(String name1) { this.name1 = name1; }
        public String getEmail1() { return email1; }
        public void setEmail1(String email1) { this.email1 = email1; }
        public String getPassword1() { return password1; }
        public void setPassword1(String password1) { this.password1 = password1; }
        public String getDob1() { return dob1; }
        public void setDob1(String dob1) { this.dob1 = dob1; }
        public String getRollno1() { return rollno1; }
        public void setRollno1(String rollno1) { this.rollno1 = rollno1; }
        public String getMedium1() { return medium1; }
        public void setMedium1(String medium1) { this.medium1 = medium1; }
        public String getStandard1() { return standard1; }
        public void setStandard1(String standard1) { this.standard1 = standard1; }
        public String getGender1() { return gender1; }
        public void setGender1(String gender1) { this.gender1 = gender1; }
    }
}

// Tried via Authentication

//package com.example.saraswatividyalaya;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.Firebase;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class add_student extends AppCompatActivity {
//    EditText email,password;
//    Button buttonReg;
//    FirebaseAuth mAuth;
//
//
////    @Override
////    public void onStart() {
////        super.onStart();
////        // Check if user is signed in (non-null) and update UI accordingly.
////        FirebaseUser currentUser = mAuth.getCurrentUser();
////        if(currentUser != null){
////            Intent intent=new Intent(getApplicationContext(), teacher_home.class);
////            startActivity(intent);
////            finish();
////
////        }
////    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_student);
//        email=findViewById(R.id.emailEditText);
//        password=findViewById(R.id.passwordEditText);
//        mAuth= FirebaseAuth.getInstance();
//        buttonReg=findViewById(R.id.loginButton);
//
//        buttonReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email1,password1;
//                email1= String.valueOf(email.getText());
//                password1= String.valueOf(password.getText());
//
//
//                if(TextUtils.isEmpty(email1))
//                {
//                    Toast.makeText(add_student.this,"Enter email",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if(TextUtils.isEmpty(password1))
//                {
//                    Toast.makeText(add_student.this,"Enter password",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(password1.length()<6)
//                {
//                    Toast.makeText(add_student.this," Password should be at least 6 characters long",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                mAuth.createUserWithEmailAndPassword(email1, password1)
//                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//
//                                    Toast.makeText(add_student.this, "Account Created.",
//                                            Toast.LENGTH_SHORT).show();
//                                    Intent intent=new Intent(getApplicationContext(), teacher_home.class);
//                                    startActivity(intent);
//                                    finish();
//
//
//                                } else {
//                                    Toast.makeText(add_student.this, "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
//
//                                }
//                            }
//
//
//                        });
//            }
//        });
//    }
//}
