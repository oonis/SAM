package SAM.math;

import SAM.structures.ImageInformation;
import SAM.structures.Measurement;
import SAM.structures.Threshold;
import SAM.structures.selections.Selection;
import SAM.structures.selections.Selections;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class FVFMComputation extends GenericComputation {

    public FVFMComputation(Selections selections, Measurement one, Measurement two) {
        super("FvFm", selections, one, two);
        mTimeVector = new long[1];
    }
    
    public FVFMComputation(Selections selections, List<Measurement> measurements) {
        super("FvFm", selections, measurements.toArray(new Measurement[measurements.size()]));
    }

    @Override
    public void compute() {
        List<ImageInformation> fmImages = mMeasurements[0].getImages();
        List<ImageInformation> f0Images = mMeasurements[1].getImages();
        
        Threshold thresh = mMeasurements[0].getDayExperiment().getSensorExperiment().getThresholds();

        double thresh1Low = thresh.getThresholdLow(mMeasurements[0].getName());
        double thresh1High = thresh.getThresholdHigh(mMeasurements[0].getName());
        
        double thresh2Low = thresh.getThresholdLow(mMeasurements[1].getName());
        double thresh2High = thresh.getThresholdHigh(mMeasurements[1].getName());
        
        ImagePlus fmImage = fmImages.get(0).getImage(false,thresh1Low,thresh1High);
        ImagePlus f0Image = f0Images.get(0).getImage(false,thresh2Low,thresh2High);

        //long time = f0Images.get(0).getTime();
        long time = Math.max(fmImages.get(0).getTime(), f0Images.get(0).getTime());
        mTimeVector = new long[1];
        mTimeVector[0] = time;
        
        List<Selection> selections = new ArrayList<>(mSelections.getSelections(time));
        for(int i = 0; i < selections.size(); i++) {
            Selection selection = selections.get(i);
            String name = selection.getName();
            
            ImagePlus fmPlant = selection.crop(fmImage, null);
            ImagePlus f0Plant = selection.crop(f0Image, null);
            
            ImageProcessor fmProcess = mMeasurements[0].generateImage(fmPlant);
            ImageProcessor f0Process = mMeasurements[1].generateImage(f0Plant);
            
            double fmAverage = average(fmProcess.getFloatArray());
            double f0Average = average(f0Process.getFloatArray());
            
            double fvfm = (fmAverage-f0Average)/fmAverage;
            
            mPlants[i] = new Plant(name,time,fvfm);
        }
        
        System.out.println("fvfm completed");
    }

}
