package dk.reflevel.Tracker;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Grishma on 17/5/16.
 */
public class Const {

    public static final String DATABASE_NAME = "Location_master.sqlite"; //Change it
    public static final String DATABASE_PATH = "/data/data/dk.reflevel/databases/"; //Change it
    public static final int DATABASE_VERSION = 1;

    /**
     * Method to get address from latitude and longitude.
     * @return
     */
    public static String getCompleteAddressString() {
		return "Address template";
    }

    public static void ExportDatabase(Context mcont) {
        File f = new File(DATABASE_PATH + DATABASE_NAME);
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(f);
            fos = new FileOutputStream("/mnt/sdcard/" + DATABASE_NAME);
            while (true) {
                int i = fis.read();
                if (i != -1) {
                    fos.write(i);
                } else {
                    break;
                }
            }
            fos.flush();
            Toast.makeText(mcont, "Database Export Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mcont, "DB dump ERROR", Toast.LENGTH_SHORT).show();
        }
    }
}
