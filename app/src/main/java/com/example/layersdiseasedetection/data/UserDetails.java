package com.example.layersdiseasedetection.data;

public class UserDetails {
    String username;
    String useremail;
    String userpassword;
    String userphone;
    String userCategory; // Updated field name to follow camel case convention

    public UserDetails() {
    }

    public UserDetails(String username, String useremail, String userpassword, String userphone, String userCategory) {
        this.username = username;
        this.useremail = useremail;
        this.userpassword = userpassword;
        this.userphone = userphone;
        this.userCategory = userCategory;
    }

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
}
