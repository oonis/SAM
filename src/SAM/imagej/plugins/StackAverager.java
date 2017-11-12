package SAM.imagej.plugins;


import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

/**
 * Averages the image stack.
 */
public final class StackAverager {

    /**
     * Averages the image stack.
     * @param imp <code>ImagePlus</code> Image to average.
     * @return <code>ImagePlus</code> Image after averaging pixel values.
     */
    public static ImagePlus run(ImagePlus imp) {
        ImageStack source = imp.getStack();
        int width = imp.getWidth();
        int height = imp.getHeight();

        ImagePlus average = NewImage.createFloatImage("Average of " + imp.getTitle(), width, height, 1, 1);

        ImageProcessor averaster = average.getProcessor();
        for (int i = 1; i <= imp.getNSlices(); i++) {
            ImageProcessor slicedata = source.getProcessor(i);
            averaster.copyBits(slicedata, 0, 0, 3);
        }
        averaster.multiply(1.0D / imp.getNSlices());
        return average;
    }
}