package zame.game;

/**
 * Class representing the link between Java and Native Code
 */
@SuppressWarnings("WeakerAccess")
public final class ZameJniRenderer {
   /*
     * Calls the Java Native Code Renderer Library
     * Removing this causes a game crash
     */
    static {
        System.loadLibrary("zameJniRenderer");
    }

    /**
     * Class constructor
     */
    private ZameJniRenderer() {
    }

    /**
     * Public front-end to call the native code
     * @param vertexBuffer Vertex to draw
     * @param colorsBuffer Colors to draw
     * @param textureBuffer Textures to draw
     * @param indicesBuffer Index
     * @param indicesBufferPos Index Position
     */
    public static void callRenderTriangles(float[] vertexBuffer,
                                           float[] colorsBuffer,
                                           float[] textureBuffer,
                                           short[] indicesBuffer,
                                           int indicesBufferPos){
        renderTriangles(vertexBuffer, colorsBuffer, textureBuffer, indicesBuffer, indicesBufferPos);
    }

    /**
     * Public front-end to call the native code
     * @param vertexBuffer Vertex to draw
     * @param colorsBuffer Colors to draw
     * @param vertexCount Vertex count
     */
    public static void callRenderLines(float[] vertexBuffer, float[] colorsBuffer, int vertexCount){
        renderLines(vertexBuffer, colorsBuffer, vertexCount);
    }

    /**
     * Calls the native C code to render Triangles
     * @param vertexBuffer Vertex to draw
     * @param colorsBuffer Colors to draw
     * @param textureBuffer Textures to draw
     * @param indicesBuffer Index
     * @param indicesBufferPos Index Position
     */
    private static native void renderTriangles(float[] vertexBuffer,
            float[] colorsBuffer,
            float[] textureBuffer,
            short[] indicesBuffer,
            int indicesBufferPos);

    /**
     * Calls the native C code to render lines
     * @param vertexBuffer Vertex to draw
     * @param colorsBuffer Colors to draw
     * @param vertexCount Vertex count
     */
    private static native void renderLines(float[] vertexBuffer, float[] colorsBuffer, int vertexCount);
}
