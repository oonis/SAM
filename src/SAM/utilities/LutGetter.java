package SAM.utilities;

import ij.plugin.LutLoader;
import ij.process.LUT;

import java.io.File;

/**
 * Gets the LUT files used when viewing images
 */
public class LutGetter {
    
    private static final String mLutFolderLocation =
            System.getProperty("user.dir")+ File.separator+"lib"+File.separator+"LUTS"+File.separator;

    /**
     * @return <code>ij.process.LUT</code> The royal LUT
     */
    public static LUT royal() {
        String royalLocation = mLutFolderLocation+"royal.lut";
        return LutLoader.openLut(royalLocation);
    }

    /**
     * @return <code>ij.process.LUT</code> The sepia LUT
     */
    public static LUT sepia() {
        String sepiaLocation = mLutFolderLocation + "sepia.lut";
        return LutLoader.openLut(sepiaLocation);
    }

}
