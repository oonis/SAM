package SAM.structures.selections;

import PhenoLog.doc.LogDocument;
import PhenoLog.doc.LogElement;
import PhenoLog.doc.StandardDocument;
import PhenoLog.enums.Type_String;
import PhenoLog.filters.HasPropertyFilter;
import PhenoLog.filters.NameFilter;
import PhenoLog.filters.ValueFilter;
import PhenoLog.io.StandardReader;
import SAM.structures.coordinates.RectangularCoordinate;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * TODO for Selections 2.0 : Make it work for more than just
 * RectangularSelection
 *
 * @version 1.1
 */
public class Selections {

    /**
     * The version number for the Selections object being looked at
     */
    public final String VERSION_NUMBER = "1.1";

    private final Map<String, List<RectangularSelection>> mSelectionLists;

    public Selections() {
        mSelectionLists = new HashMap<>();
    }

    /**
     * Creates a Selections object from a selections file
     *
     * @param file <code>File</code> Location of the selections file
     */
    public Selections(File file) {
        mSelectionLists = new HashMap<>();

        LogDocument doc = null;

        try {
            doc = StandardReader.read(Type_String.class, null, file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        // TODO [MED] Read the version number from the file
        for (LogElement i : doc.getRootElement().getChildren(new NameFilter("Selection"))) {
            String name = i.getChild(new NameFilter("Name")).getValue();
            String timeStr = i.getChild(new NameFilter("Time")).getValue();
            long time = Long.valueOf(timeStr);

            String startXStr = i.getChild(
                    new NameFilter("Start"),
                    new HasPropertyFilter(ii -> ii == 1,
                            new NameFilter("axis"),
                            new ValueFilter("x")))
                    .getValue();
            String startYStr = i.getChild(
                    new NameFilter("Start"),
                    new HasPropertyFilter(ii -> ii == 1,
                            new NameFilter("axis"),
                            new ValueFilter("y")))
                    .getValue();
            double xStart = Double.valueOf(startXStr);
            double yStart = Double.valueOf(startYStr);

            String widthStr = i.getChild(
                    new NameFilter("Dimension"),
                    new HasPropertyFilter(ii -> ii == 1,
                            new NameFilter("type"),
                            new ValueFilter("width")))
                    .getValue();

            String heightStr = i.getChild(
                    new NameFilter("Dimension"),
                    new HasPropertyFilter(ii -> ii == 1,
                            new NameFilter("type"),
                            new ValueFilter("height")))
                    .getValue();
            double width = Double.valueOf(widthStr);
            double height = Double.valueOf(heightStr);

            RectangularCoordinate coords = new RectangularCoordinate(xStart, yStart, width, height);
            RectangularSelection selection = new RectangularSelection(name, time, coords);
            addSelection(selection);
        }
    }

    /**
     * Copy constructor
     *
     * @param other <code>Selections</code> Selections file to copy.
     */
    public Selections(Selections other) {
        mSelectionLists = new HashMap<>(other.getSelectionMap());
    }

    /**
     * Add a selection to the Selections object
     *
     * @param sel <code>Selection</code> Selection to add
     */
    public void addSelection(RectangularSelection sel) {
        List<RectangularSelection> list = mSelectionLists.get(sel.getName());
        if (list == null) {
            list = new ArrayList<>();
            list.add(sel);
            mSelectionLists.put(sel.getName(), list);
        } else {
            list.add(sel);
        }
    }

    /**
     * Gets the latest selections.
     *
     * @param time <code>long</code> Latest time to get a selection
     * @return <code>Set</code> Set of most recent selections to time for each
     * name
     */
    public Set<Selection> getSelections(long time) {
        Set<Selection> out = new HashSet<>();

        for (Map.Entry<String, List<RectangularSelection>> itter : mSelectionLists.entrySet()) {
            Selection mostRecent = null;
            for (Selection i : itter.getValue()) {
                mostRecent = (i.getTime() <= time
                        ? (mostRecent == null || i.getTime() > mostRecent.getTime())
                                ? i : mostRecent : null);
            }
            if (mostRecent != null) {
                out.add(mostRecent);
            } else {
                out.add(itter.getValue().get(0));
                System.out.println("Adding null... That's bad");
            }
        }

        return out;
    }

    public Map<String, List<RectangularSelection>> getSelectionMap() {
        return mSelectionLists;
    }

    /**
     *
     * @param file
     * @throws IOException
     * @throws XMLStreamException
     */
    public void toFile(File file) throws IOException, XMLStreamException {
        PhenoLog.io.Writer.write(toDocument(), file);
    }

    /**
     * Creates the LogDocument for writing to a file Writes the selections file
     */
    public LogDocument toDocument() {

        LogDocument doc = new StandardDocument("Selections");
        LogElement ele = doc.initElement("Version", VERSION_NUMBER);
        doc.getRootElement().addChild(ele);
        for (Map.Entry<String, List<RectangularSelection>> itter : mSelectionLists.entrySet()) {
            for (RectangularSelection selection : itter.getValue()) {
                LogElement rootEle = doc.initElement("Selection");
                rootEle.addChild(doc.initElement("Name", selection.getName()));
                rootEle.addChild(doc.initElement("Time", String.valueOf(selection.getTime())));

                RectangularCoordinate coordinate = selection.getCoordinates();
                rootEle.addChild(
                        doc.initElement("Start", String.valueOf(coordinate.getRelativeX()))
                        .addProperty(doc.initProperty("axis", "x")))
                        .addChild(
                                doc.initElement("Start", String.valueOf(coordinate.getRelativeY()))
                                .addProperty(doc.initProperty("axis", "y")));

                rootEle.addChild(
                        doc.initElement("Dimension", String.valueOf(coordinate.getWidth()))
                        .addProperty(doc.initProperty("type", "width")))
                        .addChild(
                                doc.initElement("Dimension", String.valueOf(coordinate.getHeight()))
                                .addProperty(doc.initProperty("type", "height")));
                doc.getRootElement().addChild(rootEle);
            }

        }

        return doc;
    }
}
