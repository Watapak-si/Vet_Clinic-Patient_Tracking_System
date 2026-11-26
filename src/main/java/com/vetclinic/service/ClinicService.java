package com.vetclinic.ui;

import com.vetclinic.model.*;
import com.vetclinic.service.ClinicService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MainClinicFrame extends JFrame {
    private final ClinicService service = new ClinicService();

    // Top-level Role Switcher (Client vs. Clinic & Admin)
    private JTabbedPane mainRoleTabs;

    // --- CLIENT PORTAL (PET OWNER) COMPONENTS ---
    private JComboBox<Pet> cmbClientPet;
    private JTextArea txtClientPetHistory;
    private DefaultTableModel clientApptTableModel;
    private DefaultTableModel clientReminderTableModel;

    // --- CLINIC & ADMIN PORTAL COMPONENTS ---
    // Sub-tab 1: Front Desk (Search, Register, Book Queue)
    private JTextField txtSearch, txtOwnerName, txtPhone, txtAddress, txtEmail;
    private JTextField txtPetName, txtSpecies, txtBreed, txtBirthDate;
    private JComboBox<Pet> cmbSelectPet;
    private DefaultTableModel queueTableModel;

    // Sub-tab 2: Veterinarian (Examine, Diagnosis, Vaccine Decision)
    private JComboBox<String> cmbActiveAppt;
    private JTextField txtWeight, txtDiagnosis, txtTreatment;
    private JCheckBox chkVaccineDue;
    private JTextField txtVaccineName, txtBatchNo, txtNextDueDate;
    private JTextArea txtHistoryLog;

    // Sub-tab 3: Billing & Reminders
    private JComboBox<String> cmbBillAppt;
    private JTextField txtConsultFee, txtVaccineFee, txtAmountPaid;
    private JTextArea txtReceiptArea;
    private DefaultTableModel reminderTableModel;

    // Sub-tab 4: Admin (Manage Services & Generate Reports)
    private JTextField txtDefaultConsultFee, txtDefaultVaccineFee;
    private JTextArea txtAdminReportArea;

    public MainClinicFrame() {
        setTitle("Veterinary Clinic Patient Record & Appointment Tracking System");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Navigation Banner separating Client and Clinic/Admin
        add(createTopHeaderBar(), BorderLayout.NORTH);

        // Top-Level Tabs: Client vs. Clinic & Admin
        mainRoleTabs = new JTabbedPane();
        mainRoleTabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        mainRoleTabs.addTab("CLIENT PORTAL (Pet Owner)", createClientPortalPanel());
        mainRoleTabs.addTab("CLINIC & ADMIN PORTAL (Staff / Vet / Admin)", createAdminClinicPortalPanel());

        add(mainRoleTabs, BorderLayout.CENTER);
        refreshAllDropdownsAndTables();
    }

    private JPanel createTopHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 60, 90));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("VETERINARY CLINIC TRACKING SYSTEM");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel roleButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        roleButtons.setOpaque(false);

        JButton btnClientView = new JButton("Client View");
        JButton btnAdminView = new JButton("Clinic & Admin View");

        btnClientView.addActionListener(e -> mainRoleTabs.setSelectedIndex(0));
        btnAdminView.addActionListener(e -> mainRoleTabs.setSelectedIndex(1));

        roleButtons.add(btnClientView);
        roleButtons.add(btnAdminView);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(roleButtons, BorderLayout.EAST);
        return header;
    }

    // =========================================================================
    // 1. CLIENT PORTAL (Pet Owner Use Cases: Track Appt, View History, Reminders)
    // =========================================================================
    private JPanel createClientPortalPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top selector for Pet Owner
        JPanel topSelect = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topSelect.setBorder(BorderFactory.createTitledBorder("Pet Owner Account - Select Your Registered Pet"));
        cmbClientPet = new JComboBox<>();
        JButton btnLoadClient = new JButton("Refresh My Records");
        topSelect.add(new JLabel("My Pet:"));
        topSelect.add(cmbClientPet);
        topSelect.add(btnLoadClient);

        btnLoadClient.addActionListener(e -> refreshClientView());
        cmbClientPet.addActionListener(e -> refreshClientView());

        // Center: Split between Appointment Tracker + Reminders (Left) and Medical History (Right)
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 10, 10));

        // Left: Track Appointment & Reminders
        JPanel leftStack = new JPanel(new GridLayout(2, 1, 10, 10));

        clientApptTableModel = new DefaultTableModel(new String[]{"Queue #", "Date/Time", "Status"}, 0);
        JTable tblClientAppt = new JTable(clientApptTableModel);
        JScrollPane apptScroll = new JScrollPane(tblClientAppt);
        apptScroll.setBorder(BorderFactory.createTitledBorder("Track My Appointments & Queue Status"));

        clientReminderTableModel = new DefaultTableModel(new String[]{"Date", "Reminder / Notice"}, 0);
        JTable tblClientRem = new JTable(clientReminderTableModel);
        JScrollPane remScroll = new JScrollPane(tblClientRem);
        remScroll.setBorder(BorderFactory.createTitledBorder("My Vaccine & Billing Reminders"));

        leftStack.add(apptScroll);
        leftStack.add(remScroll);

        // Right: Pet Medical & Vaccination History
        txtClientPetHistory = new JTextArea();
        txtClientPetHistory.setEditable(false);
        txtClientPetHistory.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane histScroll = new JScrollPane(txtClientPetHistory);
        histScroll.setBorder(BorderFactory.createTitledBorder("My Pet's Medical & Vaccination History"));

        centerGrid.add(leftStack);
        centerGrid.add(histScroll);

        panel.add(topSelect, BorderLayout.NORTH);
        panel.add(centerGrid, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // 2. CLINIC & ADMIN PORTAL (Front Desk, Veterinarian, Billing, Admin)
    // =========================================================================
    private JPanel createAdminClinicPortalPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        JTabbedPane internalTabs = new JTabbedPane();

        internalTabs.addTab("1. Front Desk & Queue", createFrontDeskPanel());
        internalTabs.addTab("2. Veterinarian & Records", createVetPanel());
        internalTabs.addTab("3. Billing & Receipts", createBillingPanel());
        internalTabs.addTab("4. Admin: Services & Reports", createAdminManagementPanel());

        wrapper.add(internalTabs, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createFrontDeskPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

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
                displayPetHistory(appt.getPet(), txtHistoryLog);
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

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Automated System Reminders (Boosters & Balances)"));
        reminderTableModel = new DefaultTableModel(new String[]{"Target Date", "Status", "Reminder Message"}, 0);
        JTable reminderTable = new JTable(reminderTableModel);
        rightPanel.add(new JScrollPane(reminderTable), BorderLayout.CENTER);

        panel.add(leftPanel);
        panel.add(rightPanel);
        return panel;
    }

    private JPanel createAdminManagementPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: Manage Services (Standard Fee Rates)
        JPanel servicePanel = new JPanel(new GridLayout(4, 2, 5, 5));
        servicePanel.setBorder(BorderFactory.createTitledBorder("Admin: Manage Clinic Service Rates"));
        txtDefaultConsultFee = new JTextField("500.00");
        txtDefaultVaccineFee = new JTextField("800.00");
        JButton btnApplyRates = new JButton("Update Billing Default Rates");

        servicePanel.add(new JLabel("Standard Consultation Fee (PHP):"));
        servicePanel.add(txtDefaultConsultFee);
        servicePanel.add(new JLabel("Standard Vaccination Fee (PHP):"));
        servicePanel.add(txtDefaultVaccineFee);
        servicePanel.add(new JLabel(""));
        servicePanel.add(btnApplyRates);

        btnApplyRates.addActionListener(e -> {
            txtConsultFee.setText(txtDefaultConsultFee.getText());
            txtVaccineFee.setText(txtDefaultVaccineFee.getText());
            JOptionPane.showMessageDialog(this, "Default service rates updated in Billing.");
        });

        // Right: Generate Clinic Reports
        JPanel reportPanel = new JPanel(new BorderLayout(5, 5));
        reportPanel.setBorder(BorderFactory.createTitledBorder("Admin: Generate Clinic Summary Report"));
        txtAdminReportArea = new JTextArea();
        txtAdminReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtAdminReportArea.setEditable(false);
        JButton btnGenerateReport = new JButton("Generate Summary Report");

        btnGenerateReport.addActionListener(e -> {
            double totalRevenue = 0;
            double totalUnpaid = 0;
            for (Bill b : service.getBills()) {
                totalRevenue += b.getAmountPaid();
                totalUnpaid += b.getBalance();
            }
            String report = "=== VETERINARY CLINIC SUMMARY REPORT ===\n"
                    + "Total Registered Pets:    " + service.getAllPets().size() + "\n"
                    + "Total Appointments:       " + service.getAppointments().size() + "\n"
                    + "Total Bills Issued:       " + service.getBills().size() + "\n"
                    + String.format("Total Revenue Collected:  PHP %.2f\n", totalRevenue)
                    + String.format("Total Unpaid Balances:    PHP %.2f\n", totalUnpaid)
                    + "Active System Reminders:  " + service.getReminders().size() + "\n"
                    + "========================================";
            txtAdminReportArea.setText(report);
        });

        reportPanel.add(new JScrollPane(txtAdminReportArea), BorderLayout.CENTER);
        reportPanel.add(btnGenerateReport, BorderLayout.SOUTH);

        panel.add(servicePanel);
        panel.add(reportPanel);
        return panel;
    }

    private void displayPetHistory(Pet pet, JTextArea targetArea) {
        if (pet == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Patient: ").append(pet.getName()).append(" (").append(pet.getSpecies()).append(" - ").append(pet.getBreed()).append(")\n");
        sb.append("Owner:   ").append(pet.getOwner().getName()).append(" | Phone: ").append(pet.getOwner().getPhone()).append("\n");
        sb.append("------------------------------------------------------------\n");
        if (pet.getHistory().isEmpty()) {
            sb.append("No medical records logged yet.\n");
        }
        for (MedicalRecord r : pet.getHistory()) {
            sb.append("Visit Date: ").append(r.getVisitDate())
              .append(" | Diagnosis: ").append(r.getDiagnosis())
              .append(" | Treatment: ").append(r.getTreatment()).append("\n");
            for (Vaccination v : r.getVaccinations()) {
                sb.append("   -> Vaccine Given: ").append(v.getVaccineName())
                  .append(" | Next Due: ").append(v.getNextDueDate()).append("\n");
            }
        }
        targetArea.setText(sb.toString());
    }

    private void refreshClientView() {
        Pet selected = (Pet) cmbClientPet.getSelectedItem();
        if (selected == null) return;

        displayPetHistory(selected, txtClientPetHistory);

        clientApptTableModel.setRowCount(0);
        for (Appointment a : service.getAppointments()) {
            if (a.getPet().getPetId() == selected.getPetId()) {
                clientApptTableModel.addRow(new Object[]{
                        "Q-" + a.getQueueNo(), a.getDateTime().toLocalDate(), a.getStatus()
                });
            }
        }

        clientReminderTableModel.setRowCount(0);
        for (Reminder rem : service.getReminders()) {
            if (rem.getMessage().contains(selected.getName()) || rem.getMessage().contains(selected.getOwner().getName())) {
                clientReminderTableModel.addRow(new Object[]{rem.getSendDate(), rem.getMessage()});
            }
        }
    }

    private void refreshAllDropdownsAndTables() {
        Pet prevClientPet = (Pet) cmbClientPet.getSelectedItem();

        cmbSelectPet.removeAllItems();
        cmbClientPet.removeAllItems();
        for (Pet p : service.getAllPets()) {
            cmbSelectPet.addItem(p);
            cmbClientPet.addItem(p);
        }
        if (prevClientPet != null) {
            cmbClientPet.setSelectedItem(prevClientPet);
        }

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

        refreshClientView();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainClinicFrame().setVisible(true));
    }
}