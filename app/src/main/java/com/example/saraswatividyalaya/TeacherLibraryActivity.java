package com.example.saraswatividyalaya;
import android.widget.AdapterView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TeacherLibraryActivity extends AppCompatActivity {

    private Button uploadPdfButton;
    private Button deletePdfButton;
    private ListView pdfListView;
    private Uri pdfUri;
    private ArrayList<String> pdfList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;
    private ListenerRegistration pdfListListener; // Stores listener for real-time updates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_library);

        uploadPdfButton = findViewById(R.id.button_upload_pdf);
        deletePdfButton = findViewById(R.id.button_delete_pdf);
        pdfListView = findViewById(R.id.list_view_uploaded_pdfs);
        progressBar = findViewById(R.id.progress_bar);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("pdfs");
        firestore = FirebaseFirestore.getInstance();

        // Set progress bar style to determinate if known (optional)
        progressBar.setMax(100); // Adjust max value based on your upload process
        progressBar.setIndeterminate(false);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, pdfList);
        pdfListView.setAdapter(adapter);
        pdfListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Start real-time listener for PDF updates
        pdfListListener = firestore.collection("pdfs")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(TeacherLibraryActivity.this, "Error loading PDFs", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    pdfList.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        String fileName = snapshot.getString("fileName");
                        pdfList.add(fileName);
                    }
                    adapter.notifyDataSetChanged();
                });

        uploadPdfButton.setOnClickListener(view -> openPdfSelector());
        deletePdfButton.setOnClickListener(view -> deleteSelectedPdfs());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfListListener != null) {
            pdfListListener.remove(); // Unsubscribe from listener on activity destroy
        }
    }

    private void openPdfSelector() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            uploadPdfToFirebase();
        }
    }

    private void uploadPdfToFirebase() {
        if (pdfUri != null) {
            progressBar.setVisibility(View.VISIBLE); // Show progress bar

            String fileName = System.currentTimeMillis() + ".pdf";
            StorageReference fileReference = storageReference.child(fileName);

            // Use a TaskListener for more granular progress updates
            fileReference.putFile(pdfUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(TeacherLibraryActivity.this, "Upload complete", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE); // Hide progress bar
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String pdfUrl = uri.toString();
                            savePdfInfoToFirestore(fileName, pdfUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                        Toast.makeText(TeacherLibraryActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        // Optional: Update progress bar based on bytes transferred/total
                        if (taskSnapshot.getTotalByteCount() > 0) {
                            int progress = (int) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress(progress);
                        }
                    });
        }
    }

    private void savePdfInfoToFirestore(String fileName, String pdfUrl) {
        Map<String, String> pdfData = new HashMap<>();
        pdfData.put("fileName", fileName);
        pdfData.put("url", pdfUrl);
        firestore.collection("pdfs").add(pdfData)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar when done
                    Toast.makeText(TeacherLibraryActivity.this, "PDF Uploaded", Toast.LENGTH_SHORT).show();
                    loadUploadedPdfs(); // Refresh list after upload completes
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                    Toast.makeText(TeacherLibraryActivity.this, "Failed to Save PDF Info", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUploadedPdfs() {
        firestore.collection("pdfs").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pdfList.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        String fileName = snapshot.getString("fileName");
                        pdfList.add(fileName);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(TeacherLibraryActivity.this, "Error loading PDFs", Toast.LENGTH_SHORT).show());
    }

    private void deleteSelectedPdfs() {
        SparseBooleanArray checkedItemPositions = pdfListView.getCheckedItemPositions();

        if (checkedItemPositions.size() == 0) {
            Toast.makeText(this, "No PDFs selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // progressBar.setVisibility(View.VISIBLE); // Show progress bar for overall deletion (commented out)

        AtomicInteger deleteCount = new AtomicInteger();
        int totalSelected = checkedItemPositions.size(); // Pre-calculate total

        for (int i = checkedItemPositions.size() - 1; i >= 0; i--) { // Iterate backwards for efficient removal
            int position = checkedItemPositions.keyAt(i);
            if (checkedItemPositions.valueAt(i)) {
                String selectedFileName = pdfList.get(position);

                firestore.collection("pdfs")
                        .whereEqualTo("fileName", selectedFileName)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                String fileUrl = document.getString("url");
                                deletePdfFromFirebase(fileUrl, document.getReference(), totalSelected, deleteCount.incrementAndGet());
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(TeacherLibraryActivity.this, "Error finding PDFs", Toast.LENGTH_SHORT).show();
                        });

                // Remove item from list view immediately after deletion query
                pdfList.remove(position);
                adapter.notifyDataSetChanged();
            }
        }
    }




    private void deletePdfFromFirebase(String fileUrl, DocumentReference docRef, int totalSelected, int deleteCount) {
        StorageReference fileReference = firebaseStorage.getReferenceFromUrl(fileUrl);
        fileReference.delete()
                .addOnSuccessListener(aVoid -> docRef.delete()
                        .addOnSuccessListener(aVoid1 -> {
                            // Check if all selected items have been deleted
                            if (deleteCount == totalSelected) {
                                progressBar.setVisibility(View.GONE); // Hide progress bar when all done
                                Toast.makeText(TeacherLibraryActivity.this, pluralize("PDF deleted", "PDFs deleted", totalSelected), Toast.LENGTH_SHORT).show(); // Pluralize toast message
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(TeacherLibraryActivity.this, "Failed to delete from Firestore", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(TeacherLibraryActivity.this, "Failed to delete from Storage", Toast.LENGTH_SHORT).show();
                });
    }

    public static String pluralize(String singular, String plural, int count) {
        return count == 1 ? singular : plural;
    }
}