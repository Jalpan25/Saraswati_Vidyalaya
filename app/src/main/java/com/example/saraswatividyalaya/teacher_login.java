package com.example.saraswatividyalaya;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class teacher_login extends AppCompatActivity {
    EditText email, password;
    Button buttonReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        email = findViewById(R.id.usernameEditText);
        password = findViewById(R.id.passwordEditText);
        buttonReg = findViewById(R.id.loginButton);


        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(teacher_login.this,teacher_home.class);
                Log.e("Just Checking" , "Reached");
                startActivity(i);
                Log.e("Just Checking" , "Reached2");
                finish();
            }
        });
//        buttonReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String inputEmail = email.getText().toString();
//                String inputPassword = password.getText().toString();
//                if (TextUtils.isEmpty(inputEmail)) {
//                    Toast.makeText(teacher_login.this, "Enter email", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(inputPassword)) {
//                    Toast.makeText(teacher_login.this, "Enter password", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (inputEmail.equals("teacher@saraswati.ac.in") && inputPassword.equals("Password")) {
//                    Log.d("teacher_login", "Credentials matched, navigating to teacher_home");
//                    Intent intent = new Intent(teacher_login.this, teacher_home.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Toast.makeText(teacher_login.this, "Authentication failed. Incorrect email or password.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }
}
