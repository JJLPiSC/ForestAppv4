package com.coding.jjlop.forestappv4.Model;

public class Planted {
    private String id_at;
    private String lat;
    private String lng;
    private String d_plant;
    private String type;

    public Planted(String id_at, String lat, String lng, String d_plant, String type) {
        this.id_at = id_at;
        this.lat = lat;
        this.lng = lng;
        this.d_plant = d_plant;
        this.type = type;
    }

    public String getId_at() {
        return id_at;
    }

    public void setId_at(String id_at) {
        this.id_at = id_at;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getD_plant() {
        return d_plant;
    }

    public void setD_plant(String d_plant) {
        this.d_plant = d_plant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
