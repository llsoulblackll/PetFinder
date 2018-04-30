package com.petfinder.dataaccess.entity;

public class Pet {

    private int id;
    private String name;
    private int age;
    private String description;
    private double lostLat;
    private double lostLon;
    private String race;
    private String size;
    private String imageBase64;
    private String imagePath;
    private String imageExtension;

    public Pet() {
    }

    public Pet(int id, String name, int age, String description, double lostLat, double lostLon, String race, String size) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.description = description;
        this.lostLat = lostLat;
        this.lostLon = lostLon;
        this.race = race;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLostLat() {
        return lostLat;
    }

    public void setLostLat(double lostLat) {
        this.lostLat = lostLat;
    }

    public double getLostLon() {
        return lostLon;
    }

    public void setLostLon(double lostLon) {
        this.lostLon = lostLon;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public void setImageExtension(String imageExtension) {
        this.imageExtension = imageExtension;
    }

}
