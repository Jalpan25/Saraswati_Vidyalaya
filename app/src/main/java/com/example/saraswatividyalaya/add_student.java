package com.example.saraswatividyalaya;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class add_student extends AppCompatActivity {
    EditText email,password;
    Button buttonReg;
    FirebaseAuth mAuth;


//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent=new Intent(getApplicationContext(), teacher_home.class);
//            startActivity(intent);
//            finish();
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        email=findViewById(R.id.emailEditText);
        password=findViewById(R.id.passwordEditText);
        mAuth= FirebaseAuth.getInstance();
        buttonReg=findViewById(R.id.loginButton);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email1,password1;
                email1= String.valueOf(email.getText());
                password1= String.valueOf(password.getText());


                if(TextUtils.isEmpty(email1))
                {
                    Toast.makeText(add_student.this,"Enter email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password1))
                {
                    Toast.makeText(add_student.this,"Enter password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password1.length()<6)
                {
                    Toast.makeText(add_student.this," Password should be at least 6 characters long",Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email1, password1)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(add_student.this, "Account Created.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(getApplicationContext(), teacher_home.class);
                                    startActivity(intent);
                                    finish();


                                } else {
                                    Toast.makeText(add_student.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }


                        });
            }
        });
    }
}