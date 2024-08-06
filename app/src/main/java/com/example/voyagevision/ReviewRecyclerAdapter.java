package com.example.voyagevision;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewRecyclerAdapter.ViewHolder> {

    private final List<Review> reviews;
    private Context context;

    public ReviewRecyclerAdapter(Context context, List<Review> reviews) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review currentReview = reviews.get(position);
        holder.attractionTitle.setText(currentReview.getAttractionTitle());
        holder.ratingBar.setRating(currentReview.getRating());
        holder.reviewText.setText(currentReview.getReviewText());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView attractionTitle;
        public RatingBar ratingBar;
        public TextView reviewText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            attractionTitle = itemView.findViewById(R.id.attraction_title);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            reviewText = itemView.findViewById(R.id.review_text);
        }
    }
}