/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vetclinic.service;
/**
 *
 * @author Ramil Villamor
 */
import com.vetclinic.model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClinicService {
    private List<Owner> owners = new ArrayList<>();
    private List<Pet> pets = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private List<Bill> bills = new ArrayList<>();
    private List<Reminder> reminders = new ArrayList<>();

    private int ownerSeq = 1;
    private int petSeq = 101;
    private int apptSeq = 1;
    private int queueCounter = 1;
    private int recordSeq = 500;
    private int billSeq = 1000;
    private int reminderSeq = 1;

    public ClinicService() {
        // Sample seed data so you can immediately test "Registered? -> Yes"
        Owner sampleOwner = registerOwner("Juan Dela Cruz", "0917-123-4567", "Quezon City", "juan@email.com");
        registerPet("Bantay", "Dog", "Aspin", LocalDate.of(2023, 5, 10), sampleOwner);
    }

    public Owner registerOwner(String name, String phone, String address, String email) {
        Owner owner = new Owner(ownerSeq++, name, phone, address, email);
        owners.add(owner);
        return owner;
    }

    public Pet registerPet(String name, String species, String breed, LocalDate birthDate, Owner owner) {
        Pet pet = new Pet(petSeq++, name, species, breed, birthDate, owner);
        owner.addPet(pet);
        pets.add(pet);
        return pet;
    }

    public List<Pet> searchPets(String keyword) {
        List<Pet> results = new ArrayList<>();
        String q = keyword.toLowerCase().trim();
        for (Pet p : pets) {
            if (p.getName().toLowerCase().contains(q) || 
                p.getOwner().getName().toLowerCase().contains(q) ||
                String.valueOf(p.getPetId()).equals(q)) {
                results.add(p);
            }
        }
        return results;
    }

    public Appointment bookAppointment(Pet pet) {
        Appointment appt = new Appointment(apptSeq++, LocalDateTime.now(), queueCounter++, pet);
        appointments.add(appt);
        return appt;
    }

    public MedicalRecord logExamination(Appointment appt, double weight, String diagnosis, String treatment, 
                                        boolean vaccineDue, String vaccineName, String batchNo, LocalDate nextDue) {
        MedicalRecord record = new MedicalRecord(recordSeq++, LocalDate.now(), weight, diagnosis, treatment);
        if (vaccineDue && vaccineName != null && !vaccineName.isEmpty()) {
            Vaccination vax = new Vaccination(vaccineName, batchNo, LocalDate.now(), nextDue);
            record.addVaccination(vax);
            reminders.add(new Reminder(reminderSeq++, 
                "Booster Reminder for " + appt.getPet().getName() + ": " + vaccineName + " due on " + nextDue, 
                nextDue));
        }
        appt.getPet().addMedicalRecord(record);
        appt.setStatus("Examined");
        return record;
    }

    public Bill createAndPayBill(Appointment appt, double consultFee, double vaccineFee, double amountPaid) {
        Pet pet = appt.getPet();
        Bill bill = new Bill(billSeq++, pet.getOwner(), pet);
        bill.addItem(new BillItem(1, "Veterinary Consultation", consultFee, 1));
        if (vaccineFee > 0) {
            bill.addItem(new BillItem(2, "Vaccination Administration", vaccineFee, 1));
        }
        bill.recordPayment(amountPaid);
        bills.add(bill);
        appt.setStatus("Completed");

        if (bill.getBalance() > 0) {
            reminders.add(new Reminder(reminderSeq++, 
                String.format("Unpaid Balance Alert for %s: PHP %.2f remaining.", pet.getOwner().getName(), bill.getBalance()), 
                LocalDate.now().plusDays(7)));
        }
        return bill;
    }

    public List<Pet> getAllPets() { return pets; }
    public List<Appointment> getAppointments() { return appointments; }
    public List<Bill> getBills() { return bills; }
    public List<Reminder> getReminders() { return reminders; }
}
