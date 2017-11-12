package SAM.structures;

import SAM.imagej.plugins.AveringingReducer;
import SAM.imagej.plugins.StackAverager;
import SAM.imagej.plugins.SubstackMaker;
import SAM.structures.experiment.DayExperiment;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;
import java.util.List;

/**
 * Measurement information read form the XML
 */
public class Measurement {
    protected static final ImageCalculator calc = new ImageCalculator();

    protected String mName;
    protected List<ImageInformation> mImages;

    protected AverageImage mBaseImage;
    protected AverageImage mBackgroundImage;
    
    private final DayExperiment mDayExperiment;

    /**
     * Only constructor
     * @param exp
     * @param name
     * @param images
     * @param baseImage
     * @param bkg
     */
    public Measurement(DayExperiment exp, String name, List<ImageInformation> images, AverageImage baseImage, AverageImage bkg) {
        mName = name;
        mImages = images;

        mBaseImage = baseImage;
        mBackgroundImage = bkg;
        
        mDayExperiment = exp;

    }

    /**
     * Generates the measurement image
     * @param inputImage
     * @return
     */
    public ImageProcessor generateImage(ImagePlus inputImage) {
        ImagePlus reduced = new AveringingReducer().shrink(inputImage); // Can this be done outside of this class?

        ImagePlus fsSubstack = SubstackMaker.run(reduced, mBaseImage.getStartSlice(), mBaseImage.getEndSlice());
        ImagePlus foreground = StackAverager.run(fsSubstack);

        fsSubstack = SubstackMaker.run(reduced, mBackgroundImage.getStartSlice(), mBackgroundImage.getEndSlice());
        ImagePlus background = StackAverager.run(fsSubstack);

        ImageProcessor process = calc.run("Subtract create", foreground, background).getProcessor();

        process.setBackgroundValue(Double.NaN);

        return process;
    }

    /**
     * @return <code>String</code> Name of the measurement
     */
    public String getName() {
        return mName;
    }

    /**
     * @return All of the ImageInformation used for this measurement.
     */
    public List<ImageInformation> getImages() {
        return mImages;
    }
    
    /**
     * @return <code>DayExperiment</code> The day that this measurement is associated with.
     */
    public DayExperiment getDayExperiment() {
        return mDayExperiment;
    }


}