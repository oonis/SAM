package SAM.structures.experiment;

import SAM.excepts.MissingAllMeasurementsException;
import SAM.structures.Threshold;
import SAM.structures.selections.Selections;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * The sensor experiment (example: camera sensor information)
 */
public class SensorExperiment {
    private final FullExperiment mFullExperiment;
    private final List<DayExperiment> mDayExperiments = new ArrayList<>();
    private final Properties mSensorInfo = new Properties();
    private Selections mSelections;
    private final String mSensorID;

    private Threshold mThresholds;

    private final File mFile;

    /**
     * Constructor for SensorExperiment.
     *
     * @param exp <code>FullExperiment</code> Experiment to associate with the SensorExperiment
     * @param file <code>File</code> The Sensor experiment location
     * @param warnings
     * @throws IOException
     */
    public SensorExperiment(FullExperiment exp, File file, List<String> warnings) throws IOException {
        mFullExperiment = exp;
        mFile = file;

        mSelections = null;
        mThresholds = null;
        {   // Deal with the .sensorinfo file
            File[] infofiles = file.listFiles((File dir, String name) -> {
                return name.endsWith(".sensorinfo");
            });
            if (infofiles.length == 0)
                throw new IOException("No .sensorinfo file");
            else if (infofiles.length == 1)
                try (FileInputStream inputStream = new FileInputStream(infofiles[0])) {
                    mSensorInfo.load(inputStream);
                    mSensorID = mSensorInfo.getProperty("SensorID", "ERROR - NO SENSOR ID");
                    String selectionsPath = mSensorInfo.getProperty("Selections",null);
                    if(selectionsPath != null) {
                        mSelections = new Selections(new File(mFile+File.separator+selectionsPath));
                    }
                    String thresholdPath = mSensorInfo.getProperty("Thresholds",null);
                    if(thresholdPath != null) {
                        mThresholds = new Threshold(new File(mFile+File.separator+thresholdPath));
                    }
                }
            else
                throw new IOException("More than one .sensorinfo file.");
        }

        {   // Load all the DayExperiments
            for (File dayFile : file.listFiles((File f) -> {
                return (f.isDirectory() && ((f.getName().startsWith("day") || f.listFiles((File dir, String name) -> {
                    return name.endsWith(".dayinfo");
                }).length == 1)));
            }))
                try {
                    DayExperiment dayExp = new DayExperiment(this,dayFile,warnings);
                    mDayExperiments.add(dayExp);
                } catch (ParserConfigurationException e) {
                    System.out.println("ERROR: ParserConfigurationException unable to add to mDayExperiments");

                } catch (SAXException e) {
                    System.out.println("ERROR: SAXException unable to add to mDayExperiments");
                    System.out.println(e.getMessage());
                    e.getStackTrace();
                } catch( MissingAllMeasurementsException e) {
                    System.out.println("ERROR: Unable to get all measurements for day "+e.getDayNumber());
                }

        }
    }

    /**
     * @return The thresholds used within this sensor. 
     */
    public Threshold getThresholds() {
        return mThresholds;
    }

    public void setThresholds(Threshold thresh) {
        mThresholds = thresh;
    }

    /**
     * @return
     */
    public String getSensorID() {
        return mSensorID;
    }

    /**
     * @return
     */
    public List<DayExperiment> getDayExperiments() {
        return mDayExperiments;
    }

    /**
     * @return <code>File</code> The file for the current sensor.
     */
    public File getFile() {
        return mFile;
    }

    /**
     * @return <code>FullExperiment</code> The full experiment this sensor is associated with.
     */
    public FullExperiment getFullExperiment() {
        return mFullExperiment;
    }

    /**
     * @return <code>Selections</code> Selections for this Sensor, null if no selections made.
     */
    public Selections getSelections() {
        return mSelections;
    }

    /**
     * Sets the selection file for the sensor
     * @param sel <code>Selections</code> Selections file to set for this sensor
     */
    public void setSelections(Selections sel) {
        mSelections = sel;
    }
}