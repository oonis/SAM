package SAM.structures;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Image information relating to a measurement. Is used to associate images with time points.
 */
public class ImageInformation implements Comparable<ImageInformation> {
    private final String mFileLocation;
    private final long mTime; // Time when the picture was taken

    /**
     * Creates an ImageInformation
     * @param fileLocation <code>String</code> Location of the image.
     * @param time <code>String</code> Time as a string to be parsed.
     */
    public ImageInformation(String fileLocation, String time) {
        mFileLocation = fileLocation;
        mTime = setTime(time);
    }

    public ImageInformation(String fileLocation, long time) {
        mFileLocation = fileLocation;
        mTime = time;
    }

    /**
     * Copy constructor
     * @param image <code>ImageInformation</code> ImageInformation to copy.
     */
    public ImageInformation(ImageInformation image) {
        mFileLocation = image.getFileLocation();
        mTime = image.getTime();
    }

    @Override
    public int compareTo(ImageInformation o) {
        return (int) (mTime - o.getTime());
    }
    
    /**
     * @return <code>String</code> Location of the file
     */
    public String getFileLocation() {
        return mFileLocation;
    }

    /**
     * @return <code>long</code> Time that the photo was taken
     */
    public long getTime() {
        return mTime;
    }

    /**
     * Loads the image into memory for editing.
     * @param toMemory <code>boolean</code> Weather or not you want to load the image into memory.
     * @return <code>ImagePlus</code> The image for viewing.
     */
    public ImagePlus getImage(boolean toMemory) {
        if(toMemory) {
            return IJ.openImage(mFileLocation);
        } else {
            return IJ.openVirtual(mFileLocation);
        }
    }
    
    /**
     * Gets the image stored in the object.
     * @param toMemory <code>boolean</code> True if you want to edit the image and put it in memory.
     * @param threshLow <code>double</code> Lower bounds for the threshold.
     * @param threshHigh <code>double</code> Upper bounds for the threshold.
     * @return <code>ImagePlus</code> The image after being loaded.
     */
    public ImagePlus getImage(boolean toMemory, double threshLow, double threshHigh) {
        ImagePlus img = getImage(toMemory);
        
        System.out.println("thresholdLow = "+threshLow+" thresholdHigh = "+threshHigh);
        
        img.getProcessor().setThreshold(threshLow, threshLow, ImageProcessor.NO_LUT_UPDATE);
        img.getProcessor().setBackgroundValue(Double.NaN);
        return img;
    }

    private long setTime(String time) {
        int firstColon = time.indexOf(":");
        int secondColon = time.indexOf(":", firstColon + 1);

        //hour
        String ss = time.substring(0, firstColon);
        long t = Long.parseLong(ss);

        //min
        ss = time.substring(firstColon + 1, secondColon);
        t = t + Long.parseLong(ss) * 60;

        //sec
        ss = time.substring(secondColon + 1);
        t = t + Long.parseLong(ss) * (60 * 60);

        return t;
    }

    @Override
    public String toString() {
        String thing = mFileLocation + " was taken at " + mTime;
        return thing;
    }
}