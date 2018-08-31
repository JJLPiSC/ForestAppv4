package com.coding.jjlop.forestappv4.Model;

import com.google.firebase.database.PropertyName;

public class Planted {
    @PropertyName("id_at")
    private String id_at;
    @PropertyName("lat")
    private String lat;
    @PropertyName("lng")
    private String lng;
    @PropertyName("d_plant")
    private String d_plant;
    @PropertyName("type")
    private String type;
    @PropertyName("alias")
    private String alias;

    public Planted(String id_at, String lat, String lng, String d_plant, String type, String alias) {
        this.id_at = id_at;
        this.lat = lat;
        this.lng = lng;
        this.d_plant = d_plant;
        this.type = type;
        this.alias = alias;
    }

    public Planted(String id_at){
        this.id_at = id_at;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
