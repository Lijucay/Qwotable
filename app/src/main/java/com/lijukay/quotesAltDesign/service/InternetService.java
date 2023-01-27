package com.lijukay.quotesAltDesign.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.lijukay.quotesAltDesign.activities.Information;
import com.lijukay.quotesAltDesign.activities.Person;
import com.lijukay.quotesAltDesign.activities.Settings;
import com.lijukay.quotesAltDesign.fragments.home;
import com.lijukay.quotesAltDesign.fragments.quotes;
import com.lijukay.quotesAltDesign.fragments.wisdom;

public class InternetService extends Service {

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

    public boolean isOnline(Context c){
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        return ni != null && ni.isConnectedOrConnecting();
    }



    final Handler handler = new Handler();
    private final Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 1000 - SystemClock.elapsedRealtime()%1000);
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(Settings.BroadcastStringForAction);
            broadCastIntent.putExtra("online_status", ""+isOnline(InternetService.this));
            sendBroadcast(broadCastIntent);
            Intent broadCastIntentMain = new Intent();
            broadCastIntentMain.setAction(home.BroadcastStringForAction);
            broadCastIntentMain.putExtra("online_status", ""+isOnline(InternetService.this));
            sendBroadcast(broadCastIntentMain);
            Intent broadCastIntentQuote = new Intent();
            broadCastIntentQuote.setAction(quotes.BroadCastStringForAction);
            broadCastIntentQuote.putExtra("online_status", ""+isOnline(InternetService.this));
            sendBroadcast(broadCastIntentQuote);
            Intent broadCastIntentWisdom = new Intent();
            broadCastIntentWisdom.setAction(wisdom.BroadCastStringForAction);
            broadCastIntentWisdom.putExtra("online_status", ""+isOnline(InternetService.this));
            sendBroadcast(broadCastIntentWisdom);
            Intent broadCastIntentInformation = new Intent();
            broadCastIntentInformation.setAction(Information.BroadCastStringForAction);
            broadCastIntentInformation.putExtra("online_status", ""+isOnline(InternetService.this));
            sendBroadcast(broadCastIntentInformation);
            Intent broadCastIntentPerson = new Intent();
            broadCastIntentPerson.setAction(Person.BroadCastStringForAction);
            broadCastIntentPerson.putExtra("online_status", ""+isOnline(InternetService.this));
            sendBroadcast(broadCastIntentPerson);
        }
    };
}
