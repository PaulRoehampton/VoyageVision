package com.example.voyagevision;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    private TextView textViewUserName;
    private Button addNewAttractionButton;
    private Button findNearbyAttractionsButton;
    private ImageView editProfileIcon;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize TextView and addNewAttractionButton
        textViewUserName = findViewById(R.id.userName);
        addNewAttractionButton = findViewById(R.id.addNewAttraction);

        addNewAttractionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNewAttractionActivity();
            }
        });

        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, UserMenu.class);
                startActivity(intent);
            }
        });
        // Initialize the findNearbyAttractionsButton
        findNearbyAttractionsButton = findViewById(R.id.findNearbyAttractionsButton);

        // Set OnClickListener for findNearbyAttractionsButton
        findNearbyAttractionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFindNearbyAttractionsActivity();
            }
        });

        // Initialize the editProfileIcon
        editProfileIcon = findViewById(R.id.editProfileIcon);

        // Set OnClickListener for editProfileIcon
        editProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfileActivity();
            }
        });

        //Initialize the planYourItinerary Button
        Button planItineraryButton = findViewById(R.id.planItineraryButton);
        planItineraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfile.this, PlanItinerary.class));
            }
        });
    }

    // Method to open AddNewAttractionActivity activity
    private void openAddNewAttractionActivity() {
        Intent intent = new Intent(UserProfile.this, AddNewAttraction.class);
        startActivity(intent);
    }

    // Method to open FindNearbyAttractions activity
    private void openFindNearbyAttractionsActivity() {
        Intent intent = new Intent(UserProfile.this, FindNearbyAttractions.class);
        startActivity(intent);
    }

    // Method to open EditProfile activity
    private void openEditProfileActivity() {
        Intent intent = new Intent(UserProfile.this, EditProfile.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if the user is signed in
        if (currentUser != null) {
            // Get the user's name from Firebase
            mDatabaseReference.child("Users").child(currentUser.getUid()).child("fullName")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Set the text view with the user's name from Firebase
                            textViewUserName.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error here
                        }
                    });
        }
    }
}

