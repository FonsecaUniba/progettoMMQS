package zame.game.engine;

@SuppressWarnings("WeakerAccess")
public class PortalTracer {
    public static class Wall {
        public int cellX;
        public int cellY;
        public int fromX;
        public int fromY;
        public int toX;
        public int toY;
        public int texture;
    }

    public static class TouchedCell {
        public int x;
        public int y;
    }

    // +----> x
    // |
    // v
    // y

    // pts    sides
    //
    // 1 | 0  /-0-\
    // --+--  1   3
    // 2 | 3  \-2-/

    public static final float PI_M2F = (float)(Math.PI * 2.0);
    public static final float INFINITY = 0.000000001f;
    public static final int[] X_ADD = { 1, 0, 0, 1 };
    public static final int[] Y_ADD = { 0, 0, 1, 1 };

    // cell additions used by other classes
    public static final int[] X_CELL_ADD = { 0, -1, 0, 1 };
    public static final int[] Y_CELL_ADD = { -1, 0, 1, 0 };

    private static final int MAX_WALLS = 1024;
    private static final int MAX_TOUCHED_CELLS = 2048;

    private int levelWidth;
    private int levelHeight;
    private int[][] level;
    private float heroX;
    private float heroY;
    private int[][] drawedWalls = new int[Level.MAX_HEIGHT][Level.MAX_WIDTH];

    public Wall[] walls = new Wall[MAX_WALLS];
    public TouchedCell[] touchedCells = new TouchedCell[MAX_TOUCHED_CELLS];
    public boolean[][] touchedCellsMap = new boolean[Level.MAX_HEIGHT][Level.MAX_WIDTH];

    public int wallsCount;
    public int touchedCellsCount;

    public PortalTracer() {
        for (int i = 0; i < MAX_WALLS; i++) {
            walls[i] = new Wall();
        }

        for (int i = 0; i < MAX_TOUCHED_CELLS; i++) {
            touchedCells[i] = new TouchedCell();
        }
    }

    private void addWallToDraw(int cellX, int cellY, int side, int texture) {
        int mask = 2 << side;

        if ((wallsCount >= MAX_WALLS) || ((drawedWalls[cellY][cellX] & mask) != 0)) {
            return;
        }

        drawedWalls[cellY][cellX] |= mask;
        Wall wall = walls[wallsCount++];

        wall.cellX = cellX;
        wall.cellY = cellY;
        wall.fromX = cellX + X_ADD[side];
        wall.fromY = cellY + Y_ADD[side];
        wall.toX = cellX + X_ADD[(side + 1) % 4];
        wall.toY = cellY + Y_ADD[(side + 1) % 4];
        wall.texture = texture;
    }

    public static float getAngle(float dx, float dy) {
        float l = (float)Math.sqrt((dx * dx) + (dy * dy));
        float a = (float)Math.acos(dx / ((l < INFINITY) ? INFINITY : l));

        return ((dy < 0) ? a : (PI_M2F - a));
    }

    private float angleDiff(float fromAngle, float toAngle) {
        if (fromAngle > toAngle) {
            return ((toAngle - fromAngle) + PI_M2F);
        } else {
            return (fromAngle - toAngle);
        }
    }

    private int tToSide = -1;
    private int tFromSide = -1;

    private static boolean isEquals(int a, int b)
    {
        return Math.abs(a-b) < 1e-6;
    }

    private void checkVis(boolean vis_0, boolean vis_1, boolean vis_2, boolean vis_3){
        if (vis_0 && vis_1) {
            tToSide = 0;
            tFromSide = 1;
        } else if (vis_1 && vis_2) {
            tToSide = 1;
            tFromSide = 2;
        } else if (vis_2 && vis_3) {
            tToSide = 2;
            tFromSide = 3;
        } else {
            checkVis2(vis_0, vis_1, vis_2, vis_3);
        }
    }

    private void checkVis2(boolean vis_0, boolean vis_1, boolean vis_2, boolean vis_3){
        if (vis_3 && vis_0) {
            tToSide = 3;
            tFromSide = 0;
        } else if (vis_0) {
            tToSide = 0;
            tFromSide = 0;
        } else if (vis_1) {
            tToSide = 1;
            tFromSide = 1;
        } else if (vis_2) {
            tToSide = 2;
            tFromSide = 2;
        } else if (vis_3) {
            tToSide = 3;
            tFromSide = 3;
        }
    }

    private boolean getVis0(boolean includeDoors, float dy, int y, int x){
        if(includeDoors){
            return (dy > 0) && (y > 0) && (level[y - 1][x] <= 0);
        } else {
            return (dy > 0) && (y > 0) && (isEquals(level[y - 1][x],0));
        }
    }

    private boolean getVis1(boolean includeDoors, float dx, int y, int x){
        if(includeDoors){
            return (dx > 0) && (x > 0) && (level[y][x - 1] <= 0);
        } else {
            return (dx > 0) && (x > 0) && (isEquals(level[y][x - 1],0));
        }
    }

    private boolean getVis2(boolean includeDoors, float dy, int y, int x){
        if(includeDoors){
            return (dy < 0) && (y < (levelHeight - 1)) && (level[y + 1][x] <= 0);
        } else {
            return (dy < 0) && (y < (levelHeight - 1)) && (isEquals(level[y + 1][x],0));
        }
    }

    private boolean getVis3(boolean includeDoors, float dx, int y, int x){
        if(includeDoors){
            return (dx < 0) && (x < (levelWidth - 1)) && (level[y][x + 1] <= 0);
        } else {
            return (dx < 0) && (x < (levelWidth - 1)) && (isEquals(level[y][x + 1],0));
        }
    }

    @SuppressWarnings("MagicNumber")
    private void addWallBlock(int x, int y, boolean includeDoors) {
        tToSide = -1;
        tFromSide = -1;

        float dy = ((float)y + 0.5f) - heroY;
        float dx = ((float)x + 0.5f) - heroX;

        boolean vis_0=getVis0(includeDoors, dy, y, x);
        boolean vis_1=getVis1(includeDoors, dx, y, x);
        boolean vis_2=getVis2(includeDoors, dy, y, x);
        boolean vis_3=getVis3(includeDoors, dx, y, x);



        checkVis(vis_0, vis_1, vis_2, vis_3);

        if ((tToSide >= 0) && (level[y][x] > 0)) {
            for (int i = tFromSide; i != ((tToSide + 3) % 4); i = (i + 3) % 4) {
                addWallToDraw(x, y, i, level[y][x]);
            }
        }
    }

    private int getFromX(int fromX, float fromAngle){
        if (fromAngle < (0.5 * Math.PI)) {
            fromX++;
        } else if (fromAngle >= (0.75 * Math.PI)) {
            if (fromAngle < (1.5 * Math.PI)) {
                fromX--;
            } else if (fromAngle >= (1.75 * Math.PI)) {
                fromX++;
            }
        }

        return fromX;
    }

    private int getFromY(int fromY, float fromAngle){
        if (fromAngle >= (0.25 * Math.PI)) {
            if (fromAngle < Math.PI) {
                fromY--;
            } else if (fromAngle >= (1.25 * Math.PI)) {
                fromY++;
            }
        }

        return fromY;
    }

    private int getToX(int toX, float toAngle){
        if (toAngle > (1.5 * Math.PI)) {
            toX++;
        } else if (toAngle <= (1.25 * Math.PI)) {
            if (toAngle > (0.5 * Math.PI)) {
                toX--;
            } else if (toAngle <= (0.25 * Math.PI)) {
                toX++;
            }
        }

        return toX;
    }

    private int getToY(int toY, float toAngle){
        if (toAngle <= (1.75 * Math.PI)) {
            if (toAngle > Math.PI) {
                toY++;
            } else if (toAngle <= (0.75 * Math.PI)) {
                toY--;
            }
        }

        return toY;
    }

    private boolean isInSight(boolean visible, int oa, int ob){
        return visible || ((oa > 0) && (ob > 0));
    }

    private int[] updateFromValues(int fromX, int fromY, int toX, int toY){
        if (fromY > toY) {
            if (fromX >= toX) {
                fromY--;
            } else {
                fromX++;
            }
        } else if (fromY == toY) {
            if (fromX > toX) {
                fromX--;
            } else {
                fromX++;
            }
        } else {
            if (fromX <= toX) {
                fromY++;
            } else {
                fromX--;
            }
        }

        return new int[] {fromX, fromY};
    }

    private int[] getOAB(int oa, int ob, float tf, float tt){
        if (tf >= 0) {
            oa++;
        } else if (tt <= 0) {
            ob++;
        }

        return new int[] {oa, ob};
    }

    private int[] checkToXY(int x, int y, int toX, int toY){
        if (y > toY) {
            if (x >= toX) {
                y--;
            } else {
                x++;
            }
        } else if (isEquals(y,toY)) {
            if (x > toX) {
                x--;
            } else {
                x++;
            }
        } else {
            if (x <= toX) {
                y++;
            } else {
                x--;
            }
        }

        return new int[] {x, y};
    }

    private void updateTouchedCells(int x, int y){
        if (!touchedCellsMap[y][x])    // just for case
        {
            touchedCellsMap[y][x] = true;

            if ((level[y][x] <= 0) && (touchedCellsCount < MAX_TOUCHED_CELLS)) {
                touchedCells[touchedCellsCount].x = x;
                touchedCells[touchedCellsCount].y = y;
                touchedCellsCount++;
            }
        }
    }

    private int[] gettoXY(int fromX, int fromY, int toX, int toY){
        if (fromY > toY) {
            if (fromX > toX) {
                toX++;
            } else {
                toY++;
            }
        } else if (fromY == toY) {
            if (fromX > toX) {
                toX++;
            } else {
                toX--;
            }
        } else {
            if (fromX < toX) {
                toX--;
            } else {
                toY--;
            }
        }

        return new int[] {toX, toY};
    }

    private boolean isSamePosition(int fromX, int fromY, int toX, int toY){
        return (fromX == toX) && (fromY == toY);
    }

    private boolean isTFTT(float tf, float tt){
        return (tf <= 0) && (tt >= 0);
    }

    private float[] updatePortTo(float portToX, float portToY, int x, int y){

        if (tToSide >= 0) {
            portToX = (float)x + X_ADD[(tFromSide + 1) % 4];
            portToY = (float)y + Y_ADD[(tFromSide + 1) % 4];
        }

        return new float[] {portToX, portToY};
    }

    private boolean updatePortal(int x, int y, int fromX, int fromY){
        return (((!isEquals(x,fromX))) || (!isEquals(y,fromY)));
    }

    private void tracePortal(boolean portal, float portToX, float portToY, int lastX, int lastY, int fromX, int fromY, float fromAngle, float toAngle){
        if (portal) {
            float innerToAngle = ((portToX >= 0)
                    ? getAngle(portToX - heroX, portToY - heroY) /* + ANG_CORRECT (leaved here just for case) */
                    : toAngle);

            if (angleDiff(fromAngle, innerToAngle) < Math.PI) {
                traceCell(fromX, fromY, fromAngle, lastX, lastY, innerToAngle);
            }
        }
    }

    private float updateFromAngle(float portFromX, float portFromY, float fromAngle){
        if (portFromX >= 0) {
            fromAngle = getAngle(portFromX - heroX,
                    portFromY - heroY) /* - ANG_CORRECT (leaved here just for case) */;
        }

        return fromAngle;
    }

    private boolean equalToPos(int x, int y, int toX, int toY){
        return (isEquals(x,toX)) && (isEquals(y,toY));
    }

    private float[] updatePortFrom(float portFromX, float portFromY, int x, int y){
        if (tFromSide >= 0) {
            portFromX = (float)x + X_ADD[tToSide];
            portFromY = (float)y + Y_ADD[tToSide];
        }
        return new float[] {portFromX, portFromY};
    }

    private boolean updateRepeat(boolean wall, boolean repeat){
        if (wall) {
            repeat = false;
        }

        return repeat;
    }

    private boolean checkAngleDiff(float fromAngle, float toAngle, boolean repeat){
        if (angleDiff(fromAngle, toAngle) > Math.PI) {
            repeat = false;
        }

        return repeat;
    }

    private int[] checkTouchedCellsMap(int value1, int value2, float fromDx, float fromDy, float toDx, float toDy, boolean visible, int oa, int ob){
        if (!touchedCellsMap[value2][value1]) {
            for (int i = 0; i < 4; i++) {
                float dx = ((float)value1 + X_ADD[i]) - heroX;
                float dy = ((float)value2 + Y_ADD[i]) - heroY;

                float tf = (dx * fromDy) + (dy * fromDx);
                float tt = (dx * toDy) + (dy * toDx);

                if (isTFTT(tf, tt)) {
                    visible = true;
                    break;
                } else {
                    int[] result = getOAB(oa, ob, tf, tt);
                    oa = result[0];
                    ob = result[1];
                }
            }
        }

        return new int[] {oa, ob, ((visible) ? 0 : 1)};
    }

    private int[] iterateCells1(boolean repeat, int fromX, int fromY, int toX, int toY, float fromDx, float fromDy, float toDx, float toDy){
        while (true){
            boolean visible = false;
            int oa = 0;
            int ob = 0;

            int[] result = checkTouchedCellsMap(fromX, fromY, fromDx, fromDy, toDx, toDy, false, oa, ob);
            oa = result[0];
            ob = result[1];
            visible = (result[2] == 0);

            // if at least one point is between fromAngle and toAngle
            // or fromAngle and toAngle is between cell points
            if (isInSight(visible, oa, ob)) {
                break;
            }

            if (isSamePosition(fromX, fromY, toX, toY)) {
                repeat = false;
                break;
            } else {
                result = updateFromValues(fromX, fromY, toX, toY);
                fromX = result[0];
                fromY = result[1];
            }
        }

        return new int[] {fromX, fromY, ((repeat) ? 0 : 1)};
    }

    private int[] iterateCells2(boolean repeat, int fromX, int fromY, int toX, int toY, float fromDx, float fromDy, float toDx, float toDy){
        while (true){
            boolean visible = false;
            int oa = 0;
            int ob = 0;

            int[] result = checkTouchedCellsMap(toX, toY, fromDx, fromDy, toDx, toDy, visible, oa, ob);
            oa = result[0];
            ob = result[1];
            visible = (result[2] == 0);

            // if at least one point is between fromAngle and toAngle
            // or fromAngle and toAngle is between cell points
            if (isInSight(visible, oa, ob)) {
                break;
            }

            if (isSamePosition(fromX, fromY, toX, toY)) {
                repeat = false;
                break;
            } else {
                result = gettoXY(fromX, fromY, toX, toY);
                toX = result[0];
                toY = result[1];
            }
        }

        return new int[] {toX, toY, ((repeat) ? 0 : 1)};
    }

    private float[] checkPortX(boolean repeat, float toAngle, float fromAngle, float portToX, float portToY){
        if (portToX >= 0) {
            toAngle = getAngle(portToX - heroX,
                    portToY - heroY) /* + ANG_CORRECT (leaved here just for case) */;

            repeat = checkAngleDiff(fromAngle, toAngle, repeat);
        }
        return new float[] {toAngle, ((repeat) ? 0 : 2)};
    }

    @SuppressWarnings({ "MagicNumber", "ConstantConditions" })
    private void traceCell(int fromX, int fromY, float fromAngle, int toX, int toY, float toAngle) {
        boolean repeat = true;

        do {
            float fromDx = (float)Math.cos(fromAngle);
            float fromDy = (float)Math.sin(fromAngle);
            float toDx = (float)Math.cos(toAngle);
            float toDy = (float)Math.sin(toAngle);

            fromX = getFromX(fromX, fromAngle);

            fromY = getFromY(fromY, fromAngle);

            toX = getToX(toX, toAngle);

            toY = getToY(toY, toAngle);

            int[] result = iterateCells1(repeat, fromX, fromY, toX, toY, fromDx, fromDy, toDx, toDy);
            fromX = result[0];
            fromY = result[1];
            repeat = (result[2] == 0);

            result = iterateCells2(repeat, fromX, fromY, toX, toY, fromDx, fromDy, toDx, toDy);
            toX = result[0];
            toY = result[1];
            repeat = (result[2] == 0);

            int x = fromX;
            int y = fromY;
            int prevX = fromX;
            int prevY = fromY;
            int lastX = fromX;
            int lastY = fromY;
            float portFromX = -1;
            float portFromY = -1;
            float portToX = -1;
            float portToY = -1;
            boolean wall = false;
            boolean portal = false;

            while (true){
                updateTouchedCells(x, y);

                if (isEquals(level[y][x],0)) {
                    if (wall) {
                        tracePortal(portal, portToX, portToY, lastX, lastY, fromX, fromY, fromAngle, toAngle);

                        fromAngle = updateFromAngle(portFromX, portFromY, fromAngle);

                        fromX = x;
                        fromY = y;
                        wall = false;
                        portal = false;
                    }
                } else {
                    addWallBlock(x, y, level[y][x] > 0); // -1 = closed door

                    if (!wall) {
                        lastX = prevX;
                        lastY = prevY;
                        wall = true;
                        portal = updatePortal(x, y, fromX, fromY);

                        float[] floatResult = updatePortTo(portToX, portToY, x, y);
                        portToX = floatResult[0];
                        portToY = floatResult[1];
                    }

                    float[] floatResult = updatePortFrom(portFromX, portFromY, x, y);
                    portFromX = floatResult[0];
                    portFromY = floatResult[1];
                }

                if (equalToPos(x, y, toX, toY)) {
                    if (portal) {
                        toX = lastX;
                        toY = lastY;


                        float[] floatResult = checkPortX(repeat, toAngle, fromAngle, portToX, portToY);
                        toAngle = floatResult[0];
                        repeat = (floatResult[1] < 1);
                    } else {
                        repeat = updateRepeat(wall, repeat);
                    }

                    break;
                }

                prevX = x;
                prevY = y;

                result = checkToXY(x, y, toX, toY);
                x = result[0];
                y = result[1];
            }
        } while (repeat);
    }

    private int[] checkFromAngle(float fromAngle, int tx, int ty){
        if (fromAngle < (0.25 * Math.PI)) {
            tx++;
            ty++;
        } else if (fromAngle < (0.5 * Math.PI)) {
            tx++;
        } else if (fromAngle < (0.75 * Math.PI)) {
            tx++;
            ty--;
        } else if (fromAngle < Math.PI) {
            ty--;
        } else if (fromAngle < (1.25 * Math.PI)) {
            tx--;
            ty--;
        } else if (fromAngle < (1.5 * Math.PI)) {
            tx--;
        } else if (fromAngle < (1.75 * Math.PI)) {
            tx--;
            ty++;
        } else {
            ty++;
        }

        return new int[] {tx, ty};
    }

    private int[] checkToAngle(float toAngle, int tx, int ty){
        if (toAngle > (1.75 * Math.PI)) {
            tx++;
            ty--;
        } else if (toAngle > (1.5 * Math.PI)) {
            tx++;
        } else if (toAngle > (1.25 * Math.PI)) {
            tx++;
            ty++;
        } else if (toAngle > Math.PI) {
            ty++;
        } else if (toAngle > (0.75 * Math.PI)) {
            tx--;
            ty++;
        } else if (toAngle > (0.5 * Math.PI)) {
            tx--;
        } else if (toAngle > (0.25 * Math.PI)) {
            tx--;
            ty--;
        } else {
            ty--;
        }

        return new int[] {tx, ty};
    }

    // halfFov must be between (10 * PI / 180) and (45 * PI / 180)
    @SuppressWarnings("MagicNumber")
    public void trace(float x, float y, float heroAngle, float halfFov) {
        level = State.wallsMap;
        levelWidth = State.levelWidth;
        levelHeight = State.levelHeight;

        float fromAngle = heroAngle - halfFov;
        float toAngle = heroAngle + halfFov;

        if (fromAngle < 0) {
            fromAngle += PI_M2F;
        } else {
            fromAngle %= PI_M2F;
        }

        if (toAngle < 0) {
            toAngle += PI_M2F;
        } else {
            toAngle %= PI_M2F;
        }

        for (int i = 0; i < levelHeight; i++) {
            for (int j = 0; j < levelWidth; j++) {
                touchedCellsMap[i][j] = false;
                drawedWalls[i][j] = 0;
            }
        }

        heroX = x;
        heroY = y;
        wallsCount = 0;
        touchedCellsCount = 0;

        touchedCellsMap[floatToInt(y)][floatToInt(x)] = true;
        touchedCells[touchedCellsCount].x = floatToInt(x);
        touchedCells[touchedCellsCount].y = floatToInt(y);
        touchedCellsCount++;

        int tx = floatToInt(x);
        int ty = floatToInt(y);

        int[] result = checkFromAngle(fromAngle, tx, ty);
        tx = result[0];
        ty = result[1];

        if (level[ty][tx] > 0) {
            addWallBlock(tx, ty, true);
        } else {
            touchedCellsMap[ty][tx] = true;
            touchedCells[touchedCellsCount].x = tx;
            touchedCells[touchedCellsCount].y = ty;
            touchedCellsCount++;
        }

        tx = floatToInt(x);
        ty = floatToInt(y);

        result = checkToAngle(toAngle, tx, ty);
        tx = result[0];
        ty = result[1];

        if (level[ty][tx] > 0) {
            addWallBlock(tx, ty, true);
        } else {
            touchedCellsMap[ty][tx] = true;
            touchedCells[touchedCellsCount].x = tx;
            touchedCells[touchedCellsCount].y = ty;
            touchedCellsCount++;
        }

        traceCell(floatToInt(x), floatToInt(y), fromAngle, floatToInt(x), floatToInt(y), toAngle);
    }

    private static int floatToInt(float a) {
        if (a < Integer.MIN_VALUE || a > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Value not castable");
        }
        return (int) a;
    }

    private static float intToFloat(int a)
    {
        if (a < Float.MIN_VALUE || a > Float.MAX_VALUE) {
            throw new IllegalArgumentException("Value not castable");
        }
        return (float) a;
    }
}
