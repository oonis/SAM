package SAM.imagej;

/**
 * Listener for when the thresholds in ThresholdAdjuster have been set.
 */
public interface ThresholdListener {
    /**
     * Thresholds have been set and finalized
     */
    public void thresholdsSet();
}
