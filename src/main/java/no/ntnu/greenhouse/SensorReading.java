package no.ntnu.greenhouse;

import java.util.Objects;

/**
 * Represents one sensor reading (value).
 */
public class SensorReading {
    private final String type;
    private double value;
    private final String unit;

    /**
     * Create a new sensor reading.
     *
     * @param type  The type of sensor being red
     * @param value The current value of the sensor
     * @param unit  The unit, for example: %, lux
     */
    public SensorReading(String type, double value, String unit) {
        this.type = type;
        this.value = value;
        this.unit = unit;
    }

    /**
     * Get the type of sensor which is being red.
     *
     * @return The type of sensor which is being red
     */
    public String getType() {
        return type;
    }

    /**
     * Get the current value of the sensor reading.
     *
     * @return The current value of the sensor reading
     */
    public double getValue() {
        return value;
    }

    /**
     * Get the unit for the sensor reading.
     *
     * @return The unit for the sensor reading
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Set the value of the sensor reading.
     *
     * @param newValue The new value to set the sensor reading value to
     */
    public void setValue(double newValue) {
        this.value = newValue;
    }

    @Override
    public String toString() {
        return "{ type=" + type + ", value=" + value + ", unit=" + unit + " }";
    }

    /**
     * Get a human-readable (formatted) version of the current reading, including
     * the unit.
     *
     * @return The sensor reading and the unit
     */
    public String getFormatted() {
        return value + unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SensorReading that = (SensorReading) o;
        return Double.compare(value, that.value) == 0
                && Objects.equals(type, that.type)
                && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value, unit);
    }
}
