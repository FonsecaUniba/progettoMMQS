package zame.game.engine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.opengl.GLUtils;
import javax.microedition.khronos.opengles.GL10;
import zame.game.AppConfig;
import zame.game.R;

/**
 * Class representing a Texture Loader
 */
@SuppressWarnings({ "WeakerAccess", "unused" })
public final class TextureLoader {
    /**
     * Main Texture ID
     */
    public static final int TEXTURE_MAIN = 0;
    /**
     * Floor Texture ID
     */
    public static final int TEXTURE_FLOOR = 1;
    /**
     * Ceiling Texture ID
     */
    public static final int TEXTURE_CEIL = 2;
    /**
     * Monster Texture ID
     */
    public static final int TEXTURE_MON = 3;

    /**
     * Hand Texture ID
     */
    public static final int TEXTURE_HAND = AppConfig.TEXTURE_HAND;
    /**
     * Pistol Texture ID
     */
    public static final int TEXTURE_PIST = AppConfig.TEXTURE_PIST;
    /**
     * Shotgun Texture ID
     */
    public static final int TEXTURE_SHTG = AppConfig.TEXTURE_SHTG;
    /**
     * Chaingun Texture ID
     */
    public static final int TEXTURE_CHGN = AppConfig.TEXTURE_CHGN;
    /**
     * Double Shotgun Texture ID
     */
    public static final int TEXTURE_DBLSHTG = AppConfig.TEXTURE_DBLSHTG;
    /**
     * Double Chaingun Texture ID
     */
    public static final int TEXTURE_DBLCHGN = AppConfig.TEXTURE_DBLCHGN;
    /**
     * Chainsaw Texture ID
     */
    public static final int TEXTURE_SAW = AppConfig.TEXTURE_SAW;
    /**
     * Last Texture ID
     */
    public static final int TEXTURE_LAST = AppConfig.TEXTURE_LAST;

    /**
     * Base Icon ID
     */
    public static final int BASE_ICONS = 0x00;
    /**
     * Base Wall ID
     */
    public static final int BASE_WALLS = 0x10;
    /**
     * Base Transparent ID
     */
    public static final int BASE_TRANSPARENTS = 0x30;
    /**
     * Base Door F ID
     */
    public static final int BASE_DOORS_F = 0x50;
    /**
     * Base Door S ID
     */
    public static final int BASE_DOORS_S = 0x60;
    /**
     * Base Object ID
     */
    public static final int BASE_OBJECTS = 0x70;
    /**
     * Base Decoration ID
     */
    public static final int BASE_DECORATIONS = 0x80;
    /**
     * Base Additional ID
     */
    public static final int BASE_ADDITIONAL = 0x90;

    /**
     * Monster Count Texture
     */
    public static final int COUNT_MONSTER = 0x10; // block = [up, rt, dn, lt], monster = block[walk_a, walk_b, hit], die[3], shoot

    /**
     * Green Armor ID
     */
    @SuppressWarnings("PointlessArithmeticExpression") public static final int OBJ_ARMOR_GREEN = BASE_OBJECTS + 0;
    /**
     * Red Armor ID
     */
    public static final int OBJ_ARMOR_RED = BASE_OBJECTS + 1;
    /**
     * Blue Key ID
     */
    public static final int OBJ_KEY_BLUE = BASE_OBJECTS + 2;
    /**
     * Red Key ID
     */
    public static final int OBJ_KEY_RED = BASE_OBJECTS + 3;
    /**
     * Stimpack ID
     */
    public static final int OBJ_STIM = BASE_OBJECTS + 4;
    /**
     * Medikit ID
     */
    public static final int OBJ_MEDI = BASE_OBJECTS + 5;
    /**
     * Ammo clip ID
     */
    public static final int OBJ_CLIP = BASE_OBJECTS + 6;
    /**
     * Ammo ID
     */
    public static final int OBJ_AMMO = BASE_OBJECTS + 7;
    /**
     * Ammo shell ID
     */
    public static final int OBJ_SHELL = BASE_OBJECTS + 8;
    /**
     * Ammo Box ID
     */
    public static final int OBJ_SBOX = BASE_OBJECTS + 9;
    /**
     * Ammo Pack ID
     */
    public static final int OBJ_BPACK = BASE_OBJECTS + 10;
    /**
     * Shotgun ID
     */
    public static final int OBJ_SHOTGUN = BASE_OBJECTS + 11;
    /**
     * Green Key ID
     */
    public static final int OBJ_KEY_GREEN = BASE_OBJECTS + 12;
    /**
     * Chaingun ID
     */
    public static final int OBJ_CHAINGUN = BASE_OBJECTS + 13;
    /**
     * Double Shotgun ID
     */
    public static final int OBJ_DBLSHOTGUN = BASE_OBJECTS + 14;

    /**
     * Class representing the Textures that need to be loaded
     */
    public static class TextureToLoad {
        /**
         * Resource Type
         */
        public static final int TYPE_RESOURCE = 0;
        /**
         * Monster Type
         */
        public static final int TYPE_MONSTERS = 1;
        /**
         * Floor Type
         */
        public static final int TYPE_FLOOR = 2;
        /**
         * Ceiling Type
         */
        public static final int TYPE_CEIL = 3;

        /**
         * Texture to load
         */
        public int tex;
        /**
         * Resource ID
         */
        public int resId;
        /**
         * Type ID
         */
        public int type;

        /**
         * Class Constructor
         * @param tex Texture to load
         * @param resId Resource ID
         */
        public TextureToLoad(int tex, int resId) {
            this.tex = tex;
            this.resId = resId;
            this.type = TYPE_RESOURCE;
        }

        /**
         * Class Constructor
         * @param tex Texutre to load
         * @param resId Resource ID
         * @param type Type ID
         */
        public TextureToLoad(int tex, int resId, int type) {
            this.tex = tex;
            this.resId = resId;
            this.type = type;
        }
    }

    /**
     * Array of Textures to load
     */
    public static final TextureToLoad[] TEXTURES_TO_LOAD = AppConfig.TEXTURES_TO_LOAD;

    /**
     * Floor Texture Map
     */
    private static final int[] floorTexMap = { R.drawable.floor_1, R.drawable.floor_2, };
    /**
     * Ceiling Texture Map
     */
    private static final int[] ceilTexMap = { R.drawable.ceil_1, R.drawable.ceil_2, };

    /**
     * Monster Texture Map
     */
    private static final int[] monTexMap = { R.drawable.texmap_mon_1,
            R.drawable.texmap_mon_2,
            R.drawable.texmap_mon_3,
            R.drawable.texmap_mon_4,
            R.drawable.texmap_mon_5,
            R.drawable.texmap_mon_6, };

    /**
     * Is Texture initialized?
     */
    private static boolean texturesInitialized=true;
    /**
     * Array of Textures
     */
    public static int[] textures = new int[TEXTURE_LAST];

    /**
     * Bitmap Options
     */
    private static volatile BitmapFactory.Options tOpts = new BitmapFactory.Options();
    /**
     * Level Configuration
     */
    private static volatile LevelConfig levelConf = new LevelConfig(1);

    /**
     * Class constructor
     */
    private TextureLoader() {
    }

    /**
     * Loads and Binds Textures to Object
     * @param gl Renderer
     * @param tex Texture to load
     * @param resId Resource ID
     */
    private static void loadAndBindTexture(GL10 gl, int tex, int resId) {
        Bitmap img = BitmapFactory.decodeResource(Game.resources, resId, tOpts);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[tex]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, img, 0);

        img.recycle();

        //noinspection UnusedAssignment
        img = null;

        // Runtime.getRuntime().gc();
    }

    /**
     * Loads and Binds Monster Textures
     * @param gl Renderer
     * @param tex Texture ID
     * @param resId1 Resource ID 1
     * @param resId2 Resource ID 2
     * @param resId3 Resource ID 3
     * @param resId4 Resource ID 4
     */
    @SuppressWarnings("MagicNumber")
    private static void loadAndBindMonTexture(GL10 gl, int tex, int resId1, int resId2, int resId3, int resId4) {
        Bitmap img = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(img);

        Bitmap mon = BitmapFactory.decodeResource(Game.resources, resId1, tOpts);
        canvas.drawBitmap(mon, 0.0f, 0.0f, null);
        mon.recycle();

        //noinspection UnusedAssignment
        mon = null;

        mon = BitmapFactory.decodeResource(Game.resources, resId2, tOpts);
        canvas.drawBitmap(mon, 0.0f, 256.0f, null);
        mon.recycle();

        //noinspection UnusedAssignment
        mon = null;

        mon = BitmapFactory.decodeResource(Game.resources, resId3, tOpts);
        canvas.drawBitmap(mon, 0.0f, 512.0f, null);
        mon.recycle();

        //noinspection UnusedAssignment
        mon = null;

        mon = BitmapFactory.decodeResource(Game.resources, resId4, tOpts);
        canvas.drawBitmap(mon, 0.0f, 768.0f, null);
        mon.recycle();

        //noinspection UnusedAssignment
        mon = null;

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[tex]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, img, 0);

        //noinspection UnusedAssignment
        canvas = null;

        img.recycle();

        //noinspection UnusedAssignment
        img = null;

    }

    /**
     * Get Correct Texture Number
     * @param texMap Texture Map
     * @param texNum Texture ID
     * @return 0 or texNum -1
     */
    public static int getTexNum(int[] texMap, int texNum) {
        return texMap[((texNum < 1) || (texNum > texMap.length)) ? 0 : (texNum - 1)];
    }

    /**
     * Binds the Textures
     * @param gl Renderer
     * @param createdTexturesCount Number of created Textures
     */
    private static void bindTextures(GL10 gl, int createdTexturesCount){
        TextureToLoad texToLoad = TEXTURES_TO_LOAD[createdTexturesCount];

        switch (texToLoad.type) {
            case TextureToLoad.TYPE_MONSTERS:
                loadAndBindMonTexture(gl,
                        texToLoad.tex,
                        getTexNum(monTexMap, levelConf.monsters[0].texture),
                        getTexNum(monTexMap, levelConf.monsters[1].texture),
                        getTexNum(monTexMap, levelConf.monsters[2].texture),
                        getTexNum(monTexMap, levelConf.monsters[3].texture));
                break;

            case TextureToLoad.TYPE_FLOOR:
                loadAndBindTexture(gl, texToLoad.tex, getTexNum(floorTexMap, levelConf.floorTexture));
                break;

            case TextureToLoad.TYPE_CEIL:
                loadAndBindTexture(gl, texToLoad.tex, getTexNum(ceilTexMap, levelConf.ceilTexture));
                break;

            default:
                loadAndBindTexture(gl, texToLoad.tex, texToLoad.resId);
                break;
        }
    }

    /**
     * Loads Textures
     * @param gl Renderer
     * @param createdTexturesCount Number of created Textures
     * @return True if successful, false otherwise
     */
    public static synchronized boolean loadTexture(GL10 gl, int createdTexturesCount) {
        if (createdTexturesCount >= TEXTURES_TO_LOAD.length) {
            return false;
        }

        if (createdTexturesCount == 0) {
            if (texturesInitialized) {
                gl.glDeleteTextures(TEXTURE_LAST, textures, 0);
            }

            texturesInitialized = true;
            gl.glGenTextures(TEXTURE_LAST, textures, 0);

            levelConf = LevelConfig.read(Game.assetManager, State.levelNum);
        } else {
            // re-ensure levelConf
            if (levelConf == null) {
                levelConf = LevelConfig.read(Game.assetManager, State.levelNum);
            }

            // re-ensure initialized textures
            if (!texturesInitialized) {
                texturesInitialized = true;
                gl.glGenTextures(TEXTURE_LAST, textures, 0);
            }
        }

        if (tOpts == null) {
            tOpts = new BitmapFactory.Options();
            tOpts.inDither = false;
            tOpts.inPurgeable = true;
            tOpts.inInputShareable = true;
        }

         bindTextures(gl, createdTexturesCount);

        return true;
    }
}
