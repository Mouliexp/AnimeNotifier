package com.example.prabh.notifydemo;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by prabh on 6/22/2018.
 */

public class NotifyService extends Service {

    public static final int id=1234;
    public static final int id1=1235;
    int found;
    StringBuilder stringBuilder=new StringBuilder();
    String name1,fullname,episode,link;
    float timeint;
    SQLiteDatabase db;
    int count=0,mode=0,j;
    String[] names;
    int[] done;


   NotificationCompat.Builder notification=new NotificationCompat.Builder(this,"channelid");
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    Timer timer;
    TimerTask timerTask=new TimerTask() {
        @Override
        public void run() {
            Log.d("HEllo","STARTED");
            Calendar calendar=Calendar.getInstance();
            searchAnime(names);
            //searchAnime("one",221);
            /*for(int i=0;i<2;i++)
            {
                searchAnime(names[i],i);
                Log.d("Animes:","Are::"+names[i]);
            }*/

        }
    };

    private void searchAnime(final String[] name) {
        //Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();

        Thread t1=new Thread(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {


                for (j = 0; j < name.length; j++) {
                    Log.d("HEllo", "done="+done[0]+done[1]+done[2]);
                    if(done[j]==1)
                        continue;
                    try {
                        Document document = Jsoup.connect("http://www.gogoanime.io").get();
                        Elements latest = document.select("p");
                        Log.d("HEllo", "done="+done.length);

                        int i;
                        for (i = 0; i < latest.size(); i++) {
                            Element item = latest.get(i);
                            Log.d("HEllo", "j="+j);
                            Log.d("HEllo", "Iter:" +i+"----"+item.text());
                            if (item.text().toLowerCase().contains(name[j])) {
                                fullname = item.text();
                                link = "http://www2.gogoanime.se" + item.children().attr("href");
                                Log.d("HEllo", link);
                                episode = item.nextElementSibling().text();
                                done[j]=1;
                                Log.d("HEllo", "done["+j+"]="+done[j]);
                                count += 1;//count of all the animes aired
                                found = 1;//notifies individual animes
                                notify12(fullname, episode + " is out", link,j);
                                break;
                                //stringBuilder.append(item.attr("title")).append(":").append("http://www2.gogoanime.se").append(item.attr("href")).append("\n");
                            } else {
                                Log.d("HEllo", "j="+j);
                                notify12("Searching", "Keyword:" + name[j], "No link", id);//same search foreground for all
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread t2=new Thread(t1);
        t2.start();
        //if(found==1&&(count>3))
        {
            Log.d("HEllo","notify");
            //Toast.makeText(getApplicationContext(), "Latest episode of the anime is out,To watch click on the link", Toast.LENGTH_SHORT).show();
           // notify12(fullname,"100");
        }
        Log.d("HEllo","Bsas2");

        //else {
           // if (count == 1)
               //// Toast.makeText(getApplicationContext(), "Not yet released,Search for other anime", Toast.LENGTH_SHORT).show();
           // else
                //Toast.makeText(getApplicationContext(), "Checking from servers,Click on the button again to display the related results", Toast.LENGTH_LONG).show();
        //}

    }



    public void notify12(String name,String episode,String link,int iden) {
        Log.d("Hello","No. of "+iden);
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent1=new Intent(getApplicationContext(),webview.class);
        intent1.putExtra("animelink",link);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent1=PendingIntent.getActivity(getApplicationContext(),0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setSmallIcon(R.drawable.abc)
                .setTicker("Hi")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(name)
                .setContentText(episode)
                .setContentIntent(pendingIntent)
                .setTicker("Hi amigos")
                .setAutoCancel(true)
                .addAction(R.drawable.abc,"close",pendingIntent1);
                //.addAction(R.drawable.ic_launcher_background,"open link",pendingIntent)
                ;
        if(!(found==1)) {
            startForeground(iden, notification.build());
        }
        else {
            //notification.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(iden, notification.build());
        }
        //Log.d("HEllo", "done="+done[0]+done[1]+done[2]);
        //Log.d("HEllo", "count="+count);
        if(count==names.length)
            onDestroy();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //db=openOrCreateDatabase("animes",MODE_PRIVATE,null);
        //db.execSQL("create table if not exists entries (name varchar)");

        try {
            timer=new Timer();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Stop the current search and then try with a different name", Toast.LENGTH_SHORT).show();
        }
        Bundle bundle=intent.getExtras();
        names=bundle.getStringArray("names");

        timeint=bundle.getFloat("time");

        done=new int[names.length]; // an aray for maintaining found animes

        Log.d("Runing","I am running"+names[0]);
        timer.scheduleAtFixedRate(timerTask,0, (long) (1000*60*timeint));
        //Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();

    }

}

