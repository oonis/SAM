package SAM.structures;

/**
 * Time information for environment variables
 */
public class Factor {
    private final double mFactor_start, mFactor_end;
    private final double mValue_start, mValue_end;

    /**
     *
     * @param factor_start <code>String</code> Start time for the factor.
     * @param factor_end <code>String</code> End time for the factor.
     * @param value_start <code>double</code> Starting value for the factor.
     * @param value_end <code>double</code> Ending value for the factor.
     */
    public Factor(String factor_start, String factor_end, double value_start, double value_end) {
        mFactor_start = getTimeVal(factor_start);
        mFactor_end = getTimeVal(factor_end);
        mValue_start = value_start;
        mValue_end = value_end;
    }

    /**
     * @return <code>double</code> Start time for the event
     */
    public double getStartTime() {
        return mFactor_start;
    }

    /**
     * @return <code>double</code> End time for the event
     */
    public double getEndTime() {
        return mFactor_end;
    }

    /**
     * @return <code>double</code> Value of the factor at the beginning of the time span.
     */
    public double getStartValue() {
        return mValue_start;
    }

    /**
     * @return <code>double</code> Value of the factor at the end of the time span.
     */
    public double getEndValue() {
        return mValue_end;
    }

    /**
     * Get the time value based on the string time
     * @param time <code>String</code> The string representation of the time value.
     * @return <code>double</code> Value of the time in the form of a double.
     */
    private double getTimeVal(String time) {
        int firstColon = time.indexOf(":");
        int secondColon = time.indexOf(":", firstColon + 1);

        //hour
        String ss = time.substring(0, firstColon);
        double t = Double.parseDouble(ss);

        //min
        ss = time.substring(firstColon + 1, secondColon);
        t = t + Double.parseDouble(ss) / 60;

        //sec
        ss = time.substring(secondColon + 1);
        t = t + Double.parseDouble(ss) / (60 * 60);

        return t;
    }
}
