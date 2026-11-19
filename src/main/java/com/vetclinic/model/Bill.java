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

public class Bill {
    private int billId;
    private LocalDate dateIssued;
    private double totalAmount;
    private double amountPaid;
    private double balance;
    private String status; // Paid in Full, Partial / Unpaid Balance
    private Owner owner;
    private Pet pet;
    private List<BillItem> items = new ArrayList<>();

    public Bill(int billId, Owner owner, Pet pet) {
        this.billId = billId;
        this.dateIssued = LocalDate.now();
        this.owner = owner;
        this.pet = pet;
        this.status = "Unpaid";
    }

    public void addItem(BillItem item) {
        items.add(item);
        totalAmount += item.getSubtotal();
        computeBalance();
    }

    public void computeBalance() {
        this.balance = totalAmount - amountPaid;
        this.status = (this.balance <= 0) ? "Paid in Full" : "Unpaid Balance";
    }

    public void recordPayment(double payment) {
        this.amountPaid += payment;
        computeBalance();
    }

    public String issueReceipt() {
        return "=== OFFICIAL CLINIC RECEIPT ===\n"
             + "Bill ID: #" + billId + " | Date: " + dateIssued + "\n"
             + "Owner: " + owner.getName() + " | Pet: " + pet.getName() + "\n"
             + String.format("Total Amount: PHP %.2f\n", totalAmount)
             + String.format("Amount Paid:  PHP %.2f\n", amountPaid)
             + String.format("Balance Due:  PHP %.2f\n", balance)
             + "Payment Status: " + status + "\n"
             + "===============================";
    }

    public int getBillId() { return billId; }
    public double getTotalAmount() { return totalAmount; }
    public double getAmountPaid() { return amountPaid; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
    public Owner getOwner() { return owner; }
    public Pet getPet() { return pet; }
}