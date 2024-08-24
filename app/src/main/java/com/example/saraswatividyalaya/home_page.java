package com.example.saraswatividyalaya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class home_page extends AppCompatActivity implements View.OnClickListener {

    public CardView personal,attendance,fees,elibrary;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
        personal=(CardView)findViewById(R.id.c1);
        attendance=(CardView)findViewById(R.id.c2);
        fees=(CardView)findViewById(R.id.c3);
        elibrary=(CardView)findViewById(R.id.c4);

        personal.setOnClickListener(this);
        attendance.setOnClickListener(this);
        fees.setOnClickListener(this);
        elibrary.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;

        if (v.getId() == R.id.c1) {
            i = new Intent(this, personal_details.class);
            startActivity(i);
        }
        if (v.getId() == R.id.c2) {
            i = new Intent(this, add_student.class);
            startActivity(i);
        }

    }
}