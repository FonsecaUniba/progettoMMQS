package zame.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Externalizable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Random;
import zame.game.engine.State;

// http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3_r1/android/os/FileUtils.java#FileUtils

/**
 * Class representing a Common Data Pool
 */
public final class Common {
    /**
     * The Game's Name
     */
    public static final String GAME_NAME = "GloomyDungeons";
    /**
     * Degree to Radiant constant
     */
    public static final float G2RAD_F = (float)(Math.PI / 180.0);
    /**
     * Radiant to Degree constant
     */
    public static final float RAD2G_F = (float)(180.0 / Math.PI);

    /**
     * Hero Angle in Radians
     */
    public static float heroAr=0; // angle in radians
    /**
     * Hero Cosine in Radians
     */
    public static float heroCs=0; // cos of angle
    /**
     * Hero Sin in Radians
     */
    public static float heroSn=0; // sin of angle
    /**
     * Hero Current Ratio
     */
    public static float ratio=0;

    /**
     * RNG
     */
    private static volatile Random random = new Random();

    /**
     * Class Constructor
     */
    private Common() {
    }

    /**
     * Initializes Random and Ratio
     */
    public static void init() {
        if (random == null) {
            random = new Random();
        }

        ratio = 1.0f;
    }

    /**
     * Updates the Hero's Angle
     */
    @SuppressWarnings("MagicNumber")
    public static void heroAngleUpdated() {
        State.heroA = (360.0f + (State.heroA % 360.0f)) % 360.0f;

        heroAr = State.heroA * G2RAD_F;
        heroCs = (float)Math.cos(heroAr);
        heroSn = (float)Math.sin(heroAr);
    }

    /**
     * Checks if line is in not allowed range
     * @param cx1 Start position X
     * @param cx2 End Position X
     * @param cy1 Start Position Y
     * @param cy2 End Position Y
     * @return True if not allowed, false otherwise
     */
    private static boolean isCheckLineInvalid(int cx1, int cx2, int cy1, int cy2){
        return (cx1 < 0)
                || (cx1 >= State.levelWidth)
                || (cx2 < 0)
                || (cx2 >= State.levelWidth)
                || (cy1 < 0)
                || (cy1 >= State.levelHeight)
                || (cy2 < 0)
                || (cy2 >= State.levelHeight);
    }

    /**
     * Casts Float value to Int
     * @param a value to cast
     * @return int value of a
     */
    private static int floatToInt(float a) {
        if (a < Integer.MIN_VALUE || a > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Value not castable");
        }
        return (int) a;
    }

    /**
     * Casts Int value to Float
     * @param a value to cast
     * @return float value of a
     */
    private static float intToFloat(int a) {
        if (a < Float.MIN_VALUE || a > Float.MAX_VALUE) {
            throw new IllegalArgumentException("Value not castable");
        }
        return (float) a;
    }

    /**
     * Checks if two int are equal
     * @param a First value
     * @param b Second value
     * @return True if they are equal
     */
    private static boolean isEquals(int a, int b)
    {
        return Math.abs(a-b) < 1e-6;
    }

    /**
     * Updates CheckLineX value
     * @param cx1 Start Position X
     * @param cx2 End Position X
     * @param mask Mask
     * @param y Y
     * @param stepX Step to increase X
     * @param stepY Step to increase Y
     * @return True
     */
    private static boolean checkXLine(int cx1, int cx2, int mask, float y, int stepX, float stepY){
        do {
            if ((!isEquals((State.passableMap[floatToInt(y)][cx1] & mask),0))) {
                return false;
            }

            y += stepY;
            cx1 += stepX;
        } while (cx1 != cx2);

        return true;
    }

    /**
     * Updates CheckLineY value
     * @param cy1 Start Position Y
     * @param cy2 End Position Y
     * @param mask Mask
     * @param x x
     * @param stepX Step to increase X
     * @param stepY Step to increase Y
     * @return True
     */
    private static boolean checkYLine(int cy1, int cy2, int mask, float x, float stepX, int stepY){
        do {
            if (!isEquals((State.passableMap[cy1][floatToInt(x)] & mask),0)) {
                return false;
            }

            x += stepX;
            cy1 += stepY;
        } while (cy1 != cy2);

        return true;
    }

    /**
     * Returns the step
     * @param value1 First Value to check
     * @param value2 Second value to check
     * @return 1 if value2 > value1, -1 otherwise
     */
    private static int getStep(int value1, int value2){
        return (value2 > value1) ? 1 : -1;
    }

    /**
     * Returns the Partial value
     * @param value1 First value
     * @param value2 Second value
     * @param mod Modifier
     * @return New Partial value
     */
    private static float getPartial(int value1, int value2, float mod){
        return (value2 > value1)
                ? (1.0f - (mod - intToFloat(floatToInt(mod))))
                : (mod - intToFloat(floatToInt(mod))) ;
    }

    // modified Level_CheckLine from wolf3d for iphone by Carmack

    /**
     * Traces the line position
     * @param x1 Start Position X
     * @param y1 Start Position Y
     * @param x2 End Position X
     * @param y2 End Position Y
     * @param mask Current mask
     * @return True if valid, false otherwise
     */
    public static boolean traceLine(float x1, float y1, float x2, float y2, int mask) {
        float add = 0.5f;
        int cx1 = Math.round(x1+add);
        int cy1 = Math.round(y1+add);
        int cx2 = Math.round(x2+add);
        int cy2 = Math.round(y2+add);

        if (isCheckLineInvalid(cx1, cx2, cy1, cy2)) {
            return false;
        }

        if (cx1 != cx2) {
            int stepX = getStep(cx1, cx2);
            float partial=getPartial(cx1, cx2, x1);

            float dx = ((x2 >= x1) ? (x2 - x1) : (x1 - x2));
            float stepY = (y2 - y1) / dx;
            float y = y1 + (stepY * partial);

            cx1 += stepX;
            cx2 += stepX;

            if(!checkXLine(cx1, cx2, mask, y, stepX, stepY)) return false;
        }

        if (cy1 != cy2) {
            int stepY=getStep(cy1, cy2);
            float partial=getPartial(cy1, cy2, y1);

            float dy = ((y2 >= y1) ? (y2 - y1) : (y1 - y2));
            float stepX = (x2 - x1) / dy;
            float x = x1 + (stepX * partial);

            cy1 += stepY;
            cy2 += stepY;

            if(!checkYLine(cy1, cy2, mask, x, stepX, stepY)) return false;
        }

        return true;
    }

    /**
     * Get actual damage value
     * @param maxHits Pure damage value
     * @param dist Distance to target
     * @return Damage done
     */
    @SuppressWarnings("MagicNumber")
    public static int getRealHits(int maxHits, float dist) {
        float div = Math.max(1.0f, dist * 0.35f);
        int minHits = Math.max(1, (int)((float)maxHits / div));

        //noinspection UnclearExpression
        return (random.nextInt(maxHits - minHits + 1) + minHits);
    }

    /**
     * Writes a Boolean Array to file
     * @param os Output stream
     * @param list List of boolean to write
     * @throws IOException Error while writing
     */
    public static void writeBooleanArray(ObjectOutput os, boolean[] list) throws IOException {
        os.writeInt(list.length);

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < list.length; i++) {
            os.writeBoolean(list[i]);
        }
    }

    /**
     * Writes an int Array to file
     * @param os Output stream
     * @param list List of int to write
     * @throws IOException Error while writing
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static void writeIntArray(ObjectOutput os, int[] list) throws IOException {
        os.writeInt(list.length);

        for (int i = 0; i < list.length; i++) {
            os.writeInt(list[i]);
        }
    }

    /**
     * Writes an Object Array to file
     * @param os Output stream
     * @param list List of Object to write
     * @throws IOException Error while writing
     */
    public static void writeObjectArray(ObjectOutput os, Object[] list, int size) throws IOException {
        os.writeInt(size);

        for (int i = 0; i < size; i++) {
            ((Externalizable)list[i]).writeExternal(os);
        }
    }

    /**
     * Writes an Int Matrix to file
     * @param os Output stream
     * @param map Matrix of int to write
     * @throws IOException Error while writing
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static void writeInt2dArray(ObjectOutput os, int[][] map) throws IOException {
        os.writeInt(map.length);
        os.writeInt(map[0].length);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                os.writeInt(map[i][j]);
            }
        }
    }

    /**
     * Reads Boolean array from file
     * @param is Input stream
     * @return Array of int
     * @throws IOException Error while reading
     */
    public static boolean[] readBooleanArray(ObjectInput is) throws IOException {
        int size = is.readInt();
        boolean[] list = new boolean[size];

        for (int i = 0; i < size; i++) {
            list[i] = is.readBoolean();
        }

        return list;
    }

    /**
     * Reads int array from file
     * @param is Input Stream
     * @return Array of int
     * @throws IOException Error while reading
     */
    public static int[] readIntArray(ObjectInput is) throws IOException {
        int size = is.readInt();
        int[] list = new int[size];

        for (int i = 0; i < size; i++) {
            list[i] = is.readInt();
        }

        return list;
    }

    /**
     * Reads int matrix from file
     * @param is input stream
     * @return Int Matrix
     * @throws IOException Error while reading
     */
    public static int[][] readInt2dArray(ObjectInput is) throws IOException {
        int h = is.readInt();
        int w = is.readInt();
        int[][] map = new int[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                map[i][j] = is.readInt();
            }
        }

        return map;
    }

    /**
     * Reads Object Array from file
     * @param is Input Stream
     * @param list Array of Object
     * @param theClass Class of the Object
     * @return Size of the array
     * @throws IOException Error while reading
     * @throws ClassNotFoundException Class not found
     */
    public static int readObjectArray(ObjectInput is, Object[] list, Class<?> theClass) throws
            IOException,
            ClassNotFoundException {

        int size = is.readInt();

        for (int i = 0; i < size; i++) {
            Externalizable instance;

            try {
                instance = (Externalizable)theClass.newInstance();
            } catch (Exception ex) {
                Log.e(GAME_NAME, "Exception", ex);
                throw new ClassNotFoundException("Couldn't create class instance");
            }

            instance.readExternal(is);
            list[i] = instance;
        }

        return size;
    }

    /**
     * Opens a localized resource
     * @param assetManager Asset Manager
     * @param pathTemplate Path to resource
     * @return Input Stream with localized resource
     * @throws IOException Error while reading
     */
    public static InputStream openLocalizedAsset(AssetManager assetManager, String pathTemplate) throws IOException {
        String path = String.format(Locale.US,
                pathTemplate,
                "-" + Locale.getDefault().getLanguage().toLowerCase(Locale.US));

        InputStream res;

        try {
            res = assetManager.open(path);
        } catch (IOException ex) {
            path = String.format(Locale.US, pathTemplate, "");
            res = assetManager.open(path);
        }

        return res;
    }

    /**
     * Sets the font
     * @param view View
     * @param viewIds View IDs
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static void setTypeface(View view, int[] viewIds) {
        Context context = view.getContext();

        Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                "fonts/" + context.getString(R.string.font_name));

        for (int i = 0; i < viewIds.length; i++) {
            View childView = view.findViewById(viewIds[i]);

            if (childView instanceof TextView) {
                ((TextView)childView).setTypeface(typeface);
            }
        }
    }

    /**
     * Sets the font
     * @param activity Activity
     * @param viewIds View IDs
     */
    @SuppressWarnings("unused")
    public static void setTypeface(Activity activity, int[] viewIds) {
        Typeface typeface = Typeface.createFromAsset(activity.getAssets(),
                "fonts/" + activity.getString(R.string.font_name));

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < viewIds.length; i++) {
            View childView = activity.findViewById(viewIds[i]);

            if (childView instanceof TextView) {
                ((TextView)childView).setTypeface(typeface);
            }
        }
    }

    /**
     * Opens Play Store
     * @param context App Context
     * @param packageName Package to find
     * @return True if page opened, false otherwise
     */
    @SuppressWarnings("unused")
    public static boolean openMarket(Context context, String packageName) {
        try {
            context.startActivity((new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id="
                            + packageName))).addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET));

            return true;
        } catch (Exception ex) {
            Log.e(GAME_NAME, "Exception", ex);
            Toast.makeText(ZameApplication.self, "Could not launch the market application.", Toast.LENGTH_LONG).show();
        }

        return false;
    }

    /**
     * Opens browser
     * @param context App Context
     * @param uri URL to Open
     * @return True if Browser opened, False otherwise
     */
    public static boolean openBrowser(Context context, String uri) {
        try {
            context.startActivity((new Intent(Intent.ACTION_VIEW,
                    Uri.parse(uri))).addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET));

            return true;
        } catch (Exception ex) {
            Log.e(GAME_NAME, "Exception", ex);
            Toast.makeText(ZameApplication.self, "Could not launch the browser application.", Toast.LENGTH_LONG).show();
        }

        return false;
    }

    /**
     * Copy file
     * @param srcFileName Source file name
     * @param destFileName Destination file name
     * @return true if successful, false otherwise
     */
    public static boolean copyFile(String srcFileName, String destFileName) {
        boolean success = true;
        InputStream in = null;
        OutputStream out = null;
        try {
             in = new FileInputStream(srcFileName);
             out = new FileOutputStream(destFileName);

            //noinspection MagicNumber
            byte[] buf = new byte[1024];
            int len=0;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (Exception ex) {
            Log.e(Common.GAME_NAME, "Exception", ex);
            success = false;
        }finally {

            try {
                if (in != null) {
                    in.close();
                }


                if (out != null) {
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!success) {
            Toast.makeText(ZameApplication.self, R.string.msg_cant_copy_state, Toast.LENGTH_LONG).show();
        }

        return success;
    }
}
