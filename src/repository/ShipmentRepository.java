package repository;

import java.util.ArrayList;
import java.util.List;

import model.Shipment;

public class ShipmentRepository {

    private List<Shipment> shipments;

    public ShipmentRepository() {
        shipments = new ArrayList<>();
    }

    // Add a new shipment
    public void addShipment(Shipment shipment) {
        shipments.add(shipment);
    }

    // Get all shipments
    public List<Shipment> getAllShipments() {
        return shipments;
    }

    // Find shipment using Shipment ID
    public Shipment findById(String shipmentId) {

        for (Shipment shipment : shipments) {

            if (shipment.getShipmentId().equalsIgnoreCase(shipmentId)) {
                return shipment;
            }
        }

        return null;
    }

    // Delete shipment using Shipment ID
    public boolean deleteShipment(String shipmentId) {

        Shipment shipment = findById(shipmentId);

        if (shipment != null) {
            shipments.remove(shipment);
            return true;
        }

        return false;
    }

    // Check whether shipment exists
    public boolean exists(String shipmentId) {

        return findById(shipmentId) != null;
    }

    // Get total number of shipments
    public int getShipmentCount() {
        return shipments.size();
    }

    // Clear all shipments
    public void clearAll() {
        shipments.clear();
    }
}