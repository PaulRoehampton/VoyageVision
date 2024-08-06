package com.example.voyagevision;

import static com.example.voyagevision.R.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private TextView txt_email, txt_pw;
    private Button btn_login;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_login);

        //initialise the 'Register' text view
        register = (TextView) findViewById(R.id.newUser_txt);
        register.setOnClickListener(this);

        //initialise the 'Login' button, email and password variables
        btn_login = (Button) findViewById(R.id.loginButton);
        btn_login.setOnClickListener(this);

        txt_email = (TextView) findViewById(R.id.emailEditText);
        txt_pw = (TextView) findViewById(id.passwordEditText);

        //initialise the firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //initialise forgotPassword variable and setup a click listener
        forgotPassword = findViewById(id.forgotPassword_txt);
        forgotPassword.setOnClickListener(this);
    }

    //implement onClick methods
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //initialise the RegisterUser activity when the user click on the 'Register' button
            case R.id.newUser_txt:
                startActivity(new Intent(this, RegisterUser.class));
                break;

            //initialise the UserProfile activity when the user click on the 'Login' button
            case R.id.loginButton:
                userLogin();
                break;

            //initialise the ForgotPassword activity when the user click on the 'Forgot password?' button
            case id.forgotPassword_txt:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    // create userLogin method
    private void userLogin() {
        //get user credentials
        String email = txt_email.getText().toString().trim();
        String password = txt_pw.getText().toString().trim();

        //set validations and error messages for user inputs (Email, Password)
        if (email.isEmpty()) {
            txt_email.setError("Email is required!");
            txt_email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txt_email.setError("Please enter a valid email!");
            txt_email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            txt_pw.setError("Password is required!");
            txt_pw.requestFocus();
            return;
        }

        if (password.length() < 6) {
            txt_pw.setError("Min password length is 6 characters!");
            txt_pw.requestFocus();
            return;
        }

//initialise firebase authentication with email and password
        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Login.this, UserProfile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}