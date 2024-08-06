package com.example.voyagevision;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class AddNewAttraction extends AppCompatActivity implements OnMapReadyCallback {

    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private static final int REQUEST_IMAGE_CAPTURE = 104;



    private EditText attractionTitleEditText, attractionDescriptionEditText, attractionWebsiteEditText;
    private ImageView attractionImageView;
    private Button selectImageButton, takePhotoButton, addAttractionButton;
    private Uri imageUri;
    private LatLng attractionLocation;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private GoogleMap googleMap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_attraction);

        // Initialize Views
        attractionTitleEditText = findViewById(R.id.attractionTitleEditText);
        attractionDescriptionEditText = findViewById(R.id.attractionDescriptionEditText);
        attractionWebsiteEditText = findViewById(R.id.attractionWebsiteEditText);
        attractionImageView = findViewById(R.id.attractionImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        addAttractionButton = findViewById(R.id.addAttractionButton);

        // Initialize Firebase components
        databaseReference = FirebaseDatabase.getInstance().getReference("Attractions");
        storageReference = FirebaseStorage.getInstance().getReference("AttractionImages");

        // Initialize Google Map
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapFragmentContainer, mapFragment).commit();

        mapFragment.getMapAsync(this);

        // Set listeners
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        addAttractionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAttraction();
            }
        });
    }

    // Method for selecting an image from the phone's gallery
    private void pickImageFromGallery() {
        // Check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
        } else {
            // Start the gallery intent
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
        }
    }

    // Method for taking a photo using the camera
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    // Method for handling the result of runtime permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        }
    }

    // Method for handling the result of startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                attractionImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            attractionImageView.setImageBitmap(imageBitmap);
            imageUri = getImageUri(getApplicationContext(), imageBitmap);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Set the default location to the UK region
        LatLng ukLatLng = new LatLng(53.4808, -2.2426); // Latitude and longitude for a central point in the UK
        float zoomLevel = 5.0f; // Set an appropriate zoom level
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ukLatLng, zoomLevel));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                attractionLocation = latLng;
                // Remove previous marker if any
                googleMap.clear();
                // Add marker to the map
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Attraction Location");
                googleMap.addMarker(markerOptions);
            }
        });
    }

    // Method for adding a new attraction
    private void addAttraction() {
        String title = attractionTitleEditText.getText().toString().trim();
        String description = attractionDescriptionEditText.getText().toString().trim();
        String website = attractionWebsiteEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            attractionTitleEditText.setError("Title is required.");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            attractionDescriptionEditText.setError("Description is required.");
            return;
        }

        if (attractionLocation == null) {
            Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image or take a photo.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Declare variables as final
        final String attractionId = databaseReference.push().getKey();
        final AtomicReference<String> imageUrl = new AtomicReference<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Upload the image and save the attraction data
        StorageReference filepath = storageReference.child(imageUri.getLastPathSegment());
        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl.set(uri.toString());
                        final Attraction attraction = new Attraction(attractionId, title, description, imageUrl.get(), website, attractionLocation.latitude, attractionLocation.longitude,userId);

                        databaseReference.child(attractionId).setValue(attraction).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddNewAttraction.this, "New Attraction added successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddNewAttraction.this, "Failed to add Attraction: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNewAttraction.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
