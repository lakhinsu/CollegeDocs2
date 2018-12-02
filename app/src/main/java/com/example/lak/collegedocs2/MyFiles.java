package com.example.lak.collegedocs2;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class MyFiles extends AppCompatActivity {

    ListView FilesList;

    String path;
    File directory;

    boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_files);
        FilesList=findViewById(R.id.myfileslist);

        final ArrayList<String> files=new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                flag=true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
        }

        if(flag==true) {
            path=Environment.DIRECTORY_DOWNLOADS+"/CollegeDocs";

            directory=new File(Environment.getExternalStoragePublicDirectory("CollegeDocs").getAbsolutePath());
            File[] filesarry = directory.listFiles();


            Log.d("FILESARRAY", "" + directory.getAbsolutePath());
            Log.d("FILESARRAY", "" + filesarry);
            Log.d("FILESARRAY",""+directory.isDirectory());

            if(filesarry!=null) {

                for (int i = 0; i < filesarry.length; i++) {
                    files.add(filesarry[i].getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, files);

                FilesList.setAdapter(adapter);

                FilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String name = files.get(i);
                        File file = new File(Environment.getExternalStoragePublicDirectory("CollegeDocs") + File.separator + name);
                        Uri uri = Uri.parse(file.getAbsolutePath());

                        try {

                            if (uri.toString().contains(".pdf")) {
                                // PDF file
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                Intent intent1 = Intent.createChooser(intent, "Open With");
                                startActivity(intent);
                            } else if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
                                // Word document
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(file), "application/msword");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                Intent intent1 = Intent.createChooser(intent, "Open With");
                                startActivity(intent);

                            }
                        }catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"Something went Wrong , Try opening it from file manager",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please Grant Permission",Toast.LENGTH_SHORT).show();
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            flag=true;
        }
        else
        {
            Toast.makeText(getApplicationContext(),"We need this permission !",Toast.LENGTH_SHORT).show();
        }
    }
}
