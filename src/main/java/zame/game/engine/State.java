package zame.game.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import zame.game.Common;
import zame.game.ZameApplication;

/**
 * Class representing App State
 */
@SuppressWarnings("WeakerAccess")
public final class State {
    /**
     * Current Level ID
     */
    public static int levelNum=1;
    /**
     * Hero Position X
     */
    public static float heroX=0;
    /**
     * Hero Position Y
     */
    public static float heroY=0;
    /**
     * Hero Angle
     */
    public static float heroA=0;
    /**
     * Hero's Keys Mask
     */
    public static int heroKeysMask=0;
    /**
     * Hero Weapon
     */
    public static int heroWeapon=0;
    /**
     * Hero Health
     */
    public static int heroHealth=0;
    /**
     * Hero Armor
     */
    public static int heroArmor=0;
    /**
     * Which Weapons does hero have?
     */
    public static boolean[] heroHasWeapon = new boolean[8]; //File: Weapons - Row:61
    /**
     * How much Ammo does hero have?
     */
    public static int[] heroAmmo = new int[3]; //File Weapons - Row: 57

    /**
     * Total Items in Level
     */
    public static int totalItems=0;
    /**
     * Total Monsters in Level
     */
    public static int totalMonsters=0;
    /**
     * Total Secrets in Level
     */
    public static int totalSecrets=0;
    /**
     * Number of Picked Items
     */
    public static int pickedItems=0;
    /**
     * Number of Killed Monsters
     */
    public static int killedMonsters=0;
    /**
     * Number of Secrets Found
     */
    public static int foundSecrets=0;
    /**
     * Found Secret Mask
     */
    public static int foundSecretsMask=0;

    // *much* better way: reload level, than replace all changed data with data from state
    // (instead of have all these maps in state. but I'm to lazy :)

    /**
     * Level Width
     */
    public static int levelWidth=0;
    /**
     * Level Height
     */
    public static int levelHeight=0;

    /**
     * Walls Map
     */
    public static int[][] wallsMap = new int[20][20]; //Random
    /**
     * Transparent Map
     */
    public static int[][] transpMap = new int[20][20];
    /**
     * Objects Map
     */
    public static int[][] objectsMap = new int[20][20];
    /**
     * Decorations Map
     */
    public static int[][] decorationsMap = new int[20][20];
    /**
     * Passable Map
     */
    public static int[][] passableMap = new int[20][20];

    /**
     * Number of doors
     */
    public static int doorsCount=0;
    /**
     * Array of Doors
     */
    public static Door[] doors = new Door[100];

    /**
     * Number of Monsters
     */
    public static int monstersCount=0;
    /**
     * Array of Monsters
     */
    public static Monster[] monsters = new Monster[200];

    /**
     * Number of Marks
     */
    public static int marksCount=0;
    /**
     * Array of Marks
     */
    public static Mark[] marks = new Mark[200];

    /**
     * ArrayList of Actions
     */
    public static ArrayList<ArrayList<Action>> actions = new ArrayList<ArrayList<Action>>();

    /**
     * AutoWalls that have been drawn
     */
    public static int[][] drawedAutoWalls = new int[150][150];
    /**
     * Number of AutoWalls
     */
    public static int autoWallsCount=0;
    /**
     * Array of AutoWalls
     */
    public static AutoWall[] autoWalls = new AutoWall[100];

    /**
     * Do we show Auto Map?
     */
    public static boolean showAutoMap=true;
    /**
     * Temporary Elapsed Time
     */
    public static long tempElapsedTime=0;
    /**
     * Temporary Last Time
     */
    public static long tempLastTime=0;
    /**
     * Is the player in godMode?
     */
    public static boolean godMode=true;

    /**
     * Blocker Timeout
     */
    public static int sfcBlockerTimeout=0;

    /**
     * Highlighted Button Type Mask
     */
    public static int highlightedControlTypeMask=0;
    /**
     * Displayed message ID
     */
    public static int shownMessageId=0;

    /**
     * Temporary Do we reload the level?
     */
    public static boolean tmpReloadLevel=true;

    /**
     * Class constructor
     */
    private State() {
    }

    /**
     * Sets Hero Angle
     * @param angle Angle to set
     */
    public static void setHeroA(float angle) {
        heroA = angle;
        Common.heroAngleUpdated();
    }

    /**
     * Re Initializes Level State
     */
    public static void reInit() {
        heroWeapon = 1;
        heroHealth = 100;
        heroArmor = 0;
        heroHasWeapon = new boolean[Weapons.WEAPON_LAST];
        heroAmmo = new int[Weapons.AMMO_LAST];
        highlightedControlTypeMask = 0;
        shownMessageId = 0;

        for (int i = 0; i < Weapons.WEAPON_LAST; i++) {
            heroHasWeapon[i] = false;
        }

        for (int i = 0; i < Weapons.AMMO_LAST; i++) {
            heroAmmo[i] = 0;
        }

        heroHasWeapon[Weapons.WEAPON_HAND] = true;
        reInitPistol();

        Weapons.updateWeapon();
    }

    /**
     * Re Initializes Pistol
     */
    public static void reInitPistol() {
        heroHasWeapon[Weapons.WEAPON_PISTOL] = true;
        heroAmmo[Weapons.AMMO_PISTOL] = Weapons.ENSURED_PISTOL_AMMO;
    }

    /**
     * Initializes Level State
     */
    @SuppressWarnings("MagicNumber")
    public static void init() {
        levelNum = 1;
        reInit();

        drawedAutoWalls = new int[Level.MAX_HEIGHT][Level.MAX_WIDTH];
        autoWalls = new AutoWall[LevelRenderer.MAX_AUTO_WALLS];

        for (int i = 0; i < LevelRenderer.MAX_AUTO_WALLS; i++) {
            autoWalls[i] = new AutoWall();
        }

        showAutoMap = false;
        godMode = false;
        sfcBlockerTimeout = 40 * 60 * 3; // 40 updates per second, 40*60*3 = 3 minutes, used only in TYPE_SFC
    }

    /**
     * Sets Starting values
     */
    public static void setStartValues() {
        heroKeysMask = 0;
        autoWallsCount = 0;
        totalItems = 0;
        totalMonsters = 0;
        totalSecrets = 0;
        pickedItems = 0;
        killedMonsters = 0;
        foundSecrets = 0;
        foundSecretsMask = 0;

        highlightedControlTypeMask = 0;
        shownMessageId = 0;

        for (int i = 0; i < Level.MAX_HEIGHT; i++) {
            for (int j = 0; j < Level.MAX_WIDTH; j++) {
                drawedAutoWalls[i][j] = 0;
            }
        }
    }

    /**
     * Writes State to file
     * @param os Output Stream
     * @throws IOException Error while writing
     */
    public static void writeTo(ObjectOutputStream os) throws IOException {
        os.writeUTF("GloomyDungeons.7");

        os.writeUTF(ZameApplication.self.getVersionName());
        os.writeInt(levelNum);
        os.writeFloat(heroX);
        os.writeFloat(heroY);
        os.writeFloat(heroA);
        os.writeInt(heroKeysMask);
        os.writeInt(heroWeapon);
        os.writeInt(heroHealth);
        os.writeInt(heroArmor);
        Common.writeBooleanArray(os, heroHasWeapon);
        Common.writeIntArray(os, heroAmmo);
        os.writeInt(totalItems);
        os.writeInt(totalMonsters);
        os.writeInt(totalSecrets);
        os.writeInt(pickedItems);
        os.writeInt(killedMonsters);
        os.writeInt(foundSecrets);
        os.writeInt(foundSecretsMask);
        os.writeInt(levelWidth);
        os.writeInt(levelHeight);
        Common.writeInt2dArray(os, wallsMap);
        Common.writeInt2dArray(os, transpMap);
        Common.writeInt2dArray(os, objectsMap);
        Common.writeInt2dArray(os, decorationsMap);
        Common.writeInt2dArray(os, passableMap);
        Common.writeObjectArray(os, doors, doorsCount);
        Common.writeObjectArray(os, monsters, monstersCount);
        Common.writeObjectArray(os, marks, marksCount);

        os.writeInt(actions.size());

        for (ArrayList<Action> items : actions) {
            os.writeInt(items.size());

            for (Action act : items) {
                act.writeExternal(os);
            }
        }

        Common.writeInt2dArray(os, drawedAutoWalls);
        Common.writeObjectArray(os, autoWalls, autoWallsCount);

        os.writeBoolean(showAutoMap);
        os.writeLong(tempElapsedTime);
        os.writeLong(tempLastTime);
        os.writeBoolean(godMode);
        os.writeInt(sfcBlockerTimeout);

        os.writeInt(highlightedControlTypeMask);
        os.writeInt(shownMessageId);
        os.writeBoolean(false); // onScreenControlsSelector
        os.writeBoolean(false); // changeControlsDialog
    }

    /**
     * Returns Save File Version as int
     * @param saveFileVersion Save file version
     * @param ver Save File Version as String
     * @return Int for Save File Version
     */
    private static int getSaveFileVersion(int saveFileVersion, String ver){
        if ("GloomyDungeons.1".equals(ver)) {
            saveFileVersion = 1;
        } else if ("GloomyDungeons.2".equals(ver)) {
            saveFileVersion = 2;
        } else if ("GloomyDungeons.3".equals(ver)) {
            saveFileVersion = 3;
        } else if ("GloomyDungeons.4".equals(ver)) {
            saveFileVersion = 4;
        } else if ("GloomyDungeons.5".equals(ver)) {
            saveFileVersion = 5;
        } else if ("GloomyDungeons.6".equals(ver)) {
            saveFileVersion = 6;
        } else if ("GloomyDungeons.7".equals(ver)) {
            saveFileVersion = 7;
        }

        return saveFileVersion;
    }

    /**
     * Sets Level Based on Version
     * @param saveFileVersion Int value for Save File Version
     */
    private static void setLevel(int saveFileVersion){
        tmpReloadLevel = false;

        if (saveFileVersion < 6) {
            if (levelNum < 9) {
                levelNum = 1;
                tmpReloadLevel = true;
            } else if (levelNum < 14) {
                levelNum -= 7;
            } else if (levelNum == 14) {
                levelNum = 7;
                tmpReloadLevel = true;
            } else {
                levelNum -= 8;
            }
        }
    }

    /**
     * Sets Weapon
     */
    private static void setWeapons(){
        if (heroHasWeapon.length < Weapons.WEAPON_LAST) {
            boolean[] newHeroHasWeapon = new boolean[Weapons.WEAPON_LAST];

            //noinspection ManualArrayCopy
            for (int i = 0; i < heroHasWeapon.length; i++) {
                newHeroHasWeapon[i] = heroHasWeapon[i];
            }

            for (int i = heroHasWeapon.length; i < Weapons.WEAPON_LAST; i++) {
                newHeroHasWeapon[i] = false;
            }

            heroHasWeapon = newHeroHasWeapon;
        }
    }

    /**
     * Sets Monster Texture
     * @param saveFileVersion Save File Version
     */
    private static void setMonsterTextures(int saveFileVersion){
        if (saveFileVersion == 1) {
            for (int i = 0; i < monstersCount; i++) {
                monsters[i].texture -= 0xA0;
            }
        }
    }

    /**
     * Sets Actions
     */
    private static void setActions(){
        for (int i = 0; i < Level.MAX_ACTIONS; i++) {
            actions.add(new ArrayList<Action>());
        }
    }

    /**
     * Checks if save file version is allowed
     * @param saveFileVersion Save File version as int
     * @param ver Save File Version as String
     * @throws ClassNotFoundException Class Not Found Exception
     */
    private static void checkReadException(int saveFileVersion, String ver) throws ClassNotFoundException{
        if (saveFileVersion < 1) {
            throw new ClassNotFoundException(String.format(Locale.US, "Save from newer game version (%s)", ver));
        }
    }

    /**
     * Reads Action from File
     * @param is Input Stream
     * @param actionsCount Actions Counter
     * @throws IOException Error while reading
     */
    private static void readActions(ObjectInputStream is, int actionsCount) throws IOException{
        for (int i = 0; i < actionsCount; i++) {
            ArrayList<Action> items = actions.get(i);
            int itemsCount = is.readInt();

            for (int j = 0; j < itemsCount; j++) {
                Action act = new Action();
                act.readExternal(is);

                items.add(act);
            }
        }
    }

    /**
     * Reads State from File
     * @param is InputStream
     * @throws IOException Error While Reading
     * @throws ClassNotFoundException Class Not Found Exception
     */
    @SuppressWarnings("MagicNumber")
    public static void readFrom(ObjectInputStream is) throws IOException, ClassNotFoundException {
        int saveFileVersion = 0;
        String ver = is.readUTF();

        saveFileVersion = getSaveFileVersion(saveFileVersion, ver);

        checkReadException(saveFileVersion, ver);

        if (saveFileVersion >= 2) {
            is.readUTF();
        }

        levelNum = is.readInt();
        heroX = is.readFloat();
        heroY = is.readFloat();
        setHeroA(is.readFloat());
        heroKeysMask = is.readInt();
        heroWeapon = is.readInt();
        heroHealth = is.readInt();
        heroArmor = is.readInt();
        heroHasWeapon = Common.readBooleanArray(is);
        heroAmmo = Common.readIntArray(is);
        totalItems = is.readInt();
        totalMonsters = is.readInt();
        totalSecrets = is.readInt();
        pickedItems = is.readInt();
        killedMonsters = is.readInt();
        foundSecrets = is.readInt();
        foundSecretsMask = is.readInt();
        levelWidth = is.readInt();
        levelHeight = is.readInt();
        wallsMap = Common.readInt2dArray(is);
        transpMap = Common.readInt2dArray(is);
        objectsMap = Common.readInt2dArray(is);
        decorationsMap = Common.readInt2dArray(is);
        passableMap = Common.readInt2dArray(is);
        doorsCount = Common.readObjectArray(is, doors, Door.class);
        monstersCount = Common.readObjectArray(is, monsters, Monster.class);

        setMonsterTextures(saveFileVersion);

        marksCount = Common.readObjectArray(is, marks, Mark.class);

        actions = new ArrayList<ArrayList<Action>>();

        setActions();

        int actionsCount = is.readInt();

        readActions(is, actionsCount);

        drawedAutoWalls = Common.readInt2dArray(is);
        autoWallsCount = Common.readObjectArray(is, autoWalls, AutoWall.class);

        if (saveFileVersion >= 3) {
            showAutoMap = is.readBoolean();
            tempElapsedTime = is.readLong();
            tempLastTime = is.readLong();
        }

        if (saveFileVersion >= 4) {
            godMode = is.readBoolean();
        }

        if (saveFileVersion >= 5) {
            sfcBlockerTimeout = is.readInt();
        }

        if (saveFileVersion >= 6) {
            highlightedControlTypeMask = is.readInt();
            shownMessageId = is.readInt();
            is.readBoolean(); // onScreenControlsSelector
        }

        if (saveFileVersion >= 7) {
            is.readBoolean(); // changeControlsDialog
        }

        // post-load updates

        setWeapons();

        Level.updateMaps();
        LevelRenderer.updateAutoWalls();
        Weapons.updateWeapon();

        setLevel(saveFileVersion);
    }
}
