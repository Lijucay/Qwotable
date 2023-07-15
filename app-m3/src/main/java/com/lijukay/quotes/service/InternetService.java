package com.lijukay.quotes.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.lijukay.quotes.activities.MainActivity;
import com.lijukay.quotes.activities.Person;

public class InternetService extends Service {

    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(periodicUpdate);
        return START_STICKY;
    }

    public boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        return ni != null && ni.isConnectedOrConnecting();
    }

    private final Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 1000 - SystemClock.elapsedRealtime() % 1000);
            Intent broadCastIntentMain = new Intent();
            broadCastIntentMain.setAction(MainActivity.BROAD_CAST_STRING_FOR_ACTION);
            broadCastIntentMain.putExtra("online_status", "" + isOnline(InternetService.this));
            sendBroadcast(broadCastIntentMain);
            Intent broadCastIntentPerson = new Intent();
            broadCastIntentPerson.setAction(Person.BROAD_CAST_STRING_FOR_ACTION);
            broadCastIntentPerson.putExtra("online_status", "" + isOnline(InternetService.this));
            sendBroadcast(broadCastIntentPerson);
        }
    };
}
