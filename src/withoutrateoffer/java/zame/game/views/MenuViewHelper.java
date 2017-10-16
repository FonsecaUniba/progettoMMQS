package zame.game.views;

import android.app.Dialog;
import zame.game.MenuActivity;

/**
 * Class representing Menu View
 */
public final class MenuViewHelper {
    /**
     * Class constructor
     */
    private MenuViewHelper() {
    }

    /**
     * Can View be exited?
     * @param activity Activity to show
     * @return true
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean canExit(@SuppressWarnings("UnusedParameters") MenuActivity activity) {
        return true;
    }

    /**
     * When Dialog is created
     * @param activity Activity to show
     * @param data Data to show
     * @param id ID of the Dialog
     * @return null
     */
    @SuppressWarnings({ "WeakerAccess", "UnusedParameters" })
    public static Dialog onCreateDialog(final MenuActivity activity, final MenuView.Data data, int id) {
        return null;
    }
}
