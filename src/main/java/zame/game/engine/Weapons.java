package zame.game.engine;

import javax.microedition.khronos.opengles.GL10;
import zame.game.AppConfig;
import zame.game.Common;
import zame.game.Renderer;

@SuppressWarnings("WeakerAccess")
public final class Weapons {
    public static class WeaponParams {
        public int[] cycle;
        public int ammoIdx;
        public int needAmmo;
        public int hits;
        public int hitTimeout;
        public int textureBase;
        public float xmult;
        public float xoff;
        public float hgt;
        public int soundIdx;
        public boolean isNear;
        public int noHitSoundIdx;

        public WeaponParams(int[] cycle,
                int ammoIdx,
                int needAmmo,
                int hits,
                int hitTimeout,
                int textureBase,
                float xmult,
                float xoff,
                float hgt,
                int soundIdx,
                boolean isNear,
                int noHitSoundIdx) {

            this.cycle = cycle;
            this.ammoIdx = ammoIdx;
            this.needAmmo = needAmmo;
            this.hits = hits;
            this.hitTimeout = hitTimeout;
            this.textureBase = textureBase;
            this.xmult = xmult;
            this.xoff = xoff;
            this.hgt = hgt;
            this.soundIdx = soundIdx;
            this.isNear = isNear;
            this.noHitSoundIdx = noHitSoundIdx;
        }
    }

    public static final int ENSURED_PISTOL_AMMO = 50;
    public static final int ENSURED_SHOTGUN_AMMO = 25;
    public static final int MAX_PISTOL_AMMO = 150;
    public static final int MAX_SHOTGUN_AMMO = 75;

    public static final int AMMO_PISTOL = 0;
    public static final int AMMO_SHOTGUN = 1;
    public static final int AMMO_LAST = 2;

    public static final int WEAPON_HAND = 0; // required to be 0
    public static final int WEAPON_PISTOL = 1;
    public static final int WEAPON_SHOTGUN = 2;
    public static final int WEAPON_CHAINGUN = 3;
    public static final int WEAPON_DBLSHOTGUN = 4;
    public static final int WEAPON_DBLCHAINGUN = 5;
    public static final int WEAPON_CHAINSAW = 6;
    public static final int WEAPON_LAST = 7;

    public static final WeaponParams[] WEAPONS = AppConfig.WEAPONS;

    public static WeaponParams currentParams;
    public static int[] currentCycle;
    public static int shootCycle=0;
    public static int changeWeaponDir=0;
    public static int changeWeaponNext=0;
    public static long changeWeaponTime=0;

    private Weapons() {
    }

    public static void init() {
        shootCycle = 0;
        changeWeaponDir = 0;
    }

    public static void updateWeapon() {
        currentParams = WEAPONS[State.heroWeapon];
        currentCycle = currentParams.cycle;
        shootCycle = 0;
    }

    public static void switchWeapon(int type) {
        changeWeaponNext = type;
        changeWeaponTime = Game.elapsedTime;
        changeWeaponDir = -1;
    }

    public static boolean hasNoAmmo(int weaponIdx) {
        return ((WEAPONS[weaponIdx].ammoIdx >= 0) && (State.heroAmmo[WEAPONS[weaponIdx].ammoIdx]
                < WEAPONS[weaponIdx].needAmmo));
    }

    public static void nextWeapon() {
        int resWeapon = (State.heroWeapon + 1) % WEAPON_LAST;

        while ((resWeapon != 0) && (!State.heroHasWeapon[resWeapon] || hasNoAmmo(resWeapon))) {
            resWeapon = (resWeapon + 1) % WEAPON_LAST;
        }

        switchWeapon(resWeapon);
    }

    public static int getBestWeapon() {
        int resWeapon = WEAPON_LAST - 1;

        while ((resWeapon > 0) && (!State.heroHasWeapon[resWeapon]
                || hasNoAmmo(resWeapon)
                || WEAPONS[resWeapon].isNear)) {

            resWeapon--;
        }

        if (resWeapon == 0) {
            resWeapon = WEAPON_LAST - 1;

            while ((resWeapon > 0) && (!State.heroHasWeapon[resWeapon]
                    || hasNoAmmo(resWeapon)
                    || !WEAPONS[resWeapon].isNear)) {

                resWeapon--;
            }
        }

        return resWeapon;
    }

    public static void selectBestWeapon() {
        int bestWeapon = getBestWeapon();

        if (bestWeapon != State.heroWeapon) {
            switchWeapon(bestWeapon);
        }
    }

    @SuppressWarnings("MagicNumber")
    public static void render(GL10 gl, long walkTime) {
        Renderer.setQuadRGBA(1.0f, 1.0f, 1.0f, 1.0f);

        Renderer.z1 = 0.0f;
        Renderer.z2 = 0.0f;
        Renderer.z3 = 0.0f;
        Renderer.z4 = 0.0f;

        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPushMatrix();
        Renderer.loadIdentityAndOrthof(gl, -Common.ratio, Common.ratio, 0.0f, 2.0f, 0.0f, 1.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glDisable(GL10.GL_ALPHA_TEST);

        Renderer.init();

        float yoff = 0;

        if (changeWeaponDir == -1) {
            yoff = (float)(Game.elapsedTime - changeWeaponTime) / 150.0f;

            if (yoff >= (currentParams.hgt + 0.1f)) {
                State.heroWeapon = changeWeaponNext;
                updateWeapon();

                changeWeaponDir = 1;
                changeWeaponTime = Game.elapsedTime;
            }
        } else if (changeWeaponDir == 1) {
            yoff = (currentParams.hgt + 0.1f) - ((float)(Game.elapsedTime - changeWeaponTime) / 150.0f);

            if (yoff <= 0.0f) {
                yoff = 0.0f;
                changeWeaponDir = 0;
            }
        }

        float xoff = ((float)Math.sin((double)walkTime / 150.0) * (Common.ratio / 8.0f)) + (Common.ratio / 8.0f);

        float xlt = (-Common.ratio * currentParams.xmult) + (Common.ratio * currentParams.xoff) + xoff;
        float xrt = (Common.ratio * currentParams.xmult) + (Common.ratio * currentParams.xoff) + xoff;

        yoff += (Math.abs((float)Math.sin(((double)walkTime / 150.0) + (Math.PI / 2.0))) * 0.1f) + 0.05f;
        float hgt = currentParams.hgt - yoff;

        Renderer.x1 = xlt;
        Renderer.y1 = -yoff;

        Renderer.x2 = xlt;
        Renderer.y2 = hgt;

        Renderer.x3 = xrt;
        Renderer.y3 = hgt;

        Renderer.x4 = xrt;
        Renderer.y4 = -yoff;

        //

        Renderer.u1 = 0.0f;
        Renderer.v1 = 1.0f;

        Renderer.u2 = 0.0f;
        Renderer.v2 = 0.0f;

        Renderer.u3 = 1.0f;
        Renderer.v3 = 0.0f;

        Renderer.u4 = 1.0f;
        Renderer.v4 = 1.0f;

        Renderer.drawQuad();

        // just for case
        if (shootCycle > currentCycle.length) {
            shootCycle = 0;
        }

        int weaponTexture = currentCycle[shootCycle];

        if (weaponTexture < -1000) {
            weaponTexture = -1000 - weaponTexture;
        } else if (weaponTexture < 0) {
            weaponTexture = -weaponTexture;
        }

        Renderer.bindTextureCtl(gl, TextureLoader.textures[currentParams.textureBase + weaponTexture]);
        Renderer.flush(gl);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();

        gl.glDisable(GL10.GL_ALPHA_TEST);
    }
}
