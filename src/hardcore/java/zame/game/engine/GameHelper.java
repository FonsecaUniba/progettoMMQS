package zame.game.engine;

import android.content.Context;
import java.io.File;
import java.util.Locale;

public class GameHelper
{
    public static String initPaths(Context appContext)
    {
        String externalStoragePath = String.format(
            Locale.US,
            "%1$s%2$sAndroid%2$sdata%2$sorg.zamedev.gloomydungeons1hardcore.common",
            Game.getExternalStoragePath(),
            File.separator
        );

        File externalStorageFile = new File(externalStoragePath);

        if (!externalStorageFile.exists()) {
            externalStorageFile.mkdirs();
        }

        return externalStoragePath;
    }
}
