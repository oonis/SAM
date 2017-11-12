package SAM.excepts;

/**
 * Exception thrown when Missing all measurements in a day
 */
public class MissingAllMeasurementsException extends Exception {
    private final int mDay;

    /**
     * Creates a MissingAllMeasurementsException
     * @param day <code>int</code> Day which is missing all measurements.
     */
    public MissingAllMeasurementsException(int day) {
        super();
        mDay = day;
    }

    /**
     * @return <code>int</code> Day number that is missing all measurements.
     */
    public int getDayNumber() {
        return mDay;
    }
}
