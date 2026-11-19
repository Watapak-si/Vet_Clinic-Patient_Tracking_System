/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vetclinic.ui;
/**
 *
 * @author Ramil Villamor
 */
import com.vetclinic.model.*;
import com.vetclinic.service.ClinicService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MainClinicFrame extends JFrame {
    private final ClinicService service = new ClinicService();

    // Tab 1: Front Desk (Search, Register, Book Queue)
    private JTextField txtSearch, txtOwnerName, txtPhone, txtAddress, txtEmail;
    private JTextField txtPetName, txtSpecies, txtBreed, txtBirthDate;
    private JComboBox<Pet> cmbSelectPet;
    private DefaultTableModel queueTableModel;

    // Tab 2: Veterinarian (Examine, Diagnosis, Vaccine Decision)
    private JComboBox<String> cmbActiveAppt;
    private JTextField txtWeight, txtDiagnosis, txtTreatment;
    private JCheckBox chkVaccineDue;
    private JTextField txtVaccineName, txtBatchNo, txtNextDueDate;
    private JTextArea txtHistoryLog;

    // Tab 3: Billing & Reminders
    private JComboBox<String> cmbBillAppt;
    private JTextField txtConsultFee, txtVaccineFee, txtAmountPaid;
    private JTextArea txtReceiptArea;
    private DefaultTableModel reminderTableModel;

    public MainClinicFrame() {
        setTitle("Veterinary Clinic Patient Record & Appointment Tracking System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("1. Front Desk & Queue", createFrontDeskPanel());
        tabbedPane.addTab("2. Veterinarian & Records", createVetPanel());
        tabbedPane.addTab("3. Billing & Reminders", createBillingPanel());

        add(tabbedPane);
        refreshAllDropdownsAndTables();
    }

    private JPanel createFrontDeskPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: Search Record
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Step 1: Find Existing Record"));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search Pet / Owner");
        JButton btnShowAll = new JButton("Show All");
        searchPanel.add(new JLabel("Keyword:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);

        btnSearch.addActionListener(e -> {
            List<Pet> found = service.searchPets(txtSearch.getText());
            cmbSelectPet.removeAllItems();
            for (Pet p : found) cmbSelectPet.addItem(p);
            JOptionPane.showMessageDialog(this, "Found " + found.size() + " matching pet record(s).");
        });

        btnShowAll.addActionListener(e -> refreshAllDropdownsAndTables());

        // Center: Split between Register New Pet & Book Appointment
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Left: Register Owner & Pet
        JPanel regPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        regPanel.setBorder(BorderFactory.createTitledBorder("Not Registered? Register Owner & Pet"));
        txtOwnerName = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextField();
        txtEmail = new JTextField();
        txtPetName = new JTextField();
        txtSpecies = new JTextField();
        txtBreed = new JTextField();
        txtBirthDate = new JTextField("2024-01-15");
        JButton btnRegister = new JButton("Register Pet & Owner");

        regPanel.add(new JLabel("Owner Name:")); regPanel.add(txtOwnerName);
        regPanel.add(new JLabel("Phone:")); regPanel.add(txtPhone);
        regPanel.add(new JLabel("Address:")); regPanel.add(txtAddress);
        regPanel.add(new JLabel("Email:")); regPanel.add(txtEmail);
        regPanel.add(new JLabel("Pet Name:")); regPanel.add(txtPetName);
        regPanel.add(new JLabel("Species (Dog/Cat):")); regPanel.add(txtSpecies);
        regPanel.add(new JLabel("Breed:")); regPanel.add(txtBreed);
        regPanel.add(new JLabel("Birth Date (YYYY-MM-DD):")); regPanel.add(txtBirthDate);
        regPanel.add(new JLabel("")); regPanel.add(btnRegister);

        btnRegister.addActionListener(e -> {
            try {
                Owner o = service.registerOwner(txtOwnerName.getText(), txtPhone.getText(), txtAddress.getText(), txtEmail.getText());
                Pet p = service.registerPet(txtPetName.getText(), txtSpecies.getText(), txtBreed.getText(), LocalDate.parse(txtBirthDate.getText()), o);
                refreshAllDropdownsAndTables();
                cmbSelectPet.setSelectedItem(p);
                JOptionPane.showMessageDialog(this, "Registered: " + p.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Check date format (YYYY-MM-DD).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Right: Book Appointment & Queue Table
        JPanel queuePanel = new JPanel(new BorderLayout(5, 5));
        queuePanel.setBorder(BorderFactory.createTitledBorder("Book Slot & Live Clinic Queue"));

        JPanel bookTop = new JPanel(new BorderLayout(5, 5));
        cmbSelectPet = new JComboBox<>();
        JButton btnBook = new JButton("Book Slot & Assign Queue #");
        bookTop.add(cmbSelectPet, BorderLayout.CENTER);
        bookTop.add(btnBook, BorderLayout.SOUTH);

        queueTableModel = new DefaultTableModel(new String[]{"Queue #", "Appt ID", "Pet", "Owner", "Status"}, 0);
        JTable queueTable = new JTable(queueTableModel);
        JButton btnCheckIn = new JButton("Check In Selected Pet");

        btnBook.addActionListener(e -> {
            Pet selected = (Pet) cmbSelectPet.getSelectedItem();
            if (selected != null) {
                Appointment appt = service.bookAppointment(selected);
                refreshAllDropdownsAndTables();
                JOptionPane.showMessageDialog(this, "Booked! Queue Number: #" + appt.getQueueNo());
            }
        });

        btnCheckIn.addActionListener(e -> {
            int row = queueTable.getSelectedRow();
            if (row >= 0) {
                service.getAppointments().get(row).setStatus("Checked-In");
                refreshAllDropdownsAndTables();
            }
        });

        queuePanel.add(bookTop, BorderLayout.NORTH);
        queuePanel.add(new JScrollPane(queueTable), BorderLayout.CENTER);
        queuePanel.add(btnCheckIn, BorderLayout.SOUTH);

        centerPanel.add(regPanel);
        centerPanel.add(queuePanel);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createVetPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Examine Pet & Log Medical Record"));

        cmbActiveAppt = new JComboBox<>();
        txtWeight = new JTextField("12.5");
        txtDiagnosis = new JTextField();
        txtTreatment = new JTextField();
        chkVaccineDue = new JCheckBox("Vaccine Due? (Yes -> Give Vaccine)");
        txtVaccineName = new JTextField("Anti-Rabies Booster");
        txtBatchNo = new JTextField("BATCH-2026A");
        txtNextDueDate = new JTextField(LocalDate.now().plusYears(1).toString());
        JButton btnSaveExam = new JButton("Save Diagnosis & Update History");

        formPanel.add(new JLabel("Select Appointment:")); formPanel.add(cmbActiveAppt);
        formPanel.add(new JLabel("Weight (kg):")); formPanel.add(txtWeight);
        formPanel.add(new JLabel("Diagnosis:")); formPanel.add(txtDiagnosis);
        formPanel.add(new JLabel("Treatment:")); formPanel.add(txtTreatment);
        formPanel.add(new JLabel("Vaccination Decision:")); formPanel.add(chkVaccineDue);
        formPanel.add(new JLabel("Vaccine Name:")); formPanel.add(txtVaccineName);
        formPanel.add(new JLabel("Batch No:")); formPanel.add(txtBatchNo);
        formPanel.add(new JLabel("Next Booster Due Date:")); formPanel.add(txtNextDueDate);
        formPanel.add(new JLabel("")); formPanel.add(btnSaveExam);

        txtHistoryLog = new JTextArea();
        txtHistoryLog.setEditable(false);
        JScrollPane historyScroll = new JScrollPane(txtHistoryLog);
        historyScroll.setBorder(BorderFactory.createTitledBorder("Updated Medical & Vaccination History"));

        btnSaveExam.addActionListener(e -> {
            int idx = cmbActiveAppt.getSelectedIndex();
            if (idx >= 0) {
                Appointment appt = service.getAppointments().get(idx);
                service.logExamination(
                        appt,
                        Double.parseDouble(txtWeight.getText()),
                        txtDiagnosis.getText(),
                        txtTreatment.getText(),
                        chkVaccineDue.isSelected(),
                        txtVaccineName.getText(),
                        txtBatchNo.getText(),
                        LocalDate.parse(txtNextDueDate.getText())
                );
                refreshAllDropdownsAndTables();
                displayPetHistory(appt.getPet());
                JOptionPane.showMessageDialog(this, "Medical record updated for " + appt.getPet().getName());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(historyScroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: Process Payment & Receipt
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        JPanel billForm = new JPanel(new GridLayout(5, 2, 5, 5));
        billForm.setBorder(BorderFactory.createTitledBorder("Process Payment & Balance Check"));

        cmbBillAppt = new JComboBox<>();
        txtConsultFee = new JTextField("500.00");
        txtVaccineFee = new JTextField("800.00");
        txtAmountPaid = new JTextField("1300.00");
        JButton btnPay = new JButton("Take Payment & Issue Receipt");

        billForm.add(new JLabel("Appointment:")); billForm.add(cmbBillAppt);
        billForm.add(new JLabel("Consultation Fee (PHP):")); billForm.add(txtConsultFee);
        billForm.add(new JLabel("Vaccine Fee (PHP):")); billForm.add(txtVaccineFee);
        billForm.add(new JLabel("Amount Paid (PHP):")); billForm.add(txtAmountPaid);
        billForm.add(new JLabel("")); billForm.add(btnPay);

        txtReceiptArea = new JTextArea();
        txtReceiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceiptArea.setEditable(false);

        btnPay.addActionListener(e -> {
            int idx = cmbBillAppt.getSelectedIndex();
            if (idx >= 0) {
                Appointment appt = service.getAppointments().get(idx);
                Bill bill = service.createAndPayBill(
                        appt,
                        Double.parseDouble(txtConsultFee.getText()),
                        Double.parseDouble(txtVaccineFee.getText()),
                        Double.parseDouble(txtAmountPaid.getText())
                );
                txtReceiptArea.setText(bill.issueReceipt());
                refreshAllDropdownsAndTables();
            }
        });

        leftPanel.add(billForm, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(txtReceiptArea), BorderLayout.CENTER);

        // Right: Automated System Reminders
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Automated System Reminders (Boosters & Balances)"));
        reminderTableModel = new DefaultTableModel(new String[]{"Target Date", "Status", "Reminder Message"}, 0);
        JTable reminderTable = new JTable(reminderTableModel);
        rightPanel.add(new JScrollPane(reminderTable), BorderLayout.CENTER);

        panel.add(leftPanel);
        panel.add(rightPanel);
        return panel;
    }

    private void displayPetHistory(Pet pet) {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient: ").append(pet.getName()).append(" | Owner: ").append(pet.getOwner().getName()).append("\n");
        sb.append("------------------------------------------------------------\n");
        for (MedicalRecord r : pet.getHistory()) {
            sb.append("Visit Date: ").append(r.getVisitDate())
              .append(" | Diagnosis: ").append(r.getDiagnosis())
              .append(" | Treatment: ").append(r.getTreatment()).append("\n");
            for (Vaccination v : r.getVaccinations()) {
                sb.append("   -> Vaccine Given: ").append(v.getVaccineName())
                  .append(" | Next Due: ").append(v.getNextDueDate()).append("\n");
            }
        }
        txtHistoryLog.setText(sb.toString());
    }

    private void refreshAllDropdownsAndTables() {
        cmbSelectPet.removeAllItems();
        for (Pet p : service.getAllPets()) cmbSelectPet.addItem(p);

        queueTableModel.setRowCount(0);
        cmbActiveAppt.removeAllItems();
        cmbBillAppt.removeAllItems();

        for (Appointment a : service.getAppointments()) {
            queueTableModel.addRow(new Object[]{
                    "Q-" + a.getQueueNo(), a.getAppointmentId(), a.getPet().getName(), a.getPet().getOwner().getName(), a.getStatus()
            });
            String label = "Q-" + a.getQueueNo() + ": " + a.getPet().getName() + " (" + a.getStatus() + ")";
            cmbActiveAppt.addItem(label);
            cmbBillAppt.addItem(label);
        }

        reminderTableModel.setRowCount(0);
        for (Reminder rem : service.getReminders()) {
            reminderTableModel.addRow(new Object[]{rem.getSendDate(), rem.getStatus(), rem.getMessage()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainClinicFrame().setVisible(true));
    }
}