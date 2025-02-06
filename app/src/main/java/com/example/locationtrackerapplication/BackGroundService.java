package com.example.locationtrackerapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackGroundService extends Service
        implements   LocationListener, SensorEventListener {
    private LocationManager locationManager;
     private final String TAGB = "BackgroundService";
    private final String TAG_LOCATION = "TAG_LOCATION";
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    public static BackGroundCallBack createOrderCallback;

    public static SocketTimerCallback socketTimerCallback;
    public static CountDownTimerCallback timercallback;
    public static CountDownTimer countDownTimer;
    private Context context = null;
    private boolean stopService = false;
    // private double direction = 0.0;
    float direction;
    private final long delay = 1*30*1000L;
    private  float displacement = 100f;

    private float idleupdatetime= 15;
    private long apiCallTime;
    private Location currentLoc = null;
    private Handler mainHandler = null;
    private Runnable runnableUpdateApi = null;

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    private FusedLocationProviderClient mFusedLocationClient = null;

    private LocationRequest mLocationRequest = null;
    private Location mCurrentLocation = null;

    int counter = 0;
    boolean isBackApicall = false;
    Location currentLocation;
    double lat, lng;

    //  private  final String TAG = "PostLoginActivity";
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //  checkLocation();
        //   showLocationPrompt();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StartForeground();

        if (mainHandler == null) {

           /* if (!stopService) {
                requestNew();
            } else {
                Log.i(TAGB, "isGPSEnabled false " + stopService);
            }*/
            initHandler();
        } else {
            Log.i(TAGB, "mainHandler already running");
        }

        return START_STICKY;
    }

    private void initHandler() {
        Log.i(TAGB, "mainHandler initHandler ");
        mainHandler = new Handler();
        runnableUpdateApi = new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAGB, "isGPSEnabled check " + stopService);
                    /*if(!stopService){
                        if(!DrivingOrderThali.getInstance().socketClient.mSocket.connected()) {
                            if (DrivingOrderThali.getInstance().fetchBoolean("isDuty", false)) {
                                getFcmTokenJoin();
                            }
                        }
                    }*/
                    if (!stopService) {
                        requestNew();
                    } else {
                        Log.i(TAGB, "isGPSEnabled false " + stopService);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mainHandler.postDelayed(this, delay);
                }
            }
        };

        mainHandler.post(runnableUpdateApi);
    }





    private void stopUpdateService() {
        Log.i("LocationService", "stopUpdateService");
        stopService = true;
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(onlocchanged);
            Log.i(TAG_LOCATION, "Location Update Callback Removed");
        }
        if (runnableUpdateApi != null) {
            mainHandler.removeCallbacks(runnableUpdateApi);
            mainHandler.removeCallbacksAndMessages(runnableUpdateApi);
            mainHandler.removeCallbacksAndMessages(null);
        }


        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
       /* if (wakeLock.isHeld()) {
            wakeLock.release();
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAGB, "Service onDestroy");

        stopUpdateService();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void StartForeground() {
        // val pendingIntent = PendingIntent.getActivity(this, 0  Request code , intent, PendingIntent.FLAG_ONE_SHOT)
        String CHANNEL_ID = "channel_location";
        String CHANNEL_NAME = "channel_location";

         notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            builder.setChannelId(CHANNEL_ID);
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        }

        builder.setContentTitle("Location Update Service");
        builder.setContentText("You are now online");
        Uri notificationSound = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(notificationSound);
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.drawable.ic_launcher_background);

        // Set the notification priority to high
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        Notification notification = builder.build();
        try {
            startForeground(101, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateNotificationMessage(String message) {
        // Update notification text here
        if (notificationManager != null) {
            Uri notificationSound = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(notificationSound);
            // Set updated notification text
            builder.setContentText(message);
            notificationManager.notify(11, builder.build());
        }
    }

    // Rest of the code continues...
    @SuppressLint("MissingPermission")
    private void requestNew() {
        // getLocationUpdates();
        showLocationPrompt();

    }



    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    void callUpdateLocationApi(Location value, String from) {
        // DrivingOrderThali.getInstance().socketClient.setOrderAssignCallback(this);
        if (value != null) {
            currentLocation = value;
            lat = currentLocation.getLatitude();
            lng = currentLocation.getLongitude();
            direction = currentLocation.getBearing();

            // Place your API call code here
            String apiUrl = "driver/update-location";
            String contentType = "application/json";
               /* String authToken;
                authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoyNDgsImZpcnN0X25hbWUiOiJTdXJhaiIsImxhc3RfbmFtZSI6IkRvZSIsInJvbGUiOiJkZWxpdmVyeV9ib3kiLCJpYXQiOjE2ODc1MTU1NTR9.URNIBPTJ7eYxEF3Yk6WgG1YXVYFCgdht4O7wuWehq70";
*/

            updateNotificationMessage("Location update successfully at " +getCurrentTime());
            Toast.makeText(context, "location update successfully", Toast.LENGTH_SHORT).show();

            Log.d(TAGB, "currentLatLng::-  from :-" + from + lat + "\n" + lng);

            LocationUpdateRequest locationUpdate = new LocationUpdateRequest(lat, lng, direction);
            apiCallTime = System.currentTimeMillis();
            Call<LocationUpdate> call = RetrofitClient.getInstance().getMyApi().updateLocation(apiUrl, contentType,"" ,locationUpdate);
            call.enqueue(new Callback<LocationUpdate>() {
                @Override
                public void onResponse(Call<LocationUpdate> call, Response<LocationUpdate> response) {
                    Log.d(TAGB, "onResponse: " + response.body());

                    if (response.body() != null && response.body().getDuty_status() != null) {
                        //  processDuty(response.body().getDuty_status());


                        if (response.body().getVariables().get(1).getName().contains("driver_location_update_displacement ")) {
                            displacement = Float.parseFloat(response.body().getVariables().get(1).getValue());
                        }
                         //   Toast.makeText(BackGroundService.this, "Location update successfully at " +getCurrentTime(), Toast.LENGTH_SHORT).show();
                        updateNotificationMessage("Location update successfully at " +getCurrentTime());
                        Toast.makeText(context, "location update successfully", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<LocationUpdate> call, Throwable t) {
                    Log.d(TAGB, "onFailure: " + t.getMessage());
                    updateNotificationMessage("Location update successfully at " +getCurrentTime());

                    Toast.makeText(context, "location update successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i(TAGB, "onLocationChanged: " + location);
        currentLocation = location;

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }


    /*@Override
    public void onActivityStartCallback(Context context) {
        restartApp();
    }*/


    public interface BackGroundCallBack {
        void onBackgroundCallback(String mdata);
    }

    public interface SocketTimerCallback {
        void onSocketCheck(String mdata);
    }

    public interface CountDownTimerCallback {
        void timerCallback(String mdata, long millisUntilFinished);


    }




    private void showLocationPrompt() {
        Log.i(TAGB, "showLocationPrompt");
        mLocationRequest = new com.google.android.gms.location.LocationRequest()
                .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(delay)
                .setSmallestDisplacement(displacement)
                .setFastestInterval(10000)
                .setMaxWaitTime(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                    getlastLocation();
                } catch (ApiException exception) {
                    int statusCode = exception.getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            getLocationUpdatesInternet();
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                               /* resolvable.startResolutionForResult(
                                        this, com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);*/
                                Log.i(TAGB, "response " + exception);
                                // getlastLocation();

                            } catch (Exception e) {
                                // Ignore the error.
                                Log.i(TAGB, "response " + e);
                                // getlastLocation();
                            }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            Log.i(TAGB, "response Location settings are not satisfied. But could be fixed by showing the");
                            break;
                    }
                }
            }
        });


    }


    private void getLocationUpdatesInternet() {
        Log.d(TAGB, "getLocationUpdatesInternet");
        // Implement your location update logic using LocationManager or FusedLocationProviderClient
        // Example: use LocationManager

        // Here, you can request location updates using LocationManager
        // Make sure you have the necessary permissions and handle location updates

        // Sample code to obtain location using LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, displacement, new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i(TAGB, "getLocationUpdatesInternet onStatusChanged" + provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i(TAGB, "getLocationUpdatesInternet onProviderEnabled" + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i(TAGB, "getLocationUpdatesInternet onProviderDisabled" + provider);
                enableLocationSettings();
            }

            @Override
            public void onLocationChanged( Location location) {

                // Called when a new location is found by the network location provider.
                if (location != null && counter==0) {
                    long currentTime = System.currentTimeMillis();
                    long timeElapsed = currentTime - apiCallTime;
                    Log.i(TAGB, "locationListener " + location.getLongitude() + " Bearing:-" + location.getBearing() + " isMock:-" + location.isFromMockProvider());
                    if(mCurrentLocation==null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        mCurrentLocation = location;
                        currentLocation = location;
                        Gson gson = new Gson();
                        callUpdateLocationApi(mCurrentLocation, "backgroundservice NETWORK_PROVIDER");
                    }else {

                       /* if (mCurrentLocation.distanceTo(location) > displacement) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();


                            mCurrentLocation = location;
                            currentLocation = location;
                            Gson gson = new Gson();
                            callUpdateLocationApi(mCurrentLocation, "backgroundservice NETWORK_PROVIDER");
                        }else if (timeElapsed > idleupdatetime * 60 * 1000) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();


                            mCurrentLocation = location;
                            currentLocation = location;
                            Gson gson = new Gson();
                            callUpdateLocationApi(mCurrentLocation, "backgroundservice NETWORK_PROVIDER after 15 minutes");
                        }
                    */

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        mCurrentLocation = location;
                        currentLocation = location;
                        Gson gson = new Gson();
                        callUpdateLocationApi(mCurrentLocation, "backgroundservice ");

                    }
                    Log.i(TAGB, "getLocationUpdatesInternet onLocationChanged " +mCurrentLocation.distanceTo(location));
                    if (locationManager != null) {
                        locationManager.removeUpdates(this);
                    }
                    // repeatFunctionAfter1minute();


                    //       DrivingOrderThali.getInstance().locationUpdatePublish.onNext(mCurrentLocation);



                    //  DrivingOrderThali.getInstance().locationPublish.onNext(location);


                }
            }
        });
    }

    // Implement your location listener here if using LocationManager

    private void enableLocationSettings() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // The network provider is not enabled, prompt the user to enable it
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this flag
            startActivity(intent);
        }
    }


    public void getlastLocation() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        Log.i(TAGB, "getlastLocation" + counter + mCurrentLocation);


        mFusedLocationClient.requestLocationUpdates(mLocationRequest, onlocchanged, Looper.myLooper());
        if (counter==0){
            getLocationUpdatesInternet();
        }
       /* getLocationUpdateGPS();
        if (counter==0){
            getLocationUpdatesInternet();
        }*/

    }

    private void getLocationUpdateGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, displacement, new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i(TAGB, "locationListener onStatusChanged" + provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i(TAGB, "locationListener onProviderEnabled" + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i(TAGB, "locationListener onProviderDisabled" + provider);
                // enableLocationSettings();
            }

            @Override
            public void onLocationChanged( Location location) {

                // Called when a new location is found by the network location provider.
                if (location != null) {
                    Log.i(TAGB, "locationListener " + location.getLongitude() + " Bearing:-" + location.getBearing() + " isMock:-" + location.isFromMockProvider());
                    counter=1;
                    // Calculate the time elapsed since the API call
                    long currentTime = System.currentTimeMillis();
                    long timeElapsed = currentTime - apiCallTime;
                    if(mCurrentLocation==null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        mCurrentLocation = location;
                        currentLocation = location;
                        Gson gson = new Gson();
                        callUpdateLocationApi(mCurrentLocation, "backgroundservice GPS_PROVIDER ");
                    }else {

                       /* if (mCurrentLocation.distanceTo(location) > displacement) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();


                            mCurrentLocation = location;
                            currentLocation = location;
                            Gson gson = new Gson();
                            callUpdateLocationApi(mCurrentLocation, "backgroundservice GPS_PROVIDER");
                        } else if (timeElapsed > idleupdatetime * 60 * 1000) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();


                            mCurrentLocation = location;
                            currentLocation = location;
                            Gson gson = new Gson();
                            callUpdateLocationApi(mCurrentLocation, "backgroundservice GPS_PROVIDER after 15 minutes");
                        }
*/

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        mCurrentLocation = location;
                        currentLocation = location;
                        Gson gson = new Gson();
                        callUpdateLocationApi(mCurrentLocation, "backgroundservice GPS_PROVIDER ");

                    }




                    Log.i(TAGB, "getLocationUpdatesGPS onLocationChanged " +mCurrentLocation.distanceTo(location));
                    if (locationManager != null) {
                        locationManager.removeUpdates(this);
                    }
                    // repeatFunctionAfter1minute();


                    //       DrivingOrderThali.getInstance().locationUpdatePublish.onNext(mCurrentLocation);



                    //  DrivingOrderThali.getInstance().locationPublish.onNext(location);


                }

            }
        });
       // mCurrentLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    public LocationCallback onlocchanged = new LocationCallback() {

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            Log.i(TAGB, "onLocationAvailability");
            super.onLocationAvailability(locationAvailability);
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            counter=1;

            Location location = locationResult.getLastLocation();

            if (location != null) {
                long currentTime = System.currentTimeMillis();
                long timeElapsed = currentTime - apiCallTime;
                Log.i(TAGB, "locatiionfetch " + location.getLongitude() +" Bearing:-"+location.getBearing()+ " isMock:-"+location.isFromMockProvider());
                if(mCurrentLocation==null){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    mCurrentLocation = location;
                    currentLocation = location;
                    Gson gson = new Gson();
                    callUpdateLocationApi(mCurrentLocation, "backgroundservice ");
                }else {

                    /*if (mCurrentLocation.distanceTo(location) > displacement) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();


                        mCurrentLocation = location;
                        currentLocation = location;
                        Gson gson = new Gson();
                        callUpdateLocationApi(mCurrentLocation, "backgroundservice ");
                    } else if (timeElapsed > idleupdatetime * 60 * 1000) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();


                        mCurrentLocation = location;
                        currentLocation = location;
                        Gson gson = new Gson();
                        callUpdateLocationApi(mCurrentLocation, "backgroundservice  after 15 minutes");
                    }*/

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    mCurrentLocation = location;
                    currentLocation = location;
                    Gson gson = new Gson();
                    callUpdateLocationApi(mCurrentLocation, "backgroundservice ");
                }
                Log.i(TAGB, "onlocchanged calling "+mCurrentLocation.distanceTo(location));
                mFusedLocationClient.removeLocationUpdates(onlocchanged);
               // repeatFunctionAfter1minute();


             //       DrivingOrderThali.getInstance().locationUpdatePublish.onNext(mCurrentLocation);




              //  DrivingOrderThali.getInstance().locationPublish.onNext(location);

            }else{
                getLocationUpdatesInternet();
            }
        }
    };





}
