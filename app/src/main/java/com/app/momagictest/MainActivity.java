package com.app.momagictest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.momagic.AppConstant;
import com.momagic.DATB;
import com.momagic.Payload;
import com.momagic.PreferenceUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText = findViewById(R.id.editText);
        Button sendSubID = findViewById(R.id.sendSubID);
        sendSubID.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        sendSubID.setOnClickListener(v -> {
            if (editText.getText().toString().length() > 0) {
                DATB.setSubscriberID(editText.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Please Enter the SubID", Toast.LENGTH_SHORT).show();
            }
        });
        Button sendToken = findViewById(R.id.sendToken);
        sendToken.setOnClickListener(v -> {

            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(MainActivity.this);
            String token = preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN);
            if (token != null && !token.isEmpty()) {
                sendEmail(token);
            }

//            List<Payload> payloadList=DATB.getNotificationList(MainActivity.this);
//            assert payloadList != null;
//            if(payloadList.size()>0) {
//               Log.e("CountData","" + payloadList.size());
//           }
        });

//        File file = new File(MainActivity.this.getFilesDir(), "text");
//        if (!file.exists()) {
//            file.mkdir();
//        }
//        try {
//            File gpxfile = new File(file, "sample");
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append("Amit kumar Gupta");
//            writer.flush();
//            writer.close();
//            Log.e("SuccessFull1","SuccessFull");
//            Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            Log.e("SuccessFull2","SuccessFull"+e.toString());
//
//        }
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("FCMTOKEN","tokenData");
            jsonObject.put("AndroidID","bckfgsdsdsdsdsdsdsds");
            jsonObject.put("SDKVERSION","1.1.6");
            jsonObject.put("AppVersion","2.0.5");
            jsonObject.put("ADID","ADID");

        }
        catch (Exception ex)
        {

        }

//        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/folderDir/");
//        Log.e("Filepath is",Environment.getExternalStorageDirectory().getPath());
//        dir.mkdirs();

        try
        {
           // File myDir = MainActivity.this.getFilesDir();
          //  Log.e("File1",myDir.toString());
            // Documents Path
//            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "documents");
//
//            String documents = "documents/data";
//            File documentsFolder = new File(mediaStorageDir, documents);
//            documentsFolder.mkdirs(); // this line creates data folder at documents directory
//
//            String publicC = "documents/public/com.izooto";
//            File publicFolder = new File(mediaStorageDir, publicC);
//            publicFolder.mkdirs();
          //  String path = Environment.getExternalStoragePublicDirectory().getAbsolutePath().toString() + "/storage/emulated/0/appFolder";

           // File mFolder = new File(path);
//            if (!mFolder.exists()) {
//                mFolder.mkdir();
//            }
//            File directory = new File(Environment.getExternalStorageDirectory(),"/momagic");
//            if(!directory.exists())
//            {
//                Log.e("File ","Directory does not exits");
//
//            }
//            else
//            {
//                Log.e("File ","Directory  exits");
//
//            }
//            String root_sd = Environment.getExternalStorageDirectory().toString();
//            Log.e("RootSD",root_sd);
//
////            File f = new File(path);//converted string object to file
////            String[] values = f.list();
////            for(int i=0;i<values.length;i++)
////            {
////                Log.e("FileName",values[i]);
////            }
//            File docs = new File(Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOWNLOADS), "YourAppDirectory");
//// Make the directory if it does not yet exist
//            docs.mkdirs();
         //   createExternalStoragePublic();
        }
        catch (Exception ex)
        {
            Log.e("AppTag",ex.toString());
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void createExternalStoragePublic() {


        try {
            File outputDirectory = GetPhotoDirectory(Environment.DIRECTORY_NOTIFICATIONS, "com.momagictest");
            GenerateTimeStampPhotoFileUri(outputDirectory,"pid.txt");

        } catch (Exception e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " , e);
        }
    }
    public  void  GenerateTimeStampPhotoFileUri(File outputDirectory, String fileName){

        if(outputDirectory!=null) {
            File file = new File(outputDirectory, fileName);

        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("FCMTOKEN","FCMTOKEN");
            jsonObject.put("BKEY","androidID");
            jsonObject.put("PID","500");
            jsonObject.put("ADID","ADID");
            FileWriter writer = new FileWriter(file);
            writer.append(jsonObject.toString());
            writer.flush();
            writer.close();
            Log.e("SuccessFull1","SuccessFull");
            Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("SuccessFull2","SuccessFull"+e.toString());

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



    private void sendEmail(String token) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"amit@datability.co"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Device Token");
        i.putExtra(Intent.EXTRA_TEXT, "Device Token->  " + token);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


  

}
