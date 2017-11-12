package SAM.math;

import SAM.structures.Measurement;
import SAM.structures.selections.Selections;
import com.google.common.io.Files;
import ij.plugin.ImageCalculator;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An abstract class to extend when creating a new computation. Holds all generic info.
 */
public abstract class GenericComputation implements Computation {

    protected final String mName;
    protected final Selections mSelections;
    protected File mDir;
    protected final Measurement[] mMeasurements;
    protected Plant[] mPlants;
    protected long[] mTimeVector;

    protected static final ImageCalculator calc = new ImageCalculator();

    public GenericComputation(String name, Selections selections, Measurement... measurements) {
        mName = name;
        mSelections = selections;
        mMeasurements = measurements;
        mPlants = new Plant[selections.getSelectionMap().size()];
    }
    
    public void setFile(File dir) {
        mDir = dir;
    }

    /**
     * @param dir <code>File</code> Directory to store the measurement file to.
     * @throws java.io.IOException
     */
    @Override
    public void toFile(File dir) throws IOException {
        if (mPlants.length == 0) {
            // TODO [HIGH] Make this throw some kind of exception
        }

        File file = new File(dir + File.separator + "all" + mName + ".txt");
        StringBuilder str = new StringBuilder(mPlants.length * (mPlants[0].getValues().size() * 9));
        str.append("Time");
        for (float itter : mTimeVector) {
            str.append("\t").append(itter);
        }
        str.append("\n");
        for (Plant itter : mPlants) {
            str.append(itter.getName());
            List<Double> values = itter.getValues();
            values.stream().forEach((val) -> {
                str.append("\t").append(val);
            });
            str.append("\n");
        }
        
        try(PrintWriter out = new PrintWriter(file)) {
            out.print(str.toString());
        }

        //byte data[] = str.toString().getBytes();
        //Files.write(data, file);
    }

    public static float[][] subtract(float[][] a, float[][] b) {
        int rows = a.length;
        int columns = a[0].length;
        float[][] result = new float[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }

    public static float[][] divide(float[][] a, float[][] b) {
        int rows = a.length;
        int columns = a[0].length;
        float[][] result = new float[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = a[i][j] / b[i][j];
            }
        }
        return result;
    }

    
    public static double average(float[][] a) {
        int rows = a.length;
        int columns = a[0].length;
        int totalValues = 0;

        float total = 0.0f;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (Float.isFinite(a[i][j])) {
                    total += a[i][j];
                    totalValues++;
                }
            }
        }
        return total / totalValues;
    }

    @Override
    public Plant[] getPlants() {
        return mPlants;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void run() {
        compute();
        if(mDir != null) {
            try {
                toFile(mDir);
            } catch (IOException ex) {
                Logger.getLogger(GenericComputation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Stores all plants and their values
     */
    public class Plant {

        private final List<Double> mValues;
        private final String mName;

        public Plant(String name, float time, double val) {
            mName = name;
            mValues = new ArrayList<>();
            mValues.add(val);
        }

        public String getName() {
            return mName;
        }

        public List<Double> getValues() {
            return mValues;
        }

        public void addValue(double value) {
            mValues.add(value);
        }
    }
}
