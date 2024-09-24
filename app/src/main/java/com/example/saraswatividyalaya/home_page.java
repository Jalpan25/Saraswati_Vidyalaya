package com.example.saraswatividyalaya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


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

        // Retrieve rollNo from intent or SharedPreferences
        String rollNo = getIntent().getStringExtra("rollNo");
        if (rollNo == null || rollNo.isEmpty()) {
            rollNo = getSharedPreferences("userDetails", MODE_PRIVATE).getString("rollNo", null);
        }

        if (rollNo == null) {
            Toast.makeText(this, "Roll number not provided", Toast.LENGTH_SHORT).show();
            return;
        }

        if (v.getId() == R.id.c1) {
            i = new Intent(home_page.this, personal_details.class);
            startActivity(i);
            finish();
        } else if (v.getId() == R.id.c2) {
            i = new Intent(home_page.this, student_attendance.class);
            i.putExtra("rollNo", rollNo);
            startActivity(i);
            finish();
        } else if (v.getId() == R.id.c6) {
            FirebaseAuth.getInstance().signOut();
            i = new Intent(home_page.this, login.class);
            startActivity(i);
            finish();
        } else if (v.getId() == R.id.c4) {
            i = new Intent(home_page.this, StudentLibraryActivity.class);
            startActivity(i);
            finish();
        }
    }

}