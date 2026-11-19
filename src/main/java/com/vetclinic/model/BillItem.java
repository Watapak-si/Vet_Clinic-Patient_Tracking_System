/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vetclinic.model;
/**
 *
 * @author Ramil Villamor
 */
public class BillItem {
    private int itemId;
    private String description;
    private double unitPrice;
    private int quantity;
    private double subtotal;

    public BillItem(int itemId, String description, double unitPrice, int quantity) {
        this.itemId = itemId;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    public double calculateSubtotal() {
        return unitPrice * quantity;
    }

    public String getDescription() { return description; }
    public double getSubtotal() { return subtotal; }
}