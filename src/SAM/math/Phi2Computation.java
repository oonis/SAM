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
 * Class for computing the Phi2 values.
 */
public class Phi2Computation extends GenericComputation{

    public Phi2Computation(Selections selections, Measurement... measurements) {
        super("Phi2", selections, measurements);
    }
    
    public Phi2Computation(Selections selections, List<Measurement> measurements) {
        super("Phi2", selections, measurements.toArray(new Measurement[measurements.size()]));
    }

    @Override
    public void compute() {
        List<ImageInformation> fsImages = mMeasurements[0].getImages();
        List<ImageInformation> fmImages = mMeasurements[1].getImages();
        mTimeVector = new long[fsImages.size()];
        
        if(fsImages.size() != fmImages.size() && fmImages.size() != 1) {
            System.out.println("fs and fm have different number of images");
            return;
        }
        
        Threshold thresholds = mMeasurements[0].getDayExperiment().getSensorExperiment().getThresholds();
        
        double fsLow = thresholds.getThresholdLow(mMeasurements[0].getName());
        double fsHigh = thresholds.getThresholdHigh(mMeasurements[0].getName());
        double fmLow = thresholds.getThresholdLow(mMeasurements[1].getName());
        double fmHigh = thresholds.getThresholdHigh(mMeasurements[1].getName());
        
        for(int i = 0; i < fsImages.size(); i++) {
            ImagePlus fsImage = fsImages.get(i).getImage(false,fsLow,fsHigh);
            ImagePlus fmImage = fmImages.get(0).getImage(false,fmLow,fmHigh);
            
            ImageProcessor fsProcessor = mMeasurements[0].generateImage(fsImage);
            ImageProcessor fmProcess = mMeasurements[1].generateImage(fmImage);
            fsImage = new ImagePlus("fs",fsProcessor);
            fmImage = new ImagePlus("fm",fmProcess);
            
            //long time = fsImages.get(i).getTime();
            long time = Math.max(fmImages.get(0).getTime(), fsImages.get(i).getTime());
            mTimeVector[i] = time;
            
            List<Selection> selections = new ArrayList<>(mSelections.getSelections(time));
            for (int j = 0; j < selections.size(); j++) {
                Selection selection = selections.get(j);
                String name = selection.getName();
                
                ImagePlus fsPlant = selection.crop(fsImage, null);
                ImagePlus fmPlant = selection.crop(fmImage, null);
                
                double fsAverage = average(fsPlant.getProcessor().getFloatArray());
                double fmAverage = average(fmPlant.getProcessor().getFloatArray());
                
                double phi2 = (fmAverage-fsAverage)/fmAverage;
                
                if (i == 0) {
                    mPlants[j] = new Plant(name, time, phi2);
                } else {
                    if (!name.equals(mPlants[j].getName())) {
                        System.out.println(name + " is missing a value for " + mName + " at " + time);
                        mPlants[j].addValue(Float.NaN);
                        for (int x = j; x < selections.size(); x++) {
                            if (name.equals(mPlants[x].getName())) {
                                mPlants[x].addValue(phi2);
                            }
                        }
                    } else {
                        mPlants[j].addValue(phi2);
                    }
                }
            }
        }
    }
    
}
