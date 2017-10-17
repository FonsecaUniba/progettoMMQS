package zame.game.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class representing a Mark
 */
@SuppressWarnings("WeakerAccess")
public class Mark implements Externalizable {
    /**
     * Mark ID
     */
    public int id;
    /**
     * Mark Position X
     */
    public int x;
    /**
     * Mark Position Y
     */
    public int y;

    /**
     * Class constructor
     */
    public Mark(){}

    /**
     * Writes Marks on External file
     * @param os Output stream
     * @throws IOException Error while writing
     */
    @Override
    public void writeExternal(ObjectOutput os) throws IOException {
        os.writeInt(id);
        os.writeInt(x);
        os.writeInt(y);
    }

    /**
     * Reads Marks on Input file
     * @param is Input Stream
     * @throws IOException Error while reading
     */
    @Override
    public void readExternal(ObjectInput is) throws IOException {
        id = is.readInt();
        x = is.readInt();
        y = is.readInt();
    }
}
