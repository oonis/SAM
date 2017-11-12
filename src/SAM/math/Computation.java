package SAM.math;

import SAM.math.GenericComputation.Plant;
import java.io.File;
import java.io.IOException;

/**
 * Interface for all photosynthesis computations.
 */
public interface Computation extends Runnable {

    /**
     * @return <code>String</code> The name of the measurement.
     */
    public String getName();

    /**
     * Run the computation
     */
    public void compute();
    
    public void setFile(File file);

    /**
     * @return <code>Plant[]</code> All of the plants.
     */
    public Plant[] getPlants();

    /**
     * Writes the computation to a file
     * @param file <code>File</code> The file to write the values into.
     * @throws IOException Was unable to write to the file given.
     */
    public void toFile(File file) throws IOException;
}
