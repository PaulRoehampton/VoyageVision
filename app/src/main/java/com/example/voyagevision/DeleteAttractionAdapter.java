package com.example.voyagevision;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Context;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.List;

public class DeleteAttractionAdapter extends RecyclerView.Adapter<DeleteAttractionAdapter.ViewHolder> {

    private DeleteAttraction context;
    private List<Attraction> attractions;
    private DatabaseReference mDatabase;

    //adapter used to display a list of attractions with a delete button for each item
    public DeleteAttractionAdapter(DeleteAttraction context, List<Attraction> attractions) {
        // Initialize the adapter with the given context and list of attractions
        this.context = context;
        this.attractions = attractions;
        // Get a reference to the "attractions" node in the Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("attractions");
    }
    //This method inflates the layout for each item in the list view.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attraction_item_with_delete, parent, false);
        return new ViewHolder(view);
    }
    //This method sets the data for each item in the list view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the attraction at the current position in the list view
        Attraction attraction = attractions.get(position);
        // Set the title and description for the attraction in the list item layout
        holder.attractionTitle.setText(attraction.getTitle());
        holder.attractionDescription.setText(attraction.getDescription());
        // Set an on click listener for the delete button in the list item layout
        holder.deleteButton.setOnClickListener(view -> {
            // Get the ID of the attraction to delete and remove it from the Firebase database and the list of attractions
            String attractionId = attraction.getId();
            mDatabase.child(attractionId).removeValue();
            attractions.remove(position);
            notifyItemRemoved(position);
        });
    }
    //This method returns the number of items in the list view
    @Override
    public int getItemCount() {
        return attractions.size();
    }

    //This class holds the views for each item in the list view.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView attractionTitle;
        TextView attractionDescription;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get references to the views in the list item layout
            attractionTitle = itemView.findViewById(R.id.attractionTitle);
            attractionDescription = itemView.findViewById(R.id.attractionDescription);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

