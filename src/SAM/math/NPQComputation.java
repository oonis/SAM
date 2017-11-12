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
 * Compute NPQ.
 */
public class NPQComputation extends GenericComputation{

    public NPQComputation(Selections selections, Measurement... measurements) {
        super("NPQ", selections, measurements);
    }
    
    public NPQComputation(Selections selections, List<Measurement> measurements) {
        super("NPQ", selections, measurements.toArray(new Measurement[measurements.size()]));
    }

    @Override
    public void compute() {
        List<ImageInformation> fmImages = mMeasurements[0].getImages();
        List<ImageInformation> fmpImages = mMeasurements[1].getImages();
        mTimeVector = new long[fmpImages.size()];
        
        // Should this even be done? Maybe this should have been done already?
        Threshold thresholds = mMeasurements[0].getDayExperiment().getSensorExperiment().getThresholds();
        
        double fmLow = thresholds.getThresholdLow(mMeasurements[0].getName());
        double fmHigh = thresholds.getThresholdHigh(mMeasurements[0].getName());
        
        if(fmImages.size() > 1) {
            System.out.println("Why in the world do we have more than one fm...");
            return;
        }
        
        double fmpLow = thresholds.getThresholdLow(mMeasurements[1].getName());
        double fmpHigh = thresholds.getThresholdHigh(mMeasurements[1].getName());
        
        ImagePlus fmImage = fmImages.get(0).getImage(false,fmLow,fmHigh);
        ImageProcessor fmProcess = mMeasurements[0].generateImage(fmImage);
        fmImage = new ImagePlus("fm",fmProcess);
        
        for(int i = 0; i < fmpImages.size(); i++) {
            ImagePlus fmpImage = fmpImages.get(i).getImage(false,fmpLow,fmpHigh);
            ImageProcessor fmpProcess = mMeasurements[1].generateImage(fmpImage);
            fmpImage = new ImagePlus("fmp",fmpProcess);
            
            long time = Math.max(fmpImages.get(i).getTime(), fmImages.get(0).getTime());
            mTimeVector[i] = time;
            
            List<Selection> selections = new ArrayList<>(mSelections.getSelections(time));
            for (int j = 0; j < selections.size(); j++) {
                Selection selection = selections.get(j);
                String name = selection.getName();
                
                ImagePlus fmPlant = selection.crop(fmImage, null);
                ImagePlus fmpPlant = selection.crop(fmpImage, null);
                
                double fmAverage = average(fmPlant.getProcessor().getFloatArray());
                double fmpAverage = average(fmpPlant.getProcessor().getFloatArray());
                
                double npq = (fmAverage-fmpAverage)/fmpAverage;
                
                if (i == 0) {
                    mPlants[j] = new Plant(name, time, npq);
                } else {
                    if (!name.equals(mPlants[j].getName())) {
                        System.out.println(name + " is missing a value for " + mName + " at " + time);
                        mPlants[j].addValue(Float.NaN);
                        for (int x = j; x < selections.size(); x++) {
                            if (name.equals(mPlants[x].getName())) {
                                mPlants[x].addValue(npq);
                            }
                        }
                    } else {
                        mPlants[j].addValue(npq);
                    }
                }
                
                
                
            }
        }
        System.out.println("Done NPQ");
    }
    
}
