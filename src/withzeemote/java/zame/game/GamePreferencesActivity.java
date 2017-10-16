package zame.game;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Class representing Game Preferences Activity
 */
public class GamePreferencesActivity extends PreferenceActivity {
    /**
     * Creates Activity
     * @param savedInstanceState Save to restore
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_zeemote);
    }
}
