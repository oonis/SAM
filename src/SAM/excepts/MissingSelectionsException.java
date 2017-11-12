package SAM.excepts;

/**
 * Thrown when trying to run a photosynthesis calculation without selections
 */
public class MissingSelectionsException extends Exception{
    private final String mSensorID;
    private final int mDay;
    public MissingSelectionsException(String sensorID, int day) {
        super("need selections before calculating Photosynthesis values");
        mDay = day;
        mSensorID = sensorID;
    }
    
    /**
     * @return <code>String</code> The sensor which is missing selections.
     */
    public String getSensorID() {
        return mSensorID;
    }
    
    /**
     * @return <code>int</code> The day which is missing selections.
     */
    public int getDay() {
        return mDay;
    }
}
