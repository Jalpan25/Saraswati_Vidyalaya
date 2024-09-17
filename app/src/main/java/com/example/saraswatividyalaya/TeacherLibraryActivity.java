package com.example.saraswatividyalaya;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

    private ListView pdfListView;
    private Uri pdfUri;
    private ArrayList<String> pdfList = new ArrayList<>();
    private ArrayList<String> pdfUrls = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;
    private ListenerRegistration pdfListListener;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_library);

        pdfListView = findViewById(R.id.list_view_uploaded_pdfs);
        progressBar = findViewById(R.id.progress_bar);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("pdfs");
        firestore = FirebaseFirestore.getInstance();

        progressBar.setMax(100);
        progressBar.setIndeterminate(false);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, pdfList);
        pdfListView.setAdapter(adapter);
        pdfListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        pdfListListener = firestore.collection("pdfs")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(TeacherLibraryActivity.this, "Error loading PDFs", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    pdfList.clear();
                    pdfUrls.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        String fileName = snapshot.getString("fileName");
                        String url = snapshot.getString("url");
                        pdfList.add(fileName);
                        pdfUrls.add(url);
                    }
                    adapter.notifyDataSetChanged();
                });

        pdfListView.setOnItemClickListener((adapterView, view, position, id) -> {
            if (actionMode == null) {
                String pdfUrl = pdfUrls.get(position);
                openPdf(pdfUrl);
            }
        });

        pdfListView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (actionMode == null) {
                actionMode = startActionMode(actionModeCallback);
                pdfListView.setItemChecked(position, true);
            }
            return true;
        });

        findViewById(R.id.button_upload_pdf).setOnClickListener(v -> openPdfSelector());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TeacherLibraryActivity.this, teacher_home.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfListListener != null) {
            pdfListListener.remove();
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
            progressBar.setVisibility(View.VISIBLE);

            String fileName = System.currentTimeMillis() + ".pdf";
            StorageReference fileReference = storageReference.child(fileName);

            fileReference.putFile(pdfUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(TeacherLibraryActivity.this, "Upload complete", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String pdfUrl = uri.toString();
                            savePdfInfoToFirestore(fileName, pdfUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(TeacherLibraryActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
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

        firestore.collection("pdfs")
                .add(pdfData)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TeacherLibraryActivity.this, "PDF Uploaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TeacherLibraryActivity.this, "Failed to Save PDF Info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteSelectedPdfs() {
        SparseBooleanArray checkedItemPositions = pdfListView.getCheckedItemPositions();

        if (checkedItemPositions.size() == 0) {
            Toast.makeText(this, "No PDFs selected", Toast.LENGTH_SHORT).show();
            return;
        }

        AtomicInteger deleteCount = new AtomicInteger();
        int totalSelected = checkedItemPositions.size();

        for (int i = checkedItemPositions.size() - 1; i >= 0; i--) {
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
                            if (deleteCount == totalSelected) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(TeacherLibraryActivity.this, pluralize("PDF deleted", "PDFs deleted", totalSelected), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(TeacherLibraryActivity.this, "Failed to delete from Firestore", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(TeacherLibraryActivity.this, "Failed to delete from Firebase Storage", Toast.LENGTH_SHORT).show();
                });
    }

    private String pluralize(String singular, String plural, int count) {
        return count == 1 ? singular : plural;
    }

    private void openPdf(String pdfUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent chooser = Intent.createChooser(intent, "Open PDF");
        try {
            startActivity(chooser);
        } catch (Exception e) {
            Toast.makeText(this, "No app available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                deleteSelectedPdfs();
                mode.finish();
                return true;
            } else if (item.getItemId() == R.id.action_rename) {
                // Get the selected PDF position
                SparseBooleanArray checkedItemPositions = pdfListView.getCheckedItemPositions();
                if (checkedItemPositions.size() != 1) {
                    Toast.makeText(TeacherLibraryActivity.this, "Please select only one PDF to rename", Toast.LENGTH_SHORT).show();
                    return false;
                }
                int selectedPosition = checkedItemPositions.keyAt(0);

                // Show a dialog to enter the new name
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherLibraryActivity.this);
                builder.setTitle("Rename PDF");
                final EditText input = new EditText(TeacherLibraryActivity.this);
                builder.setView(input);
                builder.setPositiveButton("Rename", (dialog, which) -> {
                    String newFileName = input.getText().toString().trim();
                    if (newFileName.isEmpty()) {
                        Toast.makeText(TeacherLibraryActivity.this, "Please enter a new name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update the PDF name in Firestore
                    String selectedFileName = pdfList.get(selectedPosition);
                    firestore.collection("pdfs")
                            .whereEqualTo("fileName", selectedFileName)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    DocumentReference docRef = document.getReference();
                                    Map<String, Object> updatedData = new HashMap<>();
                                    updatedData.put("fileName", newFileName);
                                    docRef.update(updatedData)
                                            .addOnSuccessListener(aVoid -> {
                                                // Update the PDF name in the local list and adapter
                                                pdfList.set(selectedPosition, newFileName);
                                                adapter.notifyDataSetChanged();
                                                Toast.makeText(TeacherLibraryActivity.this, "PDF renamed successfully", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(TeacherLibraryActivity.this, "Failed to rename PDF", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(TeacherLibraryActivity.this, "Error finding PDF", Toast.LENGTH_SHORT).show();
                            });
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing
                });
                builder.show();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };
    }