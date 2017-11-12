package SAM.imagej.plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

/**
 * Creates a new image stack using only the part of the image in the range
 */
public final class SubstackMaker {

    public static ImagePlus run(ImagePlus imp, int start, int end) {

        String stackTitle = "Substack ("+start+"-"+end+")";
        if(stackTitle.length()>25){
            int idxA = stackTitle.indexOf(",",18);
            int idxB = stackTitle.lastIndexOf(",");
            if(idxA>=1 && idxB>=1){
                String strA = stackTitle.substring(0,idxA);
                String strB = stackTitle.substring(idxB+1);
                stackTitle = strA + ", ... " + strB;
            }
        }

        int rangeStart = start;
        int rangeEnd = end;
        int range = rangeEnd-rangeStart+1;
        return stackRange(imp,range,rangeStart,stackTitle);
    }

    private static ImagePlus stackRange(ImagePlus imp, int range, int currSlice, String stackTitle){                  // extract range of slices
        try{
            int width = imp.getWidth();
            int height = imp.getHeight();
            ImageStack stack = imp.getStack();
            ImageStack stack2 = new ImageStack(width, height, imp.getProcessor().getColorModel());

            for (int i=1; i<=range; i++) {
                if(i>1)
                    currSlice += 1;
                ImageProcessor ip2 = stack.getProcessor(currSlice);
                ip2 = ip2.crop();
                stack2.addSlice(stack.getSliceLabel(currSlice), ip2);
            }

            ImagePlus impSubstack = imp.createImagePlus();
            impSubstack.setStack(stackTitle, stack2);
            impSubstack.setCalibration(imp.getCalibration());

            return impSubstack;
        }
        catch(IllegalArgumentException e){
            IJ.error("Argument out of range: ");
        }
        return null;
    }

}