package com.vetclinic.model;

import java.util.ArrayList;
import java.util.List;

public class Owner {
    private int ownerId;
    private String name;
    private String phone;
    private String address;
    private String email;
    private List<Pet> pets = new ArrayList<>();

    public Owner(int ownerId, String name, String phone, String address, String email) {
        this.ownerId = ownerId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
    }

    public void updateInfo(String phone, String address, String email) {
        this.phone = phone;
        this.address = address;
        this.email = email;
    }

    public void addPet(Pet pet) { pets.add(pet); }
    public int getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public List<Pet> getPets() { return pets; }

    @Override
    public String toString() {
        return ownerId + " - " + name + " (" + phone + ")";
    }
}