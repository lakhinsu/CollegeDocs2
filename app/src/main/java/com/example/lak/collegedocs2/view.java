package com.example.lak.collegedocs2;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class view extends AppCompatActivity {
    private ItemAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private ListView FileListView;
    private ArrayList<FileUploadInfo> myArrayList;
    private ItemAdapter i1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noitems);

        myArrayList=new ArrayList<>();




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                continuefun();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
        }






    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            continuefun();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"We need this permission !",Toast.LENGTH_SHORT).show();
        }
    }
    public void continuefun(){
        try {

            final ProgressDialog progDailog = ProgressDialog.show(this,
                    "Please Wait",
                    "Loading Data.....", true);
            new Thread() {
                public void run() {
                    try {
                        // sleep the thread, whatever time you want.
                        sleep(3000);
                    } catch (Exception e) {
                    }
                    progDailog.dismiss();
                }
            }.start();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Something Went Wrong",Toast.LENGTH_SHORT).show();
        }

        Intent i = getIntent();
        String msg[] = i.getStringArrayExtra("Selected");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("FileInformation").child(msg[0]).child(msg[1]).child(msg[2]).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //actually calls for each item in database
                setContentView(R.layout.activity_view);
                FileListView=(ListView)findViewById(R.id.viewlist1);
                i1=new ItemAdapter(getApplicationContext(),myArrayList);
                FileListView.setAdapter(i1);
                String filename = dataSnapshot.getKey();//return file name
                String url = dataSnapshot.getValue(String.class);//return url
                if (filename == null) {
                    Toast.makeText(getApplicationContext(), "No files", Toast.LENGTH_SHORT).show();
                }
                myArrayList.add(new FileUploadInfo("3rd Year", "SEM IV", "ADA", filename, url));
                i1.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}