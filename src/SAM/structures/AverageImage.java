package SAM.structures;

/**
 * This class tells of which slices within the image in order to get a certain measurement.
 */
public class AverageImage {
    private final int mStart, mEnd;
    private final String mName;

    /**
     *
     * @param name <code>String</code> Name of the AverageImage.
     * @param id <code>String</code> ID for the AverageImage.
     * @param start <code>int</code> Start slice within the tiff stack.
     * @param end <code>int</code> End slice within the tiff stack.
     */
    public AverageImage(String name, String id, int start, int end){
        mName = name;
        mStart = start;
        mEnd = end;
    }

    /**
     * @return <code>String</code> Name of the AverageImage.
     */
    public String getName(){
        return mName;
    }

    /**
     * @return <code>int</code> The beginning slice number in the tiff stack.
     */
    public int getStartSlice(){
        return mStart;
    }

    /**
     * @return <code>int</code> The ending slice in the tiff stack.
     */
    public int getEndSlice(){
        return mEnd;
    }
}