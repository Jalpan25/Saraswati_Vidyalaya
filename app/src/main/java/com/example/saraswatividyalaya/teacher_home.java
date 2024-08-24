package com.example.saraswatividyalaya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class teacher_home extends AppCompatActivity implements View.OnClickListener {
    public CardView personal, attendance, fees, elibrary;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        personal = findViewById(R.id.c5);
        attendance = findViewById(R.id.c6);
        fees = findViewById(R.id.c7);
        elibrary = findViewById(R.id.c8);

        personal.setOnClickListener(this);
        attendance.setOnClickListener(this);
        fees.setOnClickListener(this);
        elibrary.setOnClickListener(this);
    }

    @Override
    public void onClick(View v1) {
        Intent i;

        if (v1.getId() == R.id.c5) {
            i = new Intent(this, add_student.class);
            startActivity(i);
        }

        // Handle other clicks here
    }
}
