package zame.game;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.SystemClock;
import android.view.MotionEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import zame.game.engine.State;

// http://www.droidnova.com/android-3d-game-tutorial-part-i,312.html
// http://iphonedevelopment.blogspot.com/2009/05/opengl-es-from-ground-up-table-of.html
// http://insanitydesign.com/wp/projects/nehe-android-ports/
// http://www.rbgrn.net/content/342-using-input-pipelines-your-android-game
// http://habrahabr.ru/blogs/gdev/136878/

/**
 * Abstract Class for ZameGame
 */
public abstract class ZameGame implements zame.libs.GLSurfaceView21.Renderer {
    /**
     * Constant for lockUpdate
     */
    protected static final Object lockUpdate = new Object();
    /**
     * Constant for lockControls
     */
    private static final Object lockControls = new Object();

    /**
     * Update Interval
     */
    private static long updateInterval;
    /**
     * Game Start Time
     */
    private static long startTime;
    /**
     * Last Time
     */
    private static long lastTime=0;
    /**
     * Is Game Paused?
     */
    @SuppressWarnings("FieldAccessedSynchronizedAndUnsynchronized") private static boolean isPaused=false;

    /**
     * Do we resume after surface creation?
     */
    public static boolean callResumeAfterSurfaceCreated = true;
    /**
     * Time elapsed
     */
    public static long elapsedTime=0;
    /**
     * Game width
     */
    public static int width = 1;
    /**
     * Game Height
     */
    public static int height = 1;
    /**
     * App Resources
     */
    public static Resources resources;
    /**
     * App AssetManager
     */
    public static AssetManager assetManager;

    /**
     * Class Constructor
     * @param res App Resources
     * @param assets App Assets
     */
    public ZameGame(Resources res, AssetManager assets) {
        callResumeAfterSurfaceCreated = true;
        width = 1;
        height = 1;
        updateInterval = 1000;
        resources = res;
        assetManager = assets;

        startTime = SystemClock.elapsedRealtime();        // (*1)
    }

    /**
     * Reads bytes from file
     * @param is Input stream
     * @return byte array
     * @throws IOException Error while reading
     */
    public static byte[] readBytes(InputStream is) throws IOException {
        byte[] buffer = new byte[is.available()];

        //noinspection ResultOfMethodCallIgnored
        is.read(buffer);
        is.close();

        return buffer;
    }

    /**
     * When Surface is created
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     * @param config the EGLConfig of the created surface. Can be used
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glFrontFace(GL10.GL_CCW);
        gl.glCullFace(GL10.GL_BACK);
        gl.glDisable(GL10.GL_DITHER);

        surfaceCreated(gl);

        if (callResumeAfterSurfaceCreated) {
            callResumeAfterSurfaceCreated = false;
            resume();
        }
    }

    /**
     * When Surface is changed
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     * @param larghezza Surface Width
     * @param altezza Surface Heigth
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int larghezza, int altezza) {
        gl.glViewport(0, 0, larghezza, altezza);

        ZameGame.width = ((larghezza < 1) ? 1 : larghezza);        // just for case
        ZameGame.height = ((altezza < 1) ? 1 : altezza);    // just for case

        surfaceSizeChanged(gl);
    }

    // FPS = 1000 / updateInterval

    /**
     * Sets game update interval
     * @param updateInterval New Update Interval
     */
    protected void setUpdateInterval(long updateInterval) {
        synchronized (lockUpdate) {
            ZameGame.updateInterval = ((updateInterval > 1) ? updateInterval : 1);
        }
    }

    /**
     * Pause Game
     */
    public void pause() {
        synchronized (lockUpdate) {
            if (!isPaused) {
                elapsedTime = SystemClock.elapsedRealtime() - startTime;
                isPaused = true;
            }

            State.tempElapsedTime = elapsedTime;
            State.tempLastTime = lastTime;
            saveState();
        }
    }

    /**
     * Resume Game
     */
    public void resume() {
        synchronized (lockUpdate) {
            elapsedTime = State.tempElapsedTime;
            lastTime = State.tempLastTime;

            if (isPaused) {
                startTime = SystemClock.elapsedRealtime() - elapsedTime;
                isPaused = false;
            }
        }
    }

    /**
     * Update Controls
     */
    protected abstract void updateControls();

    /**
     * Update Game
     */
    protected abstract void update();

    /**
     * Render the game
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     */
    protected abstract void render(GL10 gl);

    /**
     * When Key is released
     * @param keyCode Key released
     * @return false
     */
    protected boolean keyUp(int keyCode) { return false; }

    /**
     * When key is pressed
     * @param keyCode Key pressed
     * @return false
     */
    protected boolean keyDown(int keyCode) { return false; }

    /**
     * Handles Touch Events
     * @param event event
     */
    protected void touchEvent(MotionEvent event) {}

    /**
     * Handles Trackball Events
     * @param event Event
     */
    protected void trackballEvent(MotionEvent event) {}

    /**
     * When Surface is created
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     */
    protected void surfaceCreated(GL10 gl) {}

    /**
     * When Surface changes
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     */
    protected void surfaceSizeChanged(GL10 gl) {}

    /**
     * Save Game state
     */
    protected void saveState() {}

    /**
     * When a Frame is drawn
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        if (isPaused) {
            render(gl);
            return;
        }

        synchronized (lockControls) {
            updateControls();
        }

        synchronized (lockUpdate) {
            elapsedTime = SystemClock.elapsedRealtime() - startTime;

            // sometimes (*1) caused weird bug.
            // but sometimes without (*1) other bug occurred :)
            // so this is hacked fix:
            if (lastTime > elapsedTime) {
                lastTime = elapsedTime;
            }

            if ((elapsedTime - lastTime) > updateInterval) {
                long count = (elapsedTime - lastTime) / updateInterval;
                lastTime += updateInterval * count;

                if (count > 10) {
                    count = 10;
                    lastTime = elapsedTime;
                }

                while (count > 0) {
                    update();
                    count--;
                }
            }

            // render(gl); // (*2) render was here
        }

        render(gl); // (*2) but moved here to fix problems with locked mutex
    }

    /**
     * Handles Key Release
     * @param keyCode Key released
     * @return false
     */
    public boolean handleKeyUp(int keyCode) {
        boolean ret=true;

        synchronized (lockControls) {
            ret = keyUp(keyCode);
        }

        return ret;
    }

    /**
     * Handles Key Down
     * @param keyCode Key pressed
     * @return false
     */
    public boolean handleKeyDown(int keyCode) {
        boolean ret=true;

        synchronized (lockControls) {
            ret = keyDown(keyCode);
        }

        return ret;
    }

    /**
     * Handles Touch Event
     * @param event Event
     */
    public void handleTouchEvent(MotionEvent event) {
        synchronized (lockControls) {
            touchEvent(event);
        }
    }

    /**
     * Handles TrackBall Event
     * @param event event
     */
    public void handleTrackballEvent(MotionEvent event) {
        synchronized (lockControls) {
            trackballEvent(event);
        }
    }
}
