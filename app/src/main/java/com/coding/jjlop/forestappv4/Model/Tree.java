package com.coding.jjlop.forestappv4.Model;


import com.google.firebase.database.PropertyName;

public class Tree {
    @PropertyName("id_t")
    private String id_t;
    @PropertyName("name")
    private String name;
    @PropertyName("order")
    private String order;
    @PropertyName("species")
    private String species;
    @PropertyName("value")
    private String value;

    public Tree(){
        //Requiered for FireBase
    }



    public Tree(String id_t, String name, String order, String species, String value) {
        this.id_t = id_t;
        this.name = name;
        this.order = order;
        this.species = species;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId_t() {
        return id_t;
    }

    public void setId_t(String id_t) {
        this.id_t = id_t;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "id_t='" + id_t + '\'' +
                ", name='" + name + '\'' +
                ", order='" + order + '\'' +
                ", species='" + species + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}