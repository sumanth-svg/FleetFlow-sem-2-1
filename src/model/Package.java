package model;

/**
 * Model class representing a package inside a shipment.
 */
public class Package {

    private String packageCode;
    private String description;
    private double weight;
    private String status;

    public Package(String packageCode, String description, double weight, String status) {
        this.packageCode = packageCode;
        this.description = description;
        this.weight = weight;
        this.status = status;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public String getDescription() {
        return description;
    }

    public double getWeight() {
        return weight;
    }

    public String getStatus() {
        return status;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayPackage() {
        System.out.println("---------------------------------------------");
        System.out.println("Package Code : " + packageCode);
        System.out.println("Description  : " + description);
        System.out.println("Weight       : " + weight + " kg");
        System.out.println("Status       : " + status);
        System.out.println("---------------------------------------------");
    }

    @Override
    public String toString() {
        return packageCode + " | " + description + " | " + weight + " kg | " + status;
    }
}
