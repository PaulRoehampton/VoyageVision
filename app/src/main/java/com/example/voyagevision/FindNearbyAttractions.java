package com.example.voyagevision;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.ArrayList;

//This class displays a map with markers for nearby attractions and a list of attractions.
public class FindNearbyAttractions extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {
    // Define variables
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 106;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;
    private ListView attractionsListView;
    private DatabaseReference mDatabase;
    private final ArrayList<Attraction> attractions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby_attractions);
        // Initialize the map fragment and get a reference to the map asynchronously
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        // Initialize the fused location provider client
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Initialize the database reference and the attractions list view
        mDatabase = FirebaseDatabase.getInstance().getReference("Attractions");
        attractionsListView = findViewById(R.id.attractionsListView);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Register the OnCameraIdleListener
        mMap.setOnCameraIdleListener(this);

        // Register the OnMarkerClickListener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String attractionId = marker.getTag().toString();
                showAttractionInfo(attractionId);
                return false;
            }
        });
        // Get the location permission and update the location UI
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    //This method checks whether the app has permission to access the user's location
    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    //This method updates the location UI based on whether the app has permission to access the user's location
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    //this method gets the device's last known location and moves the camera to that location.
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastKnownLocation = task.getResult();
                            // Show the stored attractions that are within the visible map area
                            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                            showStoredAttractionsOnMap(bounds);
                            // Move the camera to the device's last known location
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            // If the device's last known location is not available, move the camera to London
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(51.509865, -0.118092), DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    //this method shows the stored attractions that are within a given map bounds
    // on the map and in the attractions list view
    private void showStoredAttractionsOnMap(LatLngBounds bounds) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                attractions.clear();
                mMap.clear();
                // Loop through the attractions in the database and
                // add markers for the attractions that are within the given bounds
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Attraction attraction = snapshot.getValue(Attraction.class);
                    if (attraction != null) {
                        LatLng latLng = new LatLng(attraction.getLatitude(), attraction.getLongitude());
                        if (bounds.contains(latLng)) {
                            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(attraction.getTitle()));
                            assert marker != null;
                            marker.setTag(attraction.getId()); // Set the marker's tag to the attraction's id
                            attractions.add(attraction);
                        }
                    }
                }

                // Notify the adapter that the data set has changed
                AttractionListAdapter adapter = new AttractionListAdapter(FindNearbyAttractions.this, attractions);
                attractionsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // Set an item click listener for the attractions list view
        // to show the attraction details and reviews when an item is clicked
        attractionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Attraction selectedAttraction = attractions.get(position);
                AttractionMakeReviewRate detailsDialog = new AttractionMakeReviewRate();

                Bundle args = new Bundle();
                args.putString("attractionId", selectedAttraction.getId());
                detailsDialog.setArguments(args);

                detailsDialog.show(getSupportFragmentManager(), "AttractionMakeReviewRate");
            }
        });
    }

    @Override
    public void onCameraIdle() {
        // When the camera stops moving, show the stored attractions that are within the visible map area
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        showStoredAttractionsOnMap(bounds);
    }

    //This method shows the details and reviews for the attraction with the given attractionId in a dialog
    private void showAttractionInfo(String attractionId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.attraction_info, null);
        // Find the views in the layout
        TextView attractionTitleTextView = view.findViewById(R.id.attractionTitleTextView);
        TextView attractionDescriptionTextView = view.findViewById(R.id.attractionDescriptionTextView);
        ImageView attractionImageView = view.findViewById(R.id.attractionImageView);
        TextView websiteTextView = view.findViewById(R.id.websiteTextView);
        LinearLayout reviewsList = view.findViewById(R.id.reviewsList);
        // Get the attraction from the database and populate the views with the attraction details
        DatabaseReference attractionsDatabase = FirebaseDatabase.getInstance().getReference("Attractions");
        attractionsDatabase.child(attractionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Attraction attraction = dataSnapshot.getValue(Attraction.class);
                if (attraction != null) {
                    attractionTitleTextView.setText(attraction.getTitle());
                    attractionDescriptionTextView.setText(attraction.getDescription());
                    if (!TextUtils.isEmpty(attraction.getImageUrl())) {
                        Glide.with(FindNearbyAttractions.this)
                                .load(attraction.getImageUrl())
                                .into(attractionImageView);
                    }
                    websiteTextView.setText(attraction.getWebsite());
                    fetchReviews(attractionId, reviewsList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //This method fetches the reviews for the attraction with the given attractionId
    // from the database and populates the reviews list view
    private void fetchReviews(String attractionId, LinearLayout reviewsList) {
        DatabaseReference reviewsDatabase = FirebaseDatabase.getInstance().getReference("Reviews");
        reviewsDatabase.child(attractionId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LayoutInflater inflater = getLayoutInflater();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    // Inflate the review item layout and populate the views with the review details
                    if (review != null) {
                        View listItemView = inflater.inflate(R.layout.review_item, null, false);
                        TextView reviewAuthorTextView = listItemView.findViewById(R.id.reviewAuthorTextView);
                        TextView reviewTextTextView = listItemView.findViewById(R.id.reviewTextTextView);
                        RatingBar reviewRatingBar = listItemView.findViewById(R.id.reviewRatingBar);

                        reviewAuthorTextView.setText(review.getUserName());
                        reviewTextTextView.setText(review.getReviewText());
                        reviewRatingBar.setRating(review.getRating());
                        // Add the review item to the reviews list view
                        reviewsList.addView(listItemView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
