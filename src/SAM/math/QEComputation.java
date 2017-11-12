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
 * Computation for getting qE.
 */
public class QEComputation extends GenericComputation{

    public QEComputation(Selections selections, Measurement... measurements) {
        super("qE", selections, measurements);
    }
    
    public QEComputation(Selections selections, List<Measurement> measurements) {
        super("qE", selections, measurements.toArray(new Measurement[measurements.size()]));
    }

    @Override
    public void compute() {
        List<ImageInformation> fmmImages = mMeasurements[0].getImages();
        List<ImageInformation> fmpImages = mMeasurements[1].getImages();
        mTimeVector = new long[fmpImages.size()];
        
        if(fmmImages.size() != fmpImages.size()) {
            System.out.println("Fs and fmp image sizes do not match up");
            return;
        }
        
        // Should this even be done? Maybe this should have been done already?
        Threshold thresholds = mMeasurements[0].getDayExperiment().getSensorExperiment().getThresholds();
        
        double fmmLow = thresholds.getThresholdLow(mMeasurements[0].getName());
        double fmmHigh = thresholds.getThresholdHigh(mMeasurements[0].getName());
        double fmpLow = thresholds.getThresholdLow(mMeasurements[1].getName());
        double fmpHigh = thresholds.getThresholdHigh(mMeasurements[1].getName());
        
        for(int i = 0; i < fmpImages.size(); i++) {
            ImagePlus fmmImage = fmmImages.get(i).getImage(false,fmmLow,fmmHigh);
            ImagePlus fmpImage = fmpImages.get(i).getImage(false,fmpLow,fmpHigh);
            
            ImageProcessor fmmProcess = mMeasurements[0].generateImage(fmmImage);
            ImageProcessor fmpProcess = mMeasurements[1].generateImage(fmpImage);
            fmmImage = new ImagePlus("fmm",fmmProcess);
            fmpImage = new ImagePlus("fmp",fmpProcess);
            
            long time = Math.max(fmpImages.get(i).getTime(), fmmImages.get(i).getTime());
            mTimeVector[i] = time;
            
            List<Selection> selections = new ArrayList<>(mSelections.getSelections(time));
            for (int j = 0; j < selections.size(); j++) {
                Selection selection = selections.get(j);
                String name = selection.getName();
                
                ImagePlus fmmPlant = selection.crop(fmmImage, null);
                ImagePlus fmpPlant = selection.crop(fmpImage, null);
                
                double fmmAverage = average(fmmPlant.getProcessor().getFloatArray());
                double fmpAverage = average(fmpPlant.getProcessor().getFloatArray());
                
                double qE = (fmpAverage-fmmAverage)/fmmAverage;
                
                if (i == 0) {
                    mPlants[j] = new Plant(name, time, qE);
                } else {
                    if (!name.equals(mPlants[j].getName())) {
                        System.out.println(name + " is missing a value for " + mName + " at " + time);
                        mPlants[j].addValue(Float.NaN);
                        for (int x = j; x < selections.size(); x++) {
                            if (name.equals(mPlants[x].getName())) {
                                mPlants[x].addValue(qE);
                            }
                        }
                    } else {
                        mPlants[j].addValue(qE);
                    }
                }
            }
        }
        System.out.println("Done qE");
    }
    
}
