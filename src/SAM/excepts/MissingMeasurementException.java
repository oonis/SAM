package SAM.excepts;

/**
 * Exception thrown when a needed measurement is missing.
 */
public class MissingMeasurementException extends Exception{
    private final int mDay;
    private final String mSensor;
    private final String mMeasurement;
    private final String mMissingMeasurement;
    
    /**
     * @param measureName <code>String</code> Name of the missing measurement.
     * @param missingMeasure <code>String</code> Measurement attempting to use the measureName.
     * @param day <code>int</code> Day being calculated.
     * @param sensorID <code>String</code> The ID for the current sensor.
     */
    public MissingMeasurementException(String measureName, String missingMeasure,int day, String sensorID) {
        super("Unable to compute "+measureName+" without "+missingMeasure);
        mMeasurement = measureName;
        mDay = day;
        mSensor = sensorID;
        mMissingMeasurement = missingMeasure;
    }
    
    /**
     * @return <code>int</code> Day where measurement is missing.
     */
    public int getDay() {
        return mDay;
    }
    
    /**
     * @return <code>String</code> Sensor where the measurement is missing.
     */
    public String getSensorID() {
        return mSensor;
    }
    
    /**
     * @return <code>String</code> Get the measurement which is being computed.
     */
    public String getMeasurementName() {
        return mMeasurement;
    }
    
    /**
     * @return <code>String</code> Get the name of the missing measurement.
     */
    public String getMissingMeasurementName() {
        return mMissingMeasurement;
    }
}
