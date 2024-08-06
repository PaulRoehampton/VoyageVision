package com.example.voyagevision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PlanItinerary extends AppCompatActivity {

    private ListView attractionsListView;
    private Button createItineraryButton;
    private DatabaseReference database;
    private DatabaseReference itinerariesDatabase;
    private final ArrayList<Attraction> attractions = new ArrayList<>();
    private final ArrayList<Attraction> selectedAttractions = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_itinerary);

        attractionsListView = findViewById(R.id.attractionsListView);
        createItineraryButton = findViewById(R.id.createItineraryButton);
        database = FirebaseDatabase.getInstance().getReference();
        itinerariesDatabase = FirebaseDatabase.getInstance().getReference("Itineraries");

        fetchAttractions();

        attractionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Attraction selectedAttraction = attractions.get(position);
                if (selectedAttractions.contains(selectedAttraction)) {
                    selectedAttractions.remove(selectedAttraction);
                } else {
                    selectedAttractions.add(selectedAttraction);
                }

                // Update the selected state in the adapter and refresh the view
                AttractionListAdapter adapter = (AttractionListAdapter) attractionsListView.getAdapter();
                adapter.toggleSelectedState(position);
                adapter.notifyDataSetChanged();
            }
        });

        createItineraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAttractions.size() >= 2) {
                    // Create itinerary
                    String itineraryId = itinerariesDatabase.push().getKey();
                    List<String> attractionIds = new ArrayList<>();
                    for (Attraction selectedAttraction : selectedAttractions) {
                        attractionIds.add(selectedAttraction.getId());
                    }
                    Itinerary itinerary = new Itinerary(itineraryId, attractionIds);

                    if (itineraryId != null) {
                        itinerariesDatabase.child(itineraryId).setValue(itinerary)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(PlanItinerary.this, "Itinerary created successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PlanItinerary.this, "Error creating itinerary.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(PlanItinerary.this, "Please select at least 2 attractions.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchAttractions() {
        database.child("Attractions").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                attractions.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Attraction attraction = snapshot.getValue(Attraction.class);
                    if (attraction != null) {
                        attractions.add(attraction);
                    }
                }
                AttractionListAdapter adapter = new AttractionListAdapter(PlanItinerary.this, attractions);
                attractionsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error handling
            }
        });
    }
}