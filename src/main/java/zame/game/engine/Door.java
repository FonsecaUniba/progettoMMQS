package zame.game.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import zame.game.SoundManager;

/**
 * Class representing a Door
 */
@SuppressWarnings("WeakerAccess")
public class Door implements Externalizable {
    /**
     * Class Constructor
     */
    public Door(){}

    /**
     * Max Open Position
     */
    public static final float OPEN_POS_MAX = 0.9f;
    /**
     * Position at which Door is Passable
     */
    public static final float OPEN_POS_PASSABLE = 0.7f;

    /**
     * Door Index
     */
    public int index;
    /**
     * Door Position X
     */
    public int x;
    /**
     * Door Position Y
     */
    public int y;
    /**
     * Door Texture
     */
    public int texture;
    /**
     * Door Opens Vertically?
     */
    public boolean vert;
    /**
     * Open Position
     */
    public float openPos;
    /**
     * Door Direction
     */
    public int dir;
    /**
     * Is Door Sticked?
     */
    public boolean sticked;
    /**
     * Does Door require key?
     */
    public int requiredKey;

    /**
     * Last Time Door was opened
     */
    public long lastTime;
    /**
     * Door mark
     */
    public Mark mark;

    /**
     * Initializes Variables
     */
    public void init() {
        openPos = 0.0f;
        dir = 0;
        lastTime = 0;
        sticked = false;
        requiredKey = 0;
        mark = null;
    }

    /**
     * Writes Door on External File
     * @param os Output Stream
     * @throws IOException Error while writing
     */
    @Override
    public void writeExternal(ObjectOutput os) throws IOException {
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(texture);
        os.writeBoolean(vert);
        os.writeFloat(openPos);
        os.writeInt(dir);
        os.writeBoolean(sticked);
        os.writeInt(requiredKey);
    }

    /**
     * Reads Door from External File
     * @param is Input Stream
     * @throws IOException Error while reading
     */
    @Override
    public void readExternal(ObjectInput is) throws IOException {
        x = is.readInt();
        y = is.readInt();
        texture = is.readInt();
        vert = is.readBoolean();
        openPos = is.readFloat();
        dir = is.readInt();
        sticked = is.readBoolean();
        requiredKey = is.readInt();

        lastTime = Game.elapsedTime;
    }

    /**
     * Sticks the Door
     * @param opened Is Open?
     */
    public void stick(boolean opened) {
        sticked = true;
        dir = (opened ? 1 : -1);
        lastTime = 0; // instant open or close
    }

    /**
     * Get Door volume
     * @return Door Volume
     */
    @SuppressWarnings("MagicNumber")
    private float getVolume() {
        float dx = State.heroX - ((float)x + 0.5f);
        float dy = State.heroY - ((float)y + 0.5f);
        float dist = (float)Math.sqrt((dx * dx) + (dy * dy));

        return (1.0f / Math.max(1.0f, dist * 0.5f));
    }

    /**
     * Opens Door
     * @return True if Open, False Otherwise
     */
    public boolean open() {
        if (dir != 0) {
            return false;
        }

        lastTime = Game.elapsedTime;
        dir = 1;

        SoundManager.playSound(SoundManager.SOUND_DOOR_OPEN, getVolume());
        return true;
    }

    /**
     * Tries to close Door
     */
    @SuppressWarnings("MagicNumber")
    public void tryClose() {
        if (sticked
                || (dir != 0)
                || (openPos < 0.9f)
                || ((Game.elapsedTime - lastTime) < (1000 * 5))
                || ((State.passableMap[y][x] & Level.PASSABLE_MASK_DOOR) != 0)) {

            return;
        }

        SoundManager.playSound(SoundManager.SOUND_DOOR_CLOSE, getVolume());
        lastTime = Game.elapsedTime;
        dir = -1;
    }

    /**
     * Is Door Passable?
     */
    private void passableDoor(){
        if (openPos >= OPEN_POS_PASSABLE) {
            State.passableMap[y][x] &= ~Level.PASSABLE_IS_DOOR;

            if (openPos >= OPEN_POS_MAX) {
                openPos = OPEN_POS_MAX;
                dir = 0;
            }
        }
    }

    /**
     * Is Door Not Passable?
     * @param elapsedTime Time elapsed
     */
    private void notPassableDoor(long elapsedTime){
        if (openPos < OPEN_POS_PASSABLE) {
            if ((dir == -1) && ((State.passableMap[y][x] & Level.PASSABLE_MASK_DOOR) != 0)) {
                dir = 1;
                lastTime = elapsedTime;
            } else {
                dir = -2;
                State.passableMap[y][x] |= Level.PASSABLE_IS_DOOR;
            }

            if (openPos <= 0.0f) {
                State.wallsMap[y][x] = -1; // mark door for PortalTracer
                openPos = 0.0f;
                dir = 0;
            }
        }
    }

    /**
     * Update Door
     * @param elapsedTime Time Elapsed
     */
    public void update(long elapsedTime) {
        if (dir > 0) {
            State.wallsMap[y][x] = 0; // clear door mark for PortalTracer

            passableDoor();
        } else if (dir < 0) {
            notPassableDoor(elapsedTime);
        }
    }
}
