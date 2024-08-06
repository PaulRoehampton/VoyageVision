package com.example.voyagevision;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {

    // Variables declaration
    EditText fullNameEditText, emailEditText, passwordEditText;
    Button registerButton;
    //Declaring reference to firebase database
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void registerUser(View view) {
        final String fullName = fullNameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)
                                        .build();

                                if (user != null) {
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("RegisterUser", "User profile updated.");

                                                        User newUser = new User(fullName, email);
                                                        FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(RegisterUser.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                                            //redirect user to Login activity
                                                                            Intent intent = new Intent(getApplicationContext(), Login.class);
                                                                            startActivity(intent);
                                                                        } else {
                                                                            // Handling errors from setValue
                                                                            Toast.makeText(RegisterUser.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                    } else {
                                                        // Handling errors from updateProfile
                                                        Toast.makeText(RegisterUser.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                // Handling errors from createUserWithEmailAndPassword
                                Toast.makeText(RegisterUser.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}

