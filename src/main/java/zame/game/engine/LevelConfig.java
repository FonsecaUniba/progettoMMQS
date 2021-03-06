package zame.game.engine;

import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Class representing Level Configuration
 */
@SuppressWarnings("WeakerAccess")
public class LevelConfig {
    /**
     * Constant for Bite Hit
     */
    public static final int HIT_TYPE_EAT = 0;
    /**
     * Constant for Pistol Hit
     */
    public static final int HIT_TYPE_PIST = 1;
    /**
     * Constant for Shotgun hit
     */
    public static final int HIT_TYPE_SHTG = 2;

    /**
     * Class representing Monster Configuration
     */
    public static class MonsterConfig {
        int texture;
        int health;
        int hits;
        int hitType;

        /**
         * Class Constructor
         * @param texture Monster Texture
         * @param health Monster Health value
         * @param hits Monster Hit value
         * @param hitType Monster Hit Type
         */
        public MonsterConfig(int texture, int health, int hits, int hitType) {
            this.texture = texture;
            this.health = health;
            this.hits = hits;
            this.hitType = hitType;
        }
    }

    /**
     * Current Level ID
     */
    public int levelNum;
    /**
     * Floor texture
     */
    public int floorTexture;
    /**
     * Ceiling texture
     */
    public int ceilTexture;
    /**
     * Configuration for all monsters
     */
    public MonsterConfig[] monsters;

    /**
     * Class Constructor
     * @param levelNum Current Level ID
     */
    public LevelConfig(int levelNum) {
        this.levelNum = levelNum;
        this.floorTexture = 2;
        this.ceilTexture = 2;

        //noinspection MagicNumber
        this.monsters = new MonsterConfig[] { new MonsterConfig(1, 4, 4, HIT_TYPE_PIST),
                new MonsterConfig(2, 8, 8, HIT_TYPE_SHTG),
                new MonsterConfig(3, 32, 32, HIT_TYPE_EAT),
                new MonsterConfig(4, 64, 64, HIT_TYPE_EAT), };
    }

    /**
     * Reads Level Configuration from file
     * @param assMan Asset Manager
     * @param currLev Current Level ID
     * @return True if read successfully, false otherwise
     */
    public static LevelConfig read(AssetManager assMan, int currLev) {
        LevelConfig res = new LevelConfig(currLev);

        try {
            InputStreamReader isr = new InputStreamReader(assMan.open(String.format(Locale.US,
                    "config/level-%d.txt",
                    currLev)), "UTF-8");

            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();

            if (line != null) {
                String[] spl = line.split(" ");

                if (spl.length == 2) {
                    res.floorTexture = Integer.parseInt(spl[0]);
                    res.ceilTexture = Integer.parseInt(spl[1]);

                    for (int i = 0; i < 4; i++) {
                        line = br.readLine();

                        if (line == null) {
                            break;
                        }

                        spl = line.split(" ");

                        if (spl.length != 4) {
                            break;
                        }

                        res.monsters[i].texture = Integer.parseInt(spl[0]);
                        res.monsters[i].health = Integer.parseInt(spl[1]);
                        res.monsters[i].hits = Integer.parseInt(spl[2]);
                        res.monsters[i].hitType = Integer.parseInt(spl[3]);
                    }
                }
            }

            br.close();
        } catch (IOException ex) {
            //throw new RuntimeException(ex);
            Logger.getAnonymousLogger().log(java.util.logging.Level.SEVERE, "Error reading file", ex);
        } catch (NumberFormatException ex) {
            //throw new RuntimeException(ex);
            Logger.getAnonymousLogger().log(java.util.logging.Level.SEVERE, "Wrong Number Format", ex);
        }

        return res;
    }
}
