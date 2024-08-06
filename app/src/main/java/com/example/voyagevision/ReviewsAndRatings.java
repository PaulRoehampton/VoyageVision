package com.example.voyagevision;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReviewsAndRatings extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noReviewsTextView;
    private List<Review> reviewList;
    private ReviewRecyclerAdapter reviewRecyclerAdapter;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_and_ratings);

        recyclerView = findViewById(R.id.reviewsAndRatingsRecyclerView);
        noReviewsTextView = findViewById(R.id.noReviewsAndRatingsTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        reviewList = new ArrayList<>();
        reviewRecyclerAdapter = new ReviewRecyclerAdapter(this, reviewList);
        recyclerView.setAdapter(reviewRecyclerAdapter);

        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Reviews");

        loadUserReviews();
    }

    private void loadUserReviews() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("Reviews");

        reviewsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        Review review = reviewSnapshot.getValue(Review.class);

                        // Check if the review belongs to the current user
                        if (review.getUserId().equals(userId)) {
                            reviewList.add(review);
                        }
                    }
                    // Update the RecyclerView
                    reviewRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}