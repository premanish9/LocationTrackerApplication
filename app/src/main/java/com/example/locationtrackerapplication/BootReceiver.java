package com.example.locationtrackerapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


public class BootReceiver extends BroadcastReceiver {
    String TAG="BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Start your service here

                    Intent serviceIntent = new Intent(context, BackGroundService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               /* if (DrivingOrderThali.getInstance().isAppInBackground()) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }*/

                        context.startForegroundService(serviceIntent);
                    } else {
                        context.startService(serviceIntent);
                    }

                }
            }
        }


