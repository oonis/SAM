package SAM.structures.coordinates;

/**
 * Abstract class which is used for any coordinates used. Also houses generic methods.
 */
public abstract class GenericCoordinate implements Coordinate{
    /**
     * Converts the relative coordinates in the image to the local pixel values.
     *
     * @param relX <code>double</code> Relative X value
     * @param relY <code>double</code> Relative Y value
     * @param imgW <code>int</code> Image Width value
     * @param imgH <code>int</code> Image Height value
     * @return <code>int[]</code> [x,y]
     */
    public static int[] convertRelativeToLocal(double relX,double relY,int imgW,int imgH) {
        return new int[]{(int)(relX*imgW),(int)(relY*imgH)};
    }

    /**
     * Converts local coordinates to the relative positions in the image.
     *
     * @param localX
     * @param localY
     * @param imgW
     * @param imgH
     * @return <code>double[]</code> [x,y]
     */
    public static double[] convertLocalToRelative(int localX,int localY,int imgW,int imgH) {
        return new double[]{(double)localX/imgW,(double)localY/imgH};
    }
}
