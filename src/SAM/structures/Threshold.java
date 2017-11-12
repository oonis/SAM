package SAM.structures;

import PhenoLog.doc.LogDocument;
import PhenoLog.doc.LogElement;
import PhenoLog.doc.StandardDocument;
import PhenoLog.enums.Type_String;
import PhenoLog.filters.NameFilter;
import PhenoLog.io.StandardReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Thresholding information for the measurements
 */
public class Threshold {

    Map<String, double[]> mThresholds;

    /**
     * Version for the current Threshold object
     */
    public final String VERSION_NUMBER = "1.0";

    public Threshold() {
        mThresholds = new HashMap<>();
    }

    public Threshold(File file) {
        mThresholds = new HashMap<>();

        LogDocument doc = null;

        try {
            doc = StandardReader.read(Type_String.class, null, file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
        }

        // TODO [MED] Read the version number from the file
        
        for (LogElement i : doc.getRootElement().getChildren(new NameFilter("Measurement"))) {
            String name = i.getChild(new NameFilter("Name")).getValue();
            double low = Double.valueOf(i.getChild(new NameFilter("Low")).getValue());
            double high = Double.valueOf(i.getChild(new NameFilter("High")).getValue());
            mThresholds.put(name.toLowerCase(), new double[]{low, high});
        }
    }

    /**
     * Sets the thresholding values for a measurement.
     *
     * @param measureName <code>String</code> Name of the measurement.
     * @param low <code>double</code> Low threshold value.
     * @param high <code>double</code> High threshold value.
     */
    public void setThreshold(String measureName, double low, double high) {

        switch (measureName.toLowerCase()) {
            case "fmpp":
                measureName = "fmm";
                break;
            case "f0fm":
                mThresholds.put("fm", new double[]{low, high});
                mThresholds.put("f0", new double[]{low, high});
                break;
            case "fsfmp":
                mThresholds.put("fs", new double[]{low, high});
                mThresholds.put("fmp", new double[]{low, high});
                break;
        }

        mThresholds.put(measureName.toLowerCase(), new double[]{low, high});
        System.out.println("Added " + measureName);
    }

    /**
     * @param measureName <code>String</code> Name of the measurement to find
     * thresholding for.
     * @return <code>double</code> The high threshold value for the given
     * measurement.
     */
    public double getThresholdHigh(String measureName) {
        double[] things = mThresholds.get(measureName.toLowerCase());
        if (things != null) {
            return things[1];
        }
        System.out.println("MISSING HIGH THRESHOLD FOR " + measureName);
        return -1;
    }

    /**
     * @param measureName <code>String</code> Name of the measurement to find
     * thresholding for.
     * @return <code>double</code> The low threshold value for the given
     * measurement.
     */
    public double getThresholdLow(String measureName) {
        double[] things = mThresholds.get(measureName.toLowerCase());
        if (things != null) {
            return things[0];
        }
        System.out.println("MISSING LOW THRESHOLD FOR " + measureName);
        return -1;
    }

    /**
     * Writes the thresholding file
     *
     * @param file <code>{@Link File}</code> Thresholding file to write to.
     * @throws IOException
     * @throws XMLStreamException
     */
    public void toFile(File file) throws IOException, XMLStreamException {
        PhenoLog.io.Writer.write(toDocument(), file);
    }

    /**
     * @return <code>LogDocument</code> The LogDocument object for writing using
     * PhenoLog.
     */
    public LogDocument toDocument() {
        LogDocument doc = new StandardDocument("Thresholds");
        LogElement ele = doc.initElement("Version", VERSION_NUMBER);
        doc.getRootElement().addChild(ele);

        mThresholds.entrySet().stream().forEach((itter) -> {
            String measureName = itter.getKey();
            double[] vals = itter.getValue();
            double low = vals[0];
            double high = vals[1];

            LogElement measureEle = doc.initElement("Measurement");
            measureEle.addChild(doc.initElement("Name", measureName));
            measureEle.addChild(doc.initElement("Low", String.valueOf(low)));
            measureEle.addChild(doc.initElement("high", String.valueOf(high)));
        });

        return doc;
    }
}
