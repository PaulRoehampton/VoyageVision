package com.example.voyagevision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.common.returnsreceiver.qual.This;

//This activity allows the user to edit their profile information, including name, email, and password.
public class EditProfile extends AppCompatActivity {

    private EditText changeNameEditText, changeEmailEditText, changePasswordEditText;
    private Button changeNameButton, changeEmailButton, changePasswordButton, deleteProfileButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        // Initialize the Firebase authentication and database references
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // Get references to the views in the layout
        changeNameEditText = findViewById(R.id.changeNameEditText);
        changeEmailEditText = findViewById(R.id.changeEmailEditText);
        changePasswordEditText = findViewById(R.id.changePasswordEditText);
        changeNameButton = findViewById(R.id.changeNameButton);
        changeEmailButton = findViewById(R.id.changeEmailButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteProfileButton = findViewById(R.id.deleteProfileButton);
        // Set click listeners for the buttons
        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName();
            }
        });

        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmail();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        deleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile();
            }
        });
    }

    //This method updates the user's name in the Firebase database.
    private void updateName() {
        String newName = changeNameEditText.getText().toString().trim();
        // Check if the new name is empty
        if (newName.isEmpty()) {
            Toast.makeText(this, "Name field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the current user and update their name in the Firebase database
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabaseReference.child("Users").child(user.getUid()).child("fullName").setValue(newName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfile.this, "Name updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfile.this, "Name update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //This method updates the user's email in the Firebase database and authentication
    private void updateEmailInDatabase(String newEmail) {
        // Get the current user and update their email in the Firebase authentication and database
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mDatabaseReference.child("Users").child(currentUser.getUid()).child("email").setValue(newEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfile.this, "Email updated in database", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfile.this, "Failed to update email in database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //This method updates the user's email in the Firebase database.
    private void updateEmail() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.updateEmail(changeEmailEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                updateEmailInDatabase(changeEmailEditText.getText().toString());
                                Toast.makeText(EditProfile.this, "Email updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfile.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //This method updates the user's password in the Firebase database.
    private void updatePassword() {
        String newPassword = changePasswordEditText.getText().toString().trim();

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Password field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the current user and update their password in the Firebase authentication
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfile.this, "Password updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfile.this, "Password update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //This method deletes the user's profile from the Firebase authentication and database.
    private void deleteProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Profile");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // Remove the user's data from the Firebase database and authentication
                    mDatabaseReference.child("Users").child(user.getUid()).removeValue();
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EditProfile.this, "Profile deleted", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditProfile.this, Login.class));
                                        finish();
                                    } else {
                                        Toast.makeText(EditProfile.this, "Profile deletion failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}