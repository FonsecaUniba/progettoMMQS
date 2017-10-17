package zame.game;

/**
 * Class representing Zame Application Analytics Helper
 */
@SuppressWarnings("WeakerAccess")
public class ZameApplicationAnalyticsHelper {
    /**
     * Tracks Page Views
     * @param pageUrl URL to track
     */
    public void trackPageView(@SuppressWarnings("UnusedParameters") final String pageUrl) {
    }

    /**
     * Tracks an event
     * @param category Event Category
     * @param action Event Action
     * @param label Event Label
     * @param value Event value
     */
    @SuppressWarnings("UnusedParameters")
    public void trackEvent(final String category, final String action, final String label, final int value) {
    }

    /**
     * Flushes all events
     */
    public void flushEvents() {
    }

    /**
     * When Activity is created
     * @param app This App
     * @param initialControlsType Initial Control Scheme
     */
    @SuppressWarnings("UnusedParameters")
    public void onCreate(ZameApplication app, String initialControlsType) {
    }
}
