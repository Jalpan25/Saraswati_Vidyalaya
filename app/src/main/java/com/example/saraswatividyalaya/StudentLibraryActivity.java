package com.example.saraswatividyalaya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class StudentLibraryActivity extends AppCompatActivity {

        private ListView pdfListView;
        private FirebaseFirestore firestore;

        private ArrayList<String> pdfList = new ArrayList<>();
        private ArrayAdapter<String> adapter;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_student_library);

            pdfListView = findViewById(R.id.list_view_pdfs);

            firestore = FirebaseFirestore.getInstance();

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pdfList);
            pdfListView.setAdapter(adapter);

            loadPdfsForStudents();

            pdfListView.setOnItemClickListener((adapterView, view, position, id) -> {
                String pdfName = pdfList.get(position);
                getPdfUrlAndOpen(pdfName);
            });
        }

        private void loadPdfsForStudents() {
            firestore.collection("pdfs").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        pdfList.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            String fileName = snapshot.getString("fileName");
                            pdfList.add(fileName);
                        }
                        adapter.notifyDataSetChanged();
                    });
        }

        private void getPdfUrlAndOpen(String pdfName) {
            firestore.collection("pdfs")
                    .whereEqualTo("fileName", pdfName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String pdfUrl = queryDocumentSnapshots.getDocuments().get(0).getString("url");
                            openPdf(pdfUrl);
                        }
                    });
        }

        private void openPdf(String pdfUrl) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
    }

