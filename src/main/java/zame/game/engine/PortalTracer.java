package zame.game.engine;

/**
 * Class representing a PortalTracer
 */
@SuppressWarnings("WeakerAccess")
public class PortalTracer {
    /**
     * Class representing a Wall
     */
    public static class Wall {
        /**
         * Cell Position X of Wall
         */
        public int cellX;
        /**
         * Cell Position Y of Wall
         */
        public int cellY;
        /**
         * Starting Position X of Wall
         */
        public int fromX;
        /**
         * Starting Position Y of Wall
         */
        public int fromY;
        /**
         * End Position X of Wall
         */
        public int toX;
        /**
         * End Position Y of Wall
         */
        public int toY;
        /**
         * Wall Texture ID
         */
        public int texture;
    }

    /**
     * Class Representing a TouchedCell
     */
    public static class TouchedCell {
        /**
         * Cell Position X
         */
        public int x;
        /**
         * Cell Position Y
         */
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

    /**
     * Two times Pi
     */
    public static final float PI_M2F = (float)(Math.PI * 2.0);
    /**
     * Constant for infinitesimal value
     */
    public static final float INFINITY = 0.000000001f;
    /**
     * X Addition to Cell
     */
    public static final int[] X_ADD = { 1, 0, 0, 1 };
    /**
     * Y Addition to Cell
     */
    public static final int[] Y_ADD = { 0, 0, 1, 1 };

    // cell additions used by other classes
    /**
     * X Cell Addition for Other classes
     */
    public static final int[] X_CELL_ADD = { 0, -1, 0, 1 };
    /**
     * Y Cell Addition for Other Classes
     */
    public static final int[] Y_CELL_ADD = { -1, 0, 1, 0 };

    /**
     * Maximum Number of Walls Allowed
     */
    private static final int MAX_WALLS = 1024;
    /**
     * Maximum Number of Touched Cells Allowed
     */
    private static final int MAX_TOUCHED_CELLS = 2048;

    /**
     * Level Width
     */
    private int levelWidth;
    /**
     * Level Height
     */
    private int levelHeight;
    /**
     * Level Map
     */
    private int[][] level;
    /**
     * Hero Position X
     */
    private float heroX;
    /**
     * Hero Position Y
     */
    private float heroY;
    /**
     * Drawn Walls Map
     */
    private int[][] drawedWalls = new int[Level.MAX_HEIGHT][Level.MAX_WIDTH];

    /**
     * Array of Walls
     */
    public Wall[] walls = new Wall[MAX_WALLS];
    /**
     * Array of Touched Cells
     */
    public TouchedCell[] touchedCells = new TouchedCell[MAX_TOUCHED_CELLS];
    /**
     * Touched Cells Map
     */
    public boolean[][] touchedCellsMap = new boolean[Level.MAX_HEIGHT][Level.MAX_WIDTH];

    /**
     * Number of Walls
     */
    public int wallsCount;
    /**
     * Number of Touched Cells
     */
    public int touchedCellsCount;

    /**
     * Class Constructor
     */
    public PortalTracer() {
        for (int i = 0; i < MAX_WALLS; i++) {
            walls[i] = new Wall();
        }

        for (int i = 0; i < MAX_TOUCHED_CELLS; i++) {
            touchedCells[i] = new TouchedCell();
        }
    }

    /**
     * Add Wall to Drawable Area
     * @param cellX Cell Position X
     * @param cellY Cell Position Y
     * @param side Wall Side
     * @param texture Wall Texture ID
     */
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

    /**
     * Returns Updated Angle
     * @param dx Derivate X
     * @param dy Derivate Y
     * @return a = acos(dx / ((l < INFINITY) ? INFINITY : l)) if dy<0, PI_M2F-a otherwise
     */
    public static float getAngle(float dx, float dy) {
        float l = (float)Math.sqrt((dx * dx) + (dy * dy));
        float a = (float)Math.acos(dx / ((l < INFINITY) ? INFINITY : l));

        return ((dy < 0) ? a : (PI_M2F - a));
    }

    /**
     * Get Angle Difference
     * @param fromAngle Starting Angle
     * @param toAngle Ending Angle
     * @return Angle difference
     */
    private float angleDiff(float fromAngle, float toAngle) {
        if (fromAngle > toAngle) {
            return ((toAngle - fromAngle) + PI_M2F);
        } else {
            return (fromAngle - toAngle);
        }
    }

    /**
     * Texture To Side
     */
    private int tToSide = -1;
    /**
     * Texture From Side
     */
    private int tFromSide = -1;

    /**
     * Checks if two ints are equal
     * @param a first int value
     * @param b second int value
     * @return true if a is equal to be, false otherwise
     */
    private static boolean isEquals(int a, int b)
    {
        return Math.abs(a-b) < 1e-6;
    }

    /**
     * Checks Vis Variables values
     * @param vis_0 First value
     * @param vis_1 Second value
     * @param vis_2 Third value
     * @param vis_3 Fourth value
     */
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

    /**
     * Checks Vis Variables values
     * @param vis_0 First value
     * @param vis_1 Second value
     * @param vis_2 Third value
     * @param vis_3 Fourth value
     */
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

    /**
     * Get vis_0 value
     * @param includeDoors Do we include Doors?
     * @param dy Derivate Y
     * @param y Position Y
     * @param x Position X
     * @return True or false
     */
    private boolean getVis0(boolean includeDoors, float dy, int y, int x){
        if(includeDoors){
            return (dy > 0) && (y > 0) && (level[y - 1][x] <= 0);
        } else {
            return (dy > 0) && (y > 0) && (isEquals(level[y - 1][x],0));
        }
    }

    /**
     * Get vis_1 value
     * @param includeDoors Do we include Doors?
     * @param dx Derivate X
     * @param y Position Y
     * @param x Position X
     * @return True or false
     */
    private boolean getVis1(boolean includeDoors, float dx, int y, int x){
        if(includeDoors){
            return (dx > 0) && (x > 0) && (level[y][x - 1] <= 0);
        } else {
            return (dx > 0) && (x > 0) && (isEquals(level[y][x - 1],0));
        }
    }

    /**
     * Get vis_2 value
     * @param includeDoors Do we include Doors?
     * @param dy Derivate Y
     * @param y Position Y
     * @param x Position X
     * @return True or False
     */
    private boolean getVis2(boolean includeDoors, float dy, int y, int x){
        if(includeDoors){
            return (dy < 0) && (y < (levelHeight - 1)) && (level[y + 1][x] <= 0);
        } else {
            return (dy < 0) && (y < (levelHeight - 1)) && (isEquals(level[y + 1][x],0));
        }
    }

    /**
     * Get vis_3 value
     * @param includeDoors Do we include Doors?
     * @param dx Derivate X
     * @param y Position Y
     * @param x Position X
     * @return True or False
     */
    private boolean getVis3(boolean includeDoors, float dx, int y, int x){
        if(includeDoors){
            return (dx < 0) && (x < (levelWidth - 1)) && (level[y][x + 1] <= 0);
        } else {
            return (dx < 0) && (x < (levelWidth - 1)) && (isEquals(level[y][x + 1],0));
        }
    }

    /**
     * Add Wall Block
     * @param x Position X
     * @param y Position Y
     * @param includeDoors Do we Include Doors?
     */
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

    /**
     * Updates fromX value
     * @param fromX Current fromX value
     * @param fromAngle Current fromAngle value
     * @return Updated fromX value
     */
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

    /**
     * Updates fromY value
     * @param fromY Current fromY value
     * @param fromAngle Current fromAngle value
     * @return Updated fromY value
     */
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

    /**
     * Updates toX value
     * @param toX Current toX value
     * @param toAngle Current toAngle value
     * @return Updated toX value
     */
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

    /**
     * Updates toY value
     * @param toY Current toY value
     * @param toAngle Current toAngle value
     * @return Updated toY value
     */
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

    /**
     * Checks if Object is in sight
     * @param visible was it visible?
     * @param oa OA value
     * @param ob OB value
     * @return True or False
     */
    private boolean isInSight(boolean visible, int oa, int ob){
        return visible || ((oa > 0) && (ob > 0));
    }

    /**
     * Updates fromX and fromY values
     * @param fromX Current fromX value
     * @param fromY Current fromY value
     * @param toX Current toX value
     * @param toY Current toY value
     * @return Array of {fromX, fromY}
     */
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

    /**
     * Updates OA and OB values
     * @param oa Current OA value
     * @param ob Current OB value
     * @param tf Current TF value
     * @param tt Current TT value
     * @return Array of {oa, ob}
     */
    private int[] getOAB(int oa, int ob, float tf, float tt){
        if (tf >= 0) {
            oa++;
        } else if (tt <= 0) {
            ob++;
        }

        return new int[] {oa, ob};
    }

    /**
     * Updates X and Y values
     * @param x Current X value
     * @param y Current Y value
     * @param toX Current toX value
     * @param toY Current toY value
     * @return Array of {x, y}
     */
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

    /**
     * Updates Touched Cells
     * @param x Position X of Cell
     * @param y Position Y of Cell
     */
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

    /**
     * Updates toX and toY values
     * @param fromX Current fromX value
     * @param fromY Current fromY value
     * @param toX Current toX value
     * @param toY Current toY value
     * @return Array of {toX, toY}
     */
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

    /**
     * Checks if from values equal to values
     * @param fromX Current fromX
     * @param fromY Current fromY
     * @param toX Current toX
     * @param toY Current toY
     * @return True if (fromX == toX) && (fromY == toY), false otherwise
     */
    private boolean isSamePosition(int fromX, int fromY, int toX, int toY){
        return (fromX == toX) && (fromY == toY);
    }

    /**
     * Checks tf and tt values
     * @param tf Current TF value
     * @param tt Current TT value
     * @return
     */
    private boolean isTFTT(float tf, float tt){
        return (tf <= 0) && (tt >= 0);
    }

    /**
     * Updates portToX and portToY values
     * @param portToX Current portToX value
     * @param portToY Current portToY value
     * @param x Current x value
     * @param y Current y value
     * @return Array of {portToX, portToY}
     */
    private float[] updatePortTo(float portToX, float portToY, int x, int y){

        if (tToSide >= 0) {
            portToX = (float)x + X_ADD[(tFromSide + 1) % 4];
            portToY = (float)y + Y_ADD[(tFromSide + 1) % 4];
        }

        return new float[] {portToX, portToY};
    }

    /**
     * Checks if not on Portal
     * @param x Current X Position
     * @param y Current Y Position
     * @param fromX Current fromX Position
     * @param fromY Current fromY Position
     * @return True if x!=fromX || y!=fromY, false otherwise
     */
    private boolean updatePortal(int x, int y, int fromX, int fromY){
        return (((!isEquals(x,fromX))) || (!isEquals(y,fromY)));
    }

    /**
     * Traces a Portal
     * @param portal Is Portal?
     * @param portToX Current portToX value
     * @param portToY Current portToY value
     * @param lastX Last Position X
     * @param lastY Last Position Y
     * @param fromX Starting Position X
     * @param fromY Starting Position Y
     * @param fromAngle Current fromAngle value
     * @param toAngle Current toAngle value
     */
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

    /**
     * Updates fromAngle value
     * @param portFromX Current portFromX value
     * @param portFromY Current portFromY value
     * @param fromAngle Current fromAngle value
     * @return Updated fromAngle value
     */
    private float updateFromAngle(float portFromX, float portFromY, float fromAngle){
        if (portFromX >= 0) {
            fromAngle = getAngle(portFromX - heroX,
                    portFromY - heroY) /* - ANG_CORRECT (leaved here just for case) */;
        }

        return fromAngle;
    }

    /**
     * Checks if x==toX and y==toY
     * @param x X position of Cell
     * @param y Y position of Cell
     * @param toX End Position X of Cell
     * @param toY End Position Y of Cell
     * @return True if both couples are equal, false otherwise
     */
    private boolean equalToPos(int x, int y, int toX, int toY){
        return (isEquals(x,toX)) && (isEquals(y,toY));
    }

    /**
     * Updates portFrom values
     * @param portFromX Current portFromX value
     * @param portFromY Current portFromY value
     * @param x X position of Cell
     * @param y Y position of Cell
     * @return Array of {portFromX, portFromY}
     */
    private float[] updatePortFrom(float portFromX, float portFromY, int x, int y){
        if (tFromSide >= 0) {
            portFromX = (float)x + X_ADD[tToSide];
            portFromY = (float)y + Y_ADD[tToSide];
        }
        return new float[] {portFromX, portFromY};
    }

    /**
     * Updates repeat value based on wall
     * @param wall Is Cell a Wall?
     * @param repeat Current repeat value
     * @return False if Cell is wall, true otherwise
     */
    private boolean updateRepeat(boolean wall, boolean repeat){
        if (wall) {
            repeat = false;
        }

        return repeat;
    }

    /**
     * Decides to continue loop based on Difference between angles value
     * @param fromAngle Current fromAngle
     * @param toAngle Current toAngle
     * @param repeat Current repeat
     * @return False if Difference > Pi, true otherwise
     */
    private boolean checkAngleDiff(float fromAngle, float toAngle, boolean repeat){
        if (angleDiff(fromAngle, toAngle) > Math.PI) {
            repeat = false;
        }

        return repeat;
    }

    /**
     * Checks TouchedCells Map
     * @param value1 Current value1
     * @param value2 Current value2
     * @param fromDx Current fromDx
     * @param fromDy Current fromDy
     * @param toDx Current toDx
     * @param toDy Current toDy
     * @param visible Is Cell Visible
     * @param oa Current OA value
     * @param ob Current OB value
     * @return Array of {oa, ob, boolean value as {true = 0, false = 1} }
     */
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

    /**
     *
     * Iterates on Cells
     * @param repeat Current repeat value
     * @param fromX Current fromX value
     * @param fromY Current fromY value
     * @param toX Current toX value
     * @param toY Current toY value
     * @param fromDx Current fromDx value
     * @param fromDy Current fromDy value
     * @param toDx Current toDx value
     * @param toDy Current toDy
     * @return Array of {fromX, fromY, boolean repeat as {true = 0, false = 1} }
     */
    private int[] iterateCells1(boolean repeat, int fromX, int fromY, int toX, int toY, float fromDx, float fromDy, float toDx, float toDy){
        while (true){
            int oa = 0;
            int ob = 0;

            int[] result = checkTouchedCellsMap(fromX, fromY, fromDx, fromDy, toDx, toDy, false, oa, ob);
            oa = result[0];
            ob = result[1];
            boolean visible = (result[2] == 0);

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

    /**
     * Iterates on Cells
     * @param repeat Current repeat value
     * @param fromX Current fromX value
     * @param fromY Current fromY value
     * @param toX Current toX value
     * @param toY Current toY value
     * @param fromDx Current fromDx value
     * @param fromDy Current fromDy value
     * @param toDx Current toDx value
     * @param toDy Current toDy
     * @return Array of {toX, toY, boolean repeat as {true = 0, false = 1} }
     */
    private int[] iterateCells2(boolean repeat, int fromX, int fromY, int toX, int toY, float fromDx, float fromDy, float toDx, float toDy){
        while (true){
            int oa = 0;
            int ob = 0;

            int[] result = checkTouchedCellsMap(toX, toY, fromDx, fromDy, toDx, toDy, false, oa, ob);
            oa = result[0];
            ob = result[1];
            boolean visible = (result[2] == 0);

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

    /**
     * Updates toAngle
     * @param repeat Current repeat value
     * @param toAngle Current toAngle value
     * @param fromAngle Current fromAngle value
     * @param portToX Current portToX value
     * @param portToY Current portToY value
     * @return Array of {toAngle, boolean value as {0 = true, 2 = false}}
     */
    private float[] checkPortX(boolean repeat, float toAngle, float fromAngle, float portToX, float portToY){
        if (portToX >= 0) {
            toAngle = getAngle(portToX - heroX,
                    portToY - heroY) /* + ANG_CORRECT (leaved here just for case) */;

            repeat = checkAngleDiff(fromAngle, toAngle, repeat);
        }
        return new float[] {toAngle, ((repeat) ? 0 : 2)};
    }

    /**
     * Traces a Cell
     * @param fromX Current fromX value
     * @param fromY Current fromY value
     * @param fromAngle Current fromAngle value
     * @param toX Current toX value
     * @param toY Current toY value
     * @param toAngle Current toAngle value
     */
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

    /**
     * Updates tx and ty values
     * @param fromAngle Current fromAngle
     * @param tx Current tx value
     * @param ty Current ty
     * @return Array of {tx, ty}
     */
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

    /**
     * Updates tx and ty values
     * @param toAngle Current toAngle
     * @param tx Current tx value
     * @param ty Current ty value
     * @return Array of {tx, ty}
     */
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

    /**
     * Traces the Portal
     * @param x Position X of Portal
     * @param y Position Y of Portal
     * @param heroAngle Hero Angle
     * @param halfFov One half Field of View
     */
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

    /**
     * Casts float value to int
     * @param a float value to cast
     * @return int value of a
     */
    private static int floatToInt(float a) {
        if (a < Integer.MIN_VALUE || a > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Value not castable");
        }
        return Math.round(a-0.5f);
    }
}
