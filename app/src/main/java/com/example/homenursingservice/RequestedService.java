package com.example.homenursingservice;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

public class RequestedService {
    public String service_id;
    public String patient_name;
    public String user_id;
    public String phone_number;
    public String service_type;
    public String service_charge;
    public String location_name;
    public GeoPoint location_geopoint;
    public String patient_age;
    public String date_time;
    public String create_at;
    public String doctor_id;
    public String doctor_name;
    public String doctor_image_path;
    public String image_path;
    public String doctor_phone_number;
    public String status;
    public float payment;

    public RequestedService(String service_id, String patient_name, String user_id, String phone_number, String service_type, String service_charge, String location_name, GeoPoint location_geopoint, String patient_age, String date_time, String create_at, String doctor_id, String doctor_name, String image_path, String doctor_phone_number,String doctor_image_path,String status, float payment) {
        this.service_id = service_id;
        this.patient_name = patient_name;
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.service_type = service_type;
        this.service_charge = service_charge;
        this.location_name = location_name;
        this.location_geopoint = location_geopoint;
        this.patient_age = patient_age;
        this.date_time = date_time;
        this.create_at = create_at;
        this.doctor_id = doctor_id;
        this.doctor_name = doctor_name;
        this.doctor_image_path = doctor_image_path;
        this.doctor_phone_number = doctor_phone_number;
        this.status = status;
        this.payment = payment;
        this.image_path=image_path;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getPatient_anme() {
        return patient_name;
    }

    public void setPatient_anme(String patient_anme) {
        this.patient_name = patient_anme;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getService_charge() {
        return service_charge;
    }

    public void setService_charge(String service_charge) {
        this.service_charge = service_charge;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public GeoPoint getLocation_geopoint() {
        return location_geopoint;
    }

    public void setLocation_geopoint(GeoPoint location_geopoint) {
        this.location_geopoint = location_geopoint;
    }

    public String getPatient_age() {
        return patient_age;
    }

    public void setPatient_age(String patient_age) {
        this.patient_age = patient_age;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDoctor_image_path() {
        return doctor_image_path;
    }

    public void setDoctor_image_path(String doctor_image_path) {
        this.doctor_image_path = doctor_image_path;
    }

    public String getDoctor_phone_number() {
        return doctor_phone_number;
    }

    public void setDoctor_phone_number(String doctor_phone_number) {
        this.doctor_phone_number = doctor_phone_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getPayment() {
        return payment;
    }

    public void setPayment(float payment) {
        this.payment = payment;
    }
}
