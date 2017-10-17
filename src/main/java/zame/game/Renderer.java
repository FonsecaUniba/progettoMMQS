package zame.game;

import javax.microedition.khronos.opengles.GL10;

// http://stackoverflow.com/questions/1848886/jni-c-library-passing-byte-ptr
// http://groups.google.com/group/android-ndk/tree/browse_frm/month/2010-01?_done=/group/android-ndk/browse_frm/month/2010-01%3F&

// Native buffers (aka ByteBuffer, ShortBuffer and FloatBuffer) suck in DalvikVM. It is terribly slow.
// So native code used to render. It's up to 4x faster than java code with native buffers.

/**
 * Class representing a Renderer
 */
@SuppressWarnings("MagicNumber")
public final class Renderer {
    /**
     * Transparency value
     */
    public static final float ALPHA_VALUE = 0.5f;

    /**
     * Max Quadrants
     */
    private static final int MAX_QUADS = 64 * 64 * 2;
    // (64*64*2 * (12*4 + 16*4 + 8*4 + 6*2 + 4*4 + 8*4))

    /**
     * Vertex Buffer
     */
    private static float[] vertexBuffer = new float[MAX_QUADS * 12];
    /**
     * Colors Buffer
     */
    private static float[] colorsBuffer = new float[MAX_QUADS * 16];
    /**
     * Texture Buffer
     */
    private static float[] textureBuffer = new float[MAX_QUADS * 8];
    /**
     * Indices Buffer
     */
    private static short[] indicesBuffer = new short[MAX_QUADS * 6];
    /**
     * Line Vertex Buffer
     */
    private static float[] lineVertexBuffer = new float[MAX_QUADS * 4];
    /**
     * Line Colors Buffer
     */
    private static float[] lineColorsBuffer = new float[MAX_QUADS * 8];

    /**
     * Vertex Buffer current position
     */
    private static int vertexBufferPos=0;
    /**
     * Colors Buffer current position
     */
    private static int colorsBufferPos=0;
    /**
     * Texture buffer current position
     */
    private static int textureBufferPos=0;
    /**
     * Indices Buffer current position
     */
    private static int indicesBufferPos=0;
    /**
     * Line Vertex Buffer Current Position
     */
    private static int lineVertexBufferPos=0;
    /**
     * Line Colors Buffer Current Position
     */
    private static int lineColorsBufferPos=0;

    /**
     * Vertex Count
     */
    private static short vertexCount=0;
    /**
     * Line Vertex Count
     */
    private static short lineVertexCount=0;

    /**
     * Class Constructor
     */
    private Renderer() {
    }

    /**
     * Initializes Renderer
     */
    public static void init() {
        vertexCount = 0;
        lineVertexCount = 0;

        vertexBufferPos = 0;
        colorsBufferPos = 0;
        textureBufferPos = 0;
        indicesBufferPos = 0;
        lineVertexBufferPos = 0;
        lineColorsBufferPos = 0;
    }

    /**
     * Flushes Renderer
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     */
    public static void flush(GL10 gl) {
        flush(gl, true);
    }

    /**
     * Flushes Renderer
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     * @param useTextures Do we reload textures?
     */
    public static void flush(GL10 gl, boolean useTextures) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        if (indicesBufferPos != 0) {
            if (useTextures) {
                gl.glEnable(GL10.GL_TEXTURE_2D);
                gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            } else {
                gl.glDisable(GL10.GL_TEXTURE_2D);
                gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            }

            ZameJniRenderer.callRenderTriangles(vertexBuffer,
                    colorsBuffer,
                    (useTextures ? textureBuffer : null),
                    indicesBuffer,
                    indicesBufferPos);
        }

        if (lineVertexCount != 0) {
            gl.glDisable(GL10.GL_TEXTURE_2D);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            ZameJniRenderer.callRenderLines(lineVertexBuffer, lineColorsBuffer, lineVertexCount);
        }
    }

    /**
     * Bind Textures to Object
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     * @param tex Textures
     */
    public static void bindTexture(GL10 gl, int tex) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, Config.levelTextureFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, Config.levelTextureFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
    }

    /**
     * Bind Textures Again
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     * @param tex Textures
     */
    public static void bindTextureRep(GL10 gl, int tex) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, Config.levelTextureFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, Config.levelTextureFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
    }

    /**
     * Bind Textures to Ctl
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     * @param tex Textures
     */
    public static void bindTextureCtl(GL10 gl, int tex) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, Config.weaponsTextureFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, Config.weaponsTextureFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
    }

    /**
     * Load Orthogonal Identity
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     * @param left Left value
     * @param right Right Value
     * @param bottom Bottom Value
     * @param top Top Value
     * @param near Near Value
     * @param far Far Value
     */
    public static void loadIdentityAndOrthof(GL10 gl,
            float left,
            float right,
            float bottom,
            float top,
            float near,
            float far) {

        gl.glLoadIdentity();

        if (Config.rotateScreen) {
            gl.glOrthof(right, left, top, bottom, near, far);
        } else {
            gl.glOrthof(left, right, bottom, top, near, far);
        }
    }

    /**
     * Load Frustum Identity
     * @param gl the GL interface. Use <code>instanceof</code> to
     * test if the interface supports GL11 or higher interfaces.
     * @param left Left Value
     * @param right Right Value
     * @param bottom Bottom Value
     * @param top Top Value
     * @param near Near Value
     * @param far Far Value
     */
    public static void loadIdentityAndFrustumf(GL10 gl,
            float left,
            float right,
            float bottom,
            float top,
            float near,
            float far) {

        gl.glLoadIdentity();

        if (Config.rotateScreen) {
            gl.glFrustumf(right, left, top, bottom, near, far);
        } else {
            gl.glFrustumf(left, right, bottom, top, near, far);
        }
    }

    /**
     * X1
     */
    public static float x1=0;
    /**
     * Y1
     */
    public static float y1=0;
    /**
     * Z1
     */
    public static float z1=0;
    /**
     * U1
     */
    public static float u1=0;
    /**
     * V1
     */
    public static float v1=0;
    /**
     * R1
     */
    public static float r1=0;
    /**
     * G1
     */
    public static float g1=0;
    /**
     * B1
     */
    public static float b1=0;
    /**
     * A1
     */
    public static float a1=0;

    /**
     * X2
     */
    public static float x2=0;
    /**
     * Y2
     */
    public static float y2=0;
    /**
     * Z2
     */
    public static float z2=0;
    /**
     * U2
     */
    public static float u2=0;
    /**
     * V2
     */
    public static float v2=0;
    /**
     * R2
     */
    public static float r2=0;
    /**
     * G2
     */
    public static float g2=0;
    /**
     * B2
     */
    public static float b2=0;
    /**
     * A2
     */
    public static float a2=0;

    /**
     * X3
     */
    public static float x3=0;
    /**
     * Y3
     */
    public static float y3=0;
    /**
     * Z3
     */
    public static float z3=0;
    /**
     * U3
     */
    public static float u3=0;
    /**
     * V3
     */
    public static float v3=0;
    /**
     * R3
     */
    public static float r3=0;
    /**
     * G3
     */
    public static float g3=0;
    /**
     * B3
     */
    public static float b3=0;
    /**
     * A3
     */
    public static float a3=0;

    /**
     * X4
     */
    public static float x4=0;
    /**
     * Y4
     */
    public static float y4=0;
    /**
     * Z4
     */
    public static float z4=0;
    /**
     * U4
     */
    public static float u4=0;
    /**
     * V4
     */
    public static float v4=0;
    /**
     * R4
     */
    public static float r4=0;
    /**
     * G4
     */
    public static float g4=0;
    /**
     * B4
     */
    public static float b4=0;
    /**
     * A4
     */
    public static float a4=0;

    // In-game:
    //
    //	1 | 2
    // ---+--->
    //	4 | 3
    //	  v
    //
    // Ortho:
    //
    //  2 | 3
    // ---+--->
    //  1 | 4
    //    v
    //

    /**
     * Draw Quadrant
     */
    public static void drawQuad() {
        vertexBuffer[vertexBufferPos++] = x1;
        vertexBuffer[vertexBufferPos++] = y1;
        vertexBuffer[vertexBufferPos++] = z1;

        vertexBuffer[vertexBufferPos++] = x2;
        vertexBuffer[vertexBufferPos++] = y2;
        vertexBuffer[vertexBufferPos++] = z2;

        vertexBuffer[vertexBufferPos++] = x3;
        vertexBuffer[vertexBufferPos++] = y3;
        vertexBuffer[vertexBufferPos++] = z3;

        vertexBuffer[vertexBufferPos++] = x4;
        vertexBuffer[vertexBufferPos++] = y4;
        vertexBuffer[vertexBufferPos++] = z4;

        //

        colorsBuffer[colorsBufferPos++] = r1;
        colorsBuffer[colorsBufferPos++] = g1;
        colorsBuffer[colorsBufferPos++] = b1;
        colorsBuffer[colorsBufferPos++] = a1;

        colorsBuffer[colorsBufferPos++] = r2;
        colorsBuffer[colorsBufferPos++] = g2;
        colorsBuffer[colorsBufferPos++] = b2;
        colorsBuffer[colorsBufferPos++] = a2;

        colorsBuffer[colorsBufferPos++] = r3;
        colorsBuffer[colorsBufferPos++] = g3;
        colorsBuffer[colorsBufferPos++] = b3;
        colorsBuffer[colorsBufferPos++] = a3;

        colorsBuffer[colorsBufferPos++] = r4;
        colorsBuffer[colorsBufferPos++] = g4;
        colorsBuffer[colorsBufferPos++] = b4;
        colorsBuffer[colorsBufferPos++] = a4;

        //

        textureBuffer[textureBufferPos++] = u1;
        textureBuffer[textureBufferPos++] = v1;

        textureBuffer[textureBufferPos++] = u2;
        textureBuffer[textureBufferPos++] = v2;

        textureBuffer[textureBufferPos++] = u3;
        textureBuffer[textureBufferPos++] = v3;

        textureBuffer[textureBufferPos++] = u4;
        textureBuffer[textureBufferPos++] = v4;

        //

        indicesBuffer[indicesBufferPos++] = vertexCount;
        indicesBuffer[indicesBufferPos++] = (short)(vertexCount + 2);
        indicesBuffer[indicesBufferPos++] = (short)(vertexCount + 1);
        indicesBuffer[indicesBufferPos++] = vertexCount;
        indicesBuffer[indicesBufferPos++] = (short)(vertexCount + 3);
        indicesBuffer[indicesBufferPos++] = (short)(vertexCount + 2);

        vertexCount += 4;
    }

    /**
     * Texture Cell
     */
    private static final float TEX_CELL = 66.0f / 1024.0f;
    /**
     * Texture 1 pixel
     */
    private static final float TEX_1PX = 1.1f / 1024.0f;
    /**
     * Texture Size
     */
    private static final float TEX_SIZE = 63.8f / 1024.0f;

    /**
     * Draw Quadrant
     * @param texNum Texture ID
     */
    public static void drawQuad(int texNum) {
        float sx = ((float)(texNum % 15) * TEX_CELL) + TEX_1PX;
        float sy = ((float)(texNum / 15) * TEX_CELL) + TEX_1PX;
        float ex = sx + TEX_SIZE;
        float ey = sy + TEX_SIZE;

        u1 = sx;
        v1 = ey;

        u2 = sx;
        v2 = sy;

        u3 = ex;
        v3 = sy;

        u4 = ex;
        v4 = ey;

        drawQuad();
    }

    /**
     * Draw Quadrant Flip Horizontally
     * @param texNum Texture ID
     */
    @SuppressWarnings("unused")
    public static void drawQuadFlipLR(int texNum) {
        float sx = ((float)(texNum % 15) * TEX_CELL) + TEX_1PX;
        float sy = ((float)(texNum / 15) * TEX_CELL) + TEX_1PX;
        float ex = sx + TEX_SIZE;
        float ey = sy + TEX_SIZE;

        u1 = ex;
        v1 = ey;

        u2 = ex;
        v2 = sy;

        u3 = sx;
        v3 = sy;

        u4 = sx;
        v4 = ey;

        drawQuad();
    }

    /**
     * Texture Cell Monster
     */
    private static final float TEX_CELL_MON = 128.0f / 1024.0f;
    /**
     * Texture Size Monster
     */
    private static final float TEX_SIZE_MON = 127.8f / 1024.0f;

    /**
     * Draw Quadrant Monster
     * @param texNum Texture ID
     */
    public static void drawQuadMon(int texNum) {
        float sx = (float)(texNum % 8) * TEX_CELL_MON;
        float sy = (float)(texNum / 8) * TEX_CELL_MON;
        float ex = sx + TEX_SIZE_MON;
        float ey = sy + TEX_SIZE_MON;

        u1 = sx;
        v1 = ey;

        u2 = sx;
        v2 = sy;

        u3 = ex;
        v3 = sy;

        u4 = ex;
        v4 = ey;

        drawQuad();
    }

    /**
     * Draw Line
     */
    @SuppressWarnings("unused")
    public static void drawLine() {
        lineVertexBuffer[lineVertexBufferPos++] = x1;
        lineVertexBuffer[lineVertexBufferPos++] = y1;

        lineVertexBuffer[lineVertexBufferPos++] = x2;
        lineVertexBuffer[lineVertexBufferPos++] = y2;

        //

        lineColorsBuffer[lineColorsBufferPos++] = r1;
        lineColorsBuffer[lineColorsBufferPos++] = g1;
        lineColorsBuffer[lineColorsBufferPos++] = b1;
        lineColorsBuffer[lineColorsBufferPos++] = a1;

        lineColorsBuffer[lineColorsBufferPos++] = r2;
        lineColorsBuffer[lineColorsBufferPos++] = g2;
        lineColorsBuffer[lineColorsBufferPos++] = b2;
        lineColorsBuffer[lineColorsBufferPos++] = a2;

        //

        lineVertexCount += 2;
    }

    /**
     * Draw Line
     * @param lx1 Start Position X
     * @param ly1 Start Position Y
     * @param lx2 End Position X
     * @param ly2 End Position Y
     */
    public static void drawLine(float lx1, float ly1, float lx2, float ly2) {
        lineVertexBuffer[lineVertexBufferPos++] = lx1;
        lineVertexBuffer[lineVertexBufferPos++] = ly1;

        lineVertexBuffer[lineVertexBufferPos++] = lx2;
        lineVertexBuffer[lineVertexBufferPos++] = ly2;

        //

        lineColorsBuffer[lineColorsBufferPos++] = r1;
        lineColorsBuffer[lineColorsBufferPos++] = g1;
        lineColorsBuffer[lineColorsBufferPos++] = b1;
        lineColorsBuffer[lineColorsBufferPos++] = a1;

        lineColorsBuffer[lineColorsBufferPos++] = r2;
        lineColorsBuffer[lineColorsBufferPos++] = g2;
        lineColorsBuffer[lineColorsBufferPos++] = b2;
        lineColorsBuffer[lineColorsBufferPos++] = a2;

        //

        lineVertexCount += 2;
    }

    /**
     * Set Quadrant Color
     * @param r Red Value 0-255
     * @param g Green Value 0-255
     * @param b Blue Value 0-255
     */
    public static void setQuadRGB(float r, float g, float b) {
        r1 = r;
        g1 = g;
        b1 = b;

        r2 = r;
        g2 = g;
        b2 = b;

        r3 = r;
        g3 = g;
        b3 = b;

        r4 = r;
        g4 = g;
        b4 = b;
    }

    /**
     * Set Quadrant Alpha
     * @param a Alpha Value
     */
    @SuppressWarnings("unused")
    public static void setQuadA(float a) {
        a1 = a;
        a2 = a;
        a3 = a;
        a4 = a;
    }

    /**
     * Set Quadrant Color and Alpha
     * @param r Red Value 0-255
     * @param g Green Value 0-255
     * @param b Blue Value 0-255
     * @param a Alpha Value
     */
    public static void setQuadRGBA(float r, float g, float b, float a) {
        r1 = r;
        g1 = g;
        b1 = b;
        a1 = a;

        r2 = r;
        g2 = g;
        b2 = b;
        a2 = a;

        r3 = r;
        g3 = g;
        b3 = b;
        a3 = a;

        r4 = r;
        g4 = g;
        b4 = b;
        a4 = a;
    }

    /**
     * Set Line Colors
     * @param r Red Value 0-255
     * @param g Green Value 0-255
     * @param b Blue Value 0-255
     */
    @SuppressWarnings("unused")
    public static void setLineRGB(float r, float g, float b) {
        r1 = r;
        g1 = g;
        b1 = b;

        r2 = r;
        g2 = g;
        b2 = b;
    }

    /**
     * Set line Transparency
     * @param a Alpha Value
     */
    @SuppressWarnings("unused")
    public static void setLineA(float a) {
        a1 = a;
        a2 = a;
    }

    /**
     * Sets Line Colors and Transparency
     * @param r Red Value 0-255
     * @param g Green Value 0-255
     * @param b Blue Value 0-255
     * @param a Alpha Value
     */
    public static void setLineRGBA(float r, float g, float b, float a) {
        r1 = r;
        g1 = g;
        b1 = b;
        a1 = a;

        r2 = r;
        g2 = g;
        b2 = b;
        a2 = a;
    }

    /**
     * Set Quadrant Orthogonal Coordinates
     * @param _x1 Start Coordinate X
     * @param _y1 Start Coordinate Y
     * @param _x2 End Coordinate X
     * @param _y2 End Coordinate Y
     */
    public static void setQuadOrthoCoords(float _x1, float _y1, float _x2, float _y2) {
        Renderer.x1 = _x1;
        Renderer.y1 = _y1;
        Renderer.z1 = 0.0f;

        Renderer.x2 = _x1;
        Renderer.y2 = _y2;
        Renderer.z2 = 0.0f;

        Renderer.x3 = _x2;
        Renderer.y3 = _y2;
        Renderer.z3 = 0.0f;

        Renderer.x4 = _x2;
        Renderer.y4 = _y1;
        Renderer.z4 = 0.0f;
    }
}
