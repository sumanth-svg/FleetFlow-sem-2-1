package model;

public class Shipment {

    private String shipmentId;
    private String packageCode;
    private String senderName;
    private String receiverName;
    private String source;
    private String destination;
    private String status;
    private double weight;

    public Shipment(String shipmentId,
                    String packageCode,
                    String senderName,
                    String receiverName,
                    String source,
                    String destination,
                    double weight) {

        this.shipmentId = shipmentId;
        this.packageCode = packageCode;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.status = "Created";
    }

    // Getters

    public String getShipmentId() {
        return shipmentId;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getStatus() {
        return status;
    }

    public double getWeight() {
        return weight;
    }

    // Setters

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    // Display shipment information

    public void displayShipment() {

        System.out.println("---------------------------------------------");
        System.out.println("Shipment ID   : " + shipmentId);
        System.out.println("Package Code  : " + packageCode);
        System.out.println("Sender        : " + senderName);
        System.out.println("Receiver      : " + receiverName);
        System.out.println("Source        : " + source);
        System.out.println("Destination   : " + destination);
        System.out.println("Weight        : " + weight + " kg");
        System.out.println("Status        : " + status);
        System.out.println("---------------------------------------------");
    }

    @Override
    public String toString() {

        return shipmentId + " | "
                + packageCode + " | "
                + senderName + " -> "
                + receiverName + " | "
                + source + " -> "
                + destination + " | "
                + status;
    }
}