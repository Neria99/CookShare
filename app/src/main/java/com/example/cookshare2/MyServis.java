package com.example.cookshare2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyServis extends Service {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("");
    private Query q;
    private boolean notifyon = true;
    private final IBinder sBinder = (IBinder) new SimpleBinder();
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    @Override
    public void onCreate() {

        notificationSetUp();
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //the last entry on orders
        q = myRef.child("My Recipes").limitToLast(1);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                builder.setContentText("new msg =  "+dataSnapshot.getValue());
                if(notifyon) {
                    manager.notify(0, builder.build());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
    public class SimpleBinder  extends Binder {
        MyServis getService() {
            return MyServis.this;
        }
    }
    public IBinder onBind(Intent arg0) {
        return sBinder;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //accepting permissions because of the version
    private void notificationSetUp() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Service channel";
            String description = "Service channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("12", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        //Build a.png new notification - Builder
        builder = new NotificationCompat.Builder(this)
                .setChannelId("12")
                .setContentTitle("You have a new message in the firebase");

        Intent notificationIntent = new Intent(this, Login.class);

        //Postpone the intent using pendingIntent
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(contentIntent);

        // Add as notification
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
    public void notifychange() {
        notifyon = !notifyon;
    }
}
