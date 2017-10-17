package zame.game.engine;

import javax.microedition.khronos.opengles.GL10;
import zame.game.Renderer;

/**
 * Class Representing an Overlay
 */
@SuppressWarnings("WeakerAccess")
public final class Overlay {
    /**
     * Constant for Blood Overlay
     */
    public static final int BLOOD = 1;
    /**
     * Constant for Item Overlay
     */
    public static final int ITEM = 2;
    /**
     * Constant for Mark Overlay
     */
    public static final int MARK = 3;

    /**
     * Constant for Color set
     */
    private static final float[][] COLORS = { new float[] { 1.0f, 0.0f, 0.0f }, // BLOOD
            new float[] { 1.0f, 1.0f, 1.0f }, // ITEM
            new float[] { 1.0f, 1.0f, 1.0f } // MARK
    };

    /**
     * Overlay Type
     */
    private static int overlayType=0;
    /**
     * Overlay Time to stay rendered
     */
    private static long overlayTime=0;
    /**
     * Label Type
     */
    private static int labelType=0;
    /**
     * Label Time to stay rendered
     */
    private static long labelTime=0;

    /**
     * Class Constructor
     */
    private Overlay() {
    }

    /**
     * Initializes the Overlay
     */
    public static void init() {
        overlayType = 0;
        labelType = 0;
    }

    /**
     * Shows the Overlay
     * @param type Type of Overlay to show
     */
    public static void showOverlay(int type) {
        overlayType = type;
        overlayTime = Game.elapsedTime;
    }

    /**
     * Show Label
     * @param type Label to show
     */
    public static void showLabel(int type) {
        labelType = type;
        labelTime = Game.elapsedTime;
    }

    /**
     * Renders Overlay and Label
     * @param gl Renderer
     */
    public static void render(GL10 gl) {
        renderOverlay(gl);
        renderLabel(gl);
    }

    /**
     * Appends Overlay Color
     * @param r Red Value 0-255
     * @param g Green Value 0-255
     * @param b Blue Value 0-255
     * @param a Alpha Value
     */
    @SuppressWarnings("MagicNumber")
    private static void appendOverlayColor(float r, float g, float b, float a) {
        float d = (Renderer.a1 + a) - (Renderer.a1 * a);

        if (d < 0.001) {
            return;
        }

        Renderer.r1 = (((Renderer.r1 * Renderer.a1) - (Renderer.r1 * Renderer.a1 * a)) + (r * a)) / d;
        Renderer.g1 = (((Renderer.g1 * Renderer.a1) - (Renderer.g1 * Renderer.a1 * a)) + (g * a)) / d;
        Renderer.b1 = (((Renderer.b1 * Renderer.a1) - (Renderer.b1 * Renderer.a1 * a)) + (b * a)) / d;
        Renderer.a1 = d;
    }

    /**
     * Renders the overlays
     * @param gl Renderer
     */
    @SuppressWarnings("MagicNumber")
    private static void renderOverlay(GL10 gl) {
        Renderer.r1 = 0.0f;
        Renderer.g1 = 0.0f;
        Renderer.b1 = 0.0f;
        Renderer.a1 = 0.0f;

        float bloodAlpha = Math.max(0.0f, 0.4f - (((float)State.heroHealth / 20.0f) * 0.4f)); // less than 20 health - show blood overlay

        if (bloodAlpha > 0.0f) {
            appendOverlayColor(COLORS[BLOOD - 1][0], COLORS[BLOOD - 1][1], COLORS[BLOOD - 1][2], bloodAlpha);
        }

        if (overlayType != 0) {
            float alpha = 0.5f - ((float)(Game.elapsedTime - overlayTime) / 300.0f);

            if (alpha > 0.0f) {
                appendOverlayColor(COLORS[overlayType - 1][0],
                        COLORS[overlayType - 1][1],
                        COLORS[overlayType - 1][2],
                        alpha);
            } else {
                overlayType = 0;
            }
        }

        if (Renderer.a1 < 0.001f) {
            return;
        }

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPushMatrix();
        Renderer.loadIdentityAndOrthof(gl, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        Renderer.init();

        Renderer.r2 = Renderer.r1;
        Renderer.g2 = Renderer.g1;
        Renderer.b2 = Renderer.b1;
        Renderer.a2 = Renderer.a1;

        Renderer.r3 = Renderer.r1;
        Renderer.g3 = Renderer.g1;
        Renderer.b3 = Renderer.b1;
        Renderer.a3 = Renderer.a1;

        Renderer.r4 = Renderer.r1;
        Renderer.g4 = Renderer.g1;
        Renderer.b4 = Renderer.b1;
        Renderer.a4 = Renderer.a1;

        Renderer.setQuadOrthoCoords(0.0f, 0.0f, 1.0f, 1.0f);
        Renderer.drawQuad();

        gl.glShadeModel(GL10.GL_FLAT);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        Renderer.flush(gl, false);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();
    }

    /**
     * Renders the Labels
     * @param gl Renderer
     */
    @SuppressWarnings("MagicNumber")
    private static void renderLabel(GL10 gl) {
        int heightOffset = ((State.shownMessageId != 0) ? 25 : 0);

        if (labelType != 0) {
            float op = Math.min(1.0f, 3.0f - ((float)(Game.elapsedTime - labelTime) / 500.0f));

            if (op <= 0.0f) {
                labelType = 0;
            } else {
                gl.glColor4f(1.0f, 1.0f, 1.0f, op);
                int labelId = Labels.map[labelType];

                Labels.maker.beginDrawing(gl, Game.width, Game.height);

                Labels.maker.draw(gl,
                        (Game.width - Labels.maker.getWidth(labelId)) / 2,
                        ((Game.height - Labels.maker.getHeight(labelId)) / 2) - heightOffset,
                        labelId);

                Labels.maker.endDrawing(gl);
            }
        }

        if (State.shownMessageId != 0) {
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            int messageLabelId = Labels.getMessageLabelId(gl, State.shownMessageId);

            Labels.msgMaker.beginDrawing(gl, Game.width, Game.height);

            Labels.msgMaker.draw(gl,
                    (Game.width - Labels.msgMaker.getWidth(messageLabelId)) / 2,
                    ((Game.height - Labels.msgMaker.getHeight(messageLabelId)) / 2) + heightOffset,
                    messageLabelId);

            Labels.msgMaker.endDrawing(gl);
        }
    }
}
