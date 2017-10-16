package zame.game;

import android.view.Menu;
import android.view.MenuItem;

/**
 * Game Activity Zeemote Helper
 */
@SuppressWarnings("WeakerAccess")
public class GameActivityZeemoteHelper {
    /**
     * Get Menu Resource ID
     * @return 0
     */
    public int getMenuResId() {
        return 0;
    }

    /**
     * When Option menu is show
     * @param menu Menu to show
     */
    public void onPrepareOptionsMenu(@SuppressWarnings("UnusedParameters") Menu menu) {
    }

    /**
     * When option is selected
     * @param item Item selected
     * @return false
     */
    public boolean onOptionsItemSelected(@SuppressWarnings("UnusedParameters") MenuItem item) {
        return false;
    }

    /**
     * When Activity is started
     * @param activity Activity to show
     */
    public void onStart(@SuppressWarnings("UnusedParameters") GameActivity activity) {
    }

    /**
     * When Activity is Paused
     */
    public void onPause() {
    }
}
