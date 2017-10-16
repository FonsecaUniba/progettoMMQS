package zame.game;

import zame.game.views.MenuViewHelper;

/**
 * Class Representing the Menu Activity Helper
 */
@SuppressWarnings("WeakerAccess")
public final class MenuActivityHelper {
    /**
     * Class Constructor
     */
    private MenuActivityHelper() {
    }

    /**
     * When Back Button is Pressed
     * @param activity
     * @return Show Rate Offer
     */
    public static boolean onBackPressed(MenuActivity activity) {
        return (!MenuViewHelper.showRateOffer(activity));
    }
}
