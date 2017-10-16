package zame.game.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class Representing an Action
 */
@SuppressWarnings("WeakerAccess")
public class Action implements Externalizable {
    /**
     * Class Constructor
     */
    public Action(){}

    /**
     * Action Type
     */
    public int type;
    /**
     * Action Mark
     */
    public int mark;
    /**
     * Action Param
     */
    public int param;

    /**
     * Write Action on file
     * @param os Output Stream
     * @throws IOException Error while writing
     */
    @Override
    public void writeExternal(ObjectOutput os) throws IOException {
        os.writeInt(type);
        os.writeInt(mark);
        os.writeInt(param);
    }

    /**
     * Reads Action from file
     * @param is Input Stream
     * @throws IOException Error while reading
     */
    @Override
    public void readExternal(ObjectInput is) throws IOException {
        type = is.readInt();
        mark = is.readInt();
        param = is.readInt();
    }
}
