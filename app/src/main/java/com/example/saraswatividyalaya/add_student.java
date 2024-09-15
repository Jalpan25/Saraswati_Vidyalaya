
package com.example.saraswatividyalaya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class add_student extends AppCompatActivity {

    EditText name, email, password, dob, rollno, medium, standard;
    RadioGroup genderGroup;
    RadioButton selectedGender;
    Button buttonOK;
    String name1, email1, password1, dob1, rollno1, medium1, standard1, gender1;
    FirebaseDatabase db;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private Uri imageUri;
    private ImageView studentImageView;
    private Button selectImageButton;
    @Override
    public void onBackPressed() {
        // Navigate to the home page when the back button is pressed
        Intent intent = new Intent(add_student.this, teacher_home.class);
        startActivity(intent);
        finish(); // Finish this activity so it doesn't remain in the back stack
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // Initialize the views
        name = findViewById(R.id.usernameEditText);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        dob = findViewById(R.id.editTextDate2);
        rollno = findViewById(R.id.rollNumberEditText);
        medium = findViewById(R.id.mediumEditText);
        standard = findViewById(R.id.standardEditText);
        genderGroup = findViewById(R.id.genderRadioGroup);
        buttonOK = findViewById(R.id.loginButton);
        studentImageView = findViewById(R.id.studentImageView);
        selectImageButton = findViewById(R.id.selectImageButton);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("student_images");

        studentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the camera to capture an image
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the gallery to select an image
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
            }
        });

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect user input
                name1 = name.getText().toString();
                email1 = email.getText().toString();
                password1 = password.getText().toString();
                dob1 = dob.getText().toString();
                rollno1 = rollno.getText().toString();
                medium1 = medium.getText().toString();
                standard1 = standard.getText().toString();

                // Get selected gender
                int selectedId = genderGroup.getCheckedRadioButtonId();
                selectedGender = findViewById(selectedId);
                gender1 = selectedGender.getText().toString();

                if (!name1.isEmpty() && !email1.isEmpty() && !password1.isEmpty() && !dob1.isEmpty()
                        && !rollno1.isEmpty() && !medium1.isEmpty() && !standard1.isEmpty() && selectedId != -1) {

                    // First create the user in Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email1, password1)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // If user creation in Firebase Authentication succeeds, proceed with image upload
                                        if (imageUri != null) {
                                            // Upload the image to Firebase Storage
                                            StorageReference fileRef = storageReference.child(rollno1 + ".jpg");
                                            fileRef.putFile(imageUri)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            // Get the download URL for the uploaded image
                                                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    String imageUrl = uri.toString();
                                                                    // Now save the student data along with the image URL to the database
                                                                    saveStudentData(imageUrl); // Pass the image URL here
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(add_student.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(add_student.this, "Please select an image", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(add_student.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(add_student.this, "Please fill all fields and select gender", Toast.LENGTH_SHORT).show();
                }
            }


            private void saveStudentData(String imageUrl) {
                // Create the Users object with the imageUrl
                Users users = new Users(name1, email1, password1, dob1, rollno1, medium1, standard1, gender1, imageUrl);

                // Save user information in Firebase Realtime Database
                db = FirebaseDatabase.getInstance();
                reference = db.getReference("Students");
                reference.child(rollno1).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Clear the input fields after successful registration
                            name.setText("");
                            email.setText("");
                            password.setText("");
                            dob.setText("");
                            rollno.setText("");
                            medium.setText("");
                            standard.setText("");
                            genderGroup.clearCheck();
                            studentImageView.setImageURI(null);

                            Toast.makeText(add_student.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

// Method to save student data with the image URL

    }
        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            studentImageView.setImageURI(imageUri);
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            studentImageView.setImageBitmap(bitmap);
            imageUri = getImageUriFromBitmap(bitmap); // Convert bitmap to Uri
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        // Code to convert bitmap to Uri (you can save it to cache)
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Student Image", null);
        return Uri.parse(path);
    }
    private void saveStudentData(String imageUrl) {
        Users users = new Users(name1, email1, password1, dob1, rollno1, medium1, standard1, gender1, imageUrl);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Students");
        reference.child(rollno1).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(add_student.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class Users {

        String name1, email1, password1, dob1, rollno1, medium1, standard1, gender1, imageUrl;

        // Default constructor (required for calls to DataSnapshot.getValue(Users.class))
        public Users() {
        }

        // Constructor that includes the imageUrl parameter
        public Users(String name1, String email1, String password1, String dob1, String rollno1, String medium1, String standard1, String gender1, String imageUrl) {
            this.name1 = name1;
            this.email1 = email1;
            this.password1 = password1;
            this.dob1 = dob1;
            this.rollno1 = rollno1;
            this.medium1 = medium1;
            this.standard1 = standard1;
            this.gender1 = gender1;
            this.imageUrl = imageUrl;  // Add imageUrl here
        }

        // Getters and setters for all the fields, including imageUrl
        public String getName1() {
            return name1;
        }

        public void setName1(String name1) {
            this.name1 = name1;
        }

        public String getEmail1() {
            return email1;
        }

        public void setEmail1(String email1) {
            this.email1 = email1;
        }

        public String getPassword1() {
            return password1;
        }

        public void setPassword1(String password1) {
            this.password1 = password1;
        }

        public String getDob1() {
            return dob1;
        }

        public void setDob1(String dob1) {
            this.dob1 = dob1;
        }

        public String getRollno1() {
            return rollno1;
        }

        public void setRollno1(String rollno1) {
            this.rollno1 = rollno1;
        }

        public String getMedium1() {
            return medium1;
        }

        public void setMedium1(String medium1) {
            this.medium1 = medium1;
        }

        public String getStandard1() {
            return standard1;
        }

        public void setStandard1(String standard1) {
            this.standard1 = standard1;
        }

        public String getGender1() {
            return gender1;
        }

        public void setGender1(String gender1) {
            this.gender1 = gender1;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

}
