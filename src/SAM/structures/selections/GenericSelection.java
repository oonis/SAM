package SAM.structures.selections;

/**
 * Generic selection information.
 */
public abstract class GenericSelection implements Selection{
    protected String mName;
    protected long mTime;
    protected int mCenterX, mCenterY;

    public GenericSelection(String name, long time) {
        mName = name;
        mTime = time;
    }

    public GenericSelection(GenericSelection sel) {
        throw new UnsupportedOperationException("Need to do this!");
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public long getTime() {
        return mTime;
    }

    @Override
    public void setTime(long time) {
        mTime = time;
    }

}
