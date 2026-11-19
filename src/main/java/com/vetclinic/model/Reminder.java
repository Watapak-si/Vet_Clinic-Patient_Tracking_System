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

public class Reminder {
    private int reminderId;
    private String message;
    private LocalDate sendDate;
    private String status;

    public Reminder(int reminderId, String message, LocalDate sendDate) {
        this.reminderId = reminderId;
        this.message = message;
        this.sendDate = sendDate;
        this.status = "Pending";
    }

    public void markSent() { this.status = "Sent"; }
    public String getMessage() { return message; }
    public LocalDate getSendDate() { return sendDate; }
    public String getStatus() { return status; }
}