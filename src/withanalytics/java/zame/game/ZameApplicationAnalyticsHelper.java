package zame.game;

import android.os.Handler;
import android.util.Log;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class ZameApplicationAnalyticsHelper {
    /**
     * Event Handler
     */
    private final Handler handler = new Handler();

    /**
     * Event to Track
     */
    public static class EventToTrack {
        /**
         * Event Category
         */
        public String category;
        /**
         * Event Action
         */
        public String action;
        /**
         * Event Label
         */
        public String label;
        /**
         * Event value
         */
        public int value;

        /**
         * Class Constructor
         * @param category Event category
         * @param action Event Action
         * @param label Event Label
         * @param value Event Value
         */
        public EventToTrack(String category, String action, String label, int value) {
            this.category = category;
            this.action = action;
            this.label = label;
            this.value = value;
        }
    }

    /**
     * Google Analytics Tracker
     */
    private GoogleAnalyticsTracker tracker;
    /**
     * ArrayList of Events to track
     */
    private ArrayList<EventToTrack> eventsToTrack = new ArrayList<EventToTrack>();

    /**
     * Tracks Page views
     * @param pageUrl Page URL to track
     */
    public void trackPageView(final String pageUrl) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    tracker.trackPageView(pageUrl);
                } catch (Exception ex) {
                    Log.e(Common.GAME_NAME, "Exception", ex);
                }
            }
        });
    }

    /**
     * Tracks an event
     * @param category Event Category
     * @param action Event Action
     * @param label Event Label
     * @param value Event Value
     */
    public void trackEvent(final String category, final String action, final String label, final int value) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                eventsToTrack.add(new EventToTrack(category, action, label, value));
            }
        });
    }

    /**
     * Flushes all events
     */
    public void flushEvents() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (EventToTrack ev : eventsToTrack) {
                    try {
                        tracker.trackEvent(ev.category, ev.action, ev.label, ev.value);
                    } catch (Exception ex) {
                        Log.e(Common.GAME_NAME, "Exception", ex);
                    }
                }

                eventsToTrack.clear();
            }
        });
    }

    /**
     * When Activity is created
     * @param app This App
     * @param initialControlsType Initial Control Scheme
     */
    public void onCreate(ZameApplication app, String initialControlsType) {
        try {
            tracker = GoogleAnalyticsTracker.getInstance();
            tracker.startNewSession(BuildConfig.GA_ACCT, 10, app);
            tracker.setDebug(true);
            tracker.setDryRun(false);
            tracker.setSampleRate(100);
            tracker.setAnonymizeIp(true);
            tracker.setCustomVar(1, "Version", app.getVersionName(), 2); // slot: 1, scope: session
            tracker.setCustomVar(2, "InitialControlsType", initialControlsType, 2); // slot: 2, scope: session
        } catch (Exception ex) {
            Log.e(Common.GAME_NAME, "Exception", ex);
            tracker = null;
        }
    }
}
