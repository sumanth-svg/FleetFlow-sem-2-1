package model;

public class Vehicle {

    private String vehicleId;
    private String vehicleNumber;
    private String vehicleType;
    private double capacity;
    private String driverId;
    private String status;

    public Vehicle(String vehicleId,
                   String vehicleNumber,
                   String vehicleType,
                   double capacity) {

        this.vehicleId = vehicleId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.capacity = capacity;
        this.driverId = null;
        this.status = "Available";
    }

    // Getters

    public String getVehicleId() {
        return vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public double getCapacity() {
        return capacity;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getStatus() {
        return status;
    }

    // Setters

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    // Display vehicle information

    public void displayVehicle() {

        System.out.println("---------------------------------------------");
        System.out.println("Vehicle ID     : " + vehicleId);
        System.out.println("Vehicle Number : " + vehicleNumber);
        System.out.println("Vehicle Type   : " + vehicleType);
        System.out.println("Capacity       : " + capacity + " kg");
        System.out.println("Driver ID      : "
                + (driverId == null ? "Not Assigned" : driverId));
        System.out.println("Status         : " + status);
        System.out.println("---------------------------------------------");
    }

    @Override
    public String toString() {

        return vehicleId + " | "
                + vehicleNumber + " | "
                + vehicleType + " | "
                + capacity + " kg | "
                + status;
    }
}