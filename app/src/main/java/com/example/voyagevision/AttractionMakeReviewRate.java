package com.example.voyagevision;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AttractionMakeReviewRate extends DialogFragment {
    private DatabaseReference attractionsDatabase;
    private DatabaseReference reviewsDatabase;
    private TextView attractionTitleTextView;
    private TextView attractionDescriptionTextView;
    private ListView reviewsListView;
    private ReviewsAdapter reviewsAdapter;

    public AttractionMakeReviewRate() {
        // Empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Initialize views
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.attraction_make_review_rate, null);
        reviewsDatabase = FirebaseDatabase.getInstance().getReference("Reviews");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        attractionsDatabase = FirebaseDatabase.getInstance().getReference("Attractions");
        String attractionId = getArguments().getString("attractionId");
        fetchAttractionDetails(attractionId);
        fetchReviews(attractionId);

        builder.setView(view);

        // Initialize the EditTexts, Buttons, and ListView
        EditText reviewEditText = view.findViewById(R.id.reviewEditText);
        Button submitReviewButton = view.findViewById(R.id.submitReviewButton);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        attractionTitleTextView = view.findViewById(R.id.attractionTitleTextView);
        attractionDescriptionTextView = view.findViewById(R.id.attractionDescriptionTextView);
        reviewsListView = view.findViewById(R.id.reviewsListView);

        // Set an OnClickListener for the submit Button
        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reviewText = reviewEditText.getText().toString();
                float rating = ratingBar.getRating();

                if (!reviewText.isEmpty()) {
                    // Call the submitReview method
                    submitReview(attractionId, rating, reviewText);
                } else {
                    Toast.makeText(getContext(), "Please write a review.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();
    }

    // Fetch attraction details from Firebase
    private void fetchAttractionDetails(String attractionId) {
        attractionsDatabase.child(attractionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Attraction attraction = dataSnapshot.getValue(Attraction.class);
                if (attraction != null) {
                    attractionTitleTextView.setText(attraction.getTitle());
                    attractionDescriptionTextView.setText(attraction.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // retrieve the user's name from Firebase
    private String getUserName() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null) {
            Log.d("AttractionMakeReviewRate", "Display name: " + currentUser.getDisplayName()); // Add this line for debugging
            return currentUser.getDisplayName();
        } else {
            return "Anonymous";
        }
    }

    // Fetch reviews for the attraction from Firebase
    private void fetchReviews(String attractionId) {
        reviewsDatabase.child(attractionId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Review> reviews = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    if (review != null) {
                        reviews.add(review);
                    }
                }

                // Check if the Fragment is still attached to the context
                if (isAdded()) {
                    // Create the ReviewsAdapter with fetched reviews
                    reviewsAdapter = new ReviewsAdapter(requireContext(), reviews);

                    // Set the adapter for the ListView
                    reviewsListView.setAdapter(reviewsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    // Submit a new review for the attraction
    private void submitReview(String attractionId, float rating, String reviewText) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            String userName = getUserName();
            String attractionTitle = attractionTitleTextView.getText().toString();

            Review newReview = new Review(userId, userName, attractionTitle, rating, reviewText);

            String reviewId = reviewsDatabase.child(attractionId).push().getKey();
            if (reviewId != null) {
                reviewsDatabase.child(attractionId).child(reviewId).setValue(newReview)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Review submitted successfully.", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error submitting review.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    // Create a custom ArrayAdapter class that extends ArrayAdapter<Review> to display reviews in ListView
    private static class ReviewsAdapter extends ArrayAdapter<Review> {
        private final Context context;
        private final List<Review> reviews;

        public ReviewsAdapter(@NonNull Context context, @NonNull List<Review> objects) {
            super(context, 0, objects);
            this.context = context;
            this.reviews = objects;
        }
        //method returns the view for each item in the list view.
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Inflate the layout for each item in the list view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listItemView = inflater.inflate(R.layout.review_item, parent, false);
            // Get references to the views in the list item layout
            TextView reviewAuthorTextView = listItemView.findViewById(R.id.reviewAuthorTextView);
            TextView reviewTextTextView = listItemView.findViewById(R.id.reviewTextTextView);
            RatingBar reviewRatingBar = listItemView.findViewById(R.id.reviewRatingBar);
            // Get the review at the current position in the list view
            Review currentReview = reviews.get(position);
            // Set the text and rating for the views in the list item layout using the review data
            reviewAuthorTextView.setText(currentReview.getUserName());
            reviewTextTextView.setText(currentReview.getReviewText());
            reviewRatingBar.setRating(currentReview.getRating());
            // Return the new view for the current item in the list view
            return listItemView;
        }
    }

}


