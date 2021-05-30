package com.example.mall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    //
    private NotificationManager mNotificationManager;
    private static Context mContext;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_shop, R.id.navigation_member, R.id.navigation_more).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //ibeacon
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind((BeaconConsumer) this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind((BeaconConsumer) this);
    }

    @Override
    public void onBeaconServiceConnect() {
        new Reminder();
    }


    private void sendNotification(String title, String body) {
        mContext = getApplicationContext();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext.getApplicationContext(), title);
        Intent ii = new Intent(mContext.getApplicationContext(), ShopFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(body);
        bigText.setBigContentTitle(title);
//        bigText.setSummaryText("Text in detail");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText("Your text");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
        //
    }

    private void sendNotification2(String title, String body) {
        mContext = getApplicationContext();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext.getApplicationContext(), title);
        Intent ii = new Intent(mContext.getApplicationContext(), ShopFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(body);
        bigText.setBigContentTitle(title);
//        bigText.setSummaryText("Text in detail");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText("Your text");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(1, mBuilder.build());
        //
    }



    public class Reminder {
        Timer timer;
        public Reminder() {
            //Get the local record
            SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
                String ad = pref.getString("ad", "");
                Log.d(TAG, "HELPME " + ad);
            if(pref.getString("ad", "").equals("on")){
                timer = new Timer();
                timer.schedule(new RemindTask(), 1 * 1000);
            }else{
                Toast.makeText(getApplicationContext(), "Turned off advertisements", Toast.LENGTH_SHORT).show();
            }
        }

        class RemindTask extends TimerTask {
            public void run() {
                if(getSharedPreferences("pref", Context.MODE_PRIVATE).getString("ad","").equals("on")) {
                    beaconManager.removeAllRangeNotifiers();
                    beaconManager.addRangeNotifier(new RangeNotifier() {
                        @Override
                        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                            Beacon.setHardwareEqualityEnforced(true);
                            try {
                                beaconManager.startRangingBeaconsInRegion(region);
                                for (Beacon b : beacons) {
                                    if (b.getBluetoothAddress().equals("05:9B:08:75:DB:A5") && b.getDistance() < 1.0) {
                                        //Din Tai Fung
                                        //send
//                                    sendNotification("Din Tai Fung is about " + String.format("%.2f", b.getDistance()) + " meters away. ");
                                        sendNotification("15% Off Din Tai Fung Coupons today!", "Click here to get more information");
                                        try {
                                            beaconManager.stopRangingBeaconsInRegion(region);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (b.getBluetoothAddress().equals("01:2D:80:1B:91:B1") && b.getDistance() < 1.0) {
                                        //Nike
                                        //send
                                        sendNotification2("25% Off Nike Coupons today!", "Click here to get more information");
                                        try {
                                            beaconManager.stopRangingBeaconsInRegion(region);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        }

                    });
                    try {
                        beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
                    } catch (RemoteException e) {
                    }
                    timer.cancel(); //Terminate the timer thread
                }
            }
        }
    }
}

