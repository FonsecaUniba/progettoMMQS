package zame.game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Logger;

import zame.game.SoundManager;
import zame.game.ZameApplication;
import zame.game.ZameGame;

/**
 * Class representing a Level
 */
@SuppressWarnings("WeakerAccess")
public final class Level {
    /**
     * First Real Level
     */
    public static final int FIRST_REAL_LEVEL = 2;
    /**
     * Max Number of levels
     */
    public static final int MAX_LEVEL = 27;

    /**
     * Max Width Allowed
     */
    public static final int MAX_WIDTH = 64;
    /**
     * Max Height Allowed
     */
    public static final int MAX_HEIGHT = 64;
    /**
     * Max number of Doors Allowed
     */
    public static final int MAX_DOORS = 128;
    /**
     * Max number of Monsters Allowed
     */
    public static final int MAX_MONSTERS = 256;
    /**
     * Max Number of Marks Allowed
     */
    public static final int MAX_MARKS = 253;
    /**
     * Max Number of Actions Allowed
     */
    public static final int MAX_ACTIONS = MAX_MARKS + 1;

    /**
     * Constant for Action close
     */
    public static final int ACTION_CLOSE = 1;
    /**
     * Constant for Action open
     */
    public static final int ACTION_OPEN = 2;
    /**
     * Constant for Requires Key
     */
    public static final int ACTION_REQ_KEY = 3;
    /**
     * Constant for Wall
     */
    public static final int ACTION_WALL = 4;
    /**
     * Constant for Next Level
     */
    public static final int ACTION_NEXT_LEVEL = 5;
    /**
     * Constant for Next Tutorial Level
     */
    public static final int ACTION_NEXT_TUTOR_LEVEL = 6;
    /**
     * Constant for Disable Pistol
     */
    public static final int ACTION_DISABLE_PISTOL = 7;
    /**
     * Constant for Enable Pistol
     */
    public static final int ACTION_ENABLE_PISTOL = 8;
    /**
     * Constant for Weapon Hand
     */
    public static final int ACTION_WEAPON_HAND = 9;
    /**
     * Constant for Restore Health
     */
    public static final int ACTION_RESTORE_HEALTH = 10;
    /**
     * Constant for Secret Found
     */
    public static final int ACTION_SECRET = 11;
    /**
     * Constant for Unmark
     */
    public static final int ACTION_UNMARK = 12;
    /**
     * Constant for Ensure Weapon
     */
    public static final int ACTION_ENSURE_WEAPON = 13;
    /**
     * Constant for Button On
     */
    public static final int ACTION_BTN_ON = 14;
    /**
     * Constant for Button Off
     */
    public static final int ACTION_BTN_OFF = 15;
    /**
     * Constant for Message On
     */
    public static final int ACTION_MSG_ON = 16;
    /**
     * Constant for Message Off
     */
    public static final int ACTION_MSG_OFF = 17;

    /**
     * Constant for Passable Wall
     */
    public static final int PASSABLE_IS_WALL = 1;
    /**
     * Constant for Passable Transparent Wall
     */
    public static final int PASSABLE_IS_TRANSP_WALL = 2;
    /**
     * Constant for Passable Object
     */
    public static final int PASSABLE_IS_OBJECT = 4;
    /**
     * Constant for Passable Decoration
     */
    public static final int PASSABLE_IS_DECORATION = 8;
    /**
     * Constant for Passable Door
     */
    public static final int PASSABLE_IS_DOOR = 16;
    /**
     * Constant for Passable Hero
     */
    public static final int PASSABLE_IS_HERO = 32;
    /**
     * Constant for Passable Monster
     */
    public static final int PASSABLE_IS_MONSTER = 64;
    /**
     * Constant for Passable Dead Monster
     */
    public static final int PASSABLE_IS_DEAD_CORPSE = 128;
    /**
     * Constant for Object Original
     */
    public static final int PASSABLE_IS_OBJECT_ORIG = 256; // original object [not leaved by monster]
    /**
     * Constant for Passable Transparent
     */
    public static final int PASSABLE_IS_TRANSP = 512; // additional state, used in LevelRenderer
    /**
     * Constant for Passable Key
     */
    public static final int PASSABLE_IS_OBJECT_KEY = 1024;
    /**
     * Constant for Passable Opened Door
     */
    public static final int PASSABLE_IS_DOOR_OPENED_BY_HERO = 2048; // was door opened by hero at least once?

    /**
     * Constant for Passable Hero Mask
     */
    public static final int PASSABLE_MASK_HERO = PASSABLE_IS_WALL
            | PASSABLE_IS_TRANSP_WALL
            | PASSABLE_IS_DECORATION
            | PASSABLE_IS_DOOR
            | PASSABLE_IS_MONSTER;

    /**
     * Constant for Passable Monster Mask
     */
    public static final int PASSABLE_MASK_MONSTER = PASSABLE_IS_WALL
            | PASSABLE_IS_TRANSP_WALL
            | PASSABLE_IS_DECORATION
            | PASSABLE_IS_DOOR
            | PASSABLE_IS_MONSTER
            | PASSABLE_IS_HERO; // PASSABLE_IS_OBJECT_KEY

    /**
     * Constant for Passable Mask Shoot Width
     */
    public static final int PASSABLE_MASK_SHOOT_W = PASSABLE_IS_WALL | PASSABLE_IS_DOOR;

    /**
     * Constant for passable Mask Shoot Heigth
     */
    public static final int PASSABLE_MASK_SHOOT_WM = PASSABLE_IS_WALL
            | PASSABLE_IS_DOOR
            | PASSABLE_IS_DECORATION
            | PASSABLE_IS_MONSTER;

    /**
     * Constant for passable Transparent Wall
     */
    public static final int PASSABLE_MASK_WALL_N_TRANSP = PASSABLE_IS_WALL | PASSABLE_IS_TRANSP;

    /**
     * Constant for Passable Mask Object
     */
    public static final int PASSABLE_MASK_OBJECT = PASSABLE_IS_OBJECT
            | PASSABLE_IS_OBJECT_ORIG
            | PASSABLE_IS_OBJECT_KEY;

    /**
     * Constant for Passable Mask Door
     */
    public static final int PASSABLE_MASK_DOOR = ~PASSABLE_IS_DOOR_OPENED_BY_HERO;

    /**
     * Constant for Passable Mask Object Dropped
     */
    public static final int PASSABLE_MASK_OBJECT_DROP = PASSABLE_IS_WALL
            | PASSABLE_IS_TRANSP_WALL
            | PASSABLE_IS_DECORATION
            | PASSABLE_IS_DOOR
            | PASSABLE_IS_OBJECT;

    /**
     * Map of Doors in level
     */
    public static Door[][] doorsMap = new Door[MAX_DOORS][MAX_DOORS];
    /**
     * Map of Marks in Level
     */
    public static Mark[][] marksMap = new Mark[MAX_MARKS][MAX_MARKS];
    /**
     * Hashmap of Marks
     */
    public static ArrayList<ArrayList<Mark>> marksHash = new ArrayList<ArrayList<Mark>>();

    /**
     * Boolean Array for Object In Wall
     */
    private static boolean[] wasAlreadyInWall = new boolean[100];
    /**
     * Quickly return object
     */
    public static boolean quickReturnFromFillInitialInWall = true; // set to false before using fillInitialInWallMap

    /**
     * Class constructor
     */
    private Level() {
    }

    /**
     * Initializes Level
     */
    public static void init() {
        State.levelWidth = 1;
        State.levelHeight = 1;

        State.doors = new Door[MAX_DOORS];
        State.monsters = new Monster[MAX_MONSTERS];
        State.marks = new Mark[MAX_MARKS];
        State.actions = new ArrayList<ArrayList<Action>>();

        marksHash = new ArrayList<ArrayList<Mark>>();

        for (int i = 0; i < MAX_DOORS; i++) {
            State.doors[i] = new Door();
        }

        for (int i = 0; i < MAX_MONSTERS; i++) {
            State.monsters[i] = new Monster();
        }

        for (int i = 0; i < MAX_MARKS; i++) {
            State.marks[i] = new Mark();
        }
    }

    /**
     * Checks if level exists
     * @param idx Level ID
     * @return True if exists, false otherwise
     */
    public static boolean exists(int idx) {
        if (idx > MAX_LEVEL) {
            return false;
        }

        try {
            Game.assetManager.open("levels/level-" + String.valueOf(idx) + ".map").close();
            return true;
        } catch (IOException ex) {
            System.err.println();
        }

        return false;
    }

    /**
     * Loads a level
     * @param idx Level ID
     */
    public static void load(int idx) {
        try {
            Level.create(ZameGame.readBytes(Game.assetManager.open(String.format(Locale.US,
                    "levels/level-%d.map",
                    idx))), LevelConfig.read(Game.assetManager, idx));

            String levelUri="";

            if (idx < FIRST_REAL_LEVEL) {
                levelUri = "/level/ep0-lv" + String.valueOf(idx);
            } else {
                levelUri = String.format(Locale.US,
                        "/level/ep%d-lv%d",
                        ((idx - FIRST_REAL_LEVEL) / 5) + 1,
                        ((idx - FIRST_REAL_LEVEL) % 5) + 1);
            }

            ZameApplication.trackPageView(levelUri);
        } catch (IOException ex) {
            //throw new RuntimeException(ex);
            Logger.getAnonymousLogger().log(java.util.logging.Level.SEVERE, "Error reading file", ex);
        }
    }

    /**
     * Checks Level Size
     */
    private static void checkLevelSize(){
        if ((State.levelWidth > MAX_WIDTH) || (State.levelHeight > MAX_HEIGHT)) {
            //throw new RuntimeException("Too big level");
            Logger.getAnonymousLogger().log(java.util.logging.Level.SEVERE, "Too big level");
        }
    }

    /**
     * Initializes Level State
     */
    private static void initializeState(){
        for (int i = 0; i < MAX_ACTIONS; i++) {
            State.actions.add(new ArrayList<Action>());
        }

        for (int i = 0; i < State.levelHeight; i++) {
            for (int j = 0; j < State.levelWidth; j++) {
                State.wallsMap[i][j] = 0;
                State.transpMap[i][j] = 0;
                State.objectsMap[i][j] = 0;
                State.decorationsMap[i][j] = 0;
                State.passableMap[i][j] = 0;
            }
        }
    }

    /**
     * Sets Starting Level Value
     * @param value Value to set
     * @param i Index I of map
     * @param j Index J of map
     * @return Updated value
     */
    private static int setValue(int value, int i, int j){
        // guarantee 1-cell wall border around level
        if (((value < 0x10) || (value >= 0x30))
                && ((i == 0)
                || (j == 0)
                || (i == (State.levelHeight - 1))
                || (j == (State.levelWidth - 1)))) {

            value = 0x10;
        }

        return value;
    }

    /**
     * Configures Monsters
     * @param mon Monster to configure
     * @param conf Level configuration
     * @param num Monster ID
     * @return Configured Monster
     */
    private static Monster configureMonster(Monster mon, LevelConfig conf, int num){
        if (conf.monsters[num].hitType == LevelConfig.HIT_TYPE_PIST) {
            mon.shootSoundIdx = SoundManager.SOUND_SHOOT_PIST;
            mon.ammoType = TextureLoader.OBJ_CLIP;
        } else if (conf.monsters[num].hitType == LevelConfig.HIT_TYPE_SHTG) {
            mon.shootSoundIdx = SoundManager.SOUND_SHOOT_SHTG;
            mon.ammoType = TextureLoader.OBJ_SHELL;
        } else { // HIT_TYPE_EAT
            mon.shootSoundIdx = SoundManager.SOUND_SHOOT_EAT;
            mon.ammoType = 0;
        }

        return mon;
    }

    /**
     * Check Door Size
     */
    private static void checkDoorSize(){
        if (State.doorsCount >= MAX_DOORS) {
            //throw new RuntimeException("Too many doors");
            Logger.getAnonymousLogger().log(java.util.logging.Level.SEVERE, "Too many doors");
        }
    }

    /**
     * Check Monster Size
     */
    private static void checkMonsterSize(){
        if (State.monstersCount >= MAX_MONSTERS) {
            //throw new RuntimeException("Too many monsters");
            Logger.getAnonymousLogger().log(java.util.logging.Level.SEVERE, "Too many monsters");
        }
    }

    /**
     * Is Door Locked?
     * @param act Action to perform
     * @return True or false
     */
    private static boolean isLocked(Action act){
        return (act.type == ACTION_REQ_KEY) || (act.type == ACTION_WALL);
    }

    /**
     * Is Open?
     * @param act Action to perform
     * @return True or false
     */
    private static boolean isOpen(Action act){
        return (act.type == ACTION_CLOSE) || (act.type == ACTION_OPEN) || (act.type == ACTION_UNMARK);
    }

    /**
     * Is Ensured
     * @param act Action to perform
     * @return True or false
     */
    private static boolean isEnsured(Action act){
        return (act.type == ACTION_ENSURE_WEAPON) || (act.type == ACTION_MSG_ON);
    }

    /**
     * Add Secret
     * @param act Action to perform
     * @param secretsMask Secret Mask
     */
    private static void addSecret(Action act, int secretsMask){
        if ((secretsMask & act.param) == 0) {
            secretsMask |= act.param;
            State.totalSecrets++;
        }
    }

    /**
     * Add Action
     * @param data Data read from file
     * @param pos data index
     * @param secretsMask Secrets Mask
     * @return updated data index
     */
    private static int addAction(byte[] data, int pos, int secretsMask){
        int mark = (int)data[pos++] & 0x000000FF;
        ArrayList<Action> actions = State.actions.get(mark);

        while (((int)data[pos] & 0x000000FF) != 0) {
            Action act = new Action();
            act.type = (int)data[pos++] & 0x000000FF;

            if (act.type == ACTION_SECRET) {
                act.param = floatToInt(data[pos++]) & 0x000000FF;

                addSecret(act, secretsMask);
            } else if (isLocked(act)) {
                act.mark = floatToInt(data[pos++]) & 0x000000FF;
                act.param = floatToInt(data[pos++]) & 0x000000FF;
            } else if (isOpen(act)) {
                act.mark = floatToInt(data[pos++]) & 0x000000FF;
            } else if (isEnsured(act)) {
                act.param = floatToInt(data[pos++]) & 0x000000FF;
            } else if (act.type == ACTION_BTN_ON) {
                act.param = 1 << ((floatToInt(data[pos++]) & 0x000000FF) - 1);
            }

            actions.add(act);
        }

        return pos;
    }

    /**
     * Sets Actions
     * @param data Data read from file
     * @param pos data index
     * @param secretsMask secrets mask
     * @return updated data index
     */
    private static int setAction(byte[] data, int pos, int secretsMask){
        while (((int)data[pos] & 0x000000FF) != 255) {
            pos = addAction(data, pos, secretsMask);

            pos++;
        }

        return pos;
    }

    /**
     * Checks if hero is transparent
     * @param value Value to check
     * @param i Index I of Map
     * @param j Index J of Map
     */
    private static void checkHeroOrTransparent(int value, int i, int j){
        if (value > 0) {
            if (value <= 4) {
                State.heroX = (float)j + 0.5f;
                State.heroY = (float)i + 0.5f;
                State.setHeroA((float)(180 - (value * 90)));

                State.passableMap[i][j] |= PASSABLE_IS_HERO;
            } else if (value == 5) {
                // special invisible, non-rendererable transparent wall (to make special errects with transparents)
                State.passableMap[i][j] |= PASSABLE_IS_TRANSP;
            }
        }
    }

    /**
     * Check if Transparent Wall
     * @param value Value to check
     * @param i Index I of Map
     * @param j Index J of Map
     */
    private static void checkPassableTransparentWall(int value, int i, int j){
        if ((value < 0x38) || (value >= 0x40)) {
            State.passableMap[i][j] |= PASSABLE_IS_TRANSP_WALL;
        }
    }

    /**
     * Checks if Key
     * @param i Index I of Map
     * @param j Index J of Map
     */
    private static void checkKey(int i, int j){
        if ((State.objectsMap[i][j] == TextureLoader.OBJ_KEY_BLUE) || (State.objectsMap[i][j]
                == TextureLoader.OBJ_KEY_RED) || (State.objectsMap[i][j] == TextureLoader.OBJ_KEY_GREEN)) {

            State.passableMap[i][j] |= PASSABLE_IS_OBJECT_KEY;
        }
    }

    /**
     * Checks if Decoration
     * @param value Value to check
     * @param i Index I of Map
     * @param j Index J of Map
     */
    private static void checkDecoration(int value, int i, int j){
        if ((value % 0x10) < 12) {
            State.passableMap[i][j] |= PASSABLE_IS_DECORATION;
        }
    }

    /**
     * Checks value
     * @param value Value to check
     * @param i Index I of Map
     * @param j Index J of Map
     * @param conf Level Configuration
     */
    private static void checkValue(int value, int i, int j, LevelConfig conf){
        if (value < 0x10) {
            checkHeroOrTransparent(value, i, j);
        } else if (value < 0x30) {
            State.wallsMap[i][j] = (value - 0x10) + TextureLoader.BASE_WALLS;
            State.passableMap[i][j] |= PASSABLE_IS_WALL;
        } else if (value < 0x50) {
            State.transpMap[i][j] = (value - 0x30) + TextureLoader.BASE_TRANSPARENTS;
            State.passableMap[i][j] |= PASSABLE_IS_TRANSP;

            checkPassableTransparentWall(value, i, j);
        } else if (value < 0x70) {
            checkDoorSize();

            State.wallsMap[i][j] = -1; // mark door for PortalTracer
            State.passableMap[i][j] |= PASSABLE_IS_DOOR;
            Door door = State.doors[State.doorsCount++];

            door.init();
            door.x = j;
            door.y = i;
            door.texture = TextureLoader.BASE_DOORS_F + (value % 0x10);
            door.vert = (value >= 0x60);
        } else if (value < 0x80) {
            State.objectsMap[i][j] = (value - 0x70) + TextureLoader.BASE_OBJECTS;
            State.decorationsMap[i][j] = -1; // optimize renderer a bit - if there is object, than there is no transparents in this cell
            State.passableMap[i][j] |= PASSABLE_IS_OBJECT | PASSABLE_IS_OBJECT_ORIG;

            checkKey(i, j);

            State.totalItems++;
        } else if (value < 0xA0) {
            State.decorationsMap[i][j] = (value - 0x80) + TextureLoader.BASE_DECORATIONS;

            checkDecoration(value, i, j);
        } else {
            checkMonsterSize();

            int num = (value - 0xA0) / 0x10;

            State.passableMap[i][j] |= PASSABLE_IS_MONSTER;
            Monster mon = State.monsters[State.monstersCount++];

            mon.init();
            mon.cellX = j;
            mon.cellY = i;
            mon.texture = TextureLoader.COUNT_MONSTER * num;
            mon.dir = (value % 0x10);

            mon.health = conf.monsters[num].health;
            mon.hits = conf.monsters[num].hits;
            mon.setAttackDist(conf.monsters[num].hitType != LevelConfig.HIT_TYPE_EAT);

            mon = configureMonster(mon, conf, num);

            State.totalMonsters++;
        }
    }

    /**
     * Create Level
     * @param data Data from file
     * @param conf Level Configuration
     */
    @SuppressWarnings("MagicNumber")
    private static void create(byte[] data, LevelConfig conf) {
        int pos = 0;
        init(); // re-init level. just for case

        State.levelHeight = data[pos++];
        State.levelWidth = data[pos++];

        checkLevelSize();

        State.setStartValues();

        State.wallsMap = new int[State.levelHeight][State.levelWidth];
        State.transpMap = new int[State.levelHeight][State.levelWidth];
        State.objectsMap = new int[State.levelHeight][State.levelWidth];
        State.decorationsMap = new int[State.levelHeight][State.levelWidth];
        State.passableMap = new int[State.levelHeight][State.levelWidth];

        State.doorsCount = 0;
        State.monstersCount = 0;
        State.marksCount = 0;
        State.actions.clear();

        initializeState();

        // ----

        for (int i = 0; i < State.levelHeight; i++) {
            for (int j = 0; j < State.levelWidth; j++) {
                int value = (int)data[pos++] & 0x000000FF;

                value = setValue(value, i, j);

                checkValue(value, i, j, conf);
            }
        }

        for (int i = 0; i < State.monstersCount; i++) {
            State.monsters[i].update();
        }

        while (((int)data[pos] & 0x000000FF) != 255) {
            Mark mark = State.marks[State.marksCount++];

            mark.id = (int)data[pos++] & 0x000000FF;
            mark.y = floatToInt(data[pos++]) & 0x000000FF;
            mark.x = floatToInt(data[pos++]) & 0x000000FF;
        }

        pos++;
        int secretsMask = 0;

        pos = setAction(data, pos, secretsMask);

        updateMaps();
        executeActions(0);
    }

    /**
     * Sets Doors and Monsters
     */
    private static void setDoorsAndMonsters(){
        for (int i = 0; i < MAX_DOORS; i++) {
            State.doors[i].index = i;
            State.doors[i].mark = null;
        }

        for (int i = 0; i < MAX_MONSTERS; i++) {
            State.monsters[i].index = i;
        }
    }

    /**
     * Sets Marks on Doors
     */
    private static void DoorsAndMarks(){
        for (int i = 0; i < State.doorsCount; i++) {
            Door door = State.doors[i];
            doorsMap[door.y][door.x] = door;
        }

        for (int i = 0; i < State.marksCount; i++) {
            Mark mark = State.marks[i];
            marksHash.get(mark.id).add(mark);

            if (doorsMap[mark.y][mark.x] == null) {
                marksMap[mark.y][mark.x] = mark;
            } else {
                doorsMap[mark.y][mark.x].mark = mark;
            }
        }
    }

    /**
     * Updates Level Map
     */
    public static void updateMaps() {
        setDoorsAndMonsters();

        doorsMap = new Door[State.levelHeight][State.levelWidth];
        marksMap = new Mark[State.levelHeight][State.levelWidth];

        marksHash.clear();

        for (int i = 0; i <= MAX_MARKS; i++) {
            marksHash.add(new ArrayList<Mark>());
        }

        for (int i = 0; i < State.levelHeight; i++) {
            for (int j = 0; j < State.levelWidth; j++) {
                doorsMap[i][j] = null;
                marksMap[i][j] = null;
            }
        }

        DoorsAndMarks();
    }

    /**
     * Marks all Doors
     * @param marks ArrayList of Marks
     * @param act Action to perform
     */
    private static void markDoors(ArrayList<Mark> marks, Action act){
        for (Mark mark : marks) {
            Door door = doorsMap[mark.y][mark.x];

            if (door != null) {
                door.stick(act.type == ACTION_OPEN);

                if (act.type == ACTION_REQ_KEY) {
                    door.requiredKey = act.param;
                }
            }
        }
    }

    /**
     * Clear All Marks
     * @param marks ArrayList of Marks
     * @param act Action to perform
     */
    private static void clearMarks(ArrayList<Mark> marks, Action act){
        for (Mark mark : marks) {
            marksMap[mark.y][mark.x] = null;
        }

        for (int i = 0; i < State.doorsCount; i++) {
            Door door = State.doors[i];

            if ((door.mark != null) && (door.mark.id == act.mark)) {
                door.mark = null;
            }
        }
    }

    /**
     * Unmark Object
     * @param marks ArrayList of Marks
     * @param act Action to perform
     */
    private static void actionUnmark(ArrayList<Mark> marks, Action act){
        clearMarks(marks, act);

        marksHash.get(act.mark).clear();
        int idx = 0;

        for (int i = 0; i < State.marksCount; i++) {
            if (idx != i) {
                State.marks[idx] = State.marks[i];
            }

            if (State.marks[idx].id != act.mark) {
                idx++;
            }
        }

        if (idx < MAX_MARKS) {
            State.marks[idx] = new Mark();    // fix references
        }

        State.marksCount = idx;
    }

    /**
     * Is Last Weapon Equipped?
     * @param act Action to perform
     * @return True or false
     */
    private static boolean isLastWeapon(Action act){
        return (act.param > 0) && (act.param < Weapons.WEAPON_LAST);
    }

    /**
     * Is Gun Equipped?
     * @param act Action to perform
     * @return True or false
     */
    private static boolean isGun(Action act){
        return (act.param == Weapons.WEAPON_PISTOL)
                || (act.param == Weapons.WEAPON_CHAINGUN)
                || (act.param == Weapons.WEAPON_DBLCHAINGUN);
    }

    /**
     * Is Shotgun Equipped?
     * @param act Action to perform
     * @return True or false
     */
    private static boolean isShotgun(Action act){
        return (act.param == Weapons.WEAPON_SHOTGUN) || (act.param == Weapons.WEAPON_DBLSHOTGUN);
    }

    /**
     * Ensures the Weapon
     * @param act Action to perform
     */
    private static void ensureWeapon(Action act){
        if (isLastWeapon(act)) {
            State.heroHasWeapon[act.param] = true;

            if (isGun(act)) {

                if (State.heroAmmo[Weapons.AMMO_PISTOL] < Weapons.ENSURED_PISTOL_AMMO) {
                    State.heroAmmo[Weapons.AMMO_PISTOL] = Weapons.ENSURED_PISTOL_AMMO;
                }
            } else if (isShotgun(act) && (State.heroAmmo[Weapons.AMMO_SHOTGUN] < Weapons.ENSURED_SHOTGUN_AMMO)) {
                State.heroAmmo[Weapons.AMMO_SHOTGUN] = Weapons.ENSURED_SHOTGUN_AMMO;
            }

            Weapons.updateWeapon();
        }
    }

    /**
     * Iterate on Monsters
     * @param mark Mark of Object
     */
    private static void iteraMonsters(Mark mark){
        for (int i = 0; i < State.monstersCount; i++) {
            Monster mon = State.monsters[i];

            if ((mon.cellX == mark.x) && (mon.cellY == mark.y)) {
                //noinspection ManualArrayCopy
                for (int j = i; j < (State.monstersCount - 1); j++) {
                    State.monsters[j] = State.monsters[j + 1];
                }

                State.monstersCount--;
                State.monsters[State.monstersCount] = new Monster();    // fix references

                break;
            }
        }
    }

    /**
     * Action on Wall
     * @param marks ArrayList of Marks
     * @param act Action to perform
     */
    private static void actionWall(ArrayList<Mark> marks, Action act){
        for (Mark mark : marks) {
            if ((State.passableMap[mark.y][mark.x] & Level.PASSABLE_IS_MONSTER) != 0) {
                iteraMonsters(mark);
            }

            if (act.param > 0) {
                State.wallsMap[mark.y][mark.x] = (TextureLoader.BASE_WALLS + act.param) - 1;
                State.transpMap[mark.y][mark.x] = 0;
                State.decorationsMap[mark.y][mark.x] = 0;
                State.passableMap[mark.y][mark.x] = PASSABLE_IS_WALL;
            } else {
                State.wallsMap[mark.y][mark.x] = 0;
                State.transpMap[mark.y][mark.x] = 0;
                State.decorationsMap[mark.y][mark.x] = 0;
                State.passableMap[mark.y][mark.x] = 0;
            }

            if (Level.doorsMap[mark.y][mark.x] != null) {
                //noinspection ManualArrayCopy
                for (int i = Level.doorsMap[mark.y][mark.x].index; i < (State.doorsCount - 1); i++) {
                    State.doors[i] = State.doors[i + 1];
                }

                Level.doorsMap[mark.y][mark.x] = null;
                State.doorsCount--;
                State.doors[State.doorsCount] = new Door(); // fix references
            }
        }
    }

    /**
     * Sets Secret as Found
     * @param act Action to perform
     */
    private static void actionSecret(Action act){
        if ((State.foundSecretsMask & act.param) == 0) {
            State.foundSecretsMask |= act.param;
            State.foundSecrets++;
            Overlay.showLabel(Labels.LABEL_SECRET_FOUND);
        }
    }

    /**
     * Determines Action Type
     * @param act Action to perform
     */
    private static void iterateActions(Action act){
        ArrayList<Mark> marks = marksHash.get(act.mark);

        switch (act.type) {
            case ACTION_CLOSE:
            case ACTION_OPEN:
            case ACTION_REQ_KEY: {
                markDoors(marks, act);
            }
            break;

            case ACTION_UNMARK: {
                actionUnmark(marks, act);
            }
            break;

            case ACTION_WALL: {
                actionWall(marks, act);
            }
            break;
            case ACTION_NEXT_LEVEL:
                Game.nextLevel(false);
                break;

            case ACTION_NEXT_TUTOR_LEVEL:
                Game.nextLevel(true);
                break;
            default :
                iterateActions2(act);
                break;
        }
    }

    /**
     * Determines Action Type
     * @param act Action to perform
     */
    private static void iterateActions2(Action act){
        switch(act.type){
            case ACTION_DISABLE_PISTOL:
                State.heroHasWeapon[Weapons.WEAPON_PISTOL] = false;
                State.heroWeapon = Weapons.WEAPON_HAND;
                Weapons.updateWeapon();
                break;

            case ACTION_ENABLE_PISTOL:
                State.reInitPistol();
                State.heroWeapon = Weapons.WEAPON_PISTOL;
                Weapons.updateWeapon();
                break;

            case ACTION_WEAPON_HAND:
                State.heroWeapon = Weapons.WEAPON_HAND;
                Weapons.updateWeapon();
                break;

            case ACTION_RESTORE_HEALTH:
                State.heroHealth = 100;
                break;

            case ACTION_SECRET:
                actionSecret(act);
                break;

            case ACTION_ENSURE_WEAPON:
                ensureWeapon(act);
                break;

            default:
                iterateActions3(act);
                break;
        }
    }

    /**
     * Determines Action type
     * @param act Action to perform
     */
    private static void iterateActions3(Action act){
        switch(act.type){
            case ACTION_BTN_ON:
                State.highlightedControlTypeMask |= act.param;
                break;

            case ACTION_BTN_OFF:
                State.highlightedControlTypeMask = 0;
                break;

            case ACTION_MSG_ON:
                State.shownMessageId = act.param;
                break;

            case ACTION_MSG_OFF:
                State.shownMessageId = 0;
                break;
            default:
                break;
        }
    }

    /**
     * Executes the Action
     * @param id Action ID
     * @return True if performed, false otherwise
     */
    public static boolean executeActions(int id) {
        ArrayList<Action> actions = State.actions.get(id);

        if (actions.isEmpty()) {
            return false;
        }

        if (State.levelNum == 1) {
            ZameApplication.trackEvent("Tutorial", "ExecAction", String.valueOf(id), 0);
        }

        for (Action act : actions) {
            iterateActions(act);
        }

        return true;
    }

    /**
     * Sets Object as Passable
     * @param x Object Position X
     * @param y Object Position Y
     * @param wallDist Distance from Wall
     * @param mask Object Mask
     */
    public static void setPassable(float x, float y, float wallDist, int mask) {
        int fx = Math.max(0, (int)(x - wallDist));
        int tx = Math.min(State.levelWidth - 1, (int)(x + wallDist));
        int fy = Math.max(0, (int)(y - wallDist));
        int ty = Math.min(State.levelHeight - 1, (int)(y + wallDist));

        for (int i = fx; i <= tx; i++) {
            for (int j = fy; j <= ty; j++) {
                State.passableMap[j][i] |= mask;
            }
        }
    }

    /**
     * Clears all passables
     * @param x Object Position X
     * @param y Object Position Y
     * @param wallDist Distance from Wall
     * @param mask Object Mask
     */
    public static void clearPassable(float x, float y, float wallDist, int mask) {
        int fx = Math.max(0, (int)(x - wallDist));
        int tx = Math.min(State.levelWidth - 1, (int)(x + wallDist));
        int fy = Math.max(0, (int)(y - wallDist));
        int ty = Math.min(State.levelHeight - 1, (int)(y + wallDist));

        mask = ~mask;

        for (int i = fx; i <= tx; i++) {
            for (int j = fy; j <= ty; j++) {
                State.passableMap[j][i] &= mask;
            }
        }
    }

    /**
     * Checks for collisions
     * @param cnt collision counter
     * @param atLeastOneAtWall Is there a wall?
     */
    private static void checkCollision(int cnt, boolean atLeastOneAtWall){
        while (cnt < 9) {
            wasAlreadyInWall[cnt++] = false;
        }

        if (!atLeastOneAtWall) {
            quickReturnFromFillInitialInWall = true;
        }
    }

    /**
     * Fills Initial Wall Map
     * @param x Object Position X
     * @param y Object Position Y
     * @param wallDist Distance from Wall
     * @param mask Object Mask
     */
    public static void fillInitialInWallMap(float x, float y, float wallDist, int mask) {
        if (quickReturnFromFillInitialInWall) {
            return;
        }

        if (wasAlreadyInWall == null) {
            wasAlreadyInWall = new boolean[9];
        }

        int fx = Math.max(0, (int)(x - wallDist));
        int tx = Math.min(State.levelWidth - 1, (int)(x + wallDist));
        int fy = Math.max(0, (int)(y - wallDist));
        int ty = Math.min(State.levelHeight - 1, (int)(y + wallDist));

        int cnt = 0;
        boolean atLeastOneAtWall = false;

        for (int i = fx; i <= tx; i++) {
            for (int j = fy; j <= ty; j++) {
                boolean inWall = ((State.passableMap[j][i] & mask) != 0);
                wasAlreadyInWall[cnt++] = inWall;
                atLeastOneAtWall = atLeastOneAtWall || inWall;

                if (cnt >= 9) {
                    return;
                }
            }
        }

        checkCollision(cnt, atLeastOneAtWall);
    }

    // call fillInitialInWallMap before using isPassable
    /**
     * Checks if Object is passable
     * @param x Object position X
     * @param y Object Position Y
     * @param wallDist Distance from wall
     * @param mask Object mask
     * @return True if passable, false otherwise
     */
    public static boolean isPassable(float x, float y, float wallDist, int mask) {
        // level always have 1-cell wall border, so we can skip border checks (like x>=0 && x<width)
        // but just for case limit coordinates

        int fx = Math.max(0, (int)(x - wallDist));
        int tx = Math.min(State.levelWidth - 1, (int)(x + wallDist));
        int fy = Math.max(0, (int)(y - wallDist));
        int ty = Math.min(State.levelHeight - 1, (int)(y + wallDist));

        int cnt = 0;

        for (int i = fx; i <= tx; i++) {
            for (int j = fy; j <= ty; j++) {
                if (((State.passableMap[j][i] & mask) != 0) && ((cnt >= 9) || !wasAlreadyInWall[cnt])) {
                    return false;
                }

                cnt++;
            }
        }

        return true;
    }

    /**
     * Casts float value to int
     * @param a float value to cast
     * @return int value of a
     */
    private static int floatToInt(float a){
        if (a < Integer.MIN_VALUE || a > Integer.MAX_VALUE){
            throw new IllegalArgumentException("Value not castable");
        }
        return Math.round(a-0.5f);
    }
}
