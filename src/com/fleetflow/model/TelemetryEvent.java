package com.fleetflow.model;

/**
 * Represents a streaming telemetry event from an active logistics vehicle.
 */
public class TelemetryEvent {
    private final String eventId;
    private final String vehicleId;
    private final String timestamp;
    private final double speedKmh;
    private final double fuelPct;
    private final double engineTempC;
    private final double latitude;
    private final double longitude;
    private final String logMessage;

    public TelemetryEvent(String eventId, String vehicleId, String timestamp,
                          double speedKmh, double fuelPct, double engineTempC,
                          double latitude, double longitude, String logMessage) {
        this.eventId = eventId;
        this.vehicleId = vehicleId;
        this.timestamp = timestamp;
        this.speedKmh = speedKmh;
        this.fuelPct = fuelPct;
        this.engineTempC = engineTempC;
        this.latitude = latitude;
        this.longitude = longitude;
        this.logMessage = logMessage;
    }

    public String getEventId() { return eventId; }
    public String getVehicleId() { return vehicleId; }
    public String getTimestamp() { return timestamp; }
    public double getSpeedKmh() { return speedKmh; }
    public double getFuelPct() { return fuelPct; }
    public double getEngineTempC() { return engineTempC; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getLogMessage() { return logMessage; }

    @Override
    public String toString() {
        return String.format("[%s @ %s] Veh: %s | Speed: %.1f km/h | Fuel: %.1f%% | Temp: %.1fC | Msg: %s",
                eventId, timestamp, vehicleId, speedKmh, fuelPct, engineTempC, logMessage);
    }
}
