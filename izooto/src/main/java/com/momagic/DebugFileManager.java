package com.momagic;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;

public class DebugFileManager {
     static boolean hasPermission()
    {
        return true;

    }
     static void createTempFolder(String folderName)
    {

    }
     static void addFileTempFolder(String folderName, File fileName,String jsonData)
    {

    }
     static boolean isFileExitNot(String folderName,File fileName)
    {
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
   static void createExternalStoragePublic(Context context,String fileName,String data) {
        try {
            File outputDirectory = GetPhotoDirectory("iz_notification", Util.getPackageName(context));
            GenerateTimeStampPhotoFileUri(context,outputDirectory,fileName+".txt",data);

        } catch (Exception e) {
            Log.w("ExternalStorage", "Error writing " , e);
        }
    }
     static void  GenerateTimeStampPhotoFileUri(Context context,File outputDirectory, String fileName,String data){

        if(outputDirectory!=null) {
            File file = new File(outputDirectory, fileName);
            PreferenceUtil preferenceUtil= PreferenceUtil.getInstance(context);

            try {
                FileWriter writer = new FileWriter(file);
                writer.append(data);
                writer.flush();
                writer.close();
                preferenceUtil.setBooleanData("FILECREATED",true);
            } catch (Exception e) {
                Log.e("FileCreated","Failed to creation"+e.toString());
                preferenceUtil.setBooleanData("FILECREATED",false);


            }
        }
    }
    public static File GetPhotoDirectory(String inWhichFolder, String yourFolderName ) {
        File outputDirectory = null;

        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {

            File pictureDirectory = Environment.getExternalStoragePublicDirectory(inWhichFolder);

            outputDirectory = new File(pictureDirectory, yourFolderName);
            if (!outputDirectory.exists()) {
                if (!outputDirectory.mkdirs()) {
                    Log.e("LogTag", "Failed to create directory: " + outputDirectory.getAbsolutePath());
                    outputDirectory = null;
                }
            }
        }
        return outputDirectory;
    }


}
