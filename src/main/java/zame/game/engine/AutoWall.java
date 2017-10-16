package zame.game.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class Representing an AutoWall
 */
@SuppressWarnings("WeakerAccess")
public class AutoWall implements Externalizable {
    /**
     * Class Constructor
     */
    public AutoWall(){}
    /**
     * Position X where Wall Starts
     */
    public float fromX;
    /**
     * Position Y where Wall Starts
     */
    public float fromY;
    /**
     * Position X where Wall Ends
     */
    public float toX;
    /**
     * Position Y where Wall Ends
     */
    public float toY;
    /**
     * Does Wall Open Vertically?
     */
    public boolean vert;
    /**
     * Wall Type
     */
    public int type;
    /**
     * Door ID
     */
    public int doorIndex; // required for save/load
    /**
     * Door attached to AutoWall
     */
    public Door door;

    /**
     * Copies Data from another AutoWall
     * @param aw AutoWall to copy
     */
    public void copyFrom(AutoWall aw) {
        fromX = aw.fromX;
        fromY = aw.fromY;
        toX = aw.toX;
        toY = aw.toY;
        vert = aw.vert;
        type = aw.type;
        doorIndex = aw.doorIndex;
        door = aw.door;
    }

    /**
     * Writes info on file
     * @param os Output Stream
     * @throws IOException Error while writing
     */
    @Override
    public void writeExternal(ObjectOutput os) throws IOException {
        os.writeFloat(fromX);
        os.writeFloat(fromY);
        os.writeFloat(toX);
        os.writeFloat(toY);
        os.writeBoolean(vert);
        os.writeInt(type);
        os.writeInt(doorIndex);
    }

    /**
     * Reads info from file
     * @param is Input Stream
     * @throws IOException Error while reading
     */
    @Override
    public void readExternal(ObjectInput is) throws IOException {
        fromX = is.readFloat();
        fromY = is.readFloat();
        toX = is.readFloat();
        toY = is.readFloat();
        vert = is.readBoolean();
        type = is.readInt();
        doorIndex = is.readInt();
    }
}
