package com.example.saraswatividyalaya;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Attendance_teacher extends AppCompatActivity {

    private GridLayout rollNumberGrid;
    private Button absentButton, uploadAttendanceButton, dateSelectButton;
    private TextView dateText;
    private Map<Integer, Boolean> attendanceMap = new HashMap<>();
    private int selectedRollNo = -1;
    private DatabaseReference attendanceReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_teacher);

        rollNumberGrid = findViewById(R.id.rollNumberGrid);
        absentButton = findViewById(R.id.absentButton);
        uploadAttendanceButton = findViewById(R.id.uploadAttendanceButton);
        dateSelectButton = findViewById(R.id.dateSelectButton);
        dateText = new TextView(this);

        // Initialize Firebase reference
        attendanceReference = FirebaseDatabase.getInstance().getReference("Attendance");

        // Set current date in full format
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        dateText.setText(currentDate);

        // Add date picker functionality
        dateSelectButton.setOnClickListener(v -> showDatePicker());

        // Populate grid with roll numbers
        populateRollNumbers();

        absentButton.setOnClickListener(v -> markAbsent());
        uploadAttendanceButton.setOnClickListener(v -> uploadAttendance());
    }
    @Override
    public void onBackPressed() {
        // Navigate to the home page when the back button is pressed
        Intent intent = new Intent(Attendance_teacher.this, teacher_home.class);
        startActivity(intent);
        finish(); // Finish this activity so it doesn't remain in the back stack
    }
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%d", selectedDay, (selectedMonth + 1), selectedYear);
                    dateText.setText(selectedDate);
                    dateSelectButton.setText(selectedDate); // Update button text with the selected date
                    resetRollNumbers(); // Reset roll numbers to green for new date
                }, year, month, day);

        datePickerDialog.show();
    }

    private void populateRollNumbers() {
        for (int i = 1; i <= 10; i++) {
            final int rollNo = i;

            TextView rollTextView = new TextView(this);
            rollTextView.setText(String.valueOf(rollNo));
            rollTextView.setTextSize(24);
            rollTextView.setBackgroundResource(R.drawable.circle_green);
            rollTextView.setPadding(20, 20, 20, 20);
            rollTextView.setTextColor(getResources().getColor(R.color.white));

            // Initially, mark all students as present
            attendanceMap.put(rollNo, true);

            // Set click listener to select a roll number
            rollTextView.setOnClickListener(v -> selectRollNumber(rollNo, rollTextView));
            rollNumberGrid.addView(rollTextView);
        }
    }

    private void resetRollNumbers() {
        for (int i = 0; i < rollNumberGrid.getChildCount(); i++) {
            TextView rollTextView = (TextView) rollNumberGrid.getChildAt(i);
            rollTextView.setBackgroundResource(R.drawable.circle_green);
            attendanceMap.put(i + 1, true);
        }
    }

    private void selectRollNumber(int rollNo, TextView rollTextView) {
        selectedRollNo = rollNo;
        Toast.makeText(this, "Selected Roll No: " + rollNo, Toast.LENGTH_SHORT).show();
    }

    private void markAbsent() {
        if (selectedRollNo != -1) {
            TextView selectedTextView = (TextView) rollNumberGrid.getChildAt(selectedRollNo - 1);
            selectedTextView.setBackgroundResource(R.drawable.circle_red);
            attendanceMap.put(selectedRollNo, false);
        } else {
            Toast.makeText(this, "Please select a student to mark absent.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAttendance() {
        String selectedDate = dateText.getText().toString();

        // Extract the month from the selected date (e.g., "01-09-2024" -> "09-2024")
        String month = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

        // Reference to the month in Firebase
        DatabaseReference monthRef = attendanceReference.child(month);

        // Iterate through the attendanceMap to upload the data
        for (Map.Entry<Integer, Boolean> entry : attendanceMap.entrySet()) {
            int rollNo = entry.getKey();
            boolean isPresent = entry.getValue();

            // Reference to the specific roll number in the selected month without the "RollNo_" prefix
            DatabaseReference rollNoRef = monthRef.child(String.valueOf(rollNo));

            // Store attendance for the selected date under the roll number
            rollNoRef.child(selectedDate).setValue(isPresent ? "Present" : "Absent");
        }

        Toast.makeText(this, "Attendance uploaded for " + selectedDate, Toast.LENGTH_SHORT).show();
    }

}
