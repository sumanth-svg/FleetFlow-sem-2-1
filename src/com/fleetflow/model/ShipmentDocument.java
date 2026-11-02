package com.fleetflow.model;

/**
 * Represents a shipment document such as a bill of lading, customs declaration, or manifest.
 */
public class ShipmentDocument {
    private final String docId;
    private final String trackingCode;
    private final String manifestType;
    private final String content;

    public ShipmentDocument(String docId, String trackingCode, String manifestType, String content) {
        this.docId = docId;
        this.trackingCode = trackingCode;
        this.manifestType = manifestType;
        this.content = content;
    }

    public String getDocId() { return docId; }
    public String getTrackingCode() { return trackingCode; }
    public String getManifestType() { return manifestType; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return String.format("[%s | %s] Type: %s | Content: %s",
                docId, trackingCode, manifestType,
                content.length() > 60 ? content.substring(0, 57) + "..." : content);
    }
}
