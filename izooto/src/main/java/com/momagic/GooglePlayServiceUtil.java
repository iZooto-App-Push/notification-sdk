package com.momagic;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GooglePlayServiceUtil {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    public static boolean checkForPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(DATAB.appContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Toast.makeText(DATAB.appContext, "Play services not available or may be not updated.", Toast.LENGTH_SHORT);
            } else {
                Log.i(AppConstant.APP_NAME_TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }


}
