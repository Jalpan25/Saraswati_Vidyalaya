package com.example.saraswatividyalaya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class teacher_home extends AppCompatActivity implements View.OnClickListener {
    public CardView personal, attendance, upload_result, elibrary,add_student,logout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        personal = findViewById(R.id.c1);
        attendance = findViewById(R.id.c2);
        upload_result = findViewById(R.id.c3);
        elibrary = findViewById(R.id.c4);
        add_student=findViewById(R.id.c5);
        logout=findViewById(R.id.c6);

        personal.setOnClickListener(this);
        attendance.setOnClickListener(this);
        upload_result.setOnClickListener(this);
        elibrary.setOnClickListener(this);
        add_student.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v1) {
        Intent i;

        if (v1.getId() == R.id.c5) {
            i = new Intent(teacher_home.this, add_student.class);
            startActivity(i);
            finish();
        }

        else if (v1.getId() == R.id.c4) {
            i = new Intent(teacher_home.this, TeacherLibraryActivity.class);
            startActivity(i);
            finish();
        }
        if (v1.getId() == R.id.c6) {
            FirebaseAuth.getInstance().signOut();
            i = new Intent(teacher_home.this, login.class);
            startActivity(i);
            finish();
        }

        // Handle other clicks here
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks here
        int id = item.getItemId();

        if (id == R.id.check_for_Updates) {
            Toast.makeText(this, "Check for Update", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.Logout) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
