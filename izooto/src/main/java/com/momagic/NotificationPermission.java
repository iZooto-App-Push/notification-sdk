package com.momagic;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.HashMap;
import java.util.Map;
public class NotificationPermission extends Activity {
    private static final int NOTIFICATION_PERMISSION_CODE = 123456;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission(android.Manifest.permission.POST_NOTIFICATIONS, NOTIFICATION_PERMISSION_CODE);
    }
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {

            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (DATB.senderId != null && !DATB.senderId.isEmpty()) {
                        DATB.init(DATB.appContext, DATB.senderId);
                    } else {
                        Lg.e(AppConstant.APP_NAME_TAG, DATB.appContext.getString(R.string.something_wrong_fcm_sender_id));
                    }
                    finish();
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    finish();
                } else {
                    finish();
                }

            }
            overridePendingTransition(R.anim.datb_notification_permission_fade_in, R.anim.datb_notification_permission_fade_out);
        }
    }

}


