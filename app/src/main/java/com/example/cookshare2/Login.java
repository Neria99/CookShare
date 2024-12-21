package com.example.cookshare2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("");

    private MyServis myServiceMessages;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder mybinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyServis.SimpleBinder binder = (MyServis.SimpleBinder) mybinder;
            myServiceMessages= binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public static ArrayList<UsersF> arrUserF = new ArrayList<>();
    Button btConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        //Receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        MyReceiver receiver = new MyReceiver();
        registerReceiver(receiver, filter);


        //Server Permissions >>>
        ActivityCompat.requestPermissions(Login.this,
                new String[] {Manifest.permission.POST_NOTIFICATIONS},
                111);
        btConnection = findViewById(R.id.btConnection);

        btConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, Home.class);
                startActivity(intent);
            }
        });
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        Intent intent = new Intent(Login.this, MyServis.class);
//        startService(intent);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//    }
    private void getAllItem(ArrayList<UsersF> arr) {
        Query q = myRef.child("User");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //ArrayList<Recipe> arr = new ArrayList<>();
                arr.clear();
                for (DataSnapshot dsitem : snapshot.getChildren()) {
                    arr.add(dsitem.getValue(UsersF.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}