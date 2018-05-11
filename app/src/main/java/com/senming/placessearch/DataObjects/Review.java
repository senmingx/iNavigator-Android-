package com.senming.placessearch.DataObjects;

public class Review {

    private String authorName;
    private String authorUrl;
    private String profilePhotoUrl;
    private int rating;
    private String time;
    private String text;
    private int originalIndex;

    public Review(String authorName, String authorUrl, String profilePhotoUrl, int rating, String time,
                  String text, int originalIndex) {
        this.authorName = authorName;
        this.authorUrl = authorUrl;
        this.profilePhotoUrl = profilePhotoUrl;
        this.rating = rating;
        this.time = time;
        this.text = text;
        this.originalIndex = originalIndex;

    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOriginalIndex() {
        return originalIndex;
    }

    public void setOriginalIndex(int originalIndex) {
        this.originalIndex = originalIndex;
    }
}
