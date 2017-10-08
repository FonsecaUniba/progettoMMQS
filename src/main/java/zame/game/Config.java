package zame.game;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import javax.microedition.khronos.opengles.GL10;
import zame.game.engine.Controls;

public final class Config {
    public static int controlsType=0;
    public static float maxRotateAngle=0;
    public static float trackballAcceleration=0;
    public static float moveSpeed=0;
    public static float strafeSpeed=0;
    public static float rotateSpeed=0;
    public static boolean invertRotation=true;
    public static float gamma=0;
    @SuppressWarnings("WeakerAccess") public static int levelTextureFilter=0;
    @SuppressWarnings("WeakerAccess") public static int weaponsTextureFilter=0;
    public static int[] keyMappings = new int[15];
    public static float mapPosition=0;
    public static boolean showCrosshair=true;
    public static boolean rotateScreen=true;
    @SuppressWarnings("WeakerAccess") public static boolean accelerometerEnabled=true;
    public static float controlsAlpha=0;
    public static float padXAccel=0;
    public static float padYAccel=0;
    public static float accelerometerAcceleration=0;

    private Config() {
    }

    private static void updateKeyMap(SharedPreferences sp, String key, int type) {
        int keyCode = sp.getInt(key, 0);

        if ((keyCode > 0) && (keyCode < keyMappings.length)) {
            keyMappings[keyCode] = type;
        }
    }

    @SuppressWarnings({ "unused", "WeakerAccess" })
    protected static int getControlMaskByName(String name) {
        if ("Action".equals(name)) {
            return Controls.ACTION;
        } else if ("NextWeapon".equals(name)) {
            return Controls.NEXT_WEAPON;
        } else if ("ToggleMap".equals(name)) {
            return Controls.TOGGLE_MAP;
        } else if ("Strafe".equals(name)) {
            return Controls.STRAFE_MODE;
        } else {
            return 0;
        }
    }

    @SuppressWarnings({ "SizeReplaceableByIsEmpty", "unused" })
    public static void checkControlsType() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZameApplication.self);

        String controlsTypeStr = sp.getString("ControlsType", "");
        String prevControlsTypeStr = sp.getString("PrevControlsType", "");

        if (prevControlsTypeStr.length() == 0) {
            SharedPreferences.Editor spEditor = sp.edit();
            spEditor.putString("PrevControlsType", controlsTypeStr);
            spEditor.commit();
        } else if ((controlsTypeStr.length() > 0) && !controlsTypeStr.equals(prevControlsTypeStr)) {
            SharedPreferences.Editor spEditor = sp.edit();
            spEditor.putString("PrevControlsType", controlsTypeStr);
            spEditor.commit();

            ZameApplication.trackEvent("Config", "ControlsTypeChanged", controlsTypeStr, 0);
            ZameApplication.flushEvents();
        }
    }

    private static boolean isZeemote(String controlsTypeStr){
        return BuildConfig.WITH_ZEEMOTE && "Zeemote".equals(controlsTypeStr);
    }

    private static boolean isClassic(String controlsTypeStr){
        return "Classic".equals(controlsTypeStr) || "TypeA".equals(controlsTypeStr);
    }

    private static boolean isExperimental(String controlsTypeStr){
        return "ExperimentalA".equals(controlsTypeStr)
                || "Experimental".equals(controlsTypeStr)
                || "TypeC".equals(controlsTypeStr);
    }

    private static void checkControls(String controlsTypeStr){
        if (isZeemote(controlsTypeStr)) {
            controlsType = Controls.TYPE_ZEEMOTE;
        } else if (isClassic(controlsTypeStr)) {
            controlsType = Controls.TYPE_CLASSIC;
        } else if (isExperimental(controlsTypeStr)) {
            controlsType = Controls.TYPE_EXPERIMENTAL_A;
        } else if ("ExperimentalB".equals(controlsTypeStr)) {
            controlsType = Controls.TYPE_EXPERIMENTAL_B;
        } else if ("PadL".equals(controlsTypeStr)) {
            controlsType = Controls.TYPE_PAD_L;
        } else if ("PadR".equals(controlsTypeStr)) {
            controlsType = Controls.TYPE_PAD_R;
        } else {
            controlsType = Controls.TYPE_IMPROVED;
        }
    }

    @SuppressWarnings("MagicNumber")
    public static void initialize() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZameApplication.self);
        String controlsTypeStr = sp.getString("ControlsType", "PadL");

        checkControls(controlsTypeStr);

        maxRotateAngle = (float)sp.getInt("MaxRotateAngle", 100);
        trackballAcceleration = (float)sp.getInt("TrackballAcceleration", 40);
        moveSpeed = 19.0f - (float)sp.getInt("MoveSpeed", 14); // default = 5.0f
        strafeSpeed = 19.0f - (float)sp.getInt("StrafeSpeed", 7); // default = 12.0f
        rotateSpeed = (float)sp.getInt("RotateSpeed", 6) / 2.0f; // default = 3.0f
        invertRotation = sp.getBoolean("InvertRotation", false);
        gamma = (float)sp.getInt("Gamma", 0) / 25.0f;
        levelTextureFilter = (sp.getBoolean("LevelTextureSmoothing", false) ? GL10.GL_LINEAR : GL10.GL_NEAREST);
        weaponsTextureFilter = (sp.getBoolean("WeaponsTextureSmoothing", true) ? GL10.GL_LINEAR : GL10.GL_NEAREST);

        if (BuildConfig.WITH_ZEEMOTE) {
            ConfigZeemote.initialize(sp);
        }

        keyMappings = new int[KeyEvent.getMaxKeyCode()];

        for (int i = 0; i < keyMappings.length; i++) {
            keyMappings[i] = 0;
        }

        updateKeyMap(sp, "KeyForward", Controls.FORWARD);
        updateKeyMap(sp, "KeyBackward", Controls.BACKWARD);
        updateKeyMap(sp, "KeyRotateLeft", Controls.ROTATE_LEFT);
        updateKeyMap(sp, "KeyRotateRight", Controls.ROTATE_RIGHT);
        updateKeyMap(sp, "KeyStrafeLeft", Controls.STRAFE_LEFT);
        updateKeyMap(sp, "KeyStrafeRight", Controls.STRAFE_RIGHT);
        updateKeyMap(sp, "KeyAction", Controls.ACTION);
        updateKeyMap(sp, "KeyNextWeapon", Controls.NEXT_WEAPON);
        updateKeyMap(sp, "KeyToggleMap", Controls.TOGGLE_MAP);
        updateKeyMap(sp, "KeyStrafeMode", Controls.STRAFE_MODE);

        mapPosition = (float)(sp.getInt("MapPosition", 5) - 5) / 5.0f;
        showCrosshair = sp.getBoolean("ShowCrosshair", false);
        rotateScreen = sp.getBoolean("RotateScreen", false);
        accelerometerEnabled = sp.getBoolean("AccelerometerEnabled", false);
        controlsAlpha = (float)sp.getInt("ControlsAlpha", 3) / 10.0f;

        int tmpPadXAccel = sp.getInt("PadXAccel", 6); // padXAccel : 0.5 (1) -> 1.0 (8) -> 2.0 (15)

        padXAccel = ((tmpPadXAccel >= 8)
                ? ((((float)tmpPadXAccel - 8.0f) / 7.0f) + 1.0f)
                : (1.0f / (2.0f - (((float)tmpPadXAccel - 1.0f) / 7.0f))));

        int tmpPadYAccel = sp.getInt("PadYAccel", 10); // padYAccel : 0.5 (1) -> 1.0 (8) -> 2.0 (15)

        padYAccel = ((tmpPadYAccel >= 8)
                ? ((((float)tmpPadYAccel - 8.0f) / 7.0f) + 1.0f)
                : (1.0f / (2.0f - (((float)tmpPadYAccel - 1.0f) / 7.0f))));

        accelerometerAcceleration = (float)sp.getInt("AccelerometerAcceleration", 5);
    }
}
