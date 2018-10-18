package com.kaiann.fypdealsmatchapp.Model;

public class Item {
    private String Name, Image, Description, Location, MenuId;

    public Item(){

    }

    public Item(String name, String image, String description, String location, String menuId) {
        Name = name;
        Image = image;
        Description = description;
        Location = location;
        MenuId = menuId;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }
}
