package com.app.momagictest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.momagic.AppConstant;
import com.momagic.DATB;
import com.momagic.PreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private Button sendToken,sendSubID;
    private EditText editText;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText=findViewById(R.id.editText);
        sendSubID=findViewById(R.id.sendSubID);
        sendSubID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().length()>0 && editText.getText().toString()!=null)
                {
                    DATB.setSubscriberID(editText.getText().toString());
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please Enter the SubID",Toast.LENGTH_SHORT).show();
                }
            }
        });
//        sendToken=findViewById(R.id.sendToken);
//        sendToken.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                PreferenceUtil preferenceUtil=PreferenceUtil.getInstance(MainActivity.this);
//                String token=preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN);
//                if(token!=null && !token.isEmpty())
//                {
//                    sendEmail(token);
//                }
//            }
//        });



      // DATB.setSubscriberID("9807484803");
    }

    private void sendEmail(String token) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"amit@datability.co"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Device Token");
        i.putExtra(Intent.EXTRA_TEXT   , "Device Token->  "+token);
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
