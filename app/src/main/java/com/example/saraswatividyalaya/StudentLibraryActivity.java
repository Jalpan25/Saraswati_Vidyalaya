package com.example.saraswatividyalaya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StudentLibraryActivity extends AppCompatActivity {

    private ListView pdfListView;
    private FirebaseFirestore firestore;

    private ArrayList<String> pdfNames = new ArrayList<>(); // Changed to store filenames
    private ArrayList<String> pdfUrls = new ArrayList<>();  // Store URLs separately for opening PDFs
    private ArrayAdapter<String> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_library);

        pdfListView = findViewById(R.id.list_view_pdfs);

        firestore = FirebaseFirestore.getInstance();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pdfNames);
        pdfListView.setAdapter(adapter);

        loadPdfsForStudents(); // Load PDFs using a real-time listener

        pdfListView.setOnItemClickListener((adapterView, view, position, id) -> {
            String pdfUrl = pdfUrls.get(position);
            openPdf(pdfUrl);
        });
    }

    // Use Firestore listener for real-time updates
    private void loadPdfsForStudents() {
        firestore.collection("pdfs")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle potential errors
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            pdfNames.clear(); // Clear the list before adding new data
                            pdfUrls.clear();  // Clear the URLs list as well

                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                String fileName = snapshot.getString("fileName");
                                String url = snapshot.getString("url");
                                if (fileName != null && url != null) {
                                    pdfNames.add(fileName); // Add each PDF's filename to the list
                                    pdfUrls.add(url);      // Store the corresponding URL
                                }
                            }
                            adapter.notifyDataSetChanged(); // Notify adapter to refresh the list
                        }
                    }
                });
    }

    // Open the PDF in a default PDF viewer
    private void openPdf(String pdfUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Navigate to the home page when the back button is pressed
        Intent intent = new Intent(StudentLibraryActivity.this, home_page.class);
        startActivity(intent);
        finish(); // Finish this activity so it doesn't remain in the back stack
    }
}
