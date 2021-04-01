package com.example.homenursingservice;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

public class User {

    public String user_id;
    public String user_name;
    public String user_type;
    public String phone_number;
    public String image_path;
    public String device_id;
    public GeoPoint geoPoint;
    public String account_status;
    public String online_status;
    public FieldValue create_at;
    public boolean notification;
    public String getDevice_id() {
        return device_id;
    }
    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
    public User(String user_id, String user_name, String user_type, String phone_number, String image_path,String device_id,String account_status,String online_status,boolean notification) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_type = user_type;
        this.phone_number = phone_number;
        this.image_path = image_path;
        this.device_id=device_id;
        this.account_status=account_status;
        this.online_status=online_status;
        this.notification=notification;
    }
    public User(String user_id, String user_name, String user_type, String phone_number, String image_path,String device_id, GeoPoint geoPoint,String account_status,String online_status,boolean notification) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_type = user_type;
        this.phone_number = phone_number;
        this.image_path = image_path;
        this.device_id=device_id;
        this.geoPoint=geoPoint;
        this.account_status=account_status;
        this.online_status=online_status;
        this.notification=notification;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public FieldValue getCreate_at() {
        return create_at;
    }

    public void setCreate_at(FieldValue create_at) {
        this.create_at = create_at;
    }
}
