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

public class Vaccination {
    private String vaccineName;
    private String batchNo;
    private LocalDate dateGiven;
    private LocalDate nextDueDate;

    public Vaccination(String vaccineName, String batchNo, LocalDate dateGiven, LocalDate nextDueDate) {
        this.vaccineName = vaccineName;
        this.batchNo = batchNo;
        this.dateGiven = dateGiven;
        this.nextDueDate = nextDueDate;
    }

    public boolean isDue() {
        return !LocalDate.now().isBefore(nextDueDate);
    }

    public String getVaccineName() { return vaccineName; }
    public LocalDate getNextDueDate() { return nextDueDate; }
}