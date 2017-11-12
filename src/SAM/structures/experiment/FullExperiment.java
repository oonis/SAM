package SAM.structures.experiment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This is an experiment that holds multiple sensorExperiments. The read from the root directory.
 */
public class FullExperiment {
    private final List<SensorExperiment> mSensorExperiments = new ArrayList<>();
    private final Properties mExperimentInfo = new Properties();
    private final String mExperimentName;
    private final File mFile;

    /**
     * Constructor for FullExperiment.
     * 
     * @param file <code>{@link File }</code>: The '.sensorinfo' file.
     * @param warnings
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public FullExperiment(File file, List<String> warnings) throws UnsupportedEncodingException, IOException {
        mFile = file;
        
        {   // The .experimentinfo
            File[] infofiles = file.listFiles((File dir, String name) -> {
                return name.endsWith(".experimentinfo");
            });
            if (infofiles.length == 0)
               throw new IOException("Missing .experimentinfo file.");
            else if (infofiles.length == 1)
                try (FileInputStream inputStream = new FileInputStream(infofiles[0])) {
                    mExperimentInfo.load(inputStream);
                    mExperimentName = mExperimentInfo.getProperty("EXPERIMENT_NAME", "ERROR: FILE MISSING NAME");
                }
            else
                throw new IOException("More than one .experimentinfo file.");
        }

        {   // Okay, initialize all the SensorExperiments.
            for (File sensorFile : file.listFiles((File f) ->
                    (f.isDirectory() && (f.getName().startsWith("cam") || f.listFiles((File dir, String name) ->
                    {
                return name.endsWith(".sensorinfo");
            }).length == 1))))
                mSensorExperiments.add(new SensorExperiment(this, sensorFile,warnings));
        }
    }
    
    /**
     * @return <code>File</code> The file for the full experiment.
     */
    public File getFile() {
        return mFile;
    }
    
    /**
     * @return <code>String</code> Name of the experiment
     */
    public String getExperimentName() {
        return mExperimentName;
    }

    /**
     * @return <Code>List</Code> All of the SensorExperiments in the full experiment
     */
    public List<SensorExperiment> getSensorExperiments() {
        return mSensorExperiments;
    }
}
