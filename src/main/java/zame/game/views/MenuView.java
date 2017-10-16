package zame.game.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zame.game.Common;
import zame.game.GameActivity;
import zame.game.MenuActivity;
import zame.game.R;
import zame.game.SoundManager;
import zame.game.ZameApplication;
import zame.game.engine.Game;

/**
 * Class representing the MenuView
 */
public class MenuView extends RelativeLayout {
    /**
     * Class representing the data
     */
    public static class Data {
        /**
         * Current Data index
         */
        @SuppressWarnings("WeakerAccess") public int currentIndex;
        /**
         * Slots Strings to Load
         */
        @SuppressWarnings("WeakerAccess") public ArrayList<String> slotStringsForLoad;
        /**
         * Slots File Names to Load
         */
        @SuppressWarnings("WeakerAccess") public ArrayList<String> slotFileNamesForLoad;
        /**
         * Slots String to Save
         */
        @SuppressWarnings("WeakerAccess") public ArrayList<String> slotStringsForSave;
        /**
         * SLots File Name to Save
         */
        @SuppressWarnings("WeakerAccess") public ArrayList<String> slotFileNamesForSave;

        /**
         * Adapter for Loading
         */
        @SuppressWarnings("WeakerAccess") public ArrayAdapter<String> loadSlotsAdapter;
        /**
         * Adapter for Saving
         */
        @SuppressWarnings("WeakerAccess") public ArrayAdapter<String> saveSlotsAdapter;

        /**
         * Dialog to show Alerts
         */
        @SuppressWarnings("WeakerAccess") public AlertDialog aboutDialog;
        /**
         * Dialog to show save slots
         */
        @SuppressWarnings("WeakerAccess") public AlertDialog saveSlotsDialog;
    }

    /**
     * Constant for New Game Warning
     */
    private static final int DIALOG_NEW_GAME_WARN = 101;
    /**
     * Constant for Load Game Warning
     */
    private static final int DIALOG_LOAD_WARN = 102;
    /**
     * Constant for Load Slots
     */
    private static final int DIALOG_LOAD_SLOTS = 103;
    /**
     * Constant for Save Slots
     */
    private static final int DIALOG_SAVE_SLOTS = 104;
    /**
     * Constant for About
     */
    private static final int DIALOG_ABOUT = 105;

    // actually 4 slots is enough, but this is required to fix issued with bad-named saves folder
    /**
     * Constant for Max Slots
     */
    private static final int MAX_SLOTS = 8;

    /**
     * This Activity
     */
    private MenuActivity activity;
    /**
     * Data to show
     */
    private Data data;

    /**
     * Class Constructor
     * @param context App Context
     * @param attrs App Attributes
     */
    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);

        activity = (MenuActivity)context;
        data = activity.menuViewData;
    }

    /**
     * When View is created
     * @param window View to create
     */
    public static void onActivityCreate(MenuActivity window) {
        PreferenceManager.getDefaultSharedPreferences(window.getApplicationContext());
    }

    /**
     * When View finishes inflating
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Common.setTypeface(this,
                new int[] { R.id.BtnContinue,
                        R.id.BtnNewGame,
                        R.id.BtnLoad,
                        R.id.BtnSave,
                        R.id.TxtHelp, });

        PreferenceManager.getDefaultSharedPreferences(getContext());

        findViewById(R.id.BtnContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.playSound(SoundManager.SOUND_BTN_PRESS);
                startGame(activity, Game.INSTANT_NAME);
            }
        });

        findViewById(R.id.BtnNewGame).setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                SoundManager.playSound(SoundManager.SOUND_BTN_PRESS);

                if (hasInstantSave() && !MenuActivity.justLoaded) {
                    activity.showDialog(DIALOG_NEW_GAME_WARN);
                } else {
                    startGame(activity, "");
                }
            }
        });

        findViewById(R.id.BtnLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                SoundManager.playSound(SoundManager.SOUND_BTN_PRESS);

                fillSlots(data.slotStringsForLoad, data.slotFileNamesForLoad, true);
                data.loadSlotsAdapter.notifyDataSetChanged();
                activity.showDialog(DIALOG_LOAD_SLOTS);
            }
        });

        findViewById(R.id.BtnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                SoundManager.playSound(SoundManager.SOUND_BTN_PRESS);
                ZameApplication.trackEvent("Menu", "Save", "", 0);
                ZameApplication.flushEvents();

                fillSlots(data.slotStringsForSave, data.slotFileNamesForSave, false);
                data.saveSlotsAdapter.notifyDataSetChanged();

                data.currentIndex = ((data.saveSlotsDialog == null)
                        ? 0
                        : data.saveSlotsDialog.getListView().getCheckedItemPosition());

                activity.showDialog(DIALOG_SAVE_SLOTS);
            }
        });

        findViewById(R.id.TxtHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                SoundManager.playSound(SoundManager.SOUND_BTN_PRESS);
                ZameApplication.trackEvent("Menu", "Menu", "", 0);
                ZameApplication.flushEvents();

                activity.openOptionsMenu();
            }
        });

        data.slotStringsForLoad = new ArrayList<String>();
        data.slotFileNamesForLoad = new ArrayList<String>();
        data.slotStringsForSave = new ArrayList<String>();
        data.slotFileNamesForSave = new ArrayList<String>();

        data.loadSlotsAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.select_dialog_item,
                data.slotStringsForLoad);

        data.saveSlotsAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.select_dialog_singlechoice,
                data.slotStringsForSave);

        updateSlotsAndButtons();
    }

    /**
     * When Focus Changes
     * @param hasWindowFocus Does View have focus?
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        if (hasWindowFocus) {
            updateSlotsAndButtons();
        }
    }

    /**
     *
     */
    private void updateSlotsAndButtons() {
        fillSlots(data.slotStringsForSave, data.slotFileNamesForSave, false);

        findViewById(R.id.BtnContinue).setEnabled(hasInstantSave());
        findViewById(R.id.BtnSave).setEnabled(hasInstantSave());
        findViewById(R.id.BtnLoad).setEnabled(fillSlots(data.slotStringsForLoad, data.slotFileNamesForLoad, true) > 0);
    }

    /**
     * Save slots
     * @param saves Saves
     * @param files File Paths
     * @param pat Pattern for matching
     * @return saves
     */
    private HashMap<Integer, Pair<String, String>>  saveSlots(HashMap<Integer, Pair<String, String>>  saves, String[] files, Pattern pat){
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < files.length; i++) {
            Matcher mt = pat.matcher(files[i]);

            if (mt.find()) {
                int slotNum = Integer.valueOf(mt.group(1)) - 1;

                if ((slotNum >= 0) && (slotNum < MAX_SLOTS)) {
                    saves.put(slotNum,
                            new Pair<String, String>(String.format(Locale.US,
                                    "Slot %d: %s %s:%s",
                                    slotNum + 1,
                                    mt.group(2),
                                    mt.group(3),
                                    mt.group(4)), files[i].substring(0, files[i].length() - 5)));
                }
            }
        }

        return saves;
    }

    /**
     * Fills the slots
     * @param slotStrings String for slots
     * @param slotFileNames Slots file names
     * @param hideUnused Do unused slots be hidden?
     * @return Saves Size
     */
    private int fillSlots(ArrayList<String> slotStrings, ArrayList<String> slotFileNames, boolean hideUnused) {
        slotStrings.clear();
        slotFileNames.clear();

        String[] files = (new File(Game.SAVES_FOLDER)).list();

        if (files == null) {
            return 0;
        }

        Pattern pat = Pattern.compile("^slot-(\\d)\\.(\\d{4}-\\d{2}-\\d{2})-(\\d{2})-(\\d{2})\\.save$");

        @SuppressLint("UseSparseArrays")
        HashMap<Integer, Pair<String, String>> saves = new HashMap<Integer, Pair<String, String>>();

        saves = saveSlots(saves, files, pat);

        for (int i = 0; i < MAX_SLOTS; i++) {
            Pair<String, String> pair = saves.get(i);

            if (pair != null) {
                slotStrings.add(pair.first);
                slotFileNames.add(pair.second);
            } else if (!hideUnused) {
                try {
                    slotStrings.add(String.format(Locale.US,
                            "Slot %d: <%s>",
                            i + 1,
                            activity.getString(R.string.val_empty)));
                } catch (Exception ex) {
                    slotStrings.add(String.format(Locale.US, "Slot %d: <Empty>", i + 1));
                }

                slotFileNames.add("");
            }
        }

        return saves.size();
    }

    /**
     * When an option is selected
     * @param window View to show
     * @param item Item selected
     * @return true if saved, false otherwise
     */
    @SuppressWarnings({ "deprecation", "MagicNumber" })
    public static boolean onOptionsItemSelected(final MenuActivity window, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                window.showDialog(DIALOG_ABOUT);

                TextView dlgText = (TextView)window.menuViewData.aboutDialog.findViewById(android.R.id.message);
                dlgText.setMovementMethod(LinkMovementMethod.getInstance());
                dlgText.setTextColor(0xFFFFFFFF);
                return true;

            case R.id.menu_site_help:
                Common.openBrowser(window,
                        "http://mobile.zame-dev.org/gloomy/help.php?hl=" + Locale.getDefault()
                                .getLanguage()
                                .toLowerCase(Locale.US));

                return true;

            case R.id.menu_exit:
                //noinspection ConstantConditions
                if (MenuViewHelper.canExit(window)) {
                    window.finish();
                }

                return true;
            default: break;
        }

        return false;

    }

    /**
     * Check if instant save is available
     * @param window View to show
     * @param info Info to show
     */
    @SuppressWarnings("deprecation")
    private static void checkInstantSave(MenuActivity window, Data info){
        if (hasInstantSave() && !MenuActivity.justLoaded) {
            window.showDialog(DIALOG_LOAD_WARN);
        } else {
            startGame(window, info.slotFileNamesForLoad.get(info.currentIndex));
        }
    }

    /**
     * Save to Slots
     * @param info Info to show
     */
    private static void saveToSlot(Data info){
        String newSaveName = String.format(Locale.US,
                "%sslot-%d.%s.save",
                Game.SAVES_ROOT,
                info.currentIndex + 1,
                (new SimpleDateFormat("yyyy-MM-dd-HH-mm",
                        Locale.US)).format(Calendar.getInstance().getTime()));

        if (Common.copyFile(Game.INSTANT_PATH, newSaveName + ".new")) {
            //noinspection SizeReplaceableByIsEmpty
            if (info.slotFileNamesForSave.get(info.currentIndex).length() != 0) {
                //noinspection ResultOfMethodCallIgnored
                (new File(Game.SAVES_ROOT
                        + info.slotFileNamesForSave.get(info.currentIndex)
                        + ".save")).delete();
            }

            //noinspection ResultOfMethodCallIgnored
            (new File(newSaveName + ".new")).renameTo(new File(newSaveName));

            Toast.makeText(ZameApplication.self, R.string.msg_game_saved, Toast.LENGTH_LONG)
                    .show();

            MenuActivity.justLoaded = true; // just saved
        }
    }

    /**
     * When Dialog is created
     * @param ownerActivity Activity who owns Dialog
     * @param id Dialog ID
     * @return Dialog to show
     */
    @SuppressWarnings("deprecation")
    public static Dialog onCreateDialog(MenuActivity ownerActivity, int id) {
        final MenuActivity activity = ownerActivity;
        final Data data = activity.menuViewData;

        switch (id) {
            case DIALOG_ABOUT: {
                data.aboutDialog = new AlertDialog.Builder(activity).setTitle(R.string.dlg_about_title)
                        .setMessage(Html.fromHtml(activity.getString(R.string.dlg_about_text)
                                .replace("{VERSION_NAME}", ZameApplication.self.getVersionName())))
                        .setPositiveButton(R.string.dlg_ok, null)
                        .create();

                return data.aboutDialog;
            }

            case DIALOG_NEW_GAME_WARN: {
                return new AlertDialog.Builder(activity).setIcon(R.drawable.ic_dialog_alert)
                        .setTitle(R.string.dlg_new_game)
                        .setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                startGame(activity, "");
                            }
                        })
                        .setNegativeButton(R.string.dlg_cancel, null)
                        .create();
            }

            case DIALOG_LOAD_WARN: {
                return new AlertDialog.Builder(activity).setIcon(R.drawable.ic_dialog_alert)
                        .setTitle(R.string.dlg_new_game)
                        .setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                startGame(activity, data.slotFileNamesForLoad.get(data.currentIndex));
                            }
                        })
                        .setNegativeButton(R.string.dlg_cancel, null)
                        .create();
            }

            case DIALOG_LOAD_SLOTS: {
                return new AlertDialog.Builder(activity).setTitle(R.string.dlg_select_slot_load)
                        .setAdapter(data.loadSlotsAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data.currentIndex = which;
                                checkInstantSave(activity, data);
                            }
                        })
                        .create();
            }

            case DIALOG_SAVE_SLOTS: {
                data.saveSlotsDialog = new AlertDialog.Builder(activity).setTitle(R.string.dlg_select_slot_save)
                        .setSingleChoiceItems(data.saveSlotsAdapter, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data.currentIndex = which;
                            }
                        })
                        .setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                saveToSlot(data);
                            }
                        })
                        .setNegativeButton(R.string.dlg_cancel, null)
                        .create();

                //noinspection ConstantConditions
                data.saveSlotsDialog.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

                return data.saveSlotsDialog;
            }
            default : break;
        }

        return MenuViewHelper.onCreateDialog(activity, data, id);
    }

    /**
     * Starts the game
     * @param window View to show
     * @param saveName Save name
     */
    private static void startGame(MenuActivity window, String saveName) {
        if (!saveName.equals(Game.INSTANT_NAME)) {
            // new game started or loaded non-instant state
            MenuActivity.justLoaded = true;
        }

        Game.savedGameParam = saveName;
        window.instantMusicPause = false;
        window.startActivity(new Intent(window, GameActivity.class));
    }

    /**
     * Does the game have instant saves?
     * @return True if instant saves exist, false otherwise
     */
    private static boolean hasInstantSave() {
        return (new File(Game.INSTANT_PATH)).exists();
    }
}
