package zame.game;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Class representing Game Preferences
 */
public class GamePreferencesActivity extends PreferenceActivity {
    /**
     * When Activity is Created
     * @param savedInstanceState Data to save
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
