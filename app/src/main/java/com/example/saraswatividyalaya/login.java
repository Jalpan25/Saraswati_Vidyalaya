package com.example.saraswatividyalaya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        Button login; jipj
        Button teacher_panel;
        login=(Button)findViewById(R.id.loginButton);
        teacher_panel=(Button)findViewById(R.id.teacherPanelButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(login.this, home_page.class);
                startActivity(it);
            }
        });
        teacher_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(login.this,teacher_login.class);
                startActivity(it);
            }
        });
    }
}