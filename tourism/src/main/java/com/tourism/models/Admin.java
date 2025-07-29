package com.tourism.models;

public class Admin extends User {
    private String adminId;
    private String name;
    private String department;
    private boolean isActive;

    // Default constructor
    public Admin() {
        super();
        this.setRole("ADMIN");
        this.setAdminLevel("REGULAR");
        this.isActive = true;
    }

    // Constructor with parameters
    public Admin(String username, String passwordHash, String adminId, String name) {
        super(null, username, passwordHash, "ADMIN");
        this.adminId = adminId;
        this.name = name;
        this.setAdminLevel("REGULAR");
        this.isActive = true;
    }

    // Admin-specific methods
    public void manageUsers() {
        // User management logic
    }

    public void generateReports() {
        // Report generation logic
    }

    public void configureSystem() {
        // System configuration logic
    }

    // Getters and Setters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
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
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", username='" + getUsername() + '\'' +
                ", adminLevel='" + getAdminLevel() + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}