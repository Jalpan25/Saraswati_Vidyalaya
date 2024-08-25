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

import com.google.firebase.auth.FirebaseAuth;

public class home_page extends AppCompatActivity implements View.OnClickListener {

    public CardView personal,attendance,fees,elibrary,logout,result;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
        personal=(CardView)findViewById(R.id.c1);
        attendance=(CardView)findViewById(R.id.c2);
        fees=(CardView)findViewById(R.id.c3);
        elibrary=(CardView)findViewById(R.id.c4);
        result=(CardView)findViewById(R.id.c5);
        logout=(CardView)findViewById(R.id.c6);


        personal.setOnClickListener(this);
        attendance.setOnClickListener(this);
        fees.setOnClickListener(this);
        elibrary.setOnClickListener(this);
        result.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;

        if (v.getId() == R.id.c1) {
            i = new Intent(home_page.this, personal_details.class);
            startActivity(i);
            finish();
        }
        else if (v.getId() == R.id.c6) {
            FirebaseAuth.getInstance().signOut();
            i = new Intent(home_page.this, login.class);
            startActivity(i);
            finish();
        }

    }
}