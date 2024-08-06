package com.example.voyagevision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

        // Declare the views
        private EditText inputEmail;
        private Button btnReset;
        private TextView iconBack;
        private FirebaseAuth auth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forgot_password);

            auth = FirebaseAuth.getInstance();
            // Initialise the views
            inputEmail = findViewById(R.id.enterEmailText);
            btnReset = findViewById(R.id.btn_reset_password);
            iconBack = findViewById(R.id.txt_resetBackArrow);

            // Set the click listener for the back icon
            iconBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ForgotPassword.this, Login.class);
                    startActivity(i);
                }
            });

            // Set the click listener for the reset password button
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the entered email
                    String email = inputEmail.getText().toString().trim();

                    // Check if the email is not empty
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Show the progress dialog
                    final ProgressDialog progressDialog = new ProgressDialog(ForgotPassword.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Sending reset password email...");
                    progressDialog.show();
                    
                    // Send the reset password email
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Hide the progress dialog
                                    progressDialog.dismiss();

                                    // Check if the task was successful
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPassword.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ForgotPassword.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }


}