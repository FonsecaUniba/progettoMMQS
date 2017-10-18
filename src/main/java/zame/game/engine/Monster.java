package zame.game.engine;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import zame.game.Common;
import zame.game.SoundManager;

/**
 * Class representing a Monster
 */
@SuppressWarnings("WeakerAccess")
public class Monster implements Externalizable {
    /**
     * Class Constructor
     */
    public Monster(){}

    /**
     * Monster ID
     */
    public int index;
    /**
     * Monster Cell Position X
     */
    public int cellX;
    /**
     * Monster Cell Position Y
     */
    public int cellY;
    /**
     * Monster Position X
     */
    public float x;
    /**
     * Monster Position Y
     */
    public float y;
    /**
     * Monster Texture
     */
    public int texture;
    /**
     * Monster Direction
     */
    public int dir; // 0 - right, 1 - up, 2 - left, 3 - down
    /**
     * Monster Max Steps
     */
    public int maxStep;
    /**
     * Monster Health
     */
    public int health;
    /**
     * Monster Damage Output
     */
    public int hits;
    /**
     * Monster Visible Distance Squared
     */
    public float visibleDistSq;
    /**
     * Monster Attack Distance Squared
     */
    public float attackDistSq;
    /**
     * Monster Shoot Sound ID
     */
    public int shootSoundIdx;
    /**
     * Monster Ammo Type
     */
    public int ammoType;

    /**
     * Monster Step
     */
    public int step;
    /**
     * Monster Previous Position X
     */
    public int prevX;
    /**
     * Monster Previous Position Y
     */
    public int prevY;
    /**
     * Monster Damage Taken Timeout
     */
    public int hitTimeout; // hero hits monster
    /**
     * Monster Damage Done Timeout
     */
    public int attackTimeout; // monster hits hero
    /**
     * Monster Despawn Timeout
     */
    public int removeTimeout;
    /**
     * Monster Death Time
     */
    public long dieTime;
    /**
     * Get Around Required Direction
     */
    public int aroundReqDir;
    /**
     * Is Monster Using Inverse Rotation?
     */
    public boolean inverseRotation;
    /**
     * Previous Around Position X
     */
    public int prevAroundX;
    /**
     * Previous Around Position Y
     */
    public int prevAroundY;
    /**
     * Monster Shooting Angle
     */
    public int shootAngle;
    /**
     * Monster Hit Timeout
     */
    public int hitHeroTimeout;
    /**
     * Monster Damage to Hero
     */
    public int hitHeroHits;
    /**
     * Is Monster chasing hero?
     */
    public boolean chaseMode;
    /**
     * Is Monster Waiting for Door to open?
     */
    public boolean waitForDoor;

    /**
     * Is Monster Hostile?
     */
    public boolean isInAttackState;
    /**
     * Is Aim on Hero?
     */
    public boolean isAimedOnHero;

    /**
     * Initializes Monster
     */
    @SuppressWarnings("MagicNumber")
    public void init() {
        step = 0;
        maxStep = 50;
        hitTimeout = 0;
        attackTimeout = 0;
        removeTimeout = 5000;
        dieTime = 0;
        aroundReqDir = -1;
        inverseRotation = false;
        prevAroundX = -1;
        prevAroundY = -1;
        visibleDistSq = 15.0f * 15.0f;
        hitHeroTimeout = 0;
        chaseMode = false;
        waitForDoor = false;
    }

    /**
     * Write Monster Info on file
     * @param os Output Stream
     * @throws IOException Error while writing
     */
    @Override
    public void writeExternal(ObjectOutput os) throws IOException {
        os.writeInt(cellX);
        os.writeInt(cellY);
        os.writeFloat(x);
        os.writeFloat(y);
        os.writeInt(texture);
        os.writeInt(dir);
        os.writeInt(maxStep);
        os.writeInt(health);
        os.writeInt(hits);
        os.writeFloat(visibleDistSq);
        os.writeFloat(attackDistSq);
        os.writeInt(shootSoundIdx);
        os.writeInt(ammoType);

        os.writeInt(step);
        os.writeInt(prevX);
        os.writeInt(prevY);
        os.writeInt(hitTimeout);
        os.writeInt(attackTimeout);
        os.writeInt(removeTimeout);
        os.writeInt(aroundReqDir);
        os.writeBoolean(inverseRotation);
        os.writeInt(prevAroundX);
        os.writeInt(prevAroundY);
        os.writeInt(shootAngle);
        os.writeInt(hitHeroTimeout);
        os.writeInt(hitHeroHits);
        os.writeBoolean(chaseMode);
        os.writeBoolean(waitForDoor);
    }

    /**
     * Reads Monster info from file
     * @param is Input Stream
     * @throws IOException Error while reading
     */
    @Override
    public void readExternal(ObjectInput is) throws IOException {
        cellX = is.readInt();
        cellY = is.readInt();
        x = is.readFloat();
        y = is.readFloat();
        texture = is.readInt();
        dir = is.readInt();
        maxStep = is.readInt();
        health = is.readInt();
        hits = is.readInt();
        visibleDistSq = is.readFloat();
        attackDistSq = is.readFloat();
        shootSoundIdx = is.readInt();
        ammoType = is.readInt();

        step = is.readInt();
        prevX = is.readInt();
        prevY = is.readInt();
        hitTimeout = is.readInt();
        attackTimeout = is.readInt();
        removeTimeout = is.readInt();
        aroundReqDir = is.readInt();
        inverseRotation = is.readBoolean();
        prevAroundX = is.readInt();
        prevAroundY = is.readInt();
        shootAngle = is.readInt();
        hitHeroTimeout = is.readInt();
        hitHeroHits = is.readInt();
        chaseMode = is.readBoolean();
        waitForDoor = is.readBoolean();

        isInAttackState = false;
        isAimedOnHero = false;
        dieTime = ((health <= 0) ? -1 : 0);
    }

    /**
     * Set Monster Attack Distance
     * @param longAttackDist Is Attack Long range?
     */
    @SuppressWarnings("MagicNumber")
    public void setAttackDist(boolean longAttackDist) {
        attackDistSq = (longAttackDist ? (10.0f * 10.0f) : (1.8f * 1.8f));
    }

    /**
     * Checks if two ints are equal
     * @param a first int value
     * @param b second int value
     * @return true if a equals b, false otherwise
     */
    private static boolean isEquals(int a, int b)
    {
        return Math.abs(a-b) < 1e-6;
    }

    /**
     * Copy monster info
     * @param mon Monster to copy from
     */
    public void copyFrom(Monster mon) {
        cellX = mon.cellX;
        cellY = mon.cellY;
        x = mon.x;
        y = mon.y;
        texture = mon.texture;
        dir = mon.dir;
        maxStep = mon.maxStep;
        health = mon.health;
        hits = mon.hits;
        visibleDistSq = mon.visibleDistSq;
        attackDistSq = mon.attackDistSq;
        shootSoundIdx = mon.shootSoundIdx;
        ammoType = mon.ammoType;

        step = mon.step;
        prevX = mon.prevX;
        prevY = mon.prevY;
        hitTimeout = mon.hitTimeout;
        attackTimeout = mon.attackTimeout;
        removeTimeout = mon.removeTimeout;
        dieTime = mon.dieTime;
        aroundReqDir = mon.aroundReqDir;
        inverseRotation = mon.inverseRotation;
        prevAroundX = mon.prevAroundX;
        prevAroundY = mon.prevAroundY;
        shootAngle = mon.shootAngle;
        hitHeroTimeout = mon.hitHeroTimeout;
        hitHeroHits = mon.hitHeroHits;
        chaseMode = mon.chaseMode;

        isInAttackState = mon.isInAttackState;
        isAimedOnHero = mon.isAimedOnHero;
    }

    /**
     * Checks if Monster is passable
     * @param dx Derivate X
     * @param dy Derivate Y
     * @return True if passable, false otherwise
     */
    private boolean checkPassable(int dx, int dy){
        return ((!isEquals(dy,0)) || (!isEquals(dx,0)))
                && (isEquals((State.passableMap[cellY + dy][cellX + dx]
                & Level.PASSABLE_MASK_OBJECT_DROP),0));
    }

    /**
     * Checks if mask is allowed
     * @return True if Mask is allowed
     */
    private boolean checkMask(){
        return (State.passableMap[cellY][cellX] & Level.PASSABLE_MASK_OBJECT_DROP) == 0;
    }

    /**
     * Checks if Loop has to continue
     * @param dx Derivate X
     * @param dy Derivate Y
     * @return True if loop continues, false otherwise
     */
    private boolean setKeepGoing(int dx, int dy){
        boolean result = true;
        if (checkPassable(dx, dy)) {

            State.objectsMap[cellY + dy][cellX + dx] = ammoType;
            State.passableMap[cellY + dy][cellX + dx] |= Level.PASSABLE_IS_OBJECT;
            result = false;
        }
        return result;
    }

    /**
     * Processes Monster Mask
     */
    private void processMask(){
        if (checkMask()) {
            State.objectsMap[cellY][cellX] = ammoType;
            State.passableMap[cellY][cellX] |= Level.PASSABLE_IS_OBJECT;
        } else {
            boolean keepGoing = true;
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    keepGoing = setKeepGoing(dx, dy);
                    if (!keepGoing) break;
                }
                if (!keepGoing) break;
            }
        }
    }

    /**
     * Monster Gets hit
     * @param amt Amount of Damage Taken
     * @param hitTm Hit Timeout
     */
    public void hit(int amt, int hitTm) {
        hitTimeout = hitTm;
        health -= amt;
        aroundReqDir = -1;

        if (health <= 0) {
            SoundManager.playSound(SoundManager.SOUND_DETH_MON);
            State.passableMap[cellY][cellX] &= ~Level.PASSABLE_IS_MONSTER;
            State.passableMap[cellY][cellX] |= Level.PASSABLE_IS_DEAD_CORPSE;

            if (ammoType > 0) {
                processMask();
            }

            State.killedMonsters++;
        }
    }

    /**
     * Removes Monster from Game
     */
    public void remove() {
        State.passableMap[cellY][cellX] &= ~Level.PASSABLE_IS_DEAD_CORPSE;
        State.monstersCount--;

        for (int i = index; i < State.monstersCount; i++) {
            State.monsters[i].copyFrom(State.monsters[i + 1]);

            if (State.monsters[i].health <= 0) {
                State.passableMap[State.monsters[i].cellY][State.monsters[i].cellX] |= Level.PASSABLE_IS_DEAD_CORPSE;
            }
        }
    }

    /**
     * Hero Timeout
     */
    private void heroTimeout(){
        if (hitHeroTimeout > 0) {
            hitHeroTimeout--;

            if (hitHeroTimeout <= 0) {
                Game.hitHero(hitHeroHits, shootSoundIdx, this);
            }
        }
    }

    /**
     * Sets Monster Direction
     * @param dx Derivate X
     * @param dy Derivate Y
     * @param distSq Distance Squared
     * @return True if direction is set, false otherwise
     */
    private boolean setDir(float dx, float dy, float distSq){
        boolean result = false;

        if (aroundReqDir >= 0) {
            if (!waitForDoor) {
                dir = (dir + (inverseRotation ? 3 : 1)) % 4;
            }
        } else if (distSq <= visibleDistSq) {
            if (Math.abs(dy) <= 1.0f) {
                dir = ((dx < 0) ? 2 : 0);
            } else {
                dir = ((dy < 0) ? 1 : 3);
            }

            result = true;
        }

        return result;
    }

    /**
     * Updates Angle Difference
     * @param angleDiff Angle Difference
     * @return Updated Angle Difference
     */
    private int updateAngleDiff(int angleDiff){
        if (angleDiff > 180) {
            angleDiff -= 360;
        } else if (angleDiff < -180) {
            angleDiff += 360;
        }

        return angleDiff;
    }

    /**
     * Checks if Monster is Visible
     * @param distSq Distance Squared
     * @return True if visible, false otherwise
     */
    private boolean checkVisible(float distSq){
        boolean result = false;
        if ((distSq <= visibleDistSq) && Common.traceLine((float)cellX + 0.5f,
                (float)cellY + 0.5f,
                State.heroX,
                State.heroY,
                Level.PASSABLE_MASK_SHOOT_WM)) {

            chaseMode = true;
            result = true;
        }
        return result;
    }

    /**
     * Sets Values
     * @param angleDiff Angle Difference
     * @param minAngle Minimal Angle
     * @param dist Distance
     */
    private void setValues(int angleDiff, int minAngle, float dist){
        if (angleDiff <= minAngle) {
            isAimedOnHero = true;
            hitHeroHits = Common.getRealHits(hits, dist);
            hitHeroTimeout = 2;
            attackTimeout = 15;
            step = 50;
        } else {
            step = 8 + (angleDiff / 5);
        }
    }

    /**
     * Updates Cell Values
     */
    private void updateCells(){
        switch (dir) {
            case 0:
                cellX++;
                break;

            case 1:
                cellY--;
                break;

            case 2:
                cellX--;
                break;

            case 3:
                cellY++;
                break;
            default : break;
        }
    }

    /**
     * Checks for Rotation
     * @param tryAround Do we try to go around?
     * @return False
     */
    private boolean checkRotation(boolean tryAround){
        if (tryAround) {
            if ((prevAroundX == cellX) && (prevAroundY == cellY)) {
                inverseRotation = !inverseRotation;
            }

            aroundReqDir = dir;
            prevAroundX = cellX;
            prevAroundY = cellY;
        }

        return false;
    }

    /**
     * Updates Attack, Hit, Step
     */
    private void finalUpdate(){
        if (attackTimeout > 0) {
            attackTimeout--;
        }

        if (hitTimeout > 0) {
            hitTimeout--;
        } else if (step > 0) {
            step--;
        }
    }

    /**
     * Updates Step
     */
    private void updateStep(){
        if (step == 0) {
            step = maxStep / 2;
        }
    }

    /**
     * Check Chase mode on Door
     * @return True if Monster can pass door, False otherwise
     */
    private boolean checkChaseDoor(){
        return chaseMode
                && ((State.passableMap[cellY][cellX] & Level.PASSABLE_IS_DOOR) != 0)
                && ((State.passableMap[cellY][cellX] & Level.PASSABLE_IS_DOOR_OPENED_BY_HERO) != 0);
    }

    /**
     * Checks Monster Mask
     * @return True or False
     */
    private boolean checkMonsterMask(){
        boolean result = true;

        if ((State.passableMap[cellY][cellX] & Level.PASSABLE_MASK_MONSTER) == 0) {
            if (dir == aroundReqDir) {
                aroundReqDir = -1;
            }

            step = maxStep;
            result = false;
        }

        return result;
    }

    /**
     * Checks Door Interaction
     * @return False if Door is opened, False otherwise
     */
    private boolean checkDoorInteraction(){
        boolean result = true;
        if (checkChaseDoor()) {

            Door door = Level.doorsMap[cellY][cellX];

            if (!door.sticked) {
                door.open();

                waitForDoor = true;
                cellX = prevX;
                cellY = prevY;
                step = 10;
                result = false;
            }
        }

        return result;
    }

    /**
     * Get absolute value of Angle Difference
     * @param angleDiff Current Angle difference
     * @return -angleDiff if angleDiff is negative, false otherwise
     */
    private int getAngleDiff(int angleDiff){
        return ((angleDiff < 0) ? -angleDiff : angleDiff);
    }

    /**
     * Checks if Visible
     * @param vis Was visible?
     * @param distSq Distance squared
     * @return True is visible, false otherwise
     */
    private boolean isVisible(boolean vis, float distSq){
        return vis && (distSq <= attackDistSq);
    }

    /**
     * Returns new direction
     * @param direction Current direction
     * @return new Direction
     */
    private int getDir(int direction){
        return (direction + (inverseRotation ? 1 : 3)) % 4;
    }

    /**
     * Updates Monster
     */
    @SuppressWarnings("MagicNumber")
    public void update() {
        if (health <= 0) {
            // removeTimeout--;	// do not remove dead corpses
            return;
        }

        heroTimeout();

        if (step == 0) {
            isInAttackState = false;
            isAimedOnHero = false;
            prevX = cellX;
            prevY = cellY;

            float dx = State.heroX - ((float)cellX + 0.5f);
            float dy = State.heroY - ((float)cellY + 0.5f);
            float distSq = (dx * dx) + (dy * dy);

            boolean tryAround = setDir(dx, dy, distSq);

            State.passableMap[cellY][cellX] &= ~Level.PASSABLE_IS_MONSTER;
            boolean vis = checkVisible(distSq);

            if (isVisible(vis, distSq)) {
                int angleToHero = (int)(PortalTracer.getAngle(dx, dy) * Common.RAD2G_F);
                int angleDiff = angleToHero - shootAngle;

                angleDiff = updateAngleDiff(angleDiff);

                angleDiff = getAngleDiff(angleDiff);
                shootAngle = angleToHero;
                float dist = (float)Math.sqrt(distSq);

                int minAngle = Math.max(1, 15 - (int)(dist * 3.0f));

                setValues(angleDiff, minAngle, dist);

                isInAttackState = true;
                dir = ((shootAngle + 45) % 360) / 90;
                aroundReqDir = -1;
            } else {
                waitForDoor = false;

                for (int i = 0; i < 4; i++) {
                    updateCells();

                    boolean keepGoing = checkMonsterMask();

                    if (!keepGoing) break;

                    keepGoing = checkDoorInteraction();

                    if (!keepGoing) break;

                    cellX = prevX;
                    cellY = prevY;

                    tryAround = checkRotation(tryAround);

                    dir = getDir(dir);
                }

                updateStep();

                shootAngle = dir * 90;
            }

            State.passableMap[cellY][cellX] |= Level.PASSABLE_IS_MONSTER;
        }

        x = (float)cellX + (((float)(prevX - cellX) * (float)step) / (float)maxStep) + 0.5f;
        y = (float)cellY + (((float)(prevY - cellY) * (float)step) / (float)maxStep) + 0.5f;

        finalUpdate();
    }
}