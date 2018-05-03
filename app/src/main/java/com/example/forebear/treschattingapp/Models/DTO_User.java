package com.example.forebear.treschattingapp.Models;


/**
 * Created by forebear on 20/2/18.
 */

public class DTO_User {

    String Id;
    String name;
    String number;
    String image;
    String active_mage;

    public DTO_User(String Id,String name, String number, String image, String active_mage) {
        this.Id = Id;
        this.name = name;
        this.number = number;
        this.image = image;
        this.active_mage = active_mage;
    }

    public DTO_User(String id, String name, String number, String image) {
        Id = id;
        this.name = name;
        this.number = number;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getId() {
        return Id;
    }

    public String getActive_mage() {
        return active_mage;
    }
}
