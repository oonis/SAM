package SAM.math;

import static SAM.math.GenericComputation.average;
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
 * Math for getting QESV using fm, fmp, fmm
 */
public class QESVComputation extends GenericComputation {

    public QESVComputation(Selections selections, Measurement... measurements) {
        super("QESV", selections, measurements);
    }
    
    public QESVComputation(Selections selections, List<Measurement> measurements) {
        super("QESV", selections, measurements.toArray(new Measurement[measurements.size()]));
    }

    @Override
    public void compute() {
        List<ImageInformation> fmImages = mMeasurements[0].getImages();
        List<ImageInformation> fmpImages = mMeasurements[1].getImages();
        List<ImageInformation> fmmImages = mMeasurements[2].getImages();
        mTimeVector = new long[fmpImages.size()];
        
        Threshold thresholds = mMeasurements[0].getDayExperiment().getSensorExperiment().getThresholds();
        
        // Get the thresholds for the measurements
        double fmLow = thresholds.getThresholdLow(mMeasurements[0].getName());
        double fmHigh = thresholds.getThresholdHigh(mMeasurements[0].getName());
        double fmpLow = thresholds.getThresholdLow(mMeasurements[1].getName());
        double fmpHigh = thresholds.getThresholdHigh(mMeasurements[1].getName());
        double fmmLow = thresholds.getThresholdLow(mMeasurements[2].getName());
        double fmmHigh = thresholds.getThresholdHigh(mMeasurements[2].getName());
        
        for(int i = 0; i < fmpImages.size(); i++) {
            ImagePlus fmImage = fmImages.get(0).getImage(false,fmLow,fmHigh);
            ImagePlus fmmImage = fmmImages.get(i).getImage(false,fmmLow,fmmHigh);
            ImagePlus fmpImage = fmpImages.get(i).getImage(false,fmpLow,fmpHigh);
            
            ImageProcessor fmProcess = mMeasurements[0].generateImage(fmImage);
            ImageProcessor fmpProcess = mMeasurements[1].generateImage(fmpImage);
            ImageProcessor fmmProcess = mMeasurements[2].generateImage(fmmImage);
            fmImage = new ImagePlus("fm",fmProcess);
            fmpImage = new ImagePlus("fmp",fmpProcess);
            fmmImage = new ImagePlus("fmm",fmmProcess);
            
            long time = Math.max(fmImages.get(0).getTime(), fmpImages.get(i).getTime()); // TODO make this also include fmm
            mTimeVector[i] = time;
            
            List<Selection> selections = new ArrayList<>(mSelections.getSelections(time));
            for (int j = 0; j < selections.size(); j++) {
                Selection selection = selections.get(j);
                String name = selection.getName();
                
                ImagePlus fmPlant = selection.crop(fmImage, null);
                ImagePlus fmpPlant = selection.crop(fmpImage,null);
                ImagePlus fmmPlant = selection.crop(fmmImage,null);
                
                double fmAverage = average(fmPlant.getProcessor().getFloatArray());
                double fmpAverage = average(fmpPlant.getProcessor().getFloatArray());
                double fmmAverage = average(fmmPlant.getProcessor().getFloatArray());
                
                // qESV = (fm/fmp)-(fm/fmm)
                double qesv = (fmAverage-fmpAverage)-(fmAverage/fmmAverage);
                
                if (i == 0) {
                    mPlants[j] = new Plant(name, time, qesv);
                } else {
                    if (!name.equals(mPlants[j].getName())) {
                        System.out.println(name + " is missing a value for " + mName + " at " + time);
                        mPlants[j].addValue(Float.NaN);
                        for (int x = j; x < selections.size(); x++) {
                            if (name.equals(mPlants[x].getName())) {
                                mPlants[x].addValue(qesv);
                            }
                        }
                    } else {
                        mPlants[j].addValue(qesv);
                    }
                }
            }
        }
        
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
