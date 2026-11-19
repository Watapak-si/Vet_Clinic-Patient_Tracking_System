/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vetclinic.model;
/**
 *
 * @author Ramil Villamor
 */
import java.time.LocalDateTime;

public class Appointment {
    private int appointmentId;
    private LocalDateTime dateTime;
    private int queueNo;
    private String status; // Booked, Checked-In, Completed, Cancelled
    private Pet pet;

    public Appointment(int appointmentId, LocalDateTime dateTime, int queueNo, Pet pet) {
        this.appointmentId = appointmentId;
        this.dateTime = dateTime;
        this.queueNo = queueNo;
        this.pet = pet;
        this.status = "Booked";
    }

    public void cancel() { this.status = "Cancelled"; }
    public void setStatus(String status) { this.status = status; }
    public int getAppointmentId() { return appointmentId; }
    public int getQueueNo() { return queueNo; }
    public String getStatus() { return status; }
    public Pet getPet() { return pet; }
    public LocalDateTime getDateTime() { return dateTime; }
}