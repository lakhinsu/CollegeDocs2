package com.example.lak.collegedocs2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    EditText entry;
    CheckBox box1,box2;
    Button save;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        entry=findViewById(R.id.settingsmail);
        box1=findViewById(R.id.checkBox);
        box2=findViewById(R.id.checkBox2);
        save=findViewById(R.id.savesettings);

        sharedPreferences=getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        if(sharedPreferences.contains("EMAIL")){
            entry.setText(sharedPreferences.getString("EMAIL",""));
            entry.setSelection(entry.getText().toString().length());
        }
       if(sharedPreferences.contains("SMAIL"))
        {
            if(sharedPreferences.getString("SMAIL","").equals("true"))
                box1.setChecked(true);
        }
        if(sharedPreferences.contains("DOWN"))
        {
            if(sharedPreferences.getString("DOWN","").equals("true"))
                box2.setChecked(true);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("EMAIL",entry.getText().toString());
                if(box1.isChecked())
                    editor.putString("SMAIL","true");
                else
                    editor.putString("SMAIL","false");
                if(box2.isChecked())
                    editor.putString("DOWN","true");
                else
                    editor.putString("DOWN","false");
                editor.commit();
                 Toast.makeText(getApplicationContext(),"Settings Saved",Toast.LENGTH_SHORT).show();
                 Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                 startActivity(intent);
                finish();
            }
        });





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
}
