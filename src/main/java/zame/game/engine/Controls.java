package zame.game.engine;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.microedition.khronos.opengles.GL10;
import zame.game.Common;
import zame.game.Config;
import zame.game.Renderer;
import zame.game.ZameApplication;

/**
 * Class that represents the Game's Controls
 */
public final class Controls {
    /**
     * Class that represents the Hero's Acceleration
     */
    @SuppressWarnings("WeakerAccess")
    public static class ControlAcceleration {
        /**
         * Minimal Acceleration Allowed
         */
        public static final float MIN_ACCELERATION = 0.01f;

        /**
         * Current Acceleration value
         */
        public float value;
        /**
         * Step by which value is altered
         */
        public float step;
        /**
         * Has value changed?
         */
        public boolean updated;

        /**
         * Class constructor
         * @param step Step at which the Hero accelerates
         */
        public ControlAcceleration(float step) {
            this.step = step;
        }

        /**
         * Is Acceleration active?
         * @return Boolean response
         */
        public boolean active() {
            return ((value <= -MIN_ACCELERATION) || (value >= MIN_ACCELERATION));
        }
    }

    /**
     * Class representing Accelerometer based Controls
     */
    @SuppressWarnings("WeakerAccess")
    public static class ControlAccelerationBind {
        /**
         * Current Control Type
         */
        public int controlType;
        /**
         * Current acceleration type
         */
        public int accelerationType;
        /**
         * Current multiplier
         */
        public int mult;

        /**
         * Class Constructor
         * @param controlType controls type
         * @param accelerationType acceleration type
         * @param mult multiplier
         */
        public ControlAccelerationBind(int controlType, int accelerationType, int mult) {
            this.controlType = controlType;
            this.accelerationType = accelerationType;
            this.mult = mult;
        }
    }

    /**
     * Class representing the Control's Button
     */
    @SuppressWarnings({ "WeakerAccess", "MagicNumber" })
    public static class ControlItem {
        /**
         * Button position X
         */
        public int x;
        /**
         * Button position Y
         */
        public int y;
        /**
         * Button type
         */
        public int type;
        /**
         * Does the Icon have effects?
         */
        public boolean decoration;
        /**
         * Icon to load for the button
         */
        public int icon;

        /**
         * Class Constructor
         * @param x Button Position X
         * @param y Button Position Y
         * @param type Button Type
         */
        public ControlItem(int x, int y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.decoration = false;

            updateIcon();
        }

        /**
         * Class Constructor
         * @param x Button Position X
         * @param y Button Position Y
         * @param type Button Type
         * @param decoration Does the button have effects?
         */
        public ControlItem(int x, int y, int type, boolean decoration) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.decoration = decoration;

            updateIcon();
        }

        /**
         * Updates the Icon after pressing
         */
        private void updateIcon() {
            switch (type) {
                case FORWARD:
                    icon = 0;
                    break;

                case BACKWARD:
                    icon = 1;
                    break;

                case STRAFE_LEFT:
                    icon = 2;
                    break;

                case STRAFE_RIGHT:
                    icon = 3;
                    break;

                case ACTION:
                    icon = 4;
                    break;

                case NEXT_WEAPON:
                    icon = 5;
                    break;

                default:
                    updateIcon2();
                    break;
            }
        }

        /**
         * Continues to choose the correct icon update
         */
        private void updateIcon2(){
            switch(type){
                case ROTATE_LEFT:
                    icon = 6;
                    break;

                case ROTATE_RIGHT:
                    icon = 7;
                    break;

                case TOGGLE_MAP:
                    icon = 14;
                    break;

                case OPEN_MENU:
                    icon = (TextureLoader.BASE_ADDITIONAL - TextureLoader.BASE_ICONS) + 1;
                    break;

                default:
                    icon = 14;
                    break;
            }
        }
    }

    /**
     * Class representing different Controls Variant
     */
    @SuppressWarnings("WeakerAccess")
    public static class ControlVariant {
        /**
         * Different Controls Array
         */
        public ControlItem[] items;
        /**
         * Are Controls Slidable Pad?
         */
        public boolean slidable;
        /**
         * Position Y of current Stats
         */
        public float statsBaseY;
        /**
         * Position Y of Key count
         */
        public float keysBaseY;
        /**
         * Position Y of Debug info
         */
        public float debugLineBaseY;
        /**
         * Is the Map active?
         */
        public boolean hasMap;
        /**
         * Map position X
         */
        public int mapX;
        /**
         * Map position Y
         */
        public int mapY;
        /**
         * Map array
         */
        public int[][] map;
        /**
         * Are Controls Pad?
         */
        public boolean hasPad;
        /**
         * Pad position X
         */
        public int padX;
        /**
         * Pad position Y
         */
        public int padY;

        /**
         * Class Constructor
         * @param slidable Is Slidable Pad?
         * @param statsBaseY Position Y of Stats
         * @param keysBaseY Position Y of Keys
         * @param debugLineBaseY Position Y of Debug Line
         * @param items Control buttons
         */
        public ControlVariant(boolean slidable,
                float statsBaseY,
                float keysBaseY,
                float debugLineBaseY,
                ControlItem[] items) {

            this.slidable = slidable;
            this.statsBaseY = statsBaseY;
            this.keysBaseY = keysBaseY;
            this.debugLineBaseY = debugLineBaseY;
            this.items = items;
            this.hasMap = false;
            this.hasPad = false;
        }

        /**
         * Class Constructor
         * @param slidable Is Slidable Pad?
         * @param statsBaseY Position Y of Stats
         * @param keysBaseY Position Y of Keys
         * @param debugLineBaseY Position Y of Debug Line
         * @param items Control buttons
         * @param padX Position X of Pad
         * @param padY Position Y of Pad
         */
        public ControlVariant(boolean slidable,
                float statsBaseY,
                float keysBaseY,
                float debugLineBaseY,
                ControlItem[] items,
                int padX,
                int padY) {

            this.slidable = slidable;
            this.statsBaseY = statsBaseY;
            this.keysBaseY = keysBaseY;
            this.debugLineBaseY = debugLineBaseY;
            this.items = items;
            this.hasMap = false;
            this.hasPad = true;
            this.padX = padX;
            this.padY = padY;
        }

        /**
         *
         * Class Constructor
         * @param slidable Is Slidable Pad?
         * @param statsBaseY Position Y of Stats
         * @param keysBaseY Position Y of Keys
         * @param debugLineBaseY Position Y of Debug Line
         * @param items Control buttons
         * @param mapX Map position X
         * @param mapY Map position Y
         * @param map Is Map Active?
         */
        public ControlVariant(boolean slidable,
                float statsBaseY,
                float keysBaseY,
                float debugLineBaseY,
                ControlItem[] items,
                int mapX,
                int mapY,
                int[][] map) {

            this.slidable = slidable;
            this.statsBaseY = statsBaseY;
            this.keysBaseY = keysBaseY;
            this.debugLineBaseY = debugLineBaseY;
            this.items = items;
            this.hasMap = true;
            this.mapX = mapX;
            this.mapY = mapY;
            this.map = map;
            this.hasPad = false;
        }
    }

    /**
     * Constant for Classic Controls
     */
    public static final int TYPE_CLASSIC = 0;
    /**
     * Constant for Improved Controls
     */
    public static final int TYPE_IMPROVED = 1;
    /**
     * Constant for Pad L Controls
     */
    public static final int TYPE_PAD_L = 2;
    /**
     * Constant for Pad R Controls
     */
    public static final int TYPE_PAD_R = 3;
    /**
     * Constant for Experimental A Controls
     */
    public static final int TYPE_EXPERIMENTAL_A = 4;
    /**
     * Constant for Experimental B Controls
     */
    public static final int TYPE_EXPERIMENTAL_B = 5;
    /**
     * Constant for Zeemote Controls
     */
    public static final int TYPE_ZEEMOTE = 6; // Must be last

    /**
     * Constant for Forward key
     */
    public static final int FORWARD = 1;
    /**
     * Constant for Backward key
     */
    public static final int BACKWARD = 2;
    /**
     * Constant for Strafe Left key
     */
    public static final int STRAFE_LEFT = 4;
    /**
     * Constant for Strafe Right key
     */
    public static final int STRAFE_RIGHT = 8;
    /**
     * Constant for Action key
     */
    public static final int ACTION = 16;
    /**
     * Constant for Next Weapon Key
     */
    public static final int NEXT_WEAPON = 32;
    /**
     * Constant for Rotate Left Key
     */
    public static final int ROTATE_LEFT = 64;
    /**
     * Constant for Rotate Right Key
     */
    public static final int ROTATE_RIGHT = 128;
    /**
     * Constant for Toogle Map Key
     */
    public static final int TOGGLE_MAP = 256;
    /**
     * Constant for Strafe Mode Key
     */
    public static final int STRAFE_MODE = 512;
    /**
     * Constant for Open Menu Key
     */
    @SuppressWarnings("WeakerAccess") public static final int OPEN_MENU = 1024;
    /**
     * Constant for MAX Mask value
     */
    @SuppressWarnings("WeakerAccess") public static final int MASK_MAX = 2048;

    /**
     * Constant for Acceleration Move Key
     */
    @SuppressWarnings("WeakerAccess") public static final int ACCELERATION_MOVE = 0;
    /**
     * Constant for Acceleration Strafe Key
     */
    @SuppressWarnings("WeakerAccess") public static final int ACCELERATION_STRAFE = 1;
    /**
     * Constant for Acceleration Rotate Key
     */
    @SuppressWarnings("WeakerAccess") public static final int ACCELERATION_ROTATE = 2;

    /**
     * Constant for Pointer Down
     */
    private static final int POINTER_DOWN = 1;
    /**
     * Constant for Pointer Move
     */
    private static final int POINTER_MOVE = 2;
    /**
     * Constant for Pointer Up
     */
    private static final int POINTER_UP = 3;

    /**
     * Constant for Max Pointer ID
     */
    private static final int POINTER_MAX_ID = 4;

    /**
     * Constant for Minimum Pad Offset
     */
    private static final float PAD_MIN_OFF = 0.05f;
    /**
     * Constant for Maximum Pad Offset
     */
    private static final float PAD_MAX_OFF = 1.125f;
    /**
     * Constant for Initial Pad Offset
     */
    private static final float PAD_INIT_OFF = 0.03f;

    /**
     * Constant for Acceleration Settings
     */
    @SuppressWarnings("WeakerAccess")
    public static final ControlAcceleration[] ACCELERATIONS = { new ControlAcceleration(0.1f),
            // ACCELERATION_MOVE
            new ControlAcceleration(0.1f),
            // ACCELERATION_STRAFE
            new ControlAcceleration(0.1f)
            // ACCELERATION_ROTATE
    };

    /**
     * Constant for Acceleration Control Bindings
     */
    @SuppressWarnings("WeakerAccess")
    public static final ControlAccelerationBind[] ACCELERATION_BINDS = { new ControlAccelerationBind(FORWARD,
            ACCELERATION_MOVE,
            1),
            new ControlAccelerationBind(BACKWARD, ACCELERATION_MOVE, -1),
            new ControlAccelerationBind(STRAFE_LEFT, ACCELERATION_STRAFE, -1),
            new ControlAccelerationBind(STRAFE_RIGHT, ACCELERATION_STRAFE, 1),
            new ControlAccelerationBind(ROTATE_LEFT, ACCELERATION_ROTATE, -1),
            new ControlAccelerationBind(ROTATE_RIGHT, ACCELERATION_ROTATE, 1) };

    // @formatter:off
    /**
     * Constant for Control Variants
     */
    @SuppressWarnings("WeakerAccess") public static final ControlVariant[] VARIANTS = {
            // TYPE_CLASSIC
            new ControlVariant(false,
                    0.8125f,
                    0.7f,
                    0.0f,
                    new ControlItem[] {
                            new ControlItem(12, 1, OPEN_MENU),
                            new ControlItem(15, 1, TOGGLE_MAP),
                            new ControlItem(18, 1, NEXT_WEAPON),
                            new ControlItem(18, 5, ACTION),
                            new ControlItem(1, 12, ROTATE_LEFT, true),
                            new ControlItem(5, 12, ROTATE_RIGHT, true),
                            new ControlItem(3, 10, FORWARD, true),
                            new ControlItem(3, 14, BACKWARD, true),
                            new ControlItem(15, 12, STRAFE_LEFT),
                            new ControlItem(18, 12, STRAFE_RIGHT) },
                    0,
                    8,
                    new int[][] {
                            new int[] { FORWARD | ROTATE_LEFT, FORWARD | ROTATE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT },
                            new int[] { FORWARD | ROTATE_LEFT, FORWARD | ROTATE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT },
                            new int[] { FORWARD | ROTATE_LEFT, FORWARD | ROTATE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT },
                            new int[] { ROTATE_LEFT, ROTATE_LEFT, 0, FORWARD, 0, ROTATE_RIGHT, ROTATE_RIGHT, ROTATE_RIGHT },
                            new int[] { ROTATE_LEFT, ROTATE_LEFT, 0, 0, 0, ROTATE_RIGHT, ROTATE_RIGHT, ROTATE_RIGHT },
                            new int[] { ROTATE_LEFT, ROTATE_LEFT, 0, BACKWARD, 0, ROTATE_RIGHT, ROTATE_RIGHT, ROTATE_RIGHT },
                            new int[] { BACKWARD | ROTATE_LEFT, BACKWARD | ROTATE_LEFT, BACKWARD, BACKWARD, BACKWARD, BACKWARD | ROTATE_RIGHT, BACKWARD | ROTATE_RIGHT, BACKWARD | ROTATE_RIGHT },
                            new int[] { BACKWARD | ROTATE_LEFT, BACKWARD | ROTATE_LEFT, BACKWARD, BACKWARD, BACKWARD, BACKWARD | ROTATE_RIGHT, BACKWARD | ROTATE_RIGHT, BACKWARD | ROTATE_RIGHT } }),
            // TYPE_IMPROVED
            new ControlVariant(false,
                    0.8125f,
                    0.7f,
                    0.0f,
                    new ControlItem[] {
                            new ControlItem(12, 1, OPEN_MENU),
                            new ControlItem(15, 1, TOGGLE_MAP),
                            new ControlItem(18, 1, NEXT_WEAPON),
                            new ControlItem(18, 5, ACTION),
                            new ControlItem(1, 12, STRAFE_LEFT, true),
                            new ControlItem(5, 12, STRAFE_RIGHT, true),
                            new ControlItem(3, 10, FORWARD, true),
                            new ControlItem(3, 14, BACKWARD, true),
                            new ControlItem(15, 12, ROTATE_LEFT),
                            new ControlItem(18, 12, ROTATE_RIGHT) },
                    0,
                    8,
                    new int[][] {
                            new int[] { FORWARD | STRAFE_LEFT, FORWARD | STRAFE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT },
                            new int[] { FORWARD | STRAFE_LEFT, FORWARD | STRAFE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT },
                            new int[] { FORWARD | STRAFE_LEFT, FORWARD | STRAFE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT },
                            new int[] { STRAFE_LEFT, STRAFE_LEFT, 0, FORWARD, 0, STRAFE_RIGHT, STRAFE_RIGHT, STRAFE_RIGHT },
                            new int[] { STRAFE_LEFT, STRAFE_LEFT, 0, 0, 0, STRAFE_RIGHT, STRAFE_RIGHT, STRAFE_RIGHT },
                            new int[] { STRAFE_LEFT, STRAFE_LEFT, 0, BACKWARD, 0, STRAFE_RIGHT, STRAFE_RIGHT, STRAFE_RIGHT },
                            new int[] { BACKWARD | STRAFE_LEFT, BACKWARD | STRAFE_LEFT, BACKWARD, BACKWARD, BACKWARD, BACKWARD | STRAFE_RIGHT, BACKWARD | STRAFE_RIGHT, BACKWARD | STRAFE_RIGHT },
                            new int[] { BACKWARD | STRAFE_LEFT, BACKWARD | STRAFE_LEFT, BACKWARD, BACKWARD, BACKWARD, BACKWARD | STRAFE_RIGHT, BACKWARD | STRAFE_RIGHT, BACKWARD | STRAFE_RIGHT } }),
            // TYPE_PAD_L
            new ControlVariant(false,
                    0.8125f,
                    0.7f,
                    0.0f,
                    new ControlItem[] {
                            new ControlItem(12, 1, OPEN_MENU),
                            new ControlItem(15, 1, TOGGLE_MAP),
                            new ControlItem(18, 1, ACTION),
                            new ControlItem(18, 5, NEXT_WEAPON) },
                    4,
                    12),
            // TYPE_PAD_R
            new ControlVariant(false,
                    0.8125f,
                    0.7f,
                    0.0f,
                    new ControlItem[] {
                            new ControlItem(1, 14, OPEN_MENU),
                            new ControlItem(1, 4, ACTION),
                            new ControlItem(4, 4, TOGGLE_MAP),
                            new ControlItem(1, 8, NEXT_WEAPON) },
                    15,
                    12),
            // TYPE_EXPERIMENTAL_A
            new ControlVariant(true,
                    -0.0625f,
                    0.05f,
                    0.2375f,
                    new ControlItem[] {
                            new ControlItem(7, 1, OPEN_MENU),
                            new ControlItem(4, 1, TOGGLE_MAP),
                            new ControlItem(1, 1, NEXT_WEAPON),
                            new ControlItem(18, 1, FORWARD),
                            new ControlItem(18, 5, BACKWARD),
                            new ControlItem(18, 10, ACTION),
                            new ControlItem(15, 14, STRAFE_LEFT),
                            new ControlItem(18, 14, STRAFE_RIGHT) }),
            // TYPE_EXPERIMENTAL_B
            new ControlVariant(true,
                    0.8125f,
                    0.7f,
                    0.0f,
                    new ControlItem[] {
                            new ControlItem(12, 1, OPEN_MENU),
                            new ControlItem(15, 1, TOGGLE_MAP),
                            new ControlItem(18, 1, NEXT_WEAPON),
                            new ControlItem(18, 5, ACTION),
                            new ControlItem(1, 12, STRAFE_LEFT, true),
                            new ControlItem(5, 12, STRAFE_RIGHT, true),
                            new ControlItem(3, 10, FORWARD, true),
                            new ControlItem(3, 14, BACKWARD, true) },
                    0,
                    8,
                    new int[][] {
                            new int[] { FORWARD | STRAFE_LEFT, FORWARD | STRAFE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT },
                            new int[] { FORWARD | STRAFE_LEFT, FORWARD | STRAFE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT },
                            new int[] { FORWARD | STRAFE_LEFT, FORWARD | STRAFE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT, FORWARD | STRAFE_RIGHT },
                            new int[] { STRAFE_LEFT, STRAFE_LEFT, MASK_MAX, FORWARD, MASK_MAX, STRAFE_RIGHT, STRAFE_RIGHT, STRAFE_RIGHT },
                            new int[] { STRAFE_LEFT, STRAFE_LEFT, MASK_MAX, MASK_MAX, MASK_MAX, STRAFE_RIGHT, STRAFE_RIGHT, STRAFE_RIGHT },
                            new int[] { STRAFE_LEFT, STRAFE_LEFT, MASK_MAX, BACKWARD, MASK_MAX, STRAFE_RIGHT, STRAFE_RIGHT, STRAFE_RIGHT },
                            new int[] { BACKWARD | STRAFE_LEFT, BACKWARD | STRAFE_LEFT, BACKWARD, BACKWARD, BACKWARD, BACKWARD | STRAFE_RIGHT, BACKWARD | STRAFE_RIGHT, BACKWARD | STRAFE_RIGHT },
                            new int[] { BACKWARD | STRAFE_LEFT, BACKWARD | STRAFE_LEFT, BACKWARD, BACKWARD, BACKWARD, BACKWARD | STRAFE_RIGHT, BACKWARD | STRAFE_RIGHT, BACKWARD | STRAFE_RIGHT } }),
            // TYPE_ZEEMOTE
            new ControlVariant(false, 0.8125f, 0.7f, 0.0f, new ControlItem[] {
                    new ControlItem(18, 1, OPEN_MENU), }), };
    // @formatter:on

    /**
     * Controls Map
     */
    @SuppressWarnings("MagicNumber") private static int[][] controlsMap = new int[20][16];
    /**
     * All Pointers Action Mask
     */
    private static int[] pointerActionsMask = new int[POINTER_MAX_ID];
    /**
     * Is Pointer Slide?
     */
    private static boolean[] pointerIsSlide = new boolean[POINTER_MAX_ID];
    /**
     * Is Pointer Pad?
     */
    private static boolean[] pointerIsPad = new boolean[POINTER_MAX_ID];
    /**
     * Pointer previous Position X
     */
    private static float[] pointerPrevX = new float[POINTER_MAX_ID];
    /**
     * Pointer Click Counter
     */
    private static int pointerClickCounter=0;
    /**
     * Pointer Click Position X
     */
    private static float[] pointerClickX = new float[POINTER_MAX_ID];
    /**
     * Pointer Click Position Y
     */
    private static float[] pointerClickY = new float[POINTER_MAX_ID];
    /**
     * Is Pointer Click Based?
     */
    private static boolean[] pointerIsClick = new boolean[POINTER_MAX_ID];
    /**
     * Touch Screen Action Mask
     */
    private static int touchActionsMask=0;
    /**
     * Buttons Action Mask
     */
    private static int keysActionsMask=0;
    /**
     * How long a Key has been pressed
     */
    @SuppressLint("UseSparseArrays") private static Map<Integer, Long> keyDownTimeMap = new HashMap<Integer, Long>();
    /**
     * How long the key is held up
     */
    @SuppressLint("UseSparseArrays") private static Map<Integer, Long> keyUpTimeMap = new HashMap<Integer, Long>();
    /**
     * Trackball Action Mask
     */
    private static int trackballActionsMask=0;
    /**
     * Trackball Position X
     */
    private static float trackballX=0;
    /**
     * Trackball Position Y
     */
    private static float trackballY=0;
    /**
     * Relative Offset of the Buttons
     */
    private static float[] relativeOffset = new float[MASK_MAX];
    /**
     * Is Pad Active?
     */
    private static boolean padActive=true;
    /**
     * Original Pad Center Position X
     */
    private static float origPadCenterX=0;
    /**
     * Original Pad Center Position Y
     */
    private static float origPadCenterY=0;
    /**
     * Current Pad Center Position X
     */
    private static float padCenterX=0;
    /**
     * Current Pad Center Position Y
     */
    private static float padCenterY=0;

    /**
     * Current control Variant
     */
    @SuppressWarnings("WeakerAccess") public static ControlVariant currentVariant = new ControlVariant(false,0.8125f, 0.7f,0.0f, new ControlItem[] {
        new ControlItem(12, 1, OPEN_MENU),
                new ControlItem(15, 1, TOGGLE_MAP),
                new ControlItem(18, 1, NEXT_WEAPON),
                new ControlItem(18, 5, ACTION),
                new ControlItem(1, 12, ROTATE_LEFT, true),
                new ControlItem(5, 12, ROTATE_RIGHT, true),
                new ControlItem(3, 10, FORWARD, true),
                new ControlItem(3, 14, BACKWARD, true),
                new ControlItem(15, 12, STRAFE_LEFT),
                new ControlItem(18, 12, STRAFE_RIGHT) },
            0,
            8,
            new int[][] {
        new int[] { FORWARD | ROTATE_LEFT, FORWARD | ROTATE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT },
                new int[] { FORWARD | ROTATE_LEFT, FORWARD | ROTATE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT },
                new int[] { FORWARD | ROTATE_LEFT, FORWARD | ROTATE_LEFT, FORWARD, FORWARD, FORWARD, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT, FORWARD | ROTATE_RIGHT },
                new int[] { ROTATE_LEFT, ROTATE_LEFT, 0, FORWARD, 0, ROTATE_RIGHT, ROTATE_RIGHT, ROTATE_RIGHT },
                new int[] { ROTATE_LEFT, ROTATE_LEFT, 0, 0, 0, ROTATE_RIGHT, ROTATE_RIGHT, ROTATE_RIGHT },
                new int[] { ROTATE_LEFT, ROTATE_LEFT, 0, BACKWARD, 0, ROTATE_RIGHT, ROTATE_RIGHT, ROTATE_RIGHT },
                new int[] { BACKWARD | ROTATE_LEFT, BACKWARD | ROTATE_LEFT, BACKWARD, BACKWARD, BACKWARD, BACKWARD | ROTATE_RIGHT, BACKWARD | ROTATE_RIGHT, BACKWARD | ROTATE_RIGHT },
                new int[] { BACKWARD | ROTATE_LEFT, BACKWARD | ROTATE_LEFT, BACKWARD, BACKWARD, BACKWARD, BACKWARD | ROTATE_RIGHT, BACKWARD | ROTATE_RIGHT, BACKWARD | ROTATE_RIGHT } });
    /**
     * Rotated Angle of the Controls
     */
    @SuppressWarnings("WeakerAccess") public static float rotatedAngle=0;
    /**
     * Joystick Position X
     */
    @SuppressWarnings("WeakerAccess") public static float joyX=0;
    /**
     * Joystick Position Y
     */
    @SuppressWarnings("WeakerAccess") public static float joyY=0;
    /**
     * Joystick Action Mask
     */
    @SuppressWarnings("WeakerAccess") public static int joyButtonsMask=0;
    /**
     * Accelerometer Position X
     */
    public static float accelerometerX=0;
    /**
     * Accelerometer Position Y
     */
    public static float accelerometerY=0;
    /**
     * Pad Position X
     */
    @SuppressWarnings("WeakerAccess") public static float padX=0;
    /**
     * Pad Position Y
     */
    @SuppressWarnings("WeakerAccess") public static float padY=0;

    /**
     * Class Constructor
     */
    private Controls() {
    }

    /**
     * Checks if Trackball is out of the Dead Zone
     * @param value Current Position
     * @return True if out of Dead Zone, False Otherwise
     */
    private static boolean isTrackballValid(float value){
        return (value <= -0.01f) || (value >= 0.01f);
    }

    /**
     * Is Trackball In Up or Left Position?
     * @param value Current Value
     * @param mask Current Mask
     * @return True or false
     */
    private static boolean isTrackballUpLeft(float value, int mask){
        return (value < 0) && (mask != 0);
    }

    /**
     * Updates the Trackball current values
     * @param maskUp Mask for Up Trackball
     * @param maskDown Mask for Down Trackball
     * @param maskLeft Mask for Left Trackball
     * @param maskRight Mask for Right Trackball
     */
    private static void updateTrackball(int maskUp, int maskDown, int maskLeft, int maskRight){
        if (isTrackballValid(trackballX)) {
            if (isTrackballUpLeft(trackballX, maskLeft)) {
                trackballActionsMask |= maskLeft;
                relativeOffset[maskLeft] = -trackballX;
            } else if (maskRight != 0) {
                trackballActionsMask |= maskRight;
                relativeOffset[maskRight] = trackballX;
            }
        }

        if (isTrackballValid(trackballY)) {
            if (isTrackballUpLeft(trackballY, maskUp)) {
                trackballActionsMask |= maskUp;
                relativeOffset[maskUp] = -trackballY;
            } else if (maskDown != 0) {
                trackballActionsMask |= maskDown;
                relativeOffset[maskDown] = trackballY;
            }
        }
    }

    /**
     * Checks if Strafe is Active
     * @param mask Current Mask
     * @return True or False
     */
    private static int checkStrafe(int mask){
        if ((mask & STRAFE_MODE) != 0) {
            mask = (mask & ~(ROTATE_LEFT | ROTATE_RIGHT | STRAFE_LEFT | STRAFE_RIGHT)) | (((mask & ROTATE_LEFT) != 0)
                    ? STRAFE_LEFT
                    : 0) | (((mask & ROTATE_RIGHT) != 0) ? STRAFE_RIGHT : 0) | (((mask & STRAFE_LEFT) != 0)
                    ? ROTATE_LEFT
                    : 0) | (((mask & STRAFE_RIGHT) != 0) ? ROTATE_RIGHT : 0);
        }
        return mask;
    }

    /**
     * Retrieves the current action mask
     * @return The Action Mask
     */
    @SuppressWarnings({ "WeakerAccess", "MagicNumber" })
    public static int getActionsMask() {
        int maskLeft=0;
        int maskRight=0;
        int maskUp=0;
        int maskDown=0;

        if (Config.rotateScreen) {
            trackballX = -trackballX;
            trackballY = -trackballY;

            maskLeft = Config.keyMappings[KeyEvent.KEYCODE_DPAD_RIGHT];
            maskRight = Config.keyMappings[KeyEvent.KEYCODE_DPAD_LEFT];
            maskUp = Config.keyMappings[KeyEvent.KEYCODE_DPAD_DOWN];
            maskDown = Config.keyMappings[KeyEvent.KEYCODE_DPAD_UP];
        } else {
            maskLeft = Config.keyMappings[KeyEvent.KEYCODE_DPAD_LEFT];
            maskRight = Config.keyMappings[KeyEvent.KEYCODE_DPAD_RIGHT];
            maskUp = Config.keyMappings[KeyEvent.KEYCODE_DPAD_UP];
            maskDown = Config.keyMappings[KeyEvent.KEYCODE_DPAD_DOWN];
        }

        trackballActionsMask = 0;

        updateTrackball(maskUp, maskDown, maskLeft, maskRight);

        trackballX = 0.0f;
        trackballY = 0.0f;

        for (Map.Entry<Integer, Long> entry : keyUpTimeMap.entrySet()) {
            if ((entry.getValue() != null) && (Game.elapsedTime > entry.getValue())) {
                keysActionsMask &= ~(Config.keyMappings[entry.getKey()]);
                entry.setValue(null);
            }
        }

        int mask = (touchActionsMask | keysActionsMask | trackballActionsMask | joyButtonsMask);

        mask = checkStrafe(mask);

        return mask;
    }

    /**
     * Initializes Joystick Variables
     */
    @SuppressWarnings("WeakerAccess")
    public static void initJoystickVars() {
        joyX = 0.0f;
        joyY = 0.0f;
        joyButtonsMask = 0;
    }

    /**
     * Updates Control Map
     */
    private static void updateControlMap(){
        for (ControlItem ci : currentVariant.items) {
            if (!ci.decoration) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        controlsMap[ci.x + i][ci.y + j] = ci.type;
                    }
                }
            }
        }

        if (currentVariant.hasMap) {
            for (int i = 0; i < currentVariant.map.length; i++) {
                for (int j = 0; j < currentVariant.map[i].length; j++) {
                    controlsMap[currentVariant.mapX + j][currentVariant.mapY + i] = currentVariant.map[i][j];
                }
            }
        }
    }

    /**
     * Fills the Map
     */
    @SuppressWarnings("MagicNumber")
    public static void fillMap() {
        rotatedAngle = 0;
        touchActionsMask = 0;
        keysActionsMask = 0;
        keyDownTimeMap.clear();
        keyUpTimeMap.clear();
        trackballX = 0.0f;
        trackballY = 0.0f;
        trackballActionsMask = 0;
        padActive = false;
        padX = 0.0f;
        padY = 0.0f;
        accelerometerX = 0.0f;
        accelerometerY = 0.0f;

        initJoystickVars();

        for (int i = 1; i < MASK_MAX; i *= 2) {
            relativeOffset[i] = 0.0f;
        }

        currentVariant = VARIANTS[Config.controlsType];
        origPadCenterX = ((float)currentVariant.padX + 0.5f) / 20.0f;
        origPadCenterY = ((float)currentVariant.padY + 0.5f) / 16.0f;
        padCenterX = origPadCenterX;
        padCenterY = origPadCenterY;

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 16; j++) {
                controlsMap[i][j] = 0;
            }
        }

        updateControlMap();

        for (int i = 0; i < POINTER_MAX_ID; i++) {
            pointerActionsMask[i] = 0;
            pointerIsSlide[i] = false;
            pointerIsPad[i] = false;
            pointerPrevX[i] = 0.0f;
            pointerClickX[i] = 0.0f;
            pointerClickY[i] = 0.0f;
            pointerIsClick[i] = false;
        }
    }

    /**
     * Checks for current variant
     * @param pid Process ID
     * @param x Control Position X
     * @param y Control Position Y
     * @param ctlX Tile Position X
     * @param ctlY Tile Position Y
     */
    private static void checkVariant(int pid, float x, float y, int ctlX, int ctlY){
        if (currentVariant.hasPad && ((currentVariant.padX < 10) ? (ctlX < 10) : (ctlX >= 10))) {
            pointerIsPad[pid] = true;
        } else if (currentVariant.slidable && (controlsMap[ctlX][ctlY] == 0)) {
            pointerIsSlide[pid] = true;
        }

        if ((State.levelNum == 1) && (controlsMap[ctlX][ctlY] == 0)) {
            pointerClickX[pid] = x;
            pointerClickY[pid] = y;
            pointerIsClick[pid] = true;
        }
    }

    /**
     * Updates the Pointer
     * @param pid Process ID
     * @param pointerAction Pointer Action Mask
     * @param x Pad Position X
     * @param y Pad Position Y
     * @param ctlX Tile Position X
     * @param ctlY Tile Position Y
     */
    private static void updatePointer(int pid, int pointerAction, float x, float y, int ctlX, int ctlY){
        if (pointerAction == POINTER_DOWN) {
            pointerActionsMask[pid] = 0;
            pointerPrevX[pid] = x;
            pointerIsPad[pid] = false;
            pointerIsSlide[pid] = false;
            pointerIsClick[pid] = false;

            checkVariant(pid, x, y, ctlX, ctlY);
        } else if ((State.levelNum == 1) && pointerIsClick[pid]) {
            float distSq = ((pointerClickX[pid] - x) * (pointerClickX[pid] - x)) + ((pointerClickY[pid] - y) * (
                    pointerClickY[pid]
                            - y));

            if (distSq > (10.0f * 10.0f)) {
                pointerIsClick[pid] = false;
            }
        }
    }

    /**
     * Center the Pad
     * @param x Current Position X
     * @param y Current Position Y
     */
    private static void centerPad(float x, float y){
        if (!padActive) {
            padActive = true;
            padCenterX = x / (float)(Game.width + 1);
            padCenterY = y / (float)(Game.height + 1);

            if (padCenterX < (origPadCenterX - PAD_INIT_OFF)) {
                padCenterX = origPadCenterX - PAD_INIT_OFF;
            }
            if (padCenterX > (origPadCenterX + PAD_INIT_OFF)) {
                padCenterX = origPadCenterX + PAD_INIT_OFF;
            }
            if (padCenterY < (origPadCenterY - PAD_INIT_OFF)) {
                padCenterY = origPadCenterY - PAD_INIT_OFF;
            }
            if (padCenterY > (origPadCenterY + PAD_INIT_OFF)) {
                padCenterY = origPadCenterY + PAD_INIT_OFF;
            }
        }
    }

    /**
     * Checks Pad Position X
     */
    private static void checkPadX(){
        if (padX > 0.0f) {
            padX -= PAD_MIN_OFF;

            if (padX < 0.0f) {
                padX = 0.0f;
            } else if (padX > PAD_MAX_OFF) {
                padX = PAD_MAX_OFF;
            }
        } else {
            padX += PAD_MIN_OFF;

            if (padX > 0.0f) {
                padX = 0.0f;
            } else if (padX < -PAD_MAX_OFF) {
                padX = -PAD_MAX_OFF;
            }
        }
    }

    /**
     * Checks Pad Position Y
     */
    private static void checkPadY(){
        if (padY > 0.0f) {
            padY -= PAD_MIN_OFF;

            if (padY < 0.0f) {
                padY = 0.0f;
            } else if (padY > PAD_MAX_OFF) {
                padY = PAD_MAX_OFF;
            }
        } else {
            padY += PAD_MIN_OFF;

            if (padY > 0.0f) {
                padY = 0.0f;
            } else if (padY < -PAD_MAX_OFF) {
                padY = -PAD_MAX_OFF;
            }
        }
    }

    /**
     * Checks if Pointer is Up
     * @param pid Process ID
     */
    private static void checkPointerUp(int pid){
        if ((State.levelNum == 1) && pointerIsClick[pid]) {
            pointerClickCounter += 1;

            if (pointerClickCounter == 5) {
                ZameApplication.trackEvent("Tutorial", "Click", "", 0);
            }
        }

        if (pointerIsPad[pid]) {
            padActive = false;
            padX = 0.0f;
            padY = 0.0f;
            padCenterX = origPadCenterX;
            padCenterY = origPadCenterY;
        }
    }

    /**
     * Is Process ID not valid?
     * @param pid Process ID
     * @return True if it's beyond range (0 < pid < Pointer_MAX_ID)
     */
    private static boolean isPIDNotValid(int pid){
        return (pid < 0) || (pid >= POINTER_MAX_ID);
    }

    /**
     * Check pad value
     * @param pad Pad current value
     * @return 1 if pad < 1, pad otherwise
     */
    private static int checkPad(int pad){
        return (pad < 1) ? 1 : pad;
    }

    /**
     * Checks What Pointer is being used
     * @param pid Process ID
     * @param x Pointer Position X
     * @param y Pointer Position Y
     * @param ctlX Tile Position X
     * @param ctlY Tile Position Y
     */
    private static void whatPointer(int pid, float x, float y, int ctlX, int ctlY){
        if (pointerIsSlide[pid]) {
            float distX = x - pointerPrevX[pid];
            float da = (distX * Config.maxRotateAngle) / (float)Game.width;

            pointerPrevX[pid] = x;

            // if angle is more than half of max angle, this is incorrect MotionEvent (in most of cases)
            // if (Math.abs(da) < (Config.maxRotateAngle / 2.0f)) {
            rotatedAngle += (Config.invertRotation ? da : -da);
            // }
        } else if (pointerIsPad[pid]) {
            centerPad(x, y);

            int padWidth = (Game.width * 3) / 20;
            padWidth = checkPad(padWidth);

            int padHeight = (Game.height * 3) / 16;

            padHeight = checkPad(padHeight);

            padX = (x - (padCenterX * intToFloat(Game.width))) / intToFloat(padWidth);
            padY = (y - (padCenterY * (float)Game.height)) / (float)padHeight;

            checkPadX();

            checkPadY();
        } else {
            pointerActionsMask[pid] = controlsMap[ctlX][ctlY];
        }
    }

    /**
     * Updates Tile Position X
     * @param ctlX Current Tile Position X
     * @return New Tile Position X
     */
    private static int getCtlX(int ctlX){
        if (ctlX < 0) {
            ctlX = 0;
        } else if (ctlX >= 20) {
            ctlX = 19;
        }

        return ctlX;
    }

    /**
     * Updates Tile Position Y
     * @param ctlY Current Tile Position Y
     * @return New Tile Position Y
     */
    private static int getCtlY(int ctlY){
        if (ctlY < 0) {
            ctlY = 0;
        } else if (ctlY >= 16) {
            ctlY = 15;
        }

        return ctlY;
    }

    /**
     * Process One Pointer
     * @param pid Process ID
     * @param x Pointer Position X
     * @param y Pointer Position Y
     * @param pointerAction Pointer Action Mask
     */
    @SuppressWarnings({ "WeakerAccess", "MagicNumber" })
    public static void processOnePointer(int pid, float x, float y, int pointerAction) {
        if (isPIDNotValid(pid)) {
            return;
        }

        if (Config.rotateScreen) {
            x = (float)Game.width - x;
            y = (float)Game.height - y;
        }

        int ctlX = (Math.round(x) * 20) / (Game.width + 1);
        int ctlY = (Math.round(y) * 16) / (Game.height + 1);

        ctlX = getCtlX(ctlX);
        ctlY = getCtlY(ctlY);

        if ((pointerAction == POINTER_DOWN) || (pointerAction == POINTER_MOVE)) {
            updatePointer(pid, pointerAction, x, y, ctlX, ctlY);

            // ----

            whatPointer(pid, x, y, ctlX, ctlY);
        } else if (pointerAction == POINTER_UP) {
            checkPointerUp(pid);

            pointerActionsMask[pid] = 0;
            pointerIsClick[pid] = false;
        }
    }

    /**
     * Casts Int to Float
     * @param a Int value to cast
     * @return Float value of a
     */
    private static float intToFloat(int a)
    {
        if (a < Float.MIN_VALUE || a > Float.MAX_VALUE) {
            throw new IllegalArgumentException("Value not castable");
        }
        return (float) a;
    }

    /**
     * Updated TouchScreen Action Mask
     */
    private static void updateTouchActionMask(){
        for (int i = 0; i < POINTER_MAX_ID; i++) {
            touchActionsMask |= pointerActionsMask[i];
        }
    }

    /**
     * Touch Event Handler
     * @param event Event
     */
    @SuppressWarnings("WeakerAccess")
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static void touchEvent(MotionEvent event) {
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        int points = event.getPointerCount();
        int aidx=0;

        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < points; i++) {
                    processOnePointer(event.getPointerId(i), event.getX(i), event.getY(i), POINTER_DOWN);
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                aidx = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

                for (int i = 0; i < points; i++) {
                    processOnePointer(event.getPointerId(i),
                            event.getX(i),
                            event.getY(i),
                            ((i == aidx) ? POINTER_DOWN : POINTER_MOVE));
                }
                break;

            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < points; i++) {
                    processOnePointer(event.getPointerId(i), event.getX(i), event.getY(i), POINTER_MOVE);
                }
                break;
            default :
                touchEvent2(event, points, action, actionCode, aidx);
                break;
        }

        touchActionsMask = 0;

        updateTouchActionMask();
    }

    /**
     * Continues Touch Event Handler Process
     * @param event Event
     * @param points Current Points
     * @param action Current Action
     * @param actionCode Current Action Code
     * @param aidx Action ID X
     */
    private static void touchEvent2(MotionEvent event, int points, int action, int actionCode, int aidx){
        switch (actionCode){
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < points; i++) {
                processOnePointer(event.getPointerId(i), event.getX(i), event.getY(i), POINTER_UP);
            }
                break;
            case MotionEvent.ACTION_CANCEL:
                for (int i = 0; i < points; i++) {
                    processOnePointer(event.getPointerId(i), event.getX(i), event.getY(i), POINTER_UP);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                aidx = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

                for (int i = 0; i < points; i++) {
                    processOnePointer(event.getPointerId(i),
                            event.getX(i),
                            event.getY(i),
                            ((i == aidx) ? POINTER_UP : POINTER_MOVE));
                }
                break;
            default: break;
        }
    }

    /**
     * Handles Key Down event
     * @param keyCode Key pressed
     * @return true if valid key, false otherwise
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean keyDown(int keyCode) {
        if ((keyCode >= 0) && (keyCode < Config.keyMappings.length) && (Config.keyMappings[keyCode] != 0)) {
            keysActionsMask |= Config.keyMappings[keyCode];

            keyDownTimeMap.put(keyCode, Game.elapsedTime);
            keyUpTimeMap.put(keyCode, null);
            return true;
        }

        return false;
    }

    /**
     * Handles Key release
     * @param keyCode Key released
     * @return true if valid key, false otherwise
     */
    @SuppressWarnings({ "WeakerAccess", "MagicNumber" })
    public static boolean keyUp(int keyCode) {
        if ((keyCode >= 0) && (keyCode < Config.keyMappings.length) && (Config.keyMappings[keyCode] != 0)) {
            // fix for emulator, because it fires onKeyDown, than immediately onKeyUp with the same keyCode
            if ((keyDownTimeMap.get(keyCode) != null) && ((Game.elapsedTime - keyDownTimeMap.get(keyCode)) < 16L)) {
                keyUpTimeMap.put(keyCode, Game.elapsedTime + 100L);
            } else {
                keyUpTimeMap.put(keyCode, null);
                keysActionsMask &= ~(Config.keyMappings[keyCode]);
            }

            return true;
        }

        return false;
    }

    /**
     * Updates ControlAcceleration values
     */
    private static void updateCA(){
        for (ControlAcceleration ca : ACCELERATIONS) {
            if (!ca.updated) {
                ca.value *= 0.5f;

                if (!ca.active()) {
                    ca.value = 0.0f;
                }
            }
        }
    }

    /**
     * Updates Acceleration Controls
     * @param mask Current Mask
     */
    @SuppressWarnings({ "WeakerAccess", "MagicNumber" })
    public static void updateAccelerations(int mask) {
        for (ControlAcceleration ca : ACCELERATIONS) {
            ca.updated = false;
        }

        for (ControlAccelerationBind cb : ACCELERATION_BINDS) {
            if ((mask & cb.controlType) != 0) {
                ControlAcceleration ca = ACCELERATIONS[cb.accelerationType];

                if ((trackballActionsMask & cb.controlType) == 0) {
                    ca.updated = true;
                    ca.value += ca.step * (float)cb.mult;

                    if (ca.value < -1.0f) {
                        ca.value = -1.0f;
                    } else if (ca.value > 1.0f) {
                        ca.value = 1.0f;
                    }
                } else {
                    ca.value += ca.step
                            * (float)cb.mult
                            * relativeOffset[cb.controlType]
                            * Config.trackballAcceleration;
                }
            }
        }

        updateCA();
    }

    /**
     * Handles Trackball Event
     * @param event Event
     */
    @SuppressWarnings("WeakerAccess")
    public static void trackballEvent(MotionEvent event) {
        trackballX += event.getX();
        trackballY += event.getY();
    }

    /**
     * Draws Control Icon
     * @param sx Position X
     * @param sy Position Y
     * @param texNum Texture ID
     * @param pressed Is Icon Pressed?
     * @param highlighted Is Icon Highlighted?
     * @param elapsedTime Time spent
     * @param inverseHighlighting Is Icon Highlighted with opposite cycle?
     */
    @SuppressWarnings("MagicNumber")
    private static void drawIcon(float sx,
            float sy,
            int texNum,
            boolean pressed,
            boolean highlighted,
            long elapsedTime,
            boolean inverseHighlighting) {

        float ex = sx + 0.25f;
        float ey = sy + 0.25f;

        Renderer.x1 = sx;
        Renderer.y1 = sy;
        Renderer.x2 = sx;
        Renderer.y2 = ey;
        Renderer.x3 = ex;
        Renderer.y3 = ey;
        Renderer.x4 = ex;
        Renderer.y4 = sy;

        if (pressed) {
            Renderer.a1 = 1.0f;
        } else if (highlighted) {
            Renderer.a1 = ((float)Math.sin(((double)elapsedTime / 150.0) + (inverseHighlighting ? 3.14f : 0.0f))
                    / 2.01f) + 0.5f;
        } else {
            Renderer.a1 = Config.controlsAlpha;
        }

        Renderer.a2 = Renderer.a1;
        Renderer.a3 = Renderer.a1;
        Renderer.a4 = Renderer.a1;

        Renderer.drawQuad(texNum);
    }

    /**
     * Draw Control Icon
     * @param xpos Position X
     * @param ypos Position Y
     * @param texNum Texture Number
     * @param pressed Is Pressed?
     * @param highlighted Is Highlighted?
     * @param elapsedTime Time elapsed
     */
    @SuppressWarnings("MagicNumber")
    private static void drawControlIcon(int xpos,
            int ypos,
            int texNum,
            boolean pressed,
            boolean highlighted,
            long elapsedTime) {

        float sx = ((((float)xpos + 0.5f) * Common.ratio) / 20.0f) - 0.125f;
        float sy = (((float)(15 - ypos) + 0.5f) / 16.0f) - 0.125f;

        drawIcon(sx, sy, texNum, pressed, highlighted, elapsedTime, false);
    }

    /**
     * Draw Pad Icons
     * @param elapsedTime Time elapsed
     */
    @SuppressWarnings({ "MagicNumber", "PointlessArithmeticExpression" })
    private static void drawPad(long elapsedTime) {
        float sx = (padCenterX * Common.ratio) - 0.125f;
        float sy = (1.0f - padCenterY) - 0.125f;

        drawIcon(sx,
                sy + 0.15f,
                TextureLoader.BASE_ICONS + 0,
                padActive,
                (State.highlightedControlTypeMask & FORWARD) != 0,
                elapsedTime,
                false);

        drawIcon(sx,
                sy - 0.15f,
                TextureLoader.BASE_ICONS + 1,
                padActive,
                (State.highlightedControlTypeMask & BACKWARD) != 0,
                elapsedTime,
                false);

        drawIcon(sx - 0.15f,
                sy,
                TextureLoader.BASE_ICONS + 2,
                padActive,
                (State.highlightedControlTypeMask & ROTATE_LEFT) != 0,
                elapsedTime,
                false);

        drawIcon(sx + 0.15f,
                sy,
                TextureLoader.BASE_ICONS + 3,
                padActive,
                (State.highlightedControlTypeMask & ROTATE_RIGHT) != 0,
                elapsedTime,
                false);

        drawIcon(sx + ((padX * Common.ratio * 2.5f) / 20.0f),
                sy - ((padY * 2.5f) / 16.0f),
                TextureLoader.BASE_ADDITIONAL + 0,
                padActive,
                false,
                elapsedTime,
                false);
    }

    /**
     * Renders the Controls
     * @param gl Renderer
     * @param elapsedTime Time elapsed
     */
    @SuppressWarnings("WeakerAccess")
    public static void render(GL10 gl, long elapsedTime) {
        Renderer.setQuadRGB(1.0f, 1.0f, 1.0f);

        Renderer.z1 = 0.0f;
        Renderer.z2 = 0.0f;
        Renderer.z3 = 0.0f;
        Renderer.z4 = 0.0f;

        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glShadeModel(GL10.GL_FLAT);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPushMatrix();
        Renderer.loadIdentityAndOrthof(gl, 0.0f, Common.ratio, 0.0f, 1.0f, 0.0f, 1.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        Renderer.init();

        for (ControlItem ci : currentVariant.items) {
            drawControlIcon(ci.x,
                    ci.y,
                    TextureLoader.BASE_ICONS + ci.icon,
                    (touchActionsMask & ci.type) != 0,
                    (State.highlightedControlTypeMask & ci.type) != 0,
                    elapsedTime);
        }

        if (currentVariant.hasPad) {
            drawPad(elapsedTime);
        }

        Renderer.bindTextureCtl(gl, TextureLoader.textures[TextureLoader.TEXTURE_MAIN]);
        Renderer.flush(gl);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();
    }
}
