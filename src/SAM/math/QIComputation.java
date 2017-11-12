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
 * Computation for getting qI
 */
public class QIComputation extends GenericComputation {

    public QIComputation(Selections selections, Measurement one, Measurement two) {
        super("qI", selections, one, two);
    }

    public QIComputation(Selections selections, List<Measurement> measurements) {
        super("qI", selections, measurements.toArray(new Measurement[measurements.size()]));
    }

    @Override
    public void compute() {
        System.out.println("starting qI");
        List<ImageInformation> firstImages = mMeasurements[0].getImages();
        List<ImageInformation> secondImages = mMeasurements[1].getImages();

        // They should both share the same sensorExperiment.
        Threshold thresholds = mMeasurements[0].getDayExperiment().getSensorExperiment().getThresholds();

        double img1Low = thresholds.getThresholdLow(mMeasurements[0].getName());
        double img1High = thresholds.getThresholdHigh(mMeasurements[0].getName());

        if (firstImages.size() != 1) {
            System.out.println("ERROR: Too many images for [measurement] in QI measure one");
            return;
        }

        mTimeVector = new long[secondImages.size()];

        ImagePlus fmImage = firstImages.get(0).getImage(false, img1Low, img1High);
        ImageProcessor fmProcess = mMeasurements[0].generateImage(fmImage);
        fmImage = new ImagePlus("fm",fmProcess);

        double img2Low = thresholds.getThresholdLow(mMeasurements[1].getName());
        double img2High = thresholds.getThresholdHigh(mMeasurements[1].getName());

        for (int i = 0; i < secondImages.size(); i++) {
            ImagePlus fmmImage = secondImages.get(i).getImage(false, img2Low, img2High);
            ImageProcessor fmmProcess = mMeasurements[1].generateImage(fmmImage);
            fmmImage = new ImagePlus("fmm",fmmProcess);
            
            long time = Math.max(firstImages.get(0).getTime(), secondImages.get(i).getTime());
            mTimeVector[i] = time;

            List<Selection> selections = new ArrayList<>(mSelections.getSelections(time));
            for (int j = 0; j < selections.size(); j++) {
                
                Selection itter = selections.get(j);
                ImagePlus fmmPlant = itter.crop(fmmImage, null);
                ImagePlus fmPlant = itter.crop(fmImage, null);
                
                double fmmAverage = average(fmmPlant.getProcessor().getFloatArray());
                double fmAverage = average(fmPlant.getProcessor().getFloatArray());
                
                double qIResult = (fmAverage-fmmAverage)/fmmAverage;
                
                String name = itter.getName();

                if (i == 0) {
                    mPlants[j] = new Plant(name, time, qIResult);
                } else {
                    if (!name.equals(mPlants[j].getName())) {
                        System.out.println(name + " is missing a value for " + mName + " at " + time);
                        mPlants[j].addValue(Float.NaN);
                        for (int x = j; x < selections.size(); x++) {
                            if (name.equals(mPlants[x].getName())) {
                                mPlants[x].addValue(qIResult);
                            }
                        }
                    } else {
                        mPlants[j].addValue(qIResult);
                    }
                }
            }
        }
        System.out.println("done qI");
    }
}
