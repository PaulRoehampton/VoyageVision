package com.example.voyagevision;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteAttraction extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userId;
    private ArrayList<Attraction> userAttractions;
    private RecyclerView recyclerView;
    private DeleteAttractionAdapter attractionAdapter;
    private static final String TAG = "DeleteAttraction";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_attraction);

        // Get user ID from FirebaseAuth
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize the RecyclerView and the Adapter
        recyclerView = findViewById(R.id.deleteAttractionRecyclerView);
        userAttractions = new ArrayList<>();
        attractionAdapter = new DeleteAttractionAdapter(DeleteAttraction.this, userAttractions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(attractionAdapter);

        // Initialize Firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("Attractions");

        loadUserAttractions();
    }
    //method loads the attractions for a given user from the database and updates the UI
    private void loadUserAttractions() {
        // Query the database for attractions that belong to the current user
        mDatabase.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the list of user attractions
                userAttractions.clear();
                // Loop through the data snapshot and add each attraction to the list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Attraction attraction = snapshot.getValue(Attraction.class);
                    if (attraction != null) {
                        userAttractions.add(attraction);
                    }
                }
                // Notify the adapter that the data set has changed
                attractionAdapter.notifyDataSetChanged();

                // Check if the list is empty and update the noAttractionsTextView visibility
                TextView noAttractionsTextView = findViewById(R.id.noAttractionsTextView);
                if (userAttractions.isEmpty()) {
                    noAttractionsTextView.setVisibility(View.VISIBLE);
                } else {
                    noAttractionsTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log an error message if there is a problem querying the database
                Log.w(TAG, "loadUserAttractions:onCancelled", databaseError.toException());
            }
        });
    }

}