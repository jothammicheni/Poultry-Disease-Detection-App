package com.example.layersdiseasedetection.data;

public class UserDetails {
    String username,useremail,userpassword,userphone;

    public UserDetails() {
    }

    public UserDetails(String username, String useremail, String userpassword, String userphone) {
        this.username = username;
        this.useremail = useremail;
        this.userpassword = userpassword;
        this.userphone = userphone;
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
}
