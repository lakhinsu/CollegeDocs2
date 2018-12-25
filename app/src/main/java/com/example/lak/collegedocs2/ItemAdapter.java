package com.example.lak.collegedocs2;

/**
 * Created by Lak on 01-12-2018.
 */

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.Manifest.permission.*;
import android.widget.Toast;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.content.ContextCompat.startActivities;
import static android.support.v4.content.ContextCompat.startActivity;

public class ItemAdapter extends ArrayAdapter<FileUploadInfo>
{
    SharedPreferences sharedPreferences;
    boolean flag=false;
    long refid;
    public ItemAdapter(Context context, ArrayList<FileUploadInfo> arrayList)
    {
        super(context,0,arrayList);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final FileUploadInfo n1=getItem(position);
        final String url=n1.getUrl();
        if(convertView==null)
        {
            convertView=LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView name=(TextView)convertView.findViewById(R.id.nameoffile);
        name.setText(n1.getType());
        final ImageButton download=(ImageButton)convertView.findViewById(R.id.button_d);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent();
                /*i1.setType(Intent.ACTION_VIEW);
                i1.setData(Uri.parse(url));
                startActivity(getContext().getApplicationContext(),i1,null);*/

                sharedPreferences=getContext().getSharedPreferences("UserPrefs",Context.MODE_PRIVATE);



                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));


                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setAllowedOverRoaming(false);
                request.setTitle("Downloading " + n1.getType());
                request.setDescription("Downloading " + n1.getType());
                request.setVisibleInDownloadsUi(true);
                request.setAllowedOverMetered(true);





                String ext="";
                String temp=n1.getType();
                for(int i=temp.lastIndexOf(":")+1;i<temp.length();i++)
                    ext+=temp.charAt(i);

               // request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,  "CollegeDocs" +n1.getSubject()+ n1.getType()+"."+ext);
                //request.setDestinationInExternalFilesDir(getContext(),"CollegeDocs","" +n1.getSubject()+ n1.getType()+"."+ext);
                String name=n1.getType().substring(0,n1.getType().lastIndexOf(":")-1);

                if(sharedPreferences.getString("SMAIL","").equals("true")) {

                    if(sharedPreferences.getString("EMAIL","").length()!=0) {
                        String upath = url.substring(url.indexOf("%"), url.indexOf("?"));
                        String token = url.substring(url.lastIndexOf("&"));
                        String newurl = upath;

                        String funtrigger = "https://us-central1-collegedocs2.cloudfunctions.net/filemailer/" + "?mail="+sharedPreferences.getString("EMAIL","") + "&filename=" + name + "." + ext + "&pathh=" + newurl + "&altt=media" + "&tokenn=" + token;

                        Toast.makeText(getContext(),"Sending Mail",Toast.LENGTH_SHORT).show();
                        new RequestTask().execute(funtrigger);
                    }
                    else
                        Toast.makeText(getContext(),"Set Email Address",Toast.LENGTH_SHORT).show();
                }


                request.setDestinationInExternalPublicDir("CollegeDocs","" + name+"."+ext);

                Log.d("SelectedSubject",n1.getSubject());
                Log.d("FILESARRAY",""+getContext().getFilesDir().getAbsolutePath()+"/CollegeDocs");

                if(sharedPreferences.getString("DOWN","").equals("true")) {

                    refid = downloadManager.enqueue(request);
                }
                else
                {
                    if(sharedPreferences.getString("DOWN","").equals("false") && sharedPreferences.getString("SMAIL","").equals("false")){
                        Toast.makeText(getContext(),"Please Select File Receiving Preferences In Settings",Toast.LENGTH_SHORT).show();
                    }
                }


                BroadcastReceiver onComplete = new BroadcastReceiver() {

                    public void onReceive(Context ctxt, Intent intent) {

                        // get the refid from the download manager
                        Log.d("HERELAK","HERE");
                        long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                        if (referenceId==refid)
                        {
                            Toast.makeText(getContext(),"Download Complete",Toast.LENGTH_SHORT).show();
                           /* final AlertDialog alertDialog = new AlertDialog.Builder(
                                    ctxt).create();

                            // Setting Dialog Title
                            alertDialog.setTitle("Download Complete");

                            // Setting Dialog Message
                            alertDialog.setMessage("Thank you for downloading :)");

                            // Setting Icon to Dialog
                            alertDialog.setIcon(android.R.drawable.ic_menu_info_details);

                            // Setting OK Button
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog closed
                                    alertDialog.dismiss();
                                }
                            });
                            alertDialog.show();*/

                        }

                    }
                };
                getContext().registerReceiver(onComplete,
                        new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            }
        });
        return convertView;
    }


}
class RequestTask extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... uri) {
        String responseString = null;
        try {
            URL url = new URL(uri[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                // Do normal input or output stream reading
            }
            else {
                responseString = "FAILED"; // See documentation for more info on response handling
            }
        }
         catch (Exception e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
    }
}
