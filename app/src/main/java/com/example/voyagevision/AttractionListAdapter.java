package com.example.voyagevision;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AttractionListAdapter extends ArrayAdapter<Attraction> {
    private final Context context;
    private final ArrayList<Attraction> attractions;
    boolean[] selectedStates;

    //Constructor for the AttractionListAdapter class
    public AttractionListAdapter(Context context, ArrayList<Attraction> attractions) {
        super(context, 0, attractions);
        this.context = context;
        this.attractions = attractions;
        this.selectedStates = new boolean[attractions.size()];
    }
    //Method to toggle the selected state of an item in the ListView
    public void toggleSelectedState(int position) {
        selectedStates[position] = !selectedStates[position];
        notifyDataSetChanged();
    }
    //get the view to be displayed for an item in the ListView .
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.attraction_list_item, parent, false);
        }

        Attraction attraction = attractions.get(position);
        ImageView attractionImageView = convertView.findViewById(R.id.attractionImageView);
        TextView attractionTitleView = convertView.findViewById(R.id.attractionTitleView);

        // Set attraction title
        attractionTitleView.setText(attraction.getTitle());

        // Load attraction image with Glide library
        Glide.with(context)
                .load(attraction.getImageUrl())
                .placeholder(R.drawable.abstract_background)
                .error(R.drawable.email_icon40)
                .into(attractionImageView);
        // Set background color of the view based on selected state
        if (selectedStates[position]) {
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }
}
