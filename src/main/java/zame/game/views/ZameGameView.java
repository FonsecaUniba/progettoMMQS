package zame.game.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import zame.game.ZameGame;

/**
 * Class representing Zame Game View
 */
public class ZameGameView extends zame.libs.GLSurfaceView21 {
    /**
     * The Game
     */
    private ZameGame game;

    /**
     * Class Constructor
     * @param context Context of the App
     * @param attrs Set of Attributes
     */
    public ZameGameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true);
        requestFocus();
        setFocusableInTouchMode(true);
    }

    /**
     * Sets the game
     * @param game The game
     */
    public void setGame(ZameGame game) {
        this.game = game;
        setRenderer(game);
    }

    /**
     * When Window focus changes
     * @param hasWindowFocus Does the Window have focus?
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (game != null) {
            if (hasWindowFocus) {
                game.resume();
            } else {
                game.pause();
            }
        }
    }

    /**
     * Is the Key selected valid?
     * @param keyCode Code of Key pressed
     * @return true if valid, false otherwise
     */
    public static boolean canUseKey(int keyCode) {
        return ((keyCode != KeyEvent.KEYCODE_BACK) && (keyCode != KeyEvent.KEYCODE_HOME) && (keyCode
                != KeyEvent.KEYCODE_MENU) && (keyCode != KeyEvent.KEYCODE_ENDCALL));
    }

    /**
     * When Key is pressed
     * @param keyCode Key pressed
     * @param event Event
     * @return True if valid, super.onKeyDown(keyCode, event)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //noinspection SimplifiableIfStatement
        if (canUseKey(keyCode) && (game != null) && game.handleKeyDown(keyCode)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * When Key is released
     * @param keyCode Key released
     * @param event Event
     * @return True if valid, super.onKeyUp(keyCode, event)
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //noinspection SimplifiableIfStatement
        if (canUseKey(keyCode) && (game != null) && game.handleKeyUp(keyCode)) {
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * When Touch Event happens
     * @param event Event
     * @return True
     */
    @SuppressWarnings("MagicNumber")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (game != null) {
            game.handleTouchEvent(event);
        }

        try {
            Thread.sleep(16);
        } catch (InterruptedException e) {
            System.err.println();
        }

        return true;
    }

    /**
     * When Trackball Event Happens
     * @param event Event
     * @return true
     */
    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (game != null) {
            game.handleTrackballEvent(event);
        }

        return true;
    }
}
