package zame.game.engine;

import android.graphics.Paint;
import android.graphics.Typeface;
import java.util.Locale;
import javax.microedition.khronos.opengles.GL10;
import zame.game.Config;
import zame.game.R;
import zame.game.ZameApplication;
import zame.libs.LabelMaker;
import zame.libs.NumericSprite;

/**
 * Class representing Labels
 */
@SuppressWarnings("WeakerAccess")
public final class Labels {
    /**
     * Constant for FPS Label
     */
    public static final int LABEL_FPS = 1;
    /**
     * Constant for Can't Open Label
     */
    public static final int LABEL_CANT_OPEN = 2;
    /**
     * Constant for Blue Key required label
     */
    public static final int LABEL_NEED_BLUE_KEY = 3;
    /**
     * Constant for Red Key Required Label
     */
    public static final int LABEL_NEED_RED_KEY = 4;
    /**
     * Constant for Green Key Required Label
     */
    public static final int LABEL_NEED_GREEN_KEY = 5;
    /**
     * Constant for Secrets Found label
     */
    public static final int LABEL_SECRET_FOUND = 6;
    /**
     * Constant for Last weapon label
     */
    public static final int LABEL_LAST = 7;

    /**
     * Constant for Press Forward label
     */
    @SuppressWarnings("unused") public static final int MSG_PRESS_FORWARD = 1;
    /**
     * Constant for Press rotate label
     */
    public static final int MSG_PRESS_ROTATE = 2;
    /**
     * Constant for Press Action to open door label
     */
    @SuppressWarnings("unused") public static final int MSG_PRESS_ACTION_TO_OPEN_DOOR = 3;
    /**
     * Constant for switch at right label
     */
    @SuppressWarnings("unused") public static final int MSG_SWITCH_AT_RIGHT = 4;
    /**
     * Constant for Press action to switch label
     */
    @SuppressWarnings("unused") public static final int MSG_PRESS_ACTION_TO_SWITCH = 5;
    /**
     * Constant for Key at left label
     */
    @SuppressWarnings("unused") public static final int MSG_KEY_AT_LEFT = 6;
    /**
     * Constant for Press Action to fight label
     */
    @SuppressWarnings("unused") public static final int MSG_PRESS_ACTION_TO_FIGHT = 7;
    /**
     * Constant for Press Map Label
     */
    @SuppressWarnings("unused") public static final int MSG_PRESS_MAP = 8;
    /**
     * Constant for Press Next Weapon label
     */
    @SuppressWarnings("unused") public static final int MSG_PRESS_NEXT_WEAPON = 9;
    /**
     * Constant for Open Door with key label
     */
    @SuppressWarnings("unused") public static final int MSG_OPEN_DOOR_USING_KEY = 10;
    /**
     * Constant for End Level Switch label
     */
    @SuppressWarnings("unused") public static final int MSG_PRESS_END_LEVEL_SWITCH = 11;
    /**
     * Constant for Go To Door Label
     */
    @SuppressWarnings("unused") public static final int MSG_GO_TO_DOOR = 12;
    /**
     * Constant for Last Message Label
     */
    public static final int MSG_LAST = 13;

    /**
     * Label Map
     */
    public static int[] map = new int[LABEL_LAST];

    /**
     * Message Map
     */
    public static final int[] MSG_MAP = { 0, R.string.lblm_press_forward, // MSG_PRESS_FORWARD
            R.string.lblm_press_rotate, // MSG_PRESS_ROTATE
            R.string.lblm_press_action_to_open_door, // MSG_PRESS_ACTION_TO_OPEN_DOOR
            R.string.lblm_switch_at_right, // MSG_SWITCH_AT_RIGHT
            R.string.lblm_press_action_to_switch, // MSG_PRESS_ACTION_TO_SWITCH
            R.string.lblm_key_at_left, // MSG_KEY_AT_LEFT
            R.string.lblm_press_action_to_fight, // MSG_PRESS_ACTION_TO_FIGHT
            R.string.lblm_press_map, // MSG_PRESS_MAP
            R.string.lblm_press_next_weapon, // MSG_PRESS_NEXT_WEAPON
            R.string.lblm_open_door_using_key, // MSG_OPEN_DOOR_USING_KEY
            R.string.lblm_press_end_level_switch, // MSG_PRESS_END_LEVEL_SWITCH
            R.string.lblm_go_to_door, // MSG_GO_TO_DOOR
    };

    /**
     * Label Maker
     */
    public static volatile LabelMaker maker = new LabelMaker(true,1000,1000);
    /**
     * Message Maker
     */
    public static volatile LabelMaker msgMaker = new LabelMaker(true,1000,1000);
    //INIZIALIZZAZIONE DI QUESTE VARIABILI PERICOLOSA PER L'ESECUZIONE DEL PROGRAMMA
    /**
     * Number of sprites
     */
    public static volatile NumericSprite numeric = null; //inizializzazione = black screen dopo new game
    /**
     * Stats Sprite
     */
    public static volatile NumericSprite statsNumeric = null;
    //FINE VARIABILI PERICOLOSE
    /**
     * Label Paint
     */
    private static Paint labelPaint  = new Paint();
    /**
     * Message Paint
     */
    private static Paint msgPaint = new Paint();
    /**
     * Stats Paint
     */
    private static Paint statsPaint = new Paint();

    /**
     * Current Message ID
     */
    private static int currentMessageId=0;
    /**
     * Current Label ID
     */
    private static int currentMessageLabelId=0;
    /**
     * Current Message Value
     */
    private static String currentMessageString ="";

    /**
     * Class constructor
     */
    private Labels() {
    }

    /**
     * Initializes Labels
     */
    @SuppressWarnings("MagicNumber")
    public static void init() {
        Typeface labelTypeface = Typeface.createFromAsset(Game.assetManager,
                "fonts/" + ZameApplication.self.getString(R.string.font_name));

        labelPaint = new Paint();
        labelPaint.setTypeface(labelTypeface);
        labelPaint.setAntiAlias(true);
        labelPaint.setARGB(0xFF, 0xFF, 0xFF, 0xFF);

        msgPaint = new Paint();
        msgPaint.setTypeface(labelTypeface);
        msgPaint.setAntiAlias(true);
        msgPaint.setARGB(0xFF, 0xFF, 0xFF, 0xFF);

        statsPaint = new Paint();
        statsPaint.setTypeface(labelTypeface);
        statsPaint.setAntiAlias(true);
        statsPaint.setARGB(0xFF, 0xFF, 0xFF, 0xFF);
    }

    /**
     * Returns int value of resource
     * @param resId Resource ID
     * @return Int value of resource
     */
    private static int getInt(int resId) {
        return Integer.parseInt(ZameApplication.self.getString(resId));
    }

    /**
     * When Surface Size Changes
     * @param width New Width
     */
    @SuppressWarnings("MagicNumber")
    public static void surfaceSizeChanged(int width) {
        labelPaint.setTextSize(getInt((width < 480)
                ? R.string.font_lbl_size_sm
                : ((width < 800) ? R.string.font_lbl_size_md : R.string.font_lbl_size_lg)));

        msgPaint.setTextSize(getInt((width < 480)
                ? R.string.font_msg_size_sm
                : ((width < 800) ? R.string.font_msg_size_md : R.string.font_msg_size_lg)));

        statsPaint.setTextSize(getInt((width < 480)
                ? R.string.font_stats_size_sm
                : ((width < 800) ? R.string.font_stats_size_md : R.string.font_stats_size_lg)));
    }

    /**
     * Get Message Label ID
     * @param gl the GL interface. Use <code>instanceof</code> to
     * @param messageId Message ID
     * @return Message ID
     */
    public static int getMessageLabelId(GL10 gl, int messageId) {
        if (currentMessageId == messageId) {
            return currentMessageLabelId;
        }

        String message="";

        if (((Config.controlsType == Controls.TYPE_EXPERIMENTAL_A) || (Config.controlsType
                == Controls.TYPE_EXPERIMENTAL_B)) && (messageId == MSG_PRESS_ROTATE)) {

            message = ZameApplication.self.getString(R.string.lblm_slide_rotate);
        } else if ((messageId > 0) && (messageId < MSG_LAST)) {
            message = ZameApplication.self.getString(MSG_MAP[messageId]);
        } else {
            message = String.format(Locale.US, "[message #%d]", messageId);
        }

        msgMaker.beginAdding(gl);
        currentMessageLabelId = msgMaker.add(gl, message, msgPaint);
        msgMaker.endAdding(gl);

        currentMessageId = messageId;
        currentMessageString = "";

        return currentMessageLabelId;
    }

    /**
     * Get Message ID From String
     * @param gl the GL interface. Use <code>instanceof</code> to
     * @param message Message to identify
     * @return Message ID
     */
    @SuppressWarnings("unused")
    public static int getMessageLabelIdForString(GL10 gl, String message) {
        if (currentMessageString.equals(message)) {
            return currentMessageLabelId;
        }

        msgMaker.beginAdding(gl);
        currentMessageLabelId = msgMaker.add(gl, message, labelPaint);
        msgMaker.endAdding(gl);

        currentMessageId = 0;
        currentMessageString = message;

        return currentMessageLabelId;
    }

    /**
     * Create Labels
     * @param gl the GL interface. Use <code>instanceof</code> to
     */
    @SuppressWarnings("MagicNumber")
    public static void createLabels(GL10 gl) {
        if (maker == null) {
            maker = new LabelMaker(true, 512, 256);
        } else {
            maker.shutdown(gl);
        }

        maker.initialize(gl);
        maker.beginAdding(gl);
        map[LABEL_FPS] = maker.add(gl, ZameApplication.self.getString(R.string.lbl_fps), labelPaint);
        map[LABEL_CANT_OPEN] = maker.add(gl, ZameApplication.self.getString(R.string.lbl_cant_open_door), labelPaint);

        map[LABEL_NEED_BLUE_KEY] = maker.add(gl,
                ZameApplication.self.getString(R.string.lbl_need_blue_key),
                labelPaint);

        map[LABEL_NEED_RED_KEY] = maker.add(gl, ZameApplication.self.getString(R.string.lbl_need_red_key), labelPaint);

        map[LABEL_NEED_GREEN_KEY] = maker.add(gl,
                ZameApplication.self.getString(R.string.lbl_need_green_key),
                labelPaint);

        map[LABEL_SECRET_FOUND] = maker.add(gl, ZameApplication.self.getString(R.string.lbl_secret_found), labelPaint);
        maker.endAdding(gl);

        if (msgMaker == null) {
            msgMaker = new LabelMaker(true, 1024, 64);
        } else {
            msgMaker.shutdown(gl);
        }

        msgMaker.initialize(gl);
        currentMessageId = 0;
        currentMessageString = "";

        if (numeric == null) {
            numeric = new NumericSprite();
        } else {
            numeric.shutdown(gl);
        }

        numeric.initialize(gl, labelPaint);

        if (statsNumeric == null) {
            statsNumeric = new NumericSprite();
        } else {
            statsNumeric.shutdown(gl);
        }

        statsNumeric.initialize(gl, statsPaint);
    }
}
