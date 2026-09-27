package model;

public class Driver {

    private String driverId;
    private String name;
    private String phoneNumber;
    private String licenseNumber;
    private String assignedVehicleId;
    private String status;

    public Driver(String driverId,
                  String name,
                  String phoneNumber,
                  String licenseNumber) {

        this.driverId = driverId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.assignedVehicleId = null;
        this.status = "Available";
    }

    // Getters

    public String getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getAssignedVehicleId() {
        return assignedVehicleId;
    }

    public String getStatus() {
        return status;
    }

    // Setters

    public void setAssignedVehicleId(String assignedVehicleId) {
        this.assignedVehicleId = assignedVehicleId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Display driver information

    public void displayDriver() {

        System.out.println("---------------------------------------------");
        System.out.println("Driver ID       : " + driverId);
        System.out.println("Name            : " + name);
        System.out.println("Phone Number    : " + phoneNumber);
        System.out.println("License Number  : " + licenseNumber);
        System.out.println("Vehicle ID      : "
                + (assignedVehicleId == null
                ? "Not Assigned"
                : assignedVehicleId));
        System.out.println("Status          : " + status);
        System.out.println("---------------------------------------------");
    }

    @Override
    public String toString() {

        return driverId + " | "
                + name + " | "
                + licenseNumber + " | "
                + "Vehicle: "
                + (assignedVehicleId == null
                ? "Not Assigned"
                : assignedVehicleId)
                + " | "
                + status;
    }
}