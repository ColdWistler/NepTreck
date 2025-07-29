package com.tourism.models;

public class Staff extends User {
    private String staffId;
    private String name;
    private String department;
    private boolean isActive;

    // Default constructor
    public Staff() {
        super();
        this.setRole("STAFF");
        this.isActive = true;
    }

    // Constructor with parameters
    public Staff(String username, String passwordHash, String staffId, String name) {
        super(null, username, passwordHash, "STAFF");
        this.staffId = staffId;
        this.name = name;
        this.isActive = true;
    }

    // Staff-specific methods
    public void assignTask() {
        // Task assignment logic
    }

    public void reportIssue() {
        // Issue reporting logic
    }

    // Getters and Setters
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "staffId='" + staffId + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", username='" + getUsername() + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}