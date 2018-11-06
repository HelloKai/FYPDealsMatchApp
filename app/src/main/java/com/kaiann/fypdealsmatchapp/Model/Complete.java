package com.kaiann.fypdealsmatchapp.Model;

public class Complete {
    private String name;
    private String phone;
    private String partner;


    public Complete(){

    }

    public Complete(String name, String phone, String partner) {
        this.name = name;
        this.phone = phone;
        this.partner = partner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }
}
