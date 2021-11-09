package com.app.momagictest;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.momagic.AppConstant;
import com.momagic.DATB;
import com.momagic.DebugFileManager;
import com.momagic.PreferenceUtil;


public class MainActivity extends AppCompatActivity {
    private EditText editText;
    String url = "https://www.izooto.com";
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST=0x5;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //askForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
       // askForReadPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);

        Button sendDebugFile = findViewById(R.id.sendDebugFile);
       sendDebugFile.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               DATB.shareFile(MainActivity.this,"","");
           }
       });
        Button beginDebugFile = findViewById(R.id.beginDebugFile);
        beginDebugFile.setOnClickListener(v -> {
            DATB.createDirectory(MainActivity.this);

//            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(MainActivity.this);
//            String token = preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN);
//            if (token != null && !token.isEmpty()) {
//                sendEmail(token);
//            }


        });
        Button deleteDebugFile = findViewById(R.id.deleteDebugFile);
        deleteDebugFile.setOnClickListener(v -> {
            DATB.deleteDirectory(MainActivity.this);
           // DATB.createDirectory(MainActivity.this);

//            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(MainActivity.this);
//            String token = preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN);
//            if (token != null && !token.isEmpty()) {
//                sendEmail(token);
//            }


        });

        // below line is setting toolbar color
        // for our custom chrome tab.
        //customIntent.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));

        // we are calling below method after
        // setting our toolbar color.
      //  openCustomTab(MainActivity.this, customIntent.build(), Uri.parse(url));
      Button   permissionFIle =findViewById(R.id.permissionFIle);
      permissionFIle.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              requestPermission();
          }
      });


    }
    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.R) {
            Log.e("Android11","Permission");
            try {
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 2296);
                }

            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            Log.e("Android11 below","Permission");

            askForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
            askForReadPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (30 >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //  Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
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

    private void askForReadPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            //  Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

  

}
