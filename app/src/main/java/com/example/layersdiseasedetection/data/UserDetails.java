package com.example.layersdiseasedetection.data;

public class UserDetails {
    String username;
    String useremail;
    String userpassword;
    String userCity;
    String userphone;
    String userCategory;
    double latitude; // Add latitude
    double longitude; // Add longitude

    public UserDetails() {
    }

    public UserDetails(String username, String useremail, String userpassword, String userphone, String userCategory, String userCity, double latitude, double longitude) {
        this.username = username;
        this.useremail = useremail;
        this.userpassword = userpassword;
        this.userphone = userphone;
        this.userCategory = userCategory;
        this.userCity = userCity;
        this.latitude = latitude; // Initialize latitude
        this.longitude = longitude; // Initialize longitude
    }

    // Getters and setters for all fields
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(String userCategory) {
        this.userCategory = userCategory;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
