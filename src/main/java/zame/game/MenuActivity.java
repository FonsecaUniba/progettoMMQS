package zame.game;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import zame.game.engine.Game;
import zame.game.views.MenuView;

/**
 * Class representing Menu Activity
 */
public class MenuActivity extends Activity {
    /**
     * Did the game just load?
     */
    public static boolean justLoaded=true; // or just saved, or just new game started
    /**
     * This activity
     */
    public static MenuActivity self = new MenuActivity();

    /**
     * Just Unpaused?
     */
    @SuppressWarnings("BooleanVariableAlwaysNegated") private boolean justAfterPause;
    /**
     * Did the sound already stop?
     */
    @SuppressWarnings("BooleanVariableAlwaysNegated") private boolean soundAlreadyStopped; // fix multi-activity issues

    /**
     * Do we pause music?
     */
    public boolean instantMusicPause = true;
    /**
     * Data to show
     */
    public MenuView.Data menuViewData = new MenuView.Data();

    /**
     * When Activity is created
     * @param state State
     */
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        self = this;

        SoundManager.init(getApplicationContext(), getAssets(), true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Game.initPaths(getApplicationContext());
        setContentView(R.layout.menu);

        MenuView.onActivityCreate(this);
        ZameApplication.trackPageView("/menu");
    }

    /**
     * When Focus Changes
     * @param hasFocus Does Activity have focus?
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            // moved from onResume, because onResume called even when app is not visible, but lock screen is visible
            if (!justAfterPause) {
                SoundManager.setPlaylist(SoundManager.LIST_MAIN);
                SoundManager.onStart();
                soundAlreadyStopped = false;
            }
        } else {
            // moved from onPause, because onPause is not called when task manager is on screen
            if (!soundAlreadyStopped) {
                SoundManager.onPause(instantMusicPause);
                soundAlreadyStopped = true;
            }

            instantMusicPause = true;
        }
    }

    /**
     * When Activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        justAfterPause = false;
    }

    /**
     * When Activity is paused
     */
    @Override
    public void onPause() {
        justAfterPause = true;

        if (!soundAlreadyStopped) {
            SoundManager.onPause(instantMusicPause);
            soundAlreadyStopped = true;
        }

        instantMusicPause = true;
        super.onPause();

    }

    /**
     * When Activity is destroyed
     */
    @Override
    protected void onDestroy() {
        self = null;
        super.onDestroy();

    }

    /**
     * When Back is pressed
     */
    @Override
    public void onBackPressed() {
        //noinspection ConstantConditions
        if (MenuActivityHelper.onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    /**
     * When Options Menu is created
     * @param menu Menu to show
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * When Option Item is selected
     * @param item Item selected
     * @return True or MenuView.onOptionsItemSelected(this, item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_options:
                startActivity(new Intent(this, GamePreferencesActivity.class));
                return true;
            default : break;
        }


        return MenuView.onOptionsItemSelected(this, item);
    }

    /**
     * When Dialog is created
     * @param id Dialog ID
     * @return Dialog or null
     */
    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        return MenuView.onCreateDialog(this, id);
    }
}
