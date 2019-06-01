package com.example.myplaces.data;

public class MyPlace {
    public String name, description, latitude, longitude;
    public String key;

    public MyPlace() {}

    public MyPlace(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public MyPlace(String name, String description, String latitude, String longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyPlace(String name) {
        this(name, "");
    }

//    public int getId() {
//        return this.id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return this.name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getDescription() {
//        return this.description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getLatitude() {
//        return this.latitude;
//    }
//
//    public void setLatitude(String latitude) {
//        this.latitude = latitude;
//    }
//
//    public String getLongitude() {
//        return this.longitude;
//    }
//
//    public void setLongitude(String longitude) {
//        this.longitude = longitude;
//    }

    @Override
    public String toString(){
        return this.name;
    }
}
