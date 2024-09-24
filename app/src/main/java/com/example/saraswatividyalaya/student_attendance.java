package com.example.saraswatividyalaya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class student_attendance extends AppCompatActivity {

    private CalendarView calendarView;
    private DatabaseReference attendanceRef;
    private String studentRollNo;
    private Button loadAttendanceButton;
    private static final String TAG = "StudentAttendance";
    private ImageView attendanceMarker;
    @Override
    public void onBackPressed() {
        // Navigate to the home page when the back button is pressed
        Intent intent = new Intent(student_attendance.this, home_page.class);
        startActivity(intent);
        finish(); // Finish this activity so it doesn't remain in the back stack
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        calendarView = findViewById(R.id.calendarView);
        loadAttendanceButton = findViewById(R.id.loadAttendanceButton);
        attendanceMarker = findViewById(R.id.attendanceMarker);

        // Retrieve student roll number from the intent
        studentRollNo = getIntent().getStringExtra("rollNo");

        if (studentRollNo == null || studentRollNo.isEmpty()) {
            // Log.e(TAG, "Roll number is null or empty");
            Toast.makeText(student_attendance.this, "No roll number provided", Toast.LENGTH_SHORT).show();
            finish(); // Exit the activity if no roll number is provided
            return;
        }


        // Set date change listener for the CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Convert the selected date to the format "dd-MM-yyyy"
            String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%d", dayOfMonth, month + 1, year);
            loadAttendanceForSpecificDate(selectedDate);  // Load attendance for the selected date
        });

        // Optionally, you can still use a button to load attendance for the whole month if needed
        loadAttendanceButton.setOnClickListener(v -> loadAttendanceDataForMonth("09-2024"));
    }

    // Method to load attendance data for a specific date
    private void loadAttendanceForSpecificDate(String selectedDate) {
        Log.d(TAG, "Loading attendance for date: " + selectedDate);

        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance")
                .child("09-2024").child(studentRollNo).child(selectedDate);

        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);

                    if (status != null) {
                        Log.d(TAG, "Fetched Date: " + selectedDate + ", Status: " + status);

                        // Show attendance marker based on the status
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        try {
                            Date date = sdf.parse(selectedDate);
                            if (date != null) {
                                calendar.setTime(date);
                                long timeInMillis = calendar.getTimeInMillis();
                                if ("absent".equalsIgnoreCase(status)) {
                                    // Mark as Absent (red)
                                    showMarker(timeInMillis, R.drawable.red_circle);
                                } else {
                                    // Mark as Present (green)
                                    showMarker(timeInMillis, R.drawable.green_circle);
                                }
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "Error parsing date: " + selectedDate, e);
                        }
                    } else {
                        Log.d(TAG, "No attendance data found for the selected date: " + selectedDate);
                        Toast.makeText(student_attendance.this, "No attendance data available for the selected date", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "No snapshot data available for the selected date: " + selectedDate);
                    Toast.makeText(student_attendance.this, "No attendance data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading attendance data: ", error.toException());
            }
        });
    }

    // Method to load attendance data for a month
    private void loadAttendanceDataForMonth(String monthYear) {
        Log.d(TAG, "Loading attendance for: " + monthYear);

        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance")
                .child(monthYear).child(studentRollNo);

        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        String status = dateSnapshot.getValue(String.class);

                        if (date != null && status != null) {
                            Log.d(TAG, "Fetched Date: " + date + ", Status: " + status);
                            // Optionally, mark each date in the month based on its attendance
                        }
                    }
                } else {
                    Log.d(TAG, "No snapshot data available for roll number: " + studentRollNo);
                    Toast.makeText(student_attendance.this, "No attendance data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading attendance data: ", error.toException());
            }
        });
    }

    private void showMarker(long timeInMillis, int drawableId) {
        // Clear the previous marker
        attendanceMarker.setVisibility(View.GONE);

        // Set new marker resource
        attendanceMarker.setImageResource(drawableId);
        attendanceMarker.setVisibility(View.VISIBLE);
    }
}