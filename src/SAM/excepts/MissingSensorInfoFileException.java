package SAM.excepts;

/**
 * Exception thrown when missing the SensorInfo file
 */
public class MissingSensorInfoFileException {
    private final String mSensor;

    /**
     * Creates a new MissingSensorInfoFileException
     * @param sensor <code>String</code> Sensor which is missing sensorInfo.
     */
    public MissingSensorInfoFileException(String sensor) {
        super();
        mSensor = sensor;
    }

    /**
     * @return <code>String</code> ID for the sensor that is messed up.
     */
    public String getSensorID() {
        return mSensor;
    }
}
