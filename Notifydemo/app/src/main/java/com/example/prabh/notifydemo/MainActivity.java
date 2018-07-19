package com.example.prabh.notifydemo;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.ResultSet;


public class MainActivity extends AppCompatActivity {
    NotificationCompat.Builder notification;
    EditText editText,time;
    SQLiteDatabase db;
    int started=0;
    Switch aSwitch;

    public static final int id=56789;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notification=new NotificationCompat.Builder(this,"default");
        notification.setAutoCancel(true);
        editText=(EditText)findViewById(R.id.editText);
        db=openOrCreateDatabase("animes",MODE_PRIVATE,null);
        db.execSQL("create table if not exists  entries (name varchar)");
        time=(EditText)findViewById(R.id.editText2);
        aSwitch=(Switch)findViewById(R.id.switch1);
    }

    public void start(View view) {

            if(started==1)
            {
                Toast.makeText(this, "Please stop the running service to search for other anime", Toast.LENGTH_SHORT).show();
            }
            else {
                started=1;
                Cursor c1=db.rawQuery("select * from entries",null);
                int no=c1.getCount();
                String[] animes=new String[no];
                float timing = Float.parseFloat(time.getText().toString());
                Intent intent = new Intent(this, NotifyService.class);
                Bundle bundle = new Bundle();
                if(aSwitch.isChecked())
                {
                    if(c1.getCount()==0)
                        Toast.makeText(this, "Database is empty", Toast.LENGTH_SHORT).show();
                    else
                    {

                        for (int i=0;i<c1.getCount();i++)
                        {
                            c1.move(1);//*******moves cursor by one position
                            animes[i]=c1.getString(0);
                        }
                        //Toast.makeText(this, "anime:"+animes, Toast.LENGTH_SHORT).show();
                        //String[] abc=animes;
                        //1Toast.makeText(this, "anime:"+abc[0], Toast.LENGTH_SHORT).show();
                        bundle.putStringArray("names",animes);
                        bundle.putFloat("time", timing);
                       // bundle.putInt("mode",2);
                        intent.putExtras(bundle);
                        //intent.putExtra("name", editText.getText().toString());
                        startService(intent);
                    }
                }
                else {
                    String[] a=new String[1];
                    a[0]=editText.getText().toString();
                    bundle.putStringArray("name",a);
                    bundle.putFloat("time", timing);
                    intent.putExtras(bundle);
                   //// bundle.putInt("mode",1);
                    //intent.putExtra("name", editText.getText().toString());
                    startService(intent);
                    //           Toast.makeText(this, "Stop the current search and then try with a different name", Toast.LENGTH_SHORT).show();
                }
            }
    }

    public void stop(View view) {
        started=0;
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
        stopService(new Intent(this,NotifyService.class));
    }

    public void showall(View view) {
        Cursor c1=db.rawQuery("select * from entries",null);
        Toast.makeText(this, "Count:"+c1.getCount(), Toast.LENGTH_SHORT).show();
    }

    public void store(View view) {
        db.execSQL("insert into entries values('" + editText.getText() + "')");
        Toast.makeText(this, "Inserted succesfully", Toast.LENGTH_SHORT).show();
    }

    public void clear(View view) {
        db.execSQL("delete from entries");
    }
}



/*   Media player code
        MediaPlayer mediaPlayer=MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer=MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        */
