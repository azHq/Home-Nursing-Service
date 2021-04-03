package com.example.homenursingservice;

public class ServiceModel {
    public String id;
    public String name;
    public String price;
    public String details;
    public int logo;
    public boolean nameEnable;
    public  ServiceModel(String id,String name,String price,String details,int logo,boolean nameEnable){
        this.id=id;
        this.name=name;
        this.price=price;
        this.details=details;
        this.logo=logo;
        this.nameEnable=nameEnable;
    }
}
