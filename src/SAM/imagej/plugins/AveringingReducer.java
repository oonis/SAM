package SAM.imagej.plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * Averages a tiff stack to a single slice
 */
public final class AveringingReducer {
    private static final int xshrink=1, yshrink=1;
    double product;
    int[] pixel = new int[3];
    int[] sum = new int[3];
    int samples;

    /**
     * Averages and shrinks the image stack
     * @param imp <code>ImagePlus</code> Image to average and reduce.
     * @return <code>ImagePlus</code> Image after averaging and reduction.
     */
    public ImagePlus shrink(final ImagePlus imp) {
        ImageStack stack1 = imp.getStack();
        ImageStack stack2 = new ImageStack(imp.getWidth()/xshrink,imp.getHeight()/yshrink);
        int n = stack1.getSize();
        for (int i=1; i<=n; i++) {
            IJ.showStatus(i+"/"+n);
            IJ.showProgress(i, n);
            ImageProcessor ip2 = shrink(stack1.getProcessor(i));
            stack2.addSlice(null, ip2);
        }
        ImagePlus imp2 = new ImagePlus("Reduced "+imp.getShortTitle(), stack2);
        imp2.setCalibration(imp.getCalibration());
        Calibration cal2 = imp2.getCalibration();
        if (cal2.scaled()) {
            cal2.pixelWidth *= xshrink;
            cal2.pixelHeight *= yshrink;
        }
        return imp2;
    }

    public ImageProcessor shrink(ImageProcessor ip) {
        if (ip instanceof FloatProcessor)
            return shrinkFloat(ip);
        samples = ip instanceof ColorProcessor ?3:1;
        int w = ip.getWidth()/xshrink;
        int h = ip.getHeight()/yshrink;
        ImageProcessor ip2 = ip.createProcessor(w, h);
        for (int y=0; y<h; y++)
            for (int x=0; x<w; x++)
                ip2.putPixel(x, y, getAverage(ip, x, y));
        return ip2;
    }

    int[] getAverage(ImageProcessor ip, int x, int y) {
        for (int i=0; i<samples; i++)
            sum[i] = 0;
        for (int y2=0; y2<yshrink; y2++) {
            for (int x2=0;  x2<xshrink; x2++) {
                pixel = ip.getPixel(x*xshrink+x2, y*yshrink+y2, pixel);
                for (int i=0; i<samples; i++)
                    sum[i] += pixel[i];
            }
        }
        for (int i=0; i<samples; i++)
            sum[i] = (int)(sum[i]/product+0.5);
        return sum;
    }

    ImageProcessor shrinkFloat(ImageProcessor ip) {
        int w = ip.getWidth()/xshrink;
        int h = ip.getHeight()/yshrink;
        ImageProcessor ip2 = ip.createProcessor(w, h);
        for (int y=0; y<h; y++)
            for (int x=0; x<w; x++)
                ip2.putPixelValue(x, y, getFloatAverage(ip, x, y));
        return ip2;
    }

    float getFloatAverage(ImageProcessor ip, int x, int y) {
        double sum = 0.0;
        for (int y2=0; y2<yshrink; y2++)
            for (int x2=0;  x2<xshrink; x2++)
                sum += ip.getPixelValue(x*xshrink+x2, y*yshrink+y2);
        return (float)(sum/product);
    }

}