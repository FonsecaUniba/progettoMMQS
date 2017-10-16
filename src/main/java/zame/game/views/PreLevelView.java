package zame.game.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.logging.Level;

import zame.game.Common;
import zame.game.GameActivity;
import zame.game.R;
import zame.game.SoundManager;
import zame.game.engine.State;

/**
 * Class representing the Pre Level View
 */
public class PreLevelView extends zame.libs.FrameLayout implements IZameView {
    /**
     * Activity to load
     */
    private GameActivity activity;
    /**
     * Event Handler
     */
    private final Handler handler = new Handler();
    /**
     * Scrollable View
     */
    private ScrollView scrollWrap;
    /**
     * TextView
     */
    private TextView txtText;
    /**
     * Text to show
     */
    private String preLevelText;
    /**
     * Current View Length
     */
    private int currentLength;
    /**
     * Current View Height
     */
    private int currentHeight;
    /**
     * Do we show more info?
     */
    private boolean showMoreTextTaskActive;
    /**
     * Timed Task
     */
    private TimerTask showMoreTextTask;

    /**
     * Sets image on view
     * @param imgId Image resource ID
     */
    private void setImage(String imgId){
        ImageView imgImage = (ImageView)findViewById(R.id.ImgImage);
        imgImage.setVisibility(View.VISIBLE);

        if ("ep_1".equals(imgId)) {
            imgImage.setImageResource(R.drawable.pre_ep_1);
        } else if ("ep_2".equals(imgId)) {
            imgImage.setImageResource(R.drawable.pre_ep_2);
        } else if ("ep_3".equals(imgId)) {
            imgImage.setImageResource(R.drawable.pre_ep_3);
        } else if ("ep_4".equals(imgId)) {
            imgImage.setImageResource(R.drawable.pre_ep_4);
        } else if ("ep_5".equals(imgId)) {
            imgImage.setImageResource(R.drawable.pre_ep_5);
        } else {
            imgImage.setVisibility(View.GONE);
        }
    }

    /**
     * Updates the text
     */
    private final Runnable updateText = new Runnable() {
        @Override
        public void run() {
            currentLength += 3;

            if (currentLength >= preLevelText.length()) {
                currentLength = preLevelText.length();

                if (showMoreTextTaskActive) {
                    showMoreTextTaskActive = false;
                    showMoreTextTask.cancel();
                }
            }

            updateTextValue(!showMoreTextTaskActive);
        }
    };

    /**
     * Updates text value
     * @param ensureScroll Is Scroll active?
     */
    private void updateTextValue(boolean ensureScroll) {
        txtText.setText(preLevelText.substring(0, currentLength));
        int newHeight = txtText.getHeight();

        if ((newHeight > currentHeight) || ensureScroll) {
            currentHeight = newHeight;

            scrollWrap.post(new Runnable() {
                @Override
                public void run() {
                    scrollWrap.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    /**
     * Class constructor
     * @param context App Context
     * @param attrs Attribute Sets
     */
    public PreLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (GameActivity)context;
    }

    /**
     * When View finishes inflating
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Common.setTypeface(this, new int[] { R.id.TxtText, R.id.BtnStartLevel, });

        scrollWrap = (ScrollView)findViewById(R.id.ScrollWrap);
        txtText = (TextView)findViewById(R.id.TxtText);

        String imgId="";

        try {
            InputStreamReader isr = new InputStreamReader(Common.openLocalizedAsset(activity.getAssets(),
                    String.format(Locale.US, "prelevel%%s/level-%d.txt", State.levelNum)), "UTF-8");

            BufferedReader br = new BufferedReader(isr);

            imgId = br.readLine();
            StringBuilder sb = new StringBuilder();
            boolean appendNewline = false;

            while(true) {
                String line = br.readLine();

                if (line == null) {
                    break;
                }

                if (appendNewline) {
                    sb.append("\n");
                }

                sb.append(line);
                appendNewline = true;
            }

            preLevelText = sb.toString();
            br.close();
        } catch (IOException ex) {
            //throw new RuntimeException(ex);
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error reading file", ex);
        }

        setImage(imgId);

        findViewById(R.id.BtnStartLevel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.playSound(SoundManager.SOUND_BTN_PRESS);

                currentLength = preLevelText.length();
                updateTextValue(true);

                GameActivity.changeView(R.layout.game);
            }
        });
    }

    /**
     * When View is Resumed
     */
    @Override
    public void onResume() {
        currentHeight = 0;
        currentLength = 1;
        updateTextValue(false);

        // there is also handler.postDelayed and handler.postAtTime (don't forget about handler.removeCallbacks in such case)

        showMoreTextTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(updateText);
            }
        };

        Timer showMoreTextTimer = new Timer();
        showMoreTextTaskActive = true;

        //noinspection MagicNumber
        showMoreTextTimer.schedule(showMoreTextTask, 30, 30);
    }

    /**
     * When View is Paused
     */
    @Override
    public void onPause() {
        if (showMoreTextTaskActive) {
            showMoreTextTaskActive = false;
            showMoreTextTask.cancel();
        }
    }
}
