package zame.game;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.zeemote.zc.Controller;
import com.zeemote.zc.event.BatteryEvent;
import com.zeemote.zc.event.ButtonEvent;
import com.zeemote.zc.event.ControllerEvent;
import com.zeemote.zc.event.DisconnectEvent;
import com.zeemote.zc.event.IButtonListener;
import com.zeemote.zc.event.IJoystickListener;
import com.zeemote.zc.event.IStatusListener;
import com.zeemote.zc.event.JoystickEvent;
import com.zeemote.zc.ui.android.ControllerAndroidUi;
import zame.game.engine.Controls;

/**
 * Class Representing Game Activity Helper
 */
@SuppressWarnings("WeakerAccess")
public class GameActivityZeemoteHelper implements IStatusListener, IJoystickListener, IButtonListener {
    private Controller zeemoteController;
    private ControllerAndroidUi zeemoteControllerUi;
    @SuppressWarnings("BooleanVariableAlwaysNegated") private boolean keepConnection;

    /**
     * Get Menu Resource ID
     * @return
     */
    public int getMenuResId() {
        return R.menu.game_zeemote;
    }

    /**
     * When Option Menu gets prepared
     * @param menu Sets Zeemote controls as visible
     */
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_zeemote).setVisible(Config.controlsType == Controls.TYPE_ZEEMOTE);
    }

    /**
     * When an option is selected
     * @param item Item selected
     * @return True if Zeemote is set, false otherwise
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_zeemote:
                if (zeemoteControllerUi != null) {
                    zeemoteControllerUi.showControllerMenu();
                    keepConnection = true;
                }

                return true;
            default : break;
        }

        return false;
    }

    /**
     * When Activity Starts
     * @param activity Current Activity
     */
    public void onStart(GameActivity activity) {
        if (Config.controlsType != Controls.TYPE_ZEEMOTE) {
            return;
        }

        if (zeemoteController == null) {
            zeemoteController = new Controller(1);
            zeemoteController.addStatusListener(this);
            zeemoteController.addButtonListener(this);
            zeemoteController.addJoystickListener(this);
        }

        if (zeemoteControllerUi == null) {
            zeemoteControllerUi = new ControllerAndroidUi(activity, zeemoteController);
            keepConnection = false;
        }

        if (!keepConnection && !zeemoteController.isConnected()) {
            zeemoteControllerUi.startConnectionProcess();
            keepConnection = true;
        } else {
            keepConnection = false;
        }
    }

    /**
     * When Activity gets paused
     */
    public void onPause() {
        if (!keepConnection && (zeemoteController != null) && zeemoteController.isConnected()) {
            try {
                zeemoteController.disconnect();
            } catch (Exception ex) {
                Log.e(Common.LOG_KEY, "Exception", ex);
            }
        }
    }

    /**
     * Updates Zeemote Battery Icon
     * @param event Event
     */
    @Override
    public void batteryUpdate(BatteryEvent event) {
    }

    /**
     * When Zeemote is Connected
     * @param event Event
     */
    @Override
    public void connected(ControllerEvent event) {
        Controls.initJoystickVars();
    }

    /**
     * When Zeemote is Disconnected
     * @param event Event
     */
    @Override
    public void disconnected(DisconnectEvent event) {
        Controls.initJoystickVars();
    }

    /**
     * When Joystick is Moved
     * @param e Event
     */
    @SuppressWarnings("MagicNumber")
    @Override
    public void joystickMoved(JoystickEvent e) {
        if (Config.controlsType == Controls.TYPE_ZEEMOTE) {
            Controls.joyX = ((float)(e.getScaledX(-100, 100)) / 150.0f) * ConfigZeemote.zeemoteXAccel;
            Controls.joyY = (-(float)(e.getScaledY(-100, 100)) / 150.0f) * ConfigZeemote.zeemoteYAccel;
        }
    }

    /**
     * When Button is Pressed
     * @param e Event
     */
    @Override
    public void buttonPressed(ButtonEvent e) {
        if (Config.controlsType == Controls.TYPE_ZEEMOTE) {
            int buttonId = e.getButtonGameAction();

            if ((buttonId >= 0) && (buttonId < ConfigZeemote.zeemoteButtonMappings.length)) {
                Controls.joyButtonsMask |= ConfigZeemote.zeemoteButtonMappings[buttonId];
            }
        }
    }

    /**
     * When Button is released
     * @param e Event
     */
    @Override
    public void buttonReleased(ButtonEvent e) {
        if (Config.controlsType == Controls.TYPE_ZEEMOTE) {
            int buttonId = e.getButtonGameAction();

            if ((buttonId >= 0) && (buttonId < ConfigZeemote.zeemoteButtonMappings.length)) {
                Controls.joyButtonsMask &= ~(ConfigZeemote.zeemoteButtonMappings[buttonId]);
            }
        }
    }
}
