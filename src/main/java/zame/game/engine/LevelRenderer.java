package zame.game.engine;

import javax.microedition.khronos.opengles.GL10;
import zame.game.Common;
import zame.game.Config;
import zame.game.Renderer;

@SuppressWarnings("WeakerAccess")
public final class LevelRenderer {
    public static class VisibleObject {
        public float midX;
        public float midY;
        public float fromX;
        public float fromY;
        public float toX;
        public float toY;
        public Object obj;
        public float dist;
    }

    private static final int MAX_VISIBLE_OBJECTS = 64;
    private static final float FLOOR_FADE_ALPHA = 1.0f;

    private static final int AUTO_WALL_TYPE_WALL = 0;
    private static final int AUTO_WALL_TYPE_TRANSP = 1;
    private static final int AUTO_WALL_TYPE_DOOR = 2;

    private static final int AUTO_WALL_MASK_HORIZONTAL = 1;
    private static final int AUTO_WALL_MASK_VERTICAL = 2;
    private static final int AUTO_WALL_MASK_DOOR = 4;

    public static final int MAX_AUTO_WALLS = Level.MAX_WIDTH * Level.MAX_HEIGHT * 2;
    public static final float HALF_WALL = 1.0f / 2.5f;

    public static PortalTracer tracer = new PortalTracer();

    public static VisibleObject[] visibleObjects = new VisibleObject[MAX_VISIBLE_OBJECTS];
    public static int visibleObjectsCount=0;
    public static VisibleObject currVis = new VisibleObject();

    private static float flatObjDx=0;
    private static float flatObjDy=0;

    public static boolean showMonstersOnMap = true;

    private LevelRenderer() {
    }

    public static void init() {
        visibleObjectsCount = 0;
        currVis = null;
        tracer = new PortalTracer();

        for (int i = 0; i < MAX_VISIBLE_OBJECTS; i++) {
            visibleObjects[i] = new VisibleObject();
        }
    }

    public static void updateAutoWalls() {
        for (int i = 0; i < State.autoWallsCount; i++) {
            AutoWall aw = State.autoWalls[i];

            if (aw.doorIndex >= 0) {
                aw.door = State.doors[aw.doorIndex];
            }
        }
    }

    public static int updateCI(float t, int ci){
        if (t >= 0) {
            ci++;
        }

        return ci;
    }

    public static int updateCO(float t, int co){
        if (t <= 0) {
            co++;
        }

        return co;
    }

    public static void sortVisibleObjects() {
        currVis = null;

        for (int i = 0; i < visibleObjectsCount; i++) {
            VisibleObject vo = visibleObjects[i];

            int ci = 0;
            int co = 0;

            float t = ((vo.fromX - State.heroX) * Common.heroSn) + ((vo.fromY - State.heroY) * Common.heroCs);

            ci = updateCI(t, ci);
            co = updateCO(t, co);

            t = ((vo.toX - State.heroX) * Common.heroSn) + ((vo.toY - State.heroY) * Common.heroCs);

            ci = updateCI(t, ci);
            co = updateCO(t, co);

            if ((ci > 0) && (co > 0)) {
                float dx = vo.midX - State.heroX;
                float dy = State.heroY - vo.midY;

                float dist = (float)Math.sqrt((dx * dx) + (dy * dy));

                if ((currVis == null) || (dist < currVis.dist)) {
                    currVis = vo;
                    currVis.dist = dist;
                }
            }
        }
    }

    @SuppressWarnings("MagicNumber")
    private static void updateDoors(long elapsedTime) {
        for (int i = 0; i < State.doorsCount; i++) {
            Door door = State.doors[i];

            if (door.dir != 0) {
                if (door.dir > 0) {
                    door.openPos = (float)(elapsedTime - door.lastTime) / 300.0f;
                } else {
                    door.openPos = Door.OPEN_POS_MAX - ((float)(elapsedTime - door.lastTime) / 200.0f);
                }

                door.update(elapsedTime);
            }
        }
    }

    @SuppressWarnings("MagicNumber")
    public static float getLightness(float x, float y) {
        x -= State.heroX;
        y -= State.heroY;

        float d = ((x * Common.heroCs) - (y * Common.heroSn));
        d = ((d < 0.0f) ? 0.0f : (d * 0.05f));

        return (1.0f - ((d > 0.8f) ? 0.8f : d));
    }

    public static void setObjLighting(float x, float y) {
        float l = getLightness(x, y);
        Renderer.setQuadRGB(l, l, l);
    }

    @SuppressWarnings("MagicNumber")
    public static void setWallLighting(float fromX, float fromY, float toX, float toY, boolean vert) {
        int ang = ((int)State.heroA + (vert ? 0 : 270)) % 360;

        if (ang > 90) {
            if (ang < 180) {
                ang = 180 - ang;
            } else if (ang < 270) {
                ang = ang - 180;
            } else {
                ang = 360 - ang;
            }
        }

        float l = 1.0f - ((0.5f * (float)ang) / 90.0f);

        float l1 = getLightness(fromX, fromY) * l;

        Renderer.r1 = l1;
        Renderer.g1 = l1;
        Renderer.b1 = l1;

        Renderer.r2 = l1;
        Renderer.g2 = l1;
        Renderer.b2 = l1;

        float l2 = getLightness(toX, toY) * l;

        Renderer.r3 = l2;
        Renderer.g3 = l2;
        Renderer.b3 = l2;

        Renderer.r4 = l2;
        Renderer.g4 = l2;
        Renderer.b4 = l2;
    }

    private static boolean isEquals(int a, int b)
    {
        return Math.abs(a-b) < 1e-6;
    }

    private static int floatToInt(float a){
        if (a < Integer.MIN_VALUE || a > Integer.MAX_VALUE){
            throw new IllegalArgumentException("Value not castable");
        }
        return (int) a;
    }

    private static float intToFloat(int a){
        if (a < Float.MIN_VALUE || a > Float.MAX_VALUE){
            throw new IllegalArgumentException("Value not castable");
        }
        return (float) a;
    }

    private static int getIndex(int index, int nextIndex){
        return ((nextIndex > index) ? (nextIndex - 1) : nextIndex);
    }

    private static boolean isValid(int value1, int value2, int value3, int value4) {
        return ((value1 == value2) && (value3 == value4));
    }

    private static boolean isSkippable(AutoWall aw, AutoWall awi, int i, int index){
        return (i == index) || (aw.door != null) || (aw.vert != awi.vert) || (aw.type != awi.type);
    }

    private static void copyAutoWalls(int index){
        for (int i = index; i < State.autoWallsCount; i++) {
            State.autoWalls[i].copyFrom(State.autoWalls[i + 1]);
        }
    }

    private static int cycleWalls(int index, int nextIndex){
        AutoWall awi = State.autoWalls[index];

        for (int i = 0; i < State.autoWallsCount; i++) {
            AutoWall aw = State.autoWalls[i];

            if (isSkippable(aw, awi, i, index)) {
                continue;
            }

            if( isValid((floatToInt(aw.fromX)), (floatToInt(awi.fromX)), (floatToInt(aw.fromY)), (floatToInt(awi.fromY))) ){
                aw.fromX = awi.toX;
                aw.fromY = awi.toY;
                nextIndex = i;
                break;
            } else if( isValid((floatToInt(aw.toX)), (floatToInt(awi.fromX)), (floatToInt(aw.toY)), (floatToInt(awi.fromY))) ){
                aw.toX = awi.toX;
                aw.toY = awi.toY;
                nextIndex = i;
                break;
            } else if( isValid((floatToInt(aw.fromX)), (floatToInt(awi.toX)), (floatToInt(aw.fromY)), (floatToInt(awi.toY))) ){
                aw.fromX = awi.fromX;
                aw.fromY = awi.fromY;
                nextIndex = i;
                break;
            } else if( isValid((floatToInt(aw.toX)), (floatToInt(awi.toX)), (floatToInt(aw.toY)), (floatToInt(awi.toY))) ){
                aw.toX = awi.fromX;
                aw.toY = awi.fromY;
                nextIndex = i;
                break;
            }
        }

        return nextIndex;
    }

    private static void swapAutoWall(int index){
        while(true) {
            int nextIndex = -1;

            nextIndex = cycleWalls(index, nextIndex);

            if (nextIndex < 0) {
                break;
            }

            State.autoWallsCount--;

            copyAutoWalls(index);

            index = getIndex(index, nextIndex);
        }
    }

    private static boolean isAwValid(AutoWall aw, boolean vert, int type){
        return (aw.door != null) || (aw.vert != vert) || (aw.type != type);
    }

    private static boolean bothEquals(int value1, int value2, int value3, int value4){
        return (isEquals(value1, value2)) && (isEquals(value3, value4));
    }

    // This method:
    // did *not* check for available space (MAX_AUTO_WALLS),
    // did *not* check if wall already exists,
    // did *not* append wall mask,
    // did *not* add doors
    public static void appendAutoWall(int fromX, int fromY, int toX, int toY, int type) {
        int index = -1;
        boolean vert = (isEquals(fromX,toX));

        for (int i = 0; i < State.autoWallsCount; i++) {
            AutoWall aw = State.autoWalls[i];

            if (isAwValid(aw, vert, type)) {
                continue;
            }

            if(bothEquals(floatToInt(aw.fromX), fromX, floatToInt(aw.fromY), fromY)){
                aw.fromX = (float)toX;
                aw.fromY = (float)toY;
                index = i;
                break;
            } else if(bothEquals(floatToInt(aw.toX), fromX, floatToInt(aw.toY), fromY)){
                aw.toX = (float)toX;
                aw.toY = (float)toY;
                index = i;
                break;
            } else if(bothEquals(floatToInt(aw.fromX), toX, floatToInt(aw.fromY), toY)){
                aw.fromX = (float)fromX;
                aw.fromY = (float)fromY;
                index = i;
                break;
            } else if(bothEquals(floatToInt(aw.toX), toX, floatToInt(aw.toY), toY)){
                aw.toX = (float)fromX;
                aw.toY = (float)fromY;
                index = i;
                break;
            }
        }

        if (index < 0) {
            AutoWall aw = State.autoWalls[State.autoWallsCount++];

            aw.fromX = intToFloat(fromX);
            aw.fromY = intToFloat(fromY);
            aw.toX = intToFloat(toX);
            aw.toY = intToFloat(toY);
            aw.vert = vert;
            aw.type = type;
            aw.doorIndex = -1;
            aw.door = null;

            return;
        }

        swapAutoWall(index);
    }

    private static int isDoorOrWall(Door door, PortalTracer.Wall wall){
        return (door != null) ? (door.texture + 0x10) : wall.texture;
    }

    private static void setVisibleObject(PortalTracer.Wall wall){
        if ((Level.marksMap[wall.cellY][wall.cellX] != null) && (visibleObjectsCount < MAX_VISIBLE_OBJECTS)) {
            VisibleObject vo = visibleObjects[visibleObjectsCount++];

            vo.midX = (wall.fromX + wall.toX) * 0.5f;
            vo.midY = (wall.fromY + wall.toY) * 0.5f;
            vo.fromX = wall.fromX;
            vo.fromY = wall.fromY;
            vo.toX = wall.toX;
            vo.toY = wall.toY;
            vo.obj = Level.marksMap[wall.cellY][wall.cellX];
        }
    }

    private static void toAppendWall(int mx, int my, PortalTracer.Wall wall, int autoWallMask){
        if ((isEquals((State.drawedAutoWalls[my][mx] & autoWallMask),0)) && (State.autoWallsCount < MAX_AUTO_WALLS)) {
            State.drawedAutoWalls[my][mx] |= autoWallMask;
            appendAutoWall(wall.fromX, wall.fromY, wall.toX, wall.toY, AUTO_WALL_TYPE_WALL);
        }
    }

    @SuppressWarnings("MagicNumber")
    public static void renderLevel() {
        Renderer.z1 = -HALF_WALL;
        Renderer.z2 = HALF_WALL;
        Renderer.z3 = HALF_WALL;
        Renderer.z4 = -HALF_WALL;

        for (int i = 0; i < tracer.wallsCount; i++) {
            int autoWallMask = 0;
            Door door = new Door();

            PortalTracer.Wall wall = tracer.walls[i];

            if (wall.fromX == wall.toX) {
                door = ((wall.fromY < wall.toY)
                        ? Level.doorsMap[wall.fromY][wall.fromX - 1]
                        : Level.doorsMap[wall.toY][wall.fromX]);

                autoWallMask = AUTO_WALL_MASK_VERTICAL;
            } else {
                door = ((wall.fromX < wall.toX)
                        ? Level.doorsMap[wall.fromY][wall.fromX]
                        : Level.doorsMap[wall.fromY - 1][wall.toX]);

                autoWallMask = AUTO_WALL_MASK_HORIZONTAL;
            }

            int mx = getMValue(wall.fromX, wall.toX);
            int my = getMValue(wall.fromY, wall.toY);

            toAppendWall(mx, my, wall, autoWallMask);

            setVisibleObject(wall);

            Renderer.x1 = (float)wall.fromX;
            Renderer.y1 = -(float)wall.fromY;

            Renderer.x2 = (float)wall.fromX;
            Renderer.y2 = -(float)wall.fromY;

            Renderer.x3 = (float)wall.toX;
            Renderer.y3 = -(float)wall.toY;

            Renderer.x4 = (float)wall.toX;
            Renderer.y4 = -(float)wall.toY;

            setWallLighting(intToFloat(wall.fromX),
                    intToFloat(wall.fromY),
                    intToFloat(wall.toX),
                    intToFloat(wall.toY),
                    (wall.fromX == wall.toX));

            Renderer.drawQuad(isDoorOrWall(door, wall));
        }
    }

    private static boolean isValidDoor(Door door){
        return ((State.drawedAutoWalls[door.y][door.x] & AUTO_WALL_MASK_DOOR) == 0)
                && (State.autoWallsCount < MAX_AUTO_WALLS);
    }

    private static boolean isDoorOpen(Door door){
        return ((door.openPos) < 0.7f) && (visibleObjectsCount < MAX_VISIBLE_OBJECTS);
    }

    @SuppressWarnings("MagicNumber")
    private static void renderDoors() {
        for (int i = 0; i < tracer.touchedCellsCount; i++) {
            PortalTracer.TouchedCell tc = tracer.touchedCells[i];
            Door door = Level.doorsMap[tc.y][tc.x];

            if (door == null) {
                continue;
            }

            float fromX=0;
            float fromY=0;
            float toX=0;
            float toY=0;

            if (door.vert) {
                fromX = (float)door.x + 0.5f;
                toX = fromX;
                fromY = (float)door.y;
                toY = fromY + 1.0f;
            } else {
                fromX = (float)door.x;
                toX = fromX + 1.0f;
                fromY = (float)door.y + 0.5f;
                toY = fromY;
            }

            if (isValidDoor(door)) {

                State.drawedAutoWalls[door.y][door.x] |= AUTO_WALL_MASK_DOOR;
                AutoWall aw = State.autoWalls[State.autoWallsCount++];

                aw.fromX = fromX;
                aw.fromY = fromY;
                aw.toX = toX;
                aw.toY = toY;
                aw.vert = door.vert;
                aw.type = AUTO_WALL_TYPE_DOOR;
                aw.doorIndex = door.index;
                aw.door = door;
            }

            // do not add opened doors to visible objects, otherwise it is impossible to shoot monsters behind opened door
            if (isDoorOpen(door)) {
                VisibleObject vo = visibleObjects[visibleObjectsCount++];

                vo.midX =  intToFloat(door.x) + 0.5f;
                vo.midY =  intToFloat(door.y) + 0.5f;
                vo.fromX = fromX;
                vo.fromY = fromY;
                vo.toX = toX;
                vo.toY = toY;
                vo.obj = door;
            }

            if (door.vert) {
                fromY += door.openPos;
                toY += door.openPos;
            } else {
                fromX += door.openPos;
                toX += door.openPos;
            }

            Renderer.x1 = fromX;
            Renderer.y1 = -fromY;

            Renderer.x2 = fromX;
            Renderer.y2 = -fromY;

            Renderer.x3 = toX;
            Renderer.y3 = -toY;

            Renderer.x4 = toX;
            Renderer.y4 = -toY;

            setWallLighting(fromX, fromY, toX, toY, door.vert);
            Renderer.drawQuad(door.texture);
        }
    }

    private static void drawTexPos(int tex, PortalTracer.TouchedCell tc){
        if (tex > 0) {
            float mx = (float)tc.x + 0.5f;
            float my = (float)tc.y + 0.5f;

            float fromX = mx + flatObjDy;
            float toX = mx - flatObjDy;
            float fromY = my - flatObjDx;
            float toY = my + flatObjDx;

            Renderer.x1 = fromX;
            Renderer.y1 = -fromY;

            Renderer.x2 = fromX;
            Renderer.y2 = -fromY;

            Renderer.x3 = toX;
            Renderer.y3 = -toY;

            Renderer.x4 = toX;
            Renderer.y4 = -toY;

            setObjLighting(mx, my);
            Renderer.drawQuad(tex);
        }
    }

    private static int getAutoWallMask(int s){
        return (((s == 1) || (s == 3))
                ? AUTO_WALL_MASK_VERTICAL
                : AUTO_WALL_MASK_HORIZONTAL);
    }

    private static void drawAutoWalls(int mx, int my, int autoWallMask, int fromX, int fromY, int toX, int toY){
        if ((isEquals(((State.drawedAutoWalls[my][mx] & autoWallMask)),0)) && (State.autoWallsCount
                < MAX_AUTO_WALLS)) {

            State.drawedAutoWalls[my][mx] |= autoWallMask;
            appendAutoWall(fromX, fromY, toX, toY, AUTO_WALL_TYPE_TRANSP);
        }
    }

    private static boolean isAutoWallDrawn(PortalTracer.TouchedCell tc){
        return ((State.drawedAutoWalls[tc.y][tc.x] & AUTO_WALL_MASK_DOOR) == 0) && (State.autoWallsCount
                < MAX_AUTO_WALLS);
    }

    private static int getMValue(int value1, int value2){
        return ((value1 < value2) ? value1 : value2);
    }

    private static void drawObject(int tex, PortalTracer.TouchedCell tc){
        if (tex > 0) {
            float mx = (float)tc.x + 0.5f;
            float my = (float)tc.y + 0.5f;

            float fromX = mx + flatObjDy;
            float toX = mx - flatObjDy;
            float fromY = my - flatObjDx;
            float toY = my + flatObjDx;

            Renderer.x1 = fromX;
            Renderer.y1 = -fromY;

            Renderer.x2 = fromX;
            Renderer.y2 = -fromY;

            Renderer.x3 = toX;
            Renderer.y3 = -toY;

            Renderer.x4 = toX;
            Renderer.y4 = -toY;

            setObjLighting(mx, my);
            Renderer.drawQuad(tex);
        }
    }

    private static void willAutoWallBeDrawn(PortalTracer.TouchedCell tc, boolean vert, float fromX, float fromY, float toX, float toY){
        if (isAutoWallDrawn(tc)) {

            State.drawedAutoWalls[tc.y][tc.x] |= AUTO_WALL_MASK_DOOR;
            AutoWall aw = State.autoWalls[State.autoWallsCount++];

            aw.fromX = fromX;
            aw.fromY = fromY;
            aw.toX = toX;
            aw.toY = toY;
            aw.vert = vert;
            aw.type = AUTO_WALL_TYPE_TRANSP;
            aw.doorIndex = -1;
            aw.door = null;
        }
    }

    private static boolean matchS(int s){
        return ((s == 1) || (s == 3));
    }

    // render objects, decorations and transparents
    @SuppressWarnings("MagicNumber")
    private static void renderObjects() {
        for (int i = 0; i < tracer.touchedCellsCount; i++) {
            PortalTracer.TouchedCell tc = tracer.touchedCells[i];
            int tex = State.objectsMap[tc.y][tc.x];

            drawTexPos(tex, tc);

            tex = State.decorationsMap[tc.y][tc.x];

            if (tex != 0) {
                // if decoration tex == -1, than there is object here (or was before hero picked up it), but there can't be transparents
                drawObject(tex, tc);
            } else {
                tex = State.transpMap[tc.y][tc.x];

                if (tex >= (TextureLoader.BASE_TRANSPARENTS + 0x10)) {
                    float fromX=0;
                    float fromY=0;
                    float toX=0;
                    float toY=0;

                    boolean vert = (tex >= (TextureLoader.BASE_TRANSPARENTS + 0x18));

                    if (vert) {
                        tex -= 8;

                        fromX =  intToFloat(tc.x) + 0.5f;
                        toX = fromX;
                        fromY =  intToFloat(tc.y);
                        toY = fromY + 1.0f;
                    } else {
                        fromX =  intToFloat(tc.x);
                        toX = fromX + 1.0f;
                        fromY =  intToFloat(tc.y) + 0.5f;
                        toY = fromY;
                    }

                    willAutoWallBeDrawn(tc, vert, fromX, fromY, toX, toY);

                    Renderer.x1 = fromX;
                    Renderer.y1 = -fromY;

                    Renderer.x2 = fromX;
                    Renderer.y2 = -fromY;

                    Renderer.x3 = toX;
                    Renderer.y3 = -toY;

                    Renderer.x4 = toX;
                    Renderer.y4 = -toY;

                    setWallLighting(fromX, fromY, toX, toY, vert);
                    Renderer.drawQuad(tex);
                } else if (tex > 0) {
                    for (int s = 0; s < 4; s++) {
                        if ((State.passableMap[tc.y + PortalTracer.Y_CELL_ADD[s]][tc.x + PortalTracer.X_CELL_ADD[s]]
                                & Level.PASSABLE_MASK_WALL_N_TRANSP) == 0) {

                            int fromX = tc.x + PortalTracer.X_ADD[s];
                            int fromY = tc.y + PortalTracer.Y_ADD[s];
                            int toX = tc.x + PortalTracer.X_ADD[(s + 1) % 4];
                            int toY = tc.y + PortalTracer.Y_ADD[(s + 1) % 4];

                            Renderer.x1 = (float)fromX;
                            Renderer.y1 = -(float)fromY;

                            Renderer.x2 = (float)fromX;
                            Renderer.y2 = -(float)fromY;

                            Renderer.x3 = (float)toX;
                            Renderer.y3 = -(float)toY;

                            Renderer.x4 = (float)toX;
                            Renderer.y4 = -(float)toY;

                            int mx = getMValue(fromX, toX);
                            int my = getMValue(fromY, toY);

                            int autoWallMask = getAutoWallMask(s);
                            drawAutoWalls(mx, my, autoWallMask, fromX, fromY, toX, toY);

                            setWallLighting( intToFloat(fromX),  intToFloat(fromY),  intToFloat(toX), intToFloat(toY), matchS(s));
                            Renderer.drawQuad(tex);
                        }
                    }
                }
            }
        }
    }

    private static long getMonDieTime(long die, long elapsed){
        if (die == 0) {
            die = elapsed;
        }
        return die;
    }

    private static int getTex(int tex, Monster mon, long elapsedTime){
        if ((mon.hitTimeout <= 0) && (mon.attackTimeout > 0)) {
            tex += 15;
        } else if (mon.isAimedOnHero) {
            tex += 2;
        } else {
            tex += (((((int)State.heroA + 360 + 45) - (mon.dir * 90)) % 360) / 90);
        }

        if (mon.hitTimeout > 0) {
            tex += 8;
        } else if (!mon.isInAttackState && ((elapsedTime % 800) > 400)) {
            tex += 4;
        }

        return tex;
    }

    private static boolean isMonsterDead(Monster mon, int x, int y, boolean deadCorpses){
        return (!(tracer.touchedCellsMap[y][x]
                || tracer.touchedCellsMap[mon.cellX][mon.cellY]))
                || (deadCorpses
                && (mon.health > 0))
                || (!deadCorpses
                && (mon.health <= 0));
    }

    @SuppressWarnings("MagicNumber")
    private static void renderMonsters(long elapsedTime, boolean deadCorpses) {
        for (int i = 0; i < State.monstersCount; i++) {
            Monster mon = State.monsters[i];

            int x = floatToInt(mon.x);
            int y = floatToInt(mon.y);

            if (isMonsterDead(mon, x, y, deadCorpses)) {

                continue;
            }

            float fromX = mon.x + flatObjDy;
            float toX = mon.x - flatObjDy;
            float fromY = mon.y - flatObjDx;
            float toY = mon.y + flatObjDx;

            int tex = mon.texture;
            setObjLighting(mon.x, mon.y);

            Renderer.x1 = fromX;
            Renderer.y1 = -fromY;

            Renderer.x2 = fromX;
            Renderer.y2 = -fromY;

            Renderer.x3 = toX;
            Renderer.y3 = -toY;

            Renderer.x4 = toX;
            Renderer.y4 = -toY;

            if (mon.health > 0) {
                tex = getTex(tex, mon, elapsedTime);

                if (visibleObjectsCount < MAX_VISIBLE_OBJECTS) {
                    VisibleObject vo = visibleObjects[visibleObjectsCount++];

                    float dx = mon.x - State.heroX;
                    float dy = mon.y - State.heroY;
                    float dist = (float)Math.sqrt((dx * dx) + (dy * dy));
                    float mult = Math.min(1.0f, 0.4f + (dist * 0.12f));

                    vo.midX = mon.x;
                    vo.midY = mon.y;
                    vo.fromX = mon.x + (flatObjDy * mult);
                    vo.fromY = mon.y - (flatObjDx * mult);
                    vo.toX = mon.x - (flatObjDy * mult);
                    vo.toY = mon.y + (flatObjDx * mult);
                    vo.obj = mon;
                }
            } else {
                mon.dieTime = getMonDieTime(mon.dieTime, elapsedTime);

                tex += 12 + ((mon.dieTime < 0) ? 2 : Math.min(2, (elapsedTime - mon.dieTime) / 150));
            }

            Renderer.drawQuadMon(tex);
        }
    }

    private static void renderFloor(GL10 gl) {
        gl.glDisable(GL10.GL_DEPTH_TEST);

        Renderer.setQuadRGBA(1.0f, 1.0f, 1.0f, 1.0f);
        Renderer.init();

        Renderer.z1 = -HALF_WALL;
        Renderer.z2 = -HALF_WALL;
        Renderer.z3 = -HALF_WALL;
        Renderer.z4 = -HALF_WALL;

        Renderer.u1 = 0.0f;
        Renderer.v1 = (float)State.levelHeight;

        Renderer.u2 = 0.0f;
        Renderer.v2 = 0.0f;

        Renderer.u3 = (float)State.levelWidth;
        Renderer.v3 = 0.0f;

        Renderer.u4 = (float)State.levelWidth;
        Renderer.v4 = (float)State.levelHeight;

        Renderer.x1 = Renderer.u1;
        Renderer.y1 = -Renderer.v1;

        Renderer.x2 = Renderer.u2;
        Renderer.y2 = -Renderer.v2;

        Renderer.x3 = Renderer.u3;
        Renderer.y3 = -Renderer.v3;

        Renderer.x4 = Renderer.u4;
        Renderer.y4 = -Renderer.v4;

        Renderer.drawQuad();

        Renderer.bindTextureRep(gl, TextureLoader.textures[TextureLoader.TEXTURE_FLOOR]);
        Renderer.flush(gl);

        Renderer.init();

        Renderer.z1 = HALF_WALL;
        Renderer.z2 = HALF_WALL;
        Renderer.z3 = HALF_WALL;
        Renderer.z4 = HALF_WALL;

        Renderer.u2 =  intToFloat(State.levelWidth);
        Renderer.v2 =  intToFloat(State.levelHeight);

        Renderer.u4 = 0.0f;
        Renderer.v4 = 0.0f;

        Renderer.x2 = Renderer.u2;
        Renderer.y2 = -Renderer.v2;

        Renderer.x4 = Renderer.u4;
        Renderer.y4 = -Renderer.v4;

        Renderer.drawQuad();

        Renderer.bindTextureRep(gl, TextureLoader.textures[TextureLoader.TEXTURE_CEIL]);
        Renderer.flush(gl);

        // Fading

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPushMatrix();
        Renderer.loadIdentityAndOrthof(gl, -1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        Renderer.init();
        Renderer.setQuadRGB(0.0f, 0.0f, 0.0f);

        Renderer.z1 = 0.0f;
        Renderer.z2 = 0.0f;
        Renderer.z3 = 0.0f;
        Renderer.z4 = 0.0f;

        Renderer.x1 = -1.0f;
        Renderer.y1 = -1.0f;
        Renderer.a1 = 0.0f;

        Renderer.x2 = -1.0f;
        Renderer.y2 = 0.0f;
        Renderer.a2 = FLOOR_FADE_ALPHA;

        Renderer.x3 = 1.0f;
        Renderer.y3 = 0.0f;
        Renderer.a3 = FLOOR_FADE_ALPHA;

        Renderer.x4 = 1.0f;
        Renderer.y4 = -1.0f;
        Renderer.a4 = 0.0f;

        Renderer.drawQuad();

        Renderer.x1 = -1.0f;
        Renderer.y1 = 0.0f;
        Renderer.a1 = FLOOR_FADE_ALPHA;

        Renderer.x2 = -1.0f;
        Renderer.y2 = 1.0f;
        Renderer.a2 = 0.0f;

        Renderer.x3 = 1.0f;
        Renderer.y3 = 1.0f;
        Renderer.a3 = 0.0f;

        Renderer.x4 = 1.0f;
        Renderer.y4 = 0.0f;
        Renderer.a4 = FLOOR_FADE_ALPHA;

        Renderer.drawQuad();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        Renderer.flush(gl, false);
        gl.glDisable(GL10.GL_BLEND);

        gl.glPopMatrix();
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    @SuppressWarnings("MagicNumber")
    public static void render(GL10 gl, long elapsedTime, float ypos) {
        updateDoors(elapsedTime);

        tracer.trace(State.heroX, State.heroY, Common.heroAr, 44.0f * Common.G2RAD_F);
        visibleObjectsCount = 0;

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_BLEND);

        gl.glTranslatef(0.0f, ypos, -0.1f);
        gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(90.0f - State.heroA, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef(-State.heroX, State.heroY, 0.0f);

        renderFloor(gl);

        // Walls & Doors

        Renderer.z1 = -HALF_WALL;
        Renderer.z2 = HALF_WALL;
        Renderer.z3 = HALF_WALL;
        Renderer.z4 = -HALF_WALL;

        Renderer.a1 = 1.0f;
        Renderer.a2 = 1.0f;
        Renderer.a3 = 1.0f;
        Renderer.a4 = 1.0f;

        gl.glEnable(GL10.GL_DEPTH_TEST);
        Renderer.bindTexture(gl, TextureLoader.textures[TextureLoader.TEXTURE_MAIN]);

        Renderer.init();
        renderLevel();
        Renderer.flush(gl);

        // Doors

        // disable cull face - it is necessary for doors and transparents, and has no affect on monsters and objects
        gl.glDisable(GL10.GL_CULL_FACE);

        Renderer.init();
        renderDoors();
        Renderer.flush(gl);

        // Monsters, Objects & Transparents

        flatObjDx = (float)Math.cos(-Common.heroAr) * 0.5f;
        flatObjDy = (float)Math.sin(-Common.heroAr) * 0.5f;

        gl.glEnable(GL10.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL10.GL_GREATER, Renderer.ALPHA_VALUE);

        // objects rendered after monsters (so if monster stay in cell with object, monster will be in front)
        // or reverse order, but set appropriate depth test function

        Renderer.bindTexture(gl, TextureLoader.textures[TextureLoader.TEXTURE_MON]);
        Renderer.init();
        renderMonsters(elapsedTime, false);
        Renderer.flush(gl);

        Renderer.bindTexture(gl, TextureLoader.textures[TextureLoader.TEXTURE_MAIN]);
        Renderer.init();
        renderObjects();
        Renderer.flush(gl);

        // dead corpses rendered to be in back

        Renderer.bindTexture(gl, TextureLoader.textures[TextureLoader.TEXTURE_MON]);
        Renderer.init();
        renderMonsters(elapsedTime, true);
        Renderer.flush(gl);

        gl.glDisable(GL10.GL_ALPHA_TEST);
        gl.glEnable(GL10.GL_CULL_FACE);
    }

    private static void drawMonster(Monster mon){
        if (mon.health <= 0) {
            Renderer.r1 = 0.5f;
            Renderer.b1 = 0.5f;
            Renderer.r2 = 0.5f;
            Renderer.b2 = 0.5f;
        } else if (mon.chaseMode) {
            Renderer.r1 = 1.0f;
            Renderer.b1 = 0.0f;
            Renderer.r2 = 1.0f;
            Renderer.b2 = 0.0f;
        } else {
            Renderer.r1 = 0.0f;
            Renderer.b1 = 1.0f;
            Renderer.r2 = 0.0f;
            Renderer.b2 = 1.0f;
        }
    }

    @SuppressWarnings("MagicNumber")
    public static void renderAutoMap(GL10 gl) {
        gl.glDisable(GL10.GL_DEPTH_TEST);

        float autoMapZoom = 20.0f;

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPushMatrix();

        Renderer.loadIdentityAndOrthof(gl,
                -autoMapZoom * Common.ratio,
                autoMapZoom * Common.ratio,
                -autoMapZoom,
                autoMapZoom,
                0.0f,
                1.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(Config.mapPosition * autoMapZoom, 0.0f, 0.0f);

        gl.glRotatef(90.0f - State.heroA, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef(-State.heroX, State.heroY, 0.0f);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        Renderer.init();

        Renderer.a1 = 0.5f;
        Renderer.a2 = Renderer.a1;

        Renderer.b1 = 0.0f;
        Renderer.b2 = 0.0f;

        for (int i = 0; i < State.autoWallsCount; i++) {
            AutoWall aw = State.autoWalls[i];

            if (aw.door != null) {
                Renderer.r1 = 0.0f;
                Renderer.r2 = 0.0f;
                Renderer.g1 = 1.0f;
                Renderer.g2 = 1.0f;

                float fromX = aw.fromX;
                float fromY = aw.fromY;
                float toX = aw.toX;
                float toY = aw.toY;

                if (aw.vert) {
                    fromY += aw.door.openPos;
                } else {
                    fromX += aw.door.openPos;
                }

                Renderer.drawLine(fromX, -fromY, toX, -toY);
            } else {
                if (aw.type == AUTO_WALL_TYPE_WALL) {
                    Renderer.r1 = 1.0f;
                    Renderer.r2 = 1.0f;
                    Renderer.g1 = 1.0f;
                    Renderer.g2 = 1.0f;
                } else {
                    Renderer.r1 = 0.5f;
                    Renderer.r2 = 0.5f;
                    Renderer.g1 = 0.5f;
                    Renderer.g2 = 0.5f;
                }

                Renderer.drawLine(aw.fromX, -aw.fromY, aw.toX, -aw.toY);
            }
        }

        if (showMonstersOnMap) {
            Renderer.g1 = 0.0f;
            Renderer.g2 = 0.0f;

            for (int i = 0; i < State.monstersCount; i++) {
                Monster mon = State.monsters[i];

                drawMonster(mon);

                float mdx = (float)Math.cos((float)mon.shootAngle * Common.G2RAD_F) * 0.75f;
                float mdy = (float)Math.sin((float)mon.shootAngle * Common.G2RAD_F) * 0.75f;

                Renderer.drawLine(mon.x, -mon.y, mon.x + mdx, -mon.y + mdy);

                mdx *= 0.25;
                mdy *= 0.25;

                Renderer.drawLine(mon.x + mdy, -mon.y - mdx, mon.x - mdy, -mon.y + mdx);
            }
        }

        Renderer.flush(gl, false);

        gl.glLoadIdentity();
        gl.glTranslatef(Config.mapPosition * autoMapZoom, 0.0f, 0.0f);
        Renderer.init();

        Renderer.r1 = 1.0f;
        Renderer.g1 = 1.0f;
        Renderer.b1 = 1.0f;

        Renderer.r2 = 1.0f;
        Renderer.g2 = 1.0f;
        Renderer.b2 = 1.0f;

        Renderer.drawLine(-0.25f, -0.25f, 0.0f, 0.25f);
        Renderer.drawLine(0.0f, 0.25f, 0.25f, -0.25f);

        Renderer.flush(gl, false);

        gl.glDisable(GL10.GL_BLEND);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();
    }

    @SuppressWarnings("MagicNumber")
    public static void surfaceSizeChanged(GL10 gl) {
        gl.glMatrixMode(GL10.GL_PROJECTION);

        float size = 0.1f * (float)Math.tan(Math.toRadians(50.0) / 2);
        Renderer.loadIdentityAndFrustumf(gl, -size, size, -size / Common.ratio, size / Common.ratio, 0.1f, 100.0f);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }
}
