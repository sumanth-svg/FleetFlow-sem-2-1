package model;

/**
 * Model representing a shipment document (e.g., Bill of Lading, Customs Declaration, Delivery Receipt).
 * Used in Suffix Structures & Document Similarity indexing (Module 2 - CO2).
 */
public class ShipmentDocument {

    private String documentId;
    private String title;
    private String documentType;
    private String content;
    private String shipmentId;

    public ShipmentDocument() {
    }

    public ShipmentDocument(String documentId, String title, String documentType, String content, String shipmentId) {
        this.documentId = documentId;
        this.title = title;
        this.documentType = documentType;
        this.content = content;
        this.shipmentId = shipmentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public void displayDocument() {
        System.out.println("Document ID   : " + documentId);
        System.out.println("Title         : " + title);
        System.out.println("Document Type : " + documentType);
        System.out.println("Shipment ID   : " + shipmentId);
        System.out.println("Content       : " + (content != null && content.length() > 100 ? content.substring(0, 100) + "..." : content));
    }

    @Override
    public String toString() {
        return "[" + documentId + "] " + title + " (" + documentType + ") - Shipment: " + shipmentId;
    }
}
