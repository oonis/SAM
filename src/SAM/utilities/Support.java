package SAM.utilities;

import ij.ImagePlus;
import ij.gui.StackWindow;

/**
 * These are various, random, functions for debugging.
 */
public class Support {
	
	/**
	 * Displays a tiff stack
	 * @param img <code>ImagePlus</code> Image to display.
	 */
	public static void displayStack(ImagePlus img) {
		StackWindow window = new StackWindow(img);
		window.setVisible(true);
	}
}
