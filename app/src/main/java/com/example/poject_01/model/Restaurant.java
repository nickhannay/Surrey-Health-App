package com.example.poject_01.model;


/**
 * This class stores all related restaurant data
 * Holds all related inspection data in inspection list - implemented with arraylist
 */

public class Restaurant implements Comparable<Restaurant> {
    private String trackingNum;
    private String name;
    private String address;
    private String city;
    private String type;
    private double latitude;
    private double longitude;
    private InspectionList inspections;
    private boolean favourite;
    private int criticalHazardYear;



    public Restaurant(String trackingNum, String name, String address, String city, String type, double latitude, double longitude, boolean favourite) {
        this.trackingNum = trackingNum;
        this.name = name;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.favourite = favourite;
        this.inspections = new InspectionList();
        this.criticalHazardYear = 0;
    }

    public int getCriticalHazardYear() {
        return criticalHazardYear;
    }

    public void addCriticalHazardYear(int numCritical) {
        this.criticalHazardYear += numCritical ;
    }


    public boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getCity() {
        return city;
    }

    public void addInspection(Inspection inspection) {
        inspections.addInspection(inspection);
        inspections.sort();
    }

    public int numInspections(){
        return(inspections.getNumInspections());
    }

    public InspectionList getInspections() {
        return inspections;
    }

    public String getTrackingNum() {
        return trackingNum;
    }

    public String getName() {
        return name;
    }

    public Inspection getLatestInspection(){
        return (inspections.getInspectionIndex(0));

    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int compareTo(Restaurant r) {
        return this.name.compareTo(r.name);
    }


    @Override
    public String toString() {
        return "Restaurant{" +
                "trackingNum='" + trackingNum + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", type='" + type + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

}
