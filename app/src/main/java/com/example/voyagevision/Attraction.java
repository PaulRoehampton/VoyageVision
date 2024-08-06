package com.example.voyagevision;

import java.util.HashMap;
import java.util.Map;

public class Attraction {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String website;
    private double latitude;
    private double longitude;
    private String userId;

    public Attraction() {
    }

    public Attraction(String id, String title, String description, String imageUrl, String website, double latitude, double longitude, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.website = website;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getWebsite() {
        return website;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public String getUserId() {
        return userId;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("description", description);
        result.put("imageUrl", imageUrl);
        result.put("website", website);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("userId", userId);
        return result;
    }
}
