package zame.game;

/**
 * Class representing Menu Activity
 */
@SuppressWarnings("WeakerAccess")
public final class MenuActivityHelper {
    /**
     * Class Constructor
     */
    private MenuActivityHelper() {
    }

    /**
     * When Back Menu is pressed
     * @param activity Activity to show
     * @return true
     */
    public static boolean onBackPressed(@SuppressWarnings("UnusedParameters") MenuActivity activity) {
        return true;
    }
}
