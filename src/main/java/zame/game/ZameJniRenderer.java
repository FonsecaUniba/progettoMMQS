package zame.game;

@SuppressWarnings("WeakerAccess")
public final class ZameJniRenderer {
   //RIMOZIONE DI QUESTA CHIAMATA PORTA A CRASH DOPO NEW GAME
    static {
        System.loadLibrary("zameJniRenderer");
    }

    private ZameJniRenderer() {
    }

    public static void callRenderTriangles(float[] vertexBuffer,
                                           float[] colorsBuffer,
                                           float[] textureBuffer,
                                           short[] indicesBuffer,
                                           int indicesBufferPos){
        renderTriangles(vertexBuffer, colorsBuffer, textureBuffer, indicesBuffer, indicesBufferPos);
    }

    public static void callRenderLines(float[] vertexBuffer, float[] colorsBuffer, int vertexCount){
        renderLines(vertexBuffer, colorsBuffer, vertexCount);
    }

    private static native void renderTriangles(float[] vertexBuffer,
            float[] colorsBuffer,
            float[] textureBuffer,
            short[] indicesBuffer,
            int indicesBufferPos);

    private static native void renderLines(float[] vertexBuffer, float[] colorsBuffer, int vertexCount);
}
