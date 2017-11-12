package SAM.structures;

import java.util.List;

/**
 * Holds all of the information about an Environment variable from the XML.
 */
public class Environment {
    private final String mName; // ex: light, humidity, etc...
    private final List<Factor> mFactors; // All of the factors for that variable

    /**
     * @param name <code>String</code> Name for the environmental factor
     * @param factors <code>List</code> All of the factors for that environment.
     */
    public Environment(String name, List<Factor> factors) {
        mName = name;
        mFactors = factors;
    }

    /**
     * @return <code>String</code> Name of the environment factor
     */
    public String getName() {
        return mName;
    }

    /**
     * @return <code>List</code> All of the factors in the environment
     */
    public List<Factor> getFactors() {
        return mFactors;
    }
    
    @Override
    public String toString() {
        return mName+" has "+ mFactors.size()+" factors.";
    }
}
