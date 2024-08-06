package com.example.voyagevision;

import com.google.firebase.database.PropertyName;

public class Review {
    @PropertyName("user_id")
    private String userId;
    @PropertyName("user_name")
    private String userName;
    @PropertyName("attraction_title")
    private String attractionTitle;
    @PropertyName("rating")
    private float rating;
    @PropertyName("review_text")
    private String reviewText;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public Review(String userId, String userName, String attractionTitle, float rating, String reviewText) {
        this.userId = userId;
        this.userName = userName;
        this.attractionTitle = attractionTitle;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    // Getter and setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and setter for userName
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Getter and setter for attractionTitle
    public String getAttractionTitle() {
        return attractionTitle;
    }

    public void setAttractionTitle(String attractionTitle) {
        this.attractionTitle = attractionTitle;
    }

    // Getter and setter for rating
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    // Getter and setter for reviewText
    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}



