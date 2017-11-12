package SAM.structures.selections;

import PhenoLog.doc.LogDocument;
import SAM.structures.coordinates.RectangularCoordinate;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import java.io.File;

/**
 * A selection that was made with a rectangular shape.
 */
public class RectangularSelection extends GenericSelection{
    private RectangularCoordinate mCoordinates;

    public RectangularSelection(String name, long time, RectangularCoordinate coords) {
        super(name,time);
        mCoordinates = coords;
    }

    /**
     * Copy constructor
     * @param sel <code>{@Link RectangularSelection}</code> Selection to copy over.
     */
    public RectangularSelection(RectangularSelection sel) {
        super(sel);
        int[] centers = sel.getCenter();
        mCenterX = centers[0];
        mCenterY = centers[1];
        mCoordinates = new RectangularCoordinate(sel.getCoordinates());
        mTime = sel.getTime();
        mName = sel.getName();

    }

	@Override
    public int[] getCenter() {
        return new int[]{mCenterX,mCenterY};
    }

    @Override
    public void toSelectionFile(LogDocument doc) {
        throw new UnsupportedOperationException("Need to do this for Selections 2.0");
    }

    @Override
    public ImagePlus crop(final ImagePlus img, File file) {
        ImageStack stack = img.getStack();
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();
        double widthRatio = mCoordinates.getWidth();
        double heightRatio = mCoordinates.getHeight();
        int depth = img.getStackSize();
        int x1 = (int)(mCoordinates.getRelativeX()*imageWidth);
        int y1 = (int) (mCoordinates.getRelativeY()*imageHeight);
        int width = (int)(widthRatio*imageWidth);
        int height = (int)(heightRatio*imageHeight);

        ImageStack trimmed = crop(stack, x1, y1, 0, width, height, depth);

        ImagePlus result = new ImagePlus(mName,trimmed);

        if(file != null) {
            IJ.save(result,file.getAbsolutePath());
        }

        return result;
    }

    private static ImageStack crop(ImageStack stack, int x, int y, int z, int width, int height, int depth) {
        ImageStack stack2 = new ImageStack(width, height, stack.getColorModel());
        for (int i=z; i<z+depth; i++) {
            ImageProcessor ip2 = stack.getProcessor(i+1);
            ip2.setRoi(x, y, width, height);
            ip2 = ip2.crop();
            stack2.addSlice(stack.getSliceLabel(i+1), ip2);
        }
        return stack2;
    }

    @Override
    public String getName() {
        return mName;
    }

    /**
     * @return <Code>RectangularCoordinate</Code> Coordinates used for drawing the selection.
     */
    public RectangularCoordinate getCoordinates() {
        return mCoordinates;
    }

    public void setCoordinates(RectangularCoordinate coordinates) {
        mCoordinates = coordinates;
    }

    @Override
    public String toString() {
        return mName+"\t"+mCoordinates.toString();
    }

    @Override
    public String getType() {
        return "Rectangular";
    }

    @Override
    public int hashCode() {
        return (int)(mCoordinates.getRelativeX()+mCoordinates.getRelativeY()+mCoordinates.getHeight())*31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RectangularSelection other = (RectangularSelection)obj;
        return (mCoordinates.equals(other.getName()) && mCoordinates.equals(other.getCoordinates()));
    }
}
