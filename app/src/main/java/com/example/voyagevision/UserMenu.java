package com.example.voyagevision;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserMenu extends AppCompatActivity {

    private ListView userMenuOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        userMenuOptions = findViewById(R.id.userMenuOptions);

        String[] menuItems = {"Delete Attraction", "Your Reviews and Ratings", "Logout"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.white_text_list_item, menuItems);
        userMenuOptions.setAdapter(adapter);

        userMenuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Handle Delete Attraction
                        Intent deleteAttractionIntent = new Intent(UserMenu.this, DeleteAttraction.class);
                        startActivity(deleteAttractionIntent);
                        break;
                    case 1:
                        // Handle Reviews and Ratings
                        Intent reviewsAndRatingsIntent = new Intent(UserMenu.this, ReviewsAndRatings.class);
                        startActivity(reviewsAndRatingsIntent);
                        break;
                    case 2:
                        // Handle Logout
                        Intent logoutIntent = new Intent(UserMenu.this, Login.class);
                        startActivity(logoutIntent);
                        finish();
                        break;
                }
            }
        });
    }
}