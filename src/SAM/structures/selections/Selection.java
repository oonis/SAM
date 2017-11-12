package SAM.structures.selections;

import PhenoLog.doc.LogDocument;
import ij.ImagePlus;

import java.io.File;

/**
 *
 */
public interface Selection {

    /**
     * Crops the file to fit the selection.
     * @param img <code>ImagePlus</code> Image to crop.
     * @param file <code>File</code> Where to save the trimmed image, null if not saving.
     * @return <code>ImagePlus</code> Image after cropping out the selection
     */
    public ImagePlus crop(ImagePlus img, File file);
    
    /**
     *
     * @param doc
     */
    public void toSelectionFile(LogDocument doc);

    /**
     * @return <code>int[]</code> Center [x,y] for the selection.
     */
    public int[] getCenter();

    /**
     * @return <code>String</code> Name of the selection.
     */
    public String getName();

    /**
     * Set the name of the Selection
     * @param name <code>String</code> Name to set the selection
     */
    public void setName(String name);

    /**
     * @return <code>long</code> Time for that selection.
     */
    public long getTime();

    /**
     * Set the time for the selection
     * @param time <code>long</code> Time to set the selection to
     */
    public void setTime(long time);

    /**
     * @return <code>String</code> The type of the selection
     */
    public String getType();
}
