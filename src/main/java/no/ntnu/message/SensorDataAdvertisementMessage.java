package no.ntnu.message;

import no.ntnu.greenhouse.SensorReading;
import java.util.List;

/**
 * Represents a message containing sensor data for advertisement.
 * This message includes information about sensor readings from a specific node.
 */
public class SensorDataAdvertisementMessage implements Message {
    private final int nodeId;
    private final List<SensorReading> sensorReadings;

    /**
     * Creates a new instance of SensorDataAdvertisementMessage with the specified node ID and sensor readings.
     *
     * @param nodeId The ID of the node from which the sensor readings are coming.
     * @param sensorReadings The list of sensor readings to be advertised.
     */
    public SensorDataAdvertisementMessage(int nodeId, List<SensorReading> sensorReadings) {
        this.nodeId = nodeId;
        this.sensorReadings = sensorReadings;
    }

    /**
     * Returns the node ID associated with this message.
     *
     * @return The node ID.
     */
    public int getNodeId() {
        return nodeId;
    }

    /**
     * Returns the list of sensor readings associated with this message.
     *
     * @return The list of SensorReading.
     */
    public List<SensorReading> getSensorReadings() {
        return sensorReadings;
    }
}
