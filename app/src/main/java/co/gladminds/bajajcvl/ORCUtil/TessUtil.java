package co.gladminds.bajajcvl.ORCUtil;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Nikhil on 22-02-2018.
 */

public class TessUtil {

    public static void copyFiles(Context context) {
        try {
            String datapath = context.getFilesDir() + "/tesseract/";

            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = context.getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkFile(File dir, Context context) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(context);
        }
        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String datapath = context.getFilesDir() + "/tesseract/";
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(context);
            }
        }
    }
}
