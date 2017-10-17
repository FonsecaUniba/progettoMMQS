package zame.game.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import zame.game.Common;
import zame.game.GameActivity;
import zame.game.R;
import zame.game.SoundManager;
import zame.game.engine.Game;

/**
 * Class representing End Level View
 */
public class EndLevelView extends zame.libs.FrameLayout implements IZameView {
    /**
     * Game Activity
     */
    private GameActivity activity;
    /**
     * Event Handler
     */
    private final Handler handler = new Handler();
    /**
     * Text showing Kills
     */
    private TextView txtKills;
    /**
     * Text showing Items
     */
    private TextView txtItems;
    /**
     * Text showing Secrets
     */
    private TextView txtSecrets;
    /**
     * Current Number of Kills
     */
    private float currentKills;
    /**
     * Current Number of Items
     */
    private float currentItems;
    /**
     * Current Number of Secrets
     */
    private float currentSecrets;
    /**
     * Current Number of Add
     */
    private float currentAdd;
    /**
     * Do we need to increase Task Values?
     */
    private boolean increaseValuesTaskActive;
    /**
     * Timed Task for Increasing Values
     */
    private TimerTask increaseValuesTask;

    /**
     * Updates the values
     */
    private final Runnable updateValues = new Runnable() {
        @SuppressWarnings("MagicNumber")
        @Override
        public void run() {
            boolean shouldCancel = true;

            currentKills += currentAdd;
            currentItems += currentAdd;
            currentSecrets += currentAdd;

            if (currentKills >= Game.endlTotalKills) {
                currentKills = (float)Game.endlTotalKills;
            } else {
                shouldCancel = false;
            }

            if (currentItems >= Game.endlTotalItems) {
                currentItems = (float)Game.endlTotalItems;
            } else {
                shouldCancel = false;
            }

            if (currentSecrets >= Game.endlTotalSecrets) {
                currentSecrets = (float)Game.endlTotalSecrets;
            } else {
                shouldCancel = false;
            }

            currentAdd += 0.2f;
            updateTxtValues();

            if (shouldCancel) {
                if (increaseValuesTaskActive) {
                    increaseValuesTaskActive = false;
                    increaseValuesTask.cancel();
                }
            } else {
                SoundManager.playSound(SoundManager.SOUND_SHOOT_PIST);
            }
        }
    };

    /**
     * Updates End Level Text
     */
    private void updateTxtValues() {
        txtKills.setText(String.format(activity.getString(R.string.endl_kills), floatToInt(currentKills)));
        txtItems.setText(String.format(activity.getString(R.string.endl_items), floatToInt(currentItems)));
        txtSecrets.setText(String.format(activity.getString(R.string.endl_secrets), floatToInt(currentSecrets)));
    }

    /**
     * Class Constructor
     * @param context App Context
     * @param attrs App Attributes
     */
    public EndLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (GameActivity)context;
    }

    /**
     * When View finishes inflating
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Common.setTypeface(this, new int[] { R.id.TxtKills, R.id.TxtItems, R.id.TxtSecrets, R.id.BtnNextLevel, });

        txtKills = (TextView)findViewById(R.id.TxtKills);
        txtItems = (TextView)findViewById(R.id.TxtItems);
        txtSecrets = (TextView)findViewById(R.id.TxtSecrets);

        findViewById(R.id.BtnNextLevel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.playSound(SoundManager.SOUND_BTN_PRESS);

                currentKills = Game.endlTotalKills;
                currentItems = Game.endlTotalItems;
                updateTxtValues();

                SoundManager.setPlaylist(SoundManager.LIST_MAIN);
                GameActivity.changeView(R.layout.pre_level);
            }
        });

        currentKills = 0.0f;
        currentItems = 0.0f;
        currentSecrets = 0.0f;
        currentAdd = 1.0f;

        updateTxtValues();
        startTask();
    }

    /**
     * Casts Float value to Int
     * @param a value to cast
     * @return Int value of a
     */
    private static int floatToInt(float a) {
        if (a < Integer.MIN_VALUE || a > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Value not castable");
        }
        return Math.round(a-0.5f);
    }

    /**
     * Starts Timed Task
     */
    private void startTask() {
        if (!increaseValuesTaskActive) {
            increaseValuesTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(updateValues);
                }
            };

            Timer increaseValuesTimer = new Timer();
            increaseValuesTaskActive = true;
            increaseValuesTimer.schedule(increaseValuesTask, 100, 100);
        }
    }

    /**
     * Stops Timed Task
     */
    private void stopTask() {
        if (increaseValuesTaskActive) {
            increaseValuesTaskActive = false;
            increaseValuesTask.cancel();
        }
    }

    /**
     * When Focus Changes
     * @param hasFocus Does View have focus?
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            startTask();
        } else {
            stopTask();
        }
    }

    /**
     * When View is resumed
     */
    @Override
    public void onResume() {
    }

    /**
     * When View is paused
     */
    @Override
    public void onPause() {
        stopTask();
    }
}
