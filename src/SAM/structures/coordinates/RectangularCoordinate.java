package SAM.structures.coordinates;

/**
 * Coordinates that draw a rectangle
 */
public class RectangularCoordinate extends GenericCoordinate {
    private final double mRelativeX, mRelativeY;
    private final double mWidth, mHeight;

    /**
     * @param relativeX
     * @param relativeY
     * @param width
     * @param height
     */
    public RectangularCoordinate(double relativeX, double relativeY, double width, double height) {
        this.mRelativeX = relativeX;
        this.mRelativeY = relativeY;
        this.mWidth = width;
        this.mHeight = height;
    }

    public RectangularCoordinate(RectangularCoordinate coord) {
        mRelativeX = coord.getRelativeX();
        mRelativeY = coord.getRelativeY();
        mWidth = coord.getWidth();
        mHeight = coord.getHeight();
    }

    /**
     * @return <code>double</code> Relative X value.
     */
    public double getRelativeX() {
        return mRelativeX;
    }

    /**
     * @return <code>double</code> Relative Y value.
     */
    public double getRelativeY() {
        return mRelativeY;
    }

    /**
     * @return <code>double</code> Relative width.
     */
    public double getWidth() {
        return mWidth;
    }

    /**
     * @return <code>double</code> Relative height.
     */
    public double getHeight() {
        return mHeight;
    }

    @Override
    public String getType() {
        return "rectangular";
    }

    @Override
    public String toString() {
        return mRelativeX + "," + mRelativeY + "," + mWidth + "," + mHeight;
    }
}
