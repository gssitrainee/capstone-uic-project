package com.thesis.project.model;

import com.google.gson.Gson;
import org.bson.Document;

import java.util.Date;

public class User {

/*
    {
        "_id" : "jpagduma",
        "password" : "BSnvv73vv70+Pe+/ve+/vUDvv71q77+9Z1Pvv73vv70=,-1290928513",
        "email" : "jpagduma@gmail.com",
        "userType" : "S"
    }
*/

    private String _id;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String userType;


    private boolean isActive;
    private Date lastModifiedDate;


    public User(){}
    public User(Document dUser){
        if(null!=dUser){
            if(null!=dUser.getString("_id"))       this._id = dUser.getString("_id");
            if(null!=dUser.getString("password"))  this.password = dUser.getString("password");
            if(null!=dUser.getString("firstName")) this.firstName = dUser.getString("firstName");
            if(null!=dUser.getString("lastName"))  this.lastName = dUser.getString("lastName");
            if(null!=dUser.getString("email"))     this.email = dUser.getString("email");
            if(null!=dUser.getString("userType"))  this.userType = dUser.getString("userType");
            if(null!=dUser.getBoolean("isActive")) this.isActive = dUser.getBoolean("isActive");
        }
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean validateUser(){
        if(null!=_id && null!=password && null!=firstName && null!=lastName && null!=email && null!=userType) return true;
        return false;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Document toDocument(){
        Document doc = new Document();

        doc.append("_id", _id);
        doc.append("password", password);
        doc.append("firstName", firstName);
        doc.append("lastName", lastName);
        doc.append("email", email);
        doc.append("userType", userType);
        doc.append("isActive", isActive);

        return doc;
    }
}
