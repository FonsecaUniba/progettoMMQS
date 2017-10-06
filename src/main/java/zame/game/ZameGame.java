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

// TODO: look at System.nanoTime()
// http://www.droidnova.com/android-3d-game-tutorial-part-i,312.html
// http://iphonedevelopment.blogspot.com/2009/05/opengl-es-from-ground-up-table-of.html
// http://insanitydesign.com/wp/projects/nehe-android-ports/
// http://www.rbgrn.net/content/342-using-input-pipelines-your-android-game
// http://habrahabr.ru/blogs/gdev/136878/

public abstract class ZameGame implements zame.libs.GLSurfaceView21.Renderer {
    protected static final Object lockUpdate = new Object();
    private static final Object lockControls = new Object();

    private static long updateInterval;
    private static long startTime;
    private static long lastTime=0;
    @SuppressWarnings("FieldAccessedSynchronizedAndUnsynchronized") private static boolean isPaused=false;

    public static boolean callResumeAfterSurfaceCreated = true;
    public static long elapsedTime=0;
    public static int width = 1;
    public static int height = 1;
    public static Resources resources;
    public static AssetManager assetManager;

    public ZameGame(Resources res, AssetManager assets) {
        callResumeAfterSurfaceCreated = true;
        width = 1;
        height = 1;
        updateInterval = 1000;
        resources = res;
        assetManager = assets;

        startTime = SystemClock.elapsedRealtime();        // (*1)
    }

    public static byte[] readBytes(InputStream is) throws IOException {
        byte[] buffer;

        buffer = new byte[is.available()];

        //noinspection ResultOfMethodCallIgnored
        is.read(buffer);
        is.close();

        return buffer;
    }

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

    @Override
    public void onSurfaceChanged(GL10 gl, int larghezza, int altezza) {
        gl.glViewport(0, 0, larghezza, altezza);

        ZameGame.width = ((larghezza < 1) ? 1 : larghezza);        // just for case
        ZameGame.height = ((altezza < 1) ? 1 : altezza);    // just for case

        surfaceSizeChanged(gl);
    }

    // FPS = 1000 / updateInterval
    protected void setUpdateInterval(long updateInterval) {
        synchronized (lockUpdate) {
            ZameGame.updateInterval = ((updateInterval > 1) ? updateInterval : 1);
        }
    }

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

    protected abstract void updateControls();

    protected abstract void update();

    protected abstract void render(GL10 gl);

    protected boolean keyUp(int keyCode) { return false; }

    protected boolean keyDown(int keyCode) { return false; }

    protected void touchEvent(MotionEvent event) {}

    protected void trackballEvent(MotionEvent event) {}

    protected void surfaceCreated(GL10 gl) {}

    protected void surfaceSizeChanged(GL10 gl) {}

    protected void saveState() {}

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

    public boolean handleKeyUp(int keyCode) {
        boolean ret;

        synchronized (lockControls) {
            ret = keyUp(keyCode);
        }

        return ret;
    }

    public boolean handleKeyDown(int keyCode) {
        boolean ret;

        synchronized (lockControls) {
            ret = keyDown(keyCode);
        }

        return ret;
    }

    public void handleTouchEvent(MotionEvent event) {
        synchronized (lockControls) {
            touchEvent(event);
        }
    }

    public void handleTrackballEvent(MotionEvent event) {
        synchronized (lockControls) {
            trackballEvent(event);
        }
    }
}
