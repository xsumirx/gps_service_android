package location.ei.com.location;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Sumir on 10/30/2016.
 */
public class LocationUpdater extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */


    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    public LocationUpdater() {
        super("LocationUpdater");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Update
        Log.i("Location Update", "Service Invoked");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else
        {
            stopSelf(); //Google Api Client is not Initialize properly, Can't Proceed Futher
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                //Push Server Update
                Log.e("Intent","Server Updated");

                LogFile("\n" + DateFormat.getDateTimeInstance().format(new Date())
                +"   Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude());
            }
        }else
        {
            //Push a Notification
        }

        if(mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopSelf();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopSelf();
    }


    private void LogFile(String s)
    {
        try {
            File myFile = new File("/sdcard/mysdfile.txt");
            if(!myFile.exists())
                myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile,true);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(s);
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {

        }
    }
}
