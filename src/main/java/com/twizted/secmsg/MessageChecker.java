package com.twizted.secmsg;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MessageChecker extends Service
{
    private final String TAG = "com.twizted.secmsg";
    private IncomingMessageManager imm;
    private Context context;

    public MessageChecker()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "Service onStartCommand");
        context = this;
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    imm = new IncomingMessageManager(context);
                    imm.execute();

                    try
                    {
                        Thread.sleep(30000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i(TAG, "Service onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
