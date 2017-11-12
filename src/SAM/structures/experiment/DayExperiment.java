package SAM.structures.experiment;

import PhenoLog.doc.LogDocument;
import PhenoLog.doc.LogElement;
import PhenoLog.enums.Type_String;
import PhenoLog.filters.HasPropertyFilter;
import PhenoLog.filters.NameFilter;
import PhenoLog.filters.ValueFilter;
import PhenoLog.io.StandardReader;
import SAM.excepts.MissingAllMeasurementsException;
import SAM.excepts.MissingMeasurementException;
import SAM.math.Computation;
import SAM.math.FVFMComputation;
import SAM.math.NPQComputation;
import SAM.math.Phi2Computation;
import SAM.math.QEComputation;
import SAM.math.QESVComputation;
import SAM.math.QIComputation;
import SAM.structures.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * The experiment info per day
 */
public final class DayExperiment {
    private File mLogFile; // The XML file for the experiment

    private Set<Measurement> mMeasurements; // Measurements found in the experiment (FsFmp...)
    private Set<Environment> mEnvironments; // All of the environmental factors

    private SensorExperiment mSensorExperiment;
    private int mDayNumber;
    private Properties mDayInfo = new Properties();

    /**
     * Constructs a new DayExperiment.
     *
     * @param exp
     * @param file
     * @param warnings
     * @throws IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws SAM.excepts.MissingAllMeasurementsException
     */
    public DayExperiment(SensorExperiment exp, File file,List<String> warnings)
            throws IOException, ParserConfigurationException, SAXException, MissingAllMeasurementsException {
        mSensorExperiment = exp;

        {   // Deal with the .sensorinfo file
            File[] infofiles = file.listFiles((File dir, String name) -> {
                return name.endsWith(".dayinfo");
            });
            if (infofiles.length == 0) {
                System.out.println(toString());
                throw new IOException("Missing .dayinfo file");
            } else if (infofiles.length == 1) {
                try (FileInputStream instream = new FileInputStream(infofiles[0]) ) {
                    mDayInfo.load(instream);
                    mDayNumber = Integer.parseInt(mDayInfo.getProperty("DayNumber"));
                    mLogFile = new File(file+File.separator+mDayInfo.getProperty("LogFile"));
                }
            } else {
                System.out.println(toString());
                throw new IOException("Multiple .dayinfo files");
            }
        }
        if(mLogFile == null) {
            System.out.println(toString());
            throw new IOException("Missing LogFile in .dayinfo");
        }
        readLog(mLogFile);
    }

    /**
     * @param file
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws SAM.excepts.MissingAllMeasurementsException
     */
    protected void readLog(File file) throws
            IOException, ParserConfigurationException, SAXException, MissingAllMeasurementsException {
        mMeasurements = new HashSet<>();

        LogDocument doc = null;
        try {
            doc = StandardReader.read(Type_String.class, null, file);
        } catch (ParserConfigurationException e) {
            System.out.println(toString());
            throw new ParserConfigurationException("Unable to read the document.");
        } catch (SAXException e) {
            System.out.println(toString());
            throw new SAXException("Unable to read the document.");

        }

        // Get all of the Measurements
        for (LogElement i : doc.getRootElement().getChildren(new NameFilter("Measurement"))) {
            readMeasurementTag(i);
        }
        if(mMeasurements.isEmpty()) {
            System.out.println(toString());
            throw new MissingAllMeasurementsException(mDayNumber);
        }

        // Now lets read the experiment tag
        LogElement expEle = doc.getRootElement().getChild(new NameFilter("Experiment"));


        // Get the environment shit
        mEnvironments = new HashSet<>();
        for (LogElement envItter : expEle.getChildren(new NameFilter("Environment"))) {
            String envType = envItter.getProperty(new NameFilter("type")).getValue();

            List<Factor> allFactors = new ArrayList<>();
            for (LogElement factorItter : envItter.getChildren(new NameFilter("Factor"))) {

                // Get the starting value of the factor
                String startVal_str = factorItter.getChild(new NameFilter("Value"),
                        new HasPropertyFilter(zz -> zz == 1,
                                new NameFilter("type"),
                                new ValueFilter("Start")))
                        .getValue();
                double startVal = Double.valueOf(startVal_str);

                // Get the ending value of the factor
                String endVal_str = factorItter.getChild(new NameFilter("Value"),
                        new HasPropertyFilter(zz -> zz == 1,
                                new NameFilter("type"),
                                new ValueFilter("End")))
                        .getValue();
                double endVal = Double.valueOf(startVal_str);

                // Get when the factor starts
                String startTime = factorItter.getChild(new NameFilter("Time"),
                        new HasPropertyFilter(zz -> zz == 1,
                                new NameFilter("type"),
                                new ValueFilter("Start")))
                        .getValue();

                // Get when the factor ends
                String endTime = factorItter.getChild(new NameFilter("Time"),
                        new HasPropertyFilter(zz -> zz == 1,
                                new NameFilter("type"),
                                new ValueFilter("End")))
                        .getValue();

                allFactors.add(new Factor(startTime, endTime, startVal, endVal));
            }
            mEnvironments.add(new Environment(envType, allFactors));
        }
        if(mEnvironments.isEmpty()) {
            System.out.println(toString());
            // This shouldn't be a problem though
            //throw new IOException("Missing all environments");
        }

    }
    
    /**
     * @return <code>SensorExperiment</code> The associated SensorExperiment
     */
    public SensorExperiment getSensorExperiment() {
        return mSensorExperiment;
    }

    /**
     * @return <code>Set</code> All of the environment's for this day.
     */
    public Set<Environment> getAllEnvironments() {
        return mEnvironments;
    }

    /**
     * @param measureName <code>String</code> Name of the measurement to get.
     * @return <code>Measurement</code> The {@Link Measurement} object being asked for.
     */
    public Measurement getMeasurement(String measureName) {
        for (Measurement itter : mMeasurements) {
            if (itter.getName().toLowerCase().equals(measureName.toLowerCase())) {
                return itter;
            }
        }
        return null;
    }

    /**
     * Gets all images sorted by date. This function gets a new List each time: It is not ideal to call it
     * repeatedly.
     *
     * @return <code>{@link List }&lt;{@link ImageInformation }&gt;</code>: A List of all ImageInformations sorted by
     * the date they were taken.
     */
    public List<ImageInformation> getAllImages() {
        List<ImageInformation> allImages = new ArrayList<>();
        mMeasurements.stream().forEach((itter) -> {
            allImages.addAll(itter.getImages());
        });
        Collections.sort(allImages);
        return allImages;
    }

    /**
     * @return <code>int</code> Day number for the experiment.
     */
    public int getDayNumber() {
        return mDayNumber;
    }

    /**
     * @param i <code>{@link LogElement}</code> The log element for the current measurement
     */
    private void readMeasurementTag(LogElement i) {
        String measurementName = i.getChild(new NameFilter("Name")).getValue();

        LogElement imageTag = i.getChild(new NameFilter("Image"));
        String path = imageTag.getChild(
                new NameFilter("Path"),
                new HasPropertyFilter(j -> j == 1,
                        new NameFilter("type"),
                        new ValueFilter("unix")))
                .getValue();

        // Get all of the ImageInformation shit
        List<ImageInformation> allImages = new ArrayList<>();
        imageTag.getChildren(new NameFilter("File")).stream().forEach((z) -> {
            String imageLocation = z.getChild(new NameFilter("Filename")).getValue();
            imageLocation = mLogFile.getParent() + File.separator + imageLocation;
            String time = z.getChild(
                    new NameFilter("Time"),
                    new HasPropertyFilter(ii -> ii == 1,
                            new NameFilter("type"),
                            new ValueFilter("Start")))
                    .getValue();
            allImages.add(new ImageInformation(imageLocation, time));
        });

        LogElement actionTag = i.getChild(new NameFilter("Action"));
        // Iterate through all AverageImage tags for the measurement
        List<AverageImage> averageImages = new ArrayList<>();
        for (LogElement averageItter : actionTag.getChildren(new NameFilter("AverageImage"))) {
            String averageID = averageItter.getChild(new NameFilter("ID")).getValue();
            String averageName = averageItter.getChild(new NameFilter("Name")).getValue();

            // NOTE: This is going to be changed to a seperate thing
            String startSliceStr = averageItter.getChild(new NameFilter("Slice"),
                    new HasPropertyFilter(zz -> zz == 1,
                            new NameFilter("type"),
                            new ValueFilter("Start")))
                    .getValue();
            String endSliceStr = averageItter.getChild(new NameFilter("Slice"),
                    new HasPropertyFilter(zz -> zz == 1,
                            new NameFilter("type"),
                            new ValueFilter("End")))
                    .getValue();

            int startSlice = Integer.parseInt(startSliceStr);
            int endSlice = Integer.parseInt(endSliceStr);

            averageImages.add(new AverageImage(averageName, averageID, startSlice, endSlice));
        }

        switch (measurementName.toLowerCase()) {
            case "f0fm":
                getF0fm(allImages, averageImages);
                break;
            case "fsfmp":
                getFmp(allImages, averageImages);
                break;
            case "fmpp":
                getFmpp(allImages, averageImages);
                break;
        }
    }

    private void getFmpp(List<ImageInformation> allImages, List<AverageImage> averageImages) {
        AverageImage fmmraw = null;
        AverageImage fmmbk = null;

        for (AverageImage itter : averageImages) {
            switch (itter.getName().toLowerCase()) {
                case "fmm":
                    fmmraw = itter;
                    break;
                case "fmmbk":
                    fmmbk = itter;
                    break;
                default:
                    System.out.println("Unknown AverageImage tag for fmpp");
                    break;
            }
        }
        Measurement fmm = new Measurement(this,"fmm", allImages, fmmraw, fmmbk);
        mMeasurements.add(fmm);
    }

    private void getFmp(List<ImageInformation> allImages, List<AverageImage> averageImages) {
        System.out.println("Getting fsfmp");
        AverageImage fsraw = null;
        AverageImage fsbk = null;
        AverageImage fmpraw = null;
        AverageImage fmpbk = null;

        for (AverageImage itter : averageImages) {
            switch (itter.getName().toLowerCase()) {
                case "fsraw":
                    fsraw = itter;
                    break;
                case "fsbk":
                    fsbk = itter;
                    break;
                case "fmpraw":
                    fmpraw = itter;
                    break;
                case "fmpbk":
                    fmpbk = itter;
                    break;
                default:
                    System.out.println("Unknown AverageImage tag for fmp");
                    break;
            }
        }

        Measurement fs = new Measurement(this,"fs", allImages, fsraw, fsbk);
        Measurement fmp = new Measurement(this,"fmp", allImages, fmpraw, fmpbk);

        mMeasurements.add(fs);
        mMeasurements.add(fmp);
    }
    
    /**
     * Generates phi2 for the day
     * @return <code>{@Link Computation}</code> The phi2 computation for the day.
     * @throws MissingMeasurementException Either fs or fmp are missing for this day.
     */
    public Computation getPhi2() throws MissingMeasurementException {
        Measurement fs = getMeasurement("fs");
        if(fs == null) {
            throw new MissingMeasurementException("phi2","fs",mDayNumber,mSensorExperiment.getSensorID());
        }
        Measurement fm = getMeasurement("fm");
        if(fm == null) {
            throw new MissingMeasurementException("phi2","fm",mDayNumber,mSensorExperiment.getSensorID());
        }
		List<Measurement> measurements = new ArrayList<>();
		measurements.add(fs);
		measurements.add(fm);
        return new Phi2Computation(mSensorExperiment.getSelections(),measurements);
    }
    
    /**
     * Generates qI for the day.
     * @return <code>{@Link Computation}</code> The qI computation for the day.
     * @throws MissingMeasurementException Either fm or fmm is missing for this day.
     */
    public Computation getQI() throws MissingMeasurementException {
        Measurement fm = getMeasurement("fm");
        if(fm == null) {
            throw new MissingMeasurementException("qI","fm",mDayNumber,mSensorExperiment.getSensorID());
        }
        Measurement fmm = getMeasurement("fmm");
        if(fmm == null) {
            throw new MissingMeasurementException("qI","fmm",mDayNumber,mSensorExperiment.getSensorID());
        }
		List<Measurement> measurements = new ArrayList<>();
		measurements.add(fm);
		measurements.add(fmm);
        return new QIComputation(mSensorExperiment.getSelections(),measurements);
    }
    
    /**
     * Generates NPQ for the day.
     * @return <code>{@Link Computation}</code> The NPQ computation for the day.
     * @throws MissingMeasurementException Either fm or fmp is missing for this day.
     */
    public Computation getNPQ() throws MissingMeasurementException {
        Measurement fm = getMeasurement("fm");
        if(fm == null) {
            throw new MissingMeasurementException("NPQ", "fm", mDayNumber, mSensorExperiment.getSensorID());
        }
        Measurement fmp = getMeasurement("fmp");
        if(fmp == null) {
            throw new MissingMeasurementException("NPQ", "fmp", mDayNumber, mSensorExperiment.getSensorID());
        }
        
        List<Measurement> measurements = new ArrayList<>();
        
        measurements.add(fm);
        measurements.add(fmp);
        
        return new NPQComputation(mSensorExperiment.getSelections(),measurements);
    }
    
    /**
     * Generates qE for the day.
     * @return <code>{@Link Computation}</code> The qE computation for the day.
     * @throws MissingMeasurementException Either fs or fmp are missing for this day.
     */
    public Computation getQE() throws MissingMeasurementException {
        Measurement fmm = getMeasurement("fmm");
        if(fmm == null) {
            throw new MissingMeasurementException("qE", "fmm", mDayNumber, mSensorExperiment.getSensorID());
        }
        Measurement fmp = getMeasurement("fmp");
        if(fmp == null) {
            throw new MissingMeasurementException("qE", "fmp", mDayNumber, mSensorExperiment.getSensorID());
        }
        
        List<Measurement> measurements = new ArrayList<>();
        
        measurements.add(fmm);
        measurements.add(fmp);
        
        return new QEComputation(mSensorExperiment.getSelections(),measurements);
    }
    
    public Computation getQESV() throws MissingMeasurementException {
        Measurement fm = getMeasurement("fm");
        Measurement fmp = getMeasurement("fmp");
        Measurement fmm = getMeasurement("fmm");
        
        if(fmm == null) {
            throw new MissingMeasurementException("qESV", "fmm", mDayNumber, mSensorExperiment.getSensorID());
        }
        if(fm == null) {
            throw new MissingMeasurementException("qESV", "fm", mDayNumber, mSensorExperiment.getSensorID());
        }
        if(fmp == null) {
            throw new MissingMeasurementException("qESV", "fmp", mDayNumber, mSensorExperiment.getSensorID());
        }
        
        List<Measurement> measurements = new ArrayList<>();
        measurements.add(fm);
        measurements.add(fmp);
        measurements.add(fmm);
        
        return new QESVComputation(mSensorExperiment.getSelections(),measurements);
    }
    
    public Computation getFvFm() throws MissingMeasurementException {
        Measurement fm = getMeasurement("fm");
        if(fm == null) {
            throw new MissingMeasurementException("FvFm", "fm", mDayNumber, mSensorExperiment.getSensorID());
        }
        Measurement f0 = getMeasurement("f0");
        if(f0 == null) {
            throw new MissingMeasurementException("FvFm", "f0", mDayNumber, mSensorExperiment.getSensorID());
        }
        
        List<Measurement> measurements = new ArrayList<>();
        
        measurements.add(fm);
        measurements.add(f0);
        
        return new FVFMComputation(mSensorExperiment.getSelections(),measurements);
    }

    private void getF0fm(List<ImageInformation> allImages, List<AverageImage> averageImages) {
        AverageImage f0raw = null;
        AverageImage f0bkgd = null;
        AverageImage fmraw = null;
        AverageImage fmbkgd = null;

        // Get all of the averageImages
        for (AverageImage itter : averageImages) {
            switch (itter.getName().toLowerCase()) {
                case "f0raw":
                    f0raw = itter;
                    break;
                case "f0bkgd":
                    f0bkgd = itter;
                    break;
                case "fmraw":
                    fmraw = itter;
                    break;
                case "fmbkgd":
                    fmbkgd = itter;
                    break;
                default:
                    System.out.println("Unknown AverageImage tag for f0fm");
                    break;
            }
        }

        Measurement f0 = new Measurement(this,"f0", allImages, f0raw, f0bkgd);
        Measurement fm = new Measurement(this,"fm", allImages, fmraw, fmbkgd);

        mMeasurements.add(f0);
        mMeasurements.add(fm);
    }

    /**
     * @return <code>Set</code> all base measurements.
     */
    public Set<Measurement> getAllMeasurements() {
        return mMeasurements;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }


}
