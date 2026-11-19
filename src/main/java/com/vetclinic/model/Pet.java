/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.vetclinic.model;
/**
 *
 * @author Ramil Villamor
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pet {
    private int petId;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    private Owner owner;
    private List<MedicalRecord> history = new ArrayList<>();

    public Pet(int petId, String name, String species, String breed, LocalDate birthDate, Owner owner) {
        this.petId = petId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
        this.owner = owner;
    }

    public void addMedicalRecord(MedicalRecord record) {
        history.add(record);
    }

    public List<MedicalRecord> getHistory() { return history; }
    public int getPetId() { return petId; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getBreed() { return breed; }
    public Owner getOwner() { return owner; }

    @Override
    public String toString() {
        return "Pet #" + petId + ": " + name + " (" + species + " - " + breed + ") | Owner: " + owner.getName();
    }
}