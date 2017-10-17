package zame.game.engine;

import android.content.Context;
import android.widget.Toast;
import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;
import zame.game.Common;
import zame.game.R;
import zame.game.ZameApplication;

/**
 * Class Representing Normal Game Helper
 */
@SuppressWarnings("WeakerAccess")
public final class GameHelper {
    /**
     * Class Constructor
     */
    private GameHelper() {
    }

    /**
     * Initializes External Storage Path
     * @param appContext App Context
     * @return External Storage Path
     */
    public static String initPaths(@SuppressWarnings("UnusedParameters") Context appContext) {
        String externalStoragePath = String.format(Locale.US,
                "%1$s%2$sAndroid%2$sdata%2$szame.GloomyDungeons.common",
                Game.getExternalStoragePath(),
                File.separator);

        File externalStorageFile = new File(externalStoragePath);

        if (!externalStorageFile.exists()) {
            String oldExternalStoragePath = String.format(Locale.US,
                    "%1$s%2$sAndroid%2$sdata%2$s{" + "PKG_COMMON" + "}",
                    Game.getExternalStoragePath(),
                    File.separator);

            externalStorageFile = checkOldPath();
        } else {
            String oldExternalStoragePath = String.format(Locale.US,
                    "%1$s%2$sAndroid%2$sdata%2$s{" + "PKG_COMMON" + "}",
                    Game.getExternalStoragePath(),
                    File.separator);

            File oldExternalStorageFile = new File(oldExternalStoragePath);

            if (oldExternalStorageFile.exists()) {
                // both old good folder and folder with bad name exists
                String[] files = oldExternalStorageFile.list();

                files = writeFiles();

                //noinspection ResultOfMethodCallIgnored
                oldExternalStorageFile.delete();

                if (ZameApplication.self != null) {
                    Toast.makeText(ZameApplication.self, R.string.msg_old_saves_restored, Toast.LENGTH_LONG).show();
                }
            }
        }

        return externalStoragePath;
    }
}
