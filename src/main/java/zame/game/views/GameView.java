package zame.game.views;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import zame.game.GameActivity;
import zame.game.R;
import zame.game.SoundManager;
import zame.game.engine.Controls;
import zame.game.engine.Game;

/**
 * Class representing GameView
 */
public class GameView extends FrameLayout implements IZameView {
    /**
     * Class representing Data
     */
    public static class Data {
        /**
         * The Game
         */
        public Game game;
        /**
         * Render a Black Screen?
         */
        public boolean noClearRenderBlackScreenOnce;

        /**
         * Class Constructor
         * @param resources Resources
         * @param assets Resource Manager
         */
        public Data(Resources resources, AssetManager assets) {
            game = new Game(resources, assets);
            noClearRenderBlackScreenOnce = false;
        }
    }

    /**
     * Game data
     */
    private Data data;
    /**
     * Game View
     */
    private ZameGameView view;

    /**
     * Class Constructor
     * @param context App Context
     * @param attrs App Attributes
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        GameActivity activity = (GameActivity)context;
        data = activity.gameViewData;
    }

    /**
     * When Activity Finishes loading
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        view = (ZameGameView)findViewById(R.id.ZameGameView);
        view.setGame(data.game);
    }

    /**
     * When Activity is Resumed
     */
    @Override
    public void onResume() {
        if (data.noClearRenderBlackScreenOnce) {
            data.noClearRenderBlackScreenOnce = false;
        } else {
            Game.renderBlackScreen = false;
        }

        SoundManager.setPlaylist(SoundManager.LIST_MAIN);
        Controls.fillMap();

        Game.callResumeAfterSurfaceCreated = true;
        view.onResume();
    }

    /**
     * When Activity is Paused
     */
    @Override
    public void onPause() {
        view.onPause();
        data.game.pause();
    }
}
