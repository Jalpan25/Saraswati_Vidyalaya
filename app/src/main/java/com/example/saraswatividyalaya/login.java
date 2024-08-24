package com.example.saraswatividyalaya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    EditText email,password;
    Button buttonlogin;
    FirebaseAuth mAuth;
    Button teacher_panel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.usernameEditText);
        password=findViewById(R.id.passwordEditText);
        mAuth= FirebaseAuth.getInstance();
        buttonlogin=findViewById(R.id.loginButton);
        teacher_panel=findViewById(R.id.teacherPanelButton);

        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email1,password1;
                email1= String.valueOf(email.getText());
                password1= String.valueOf(password.getText());
                Intent intent=new Intent(getApplicationContext(), home_page.class);
                startActivity(intent);
                finish();
        teacher_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(login.this,teacher_login.class);
                startActivity(it);
            }
        });
    }
}
