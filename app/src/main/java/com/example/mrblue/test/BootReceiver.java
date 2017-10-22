package com.example.mrblue.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mr.Blue on 10/22/2017.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        Intent serviceIntent = new Intent(context, ShakeService.class);
        context.startService(serviceIntent);
    }

}