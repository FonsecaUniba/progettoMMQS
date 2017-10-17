package zame.game;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Class representing the App
 */
public class ZameApplication extends Application {
    /**
     * This App
     */
    public static ZameApplication self = new ZameApplication();

    /**
     * Current App Version
     */
    private String cachedVersionName;

    /**
     * Helper to load Google Analytics
     */
    @SuppressWarnings("ConstantConditions")
    private ZameApplicationAnalyticsHelper analyticsHelper = (BuildConfig.WITH_ANALYTICS
            ? new ZameApplicationAnalyticsHelper()
            : null);

    /**
     * Tracks the number of times a page has been viewed
     * @param pageUrl Page to count
     */
    public static void trackPageView(String pageUrl) {
        if ((ZameApplication.self != null) && (ZameApplication.self.analyticsHelper != null)) {
            ZameApplication.self.analyticsHelper.trackPageView(pageUrl);
        }
    }

    /**
     * Tracks all the event
     * @param category Category event
     * @param action Action
     * @param label Action Label
     * @param value Action Value
     */
    public static void trackEvent(String category, String action, String label, int value) {
        if ((ZameApplication.self != null) && (ZameApplication.self.analyticsHelper != null)) {
            ZameApplication.self.analyticsHelper.trackEvent(category, action, label, value);
        }
    }

    /**
     * Flushes all Events
     */
    public static void flushEvents() {
        if ((ZameApplication.self != null) && (ZameApplication.self.analyticsHelper != null)) {
            ZameApplication.self.analyticsHelper.flushEvents();
        }
    }

    /**
     * When App is created
     */
    @Override
    public void onCreate() {
        super.onCreate();
        self = this;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
        String initialControlsType = sp.getString("InitialControlsType", "");

        //noinspection SizeReplaceableByIsEmpty
        if (initialControlsType.length() == 0) {
            Common.init();
            initialControlsType = "Improved";

            SharedPreferences.Editor spEditor = sp.edit();
            spEditor.putString("InitialControlsType", initialControlsType);
            spEditor.putString("ControlsType", initialControlsType);
            spEditor.putString("PrevControlsType", initialControlsType);
            spEditor.commit();
        }

        if (analyticsHelper != null) {
            analyticsHelper.onCreate(this, initialControlsType);
        }
    }

    /**
     * Returns the App Version
     * @return String containing App Version
     */
    public String getVersionName() {
        if (cachedVersionName == null) {
            cachedVersionName = "xxxx.xx.xx.xxxx";

            try {
                cachedVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (Exception ex) {
                Log.e(Common.GAME_NAME, "Exception", ex);
            }
        }

        return cachedVersionName;
    }
}
