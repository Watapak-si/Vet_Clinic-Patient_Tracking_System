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

public class MedicalRecord {
    private int recordId;
    private LocalDate visitDate;
    private double weight;
    private String diagnosis;
    private String treatment;
    private List<Vaccination> vaccinations = new ArrayList<>();

    public MedicalRecord(int recordId, LocalDate visitDate, double weight, String diagnosis, String treatment) {
        this.recordId = recordId;
        this.visitDate = visitDate;
        this.weight = weight;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    public void addVaccination(Vaccination v) { vaccinations.add(v); }
    public int getRecordId() { return recordId; }
    public LocalDate getVisitDate() { return visitDate; }
    public String getDiagnosis() { return diagnosis; }
    public String getTreatment() { return treatment; }
    public List<Vaccination> getVaccinations() { return vaccinations; }
}