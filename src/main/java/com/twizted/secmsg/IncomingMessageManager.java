package com.twizted.secmsg;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.sql.Timestamp;

public class IncomingMessageManager extends AsyncTask<String, Void, String>
{
    private DBCon dbCon;
    private Context context;
    private Me me;
    private NotificationCompat.Builder notification;
    private static final int uniqueID = 564564;
    private Uri notificationSound;

    public IncomingMessageManager(Context context)
    {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params)
    {
        String result = "";
        try
        {
            System.out.println("IncomingMessageManager run");
            dbCon = new DBCon(context, null);
            String serverIpAddress = "192.168.0.2"; //change this

            Socket socket = new Socket(serverIpAddress, 55555);
            if (socket.isConnected())
            {
                System.out.println("CONNECTION VALID");
            }else
            {
                System.out.println("CONNECTION NOT VALID");
            }

            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            me = dbCon.getMe();
            String message = "checkMessages" + "%%" + me.getPhoneNumber();
            objectOutputStream.writeObject(message);

            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            result = (String) objectInputStream.readObject();
            System.out.println("Result: " + result);
            if(!result.equals("none"))
            {

                String[] test = result.split("%%%");


                for (String string : test)
                {
                    String[] split = string.split("%%");
                    String contact = split[0];
                    String messageText = split[1];
                    Timestamp t = new Timestamp(Long.valueOf(split[3]));


                    Contact c = dbCon.getContact(contact);

                    Message m = new Message(c.get_id(), false, messageText, t.toString());
                    dbCon.setMessage(m);


                    notification = new NotificationCompat.Builder(context);
                    notification.setAutoCancel(true);

                    notification.setSmallIcon(R.drawable.ic_launcher);
                    notification.setTicker("Secure message received");
                    notification.setWhen(System.currentTimeMillis());
                    notification.setContentTitle(c.get_name());
                    //Will need to decrypt this later....
                    notification.setContentText(messageText);

                    notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    notification.setSound(notificationSound);

                    //Allow notification to open app.
                    Intent intent = new Intent(context, ViewMessagesActivity.class);
                    intent.putExtra("contactID", c.get_id());
                    intent.putExtra("contactName", c.get_name());
                    intent.putExtra("contactNumber", c.get_phonenumber());
                    intent.putExtra("contactKey", c.get_key());
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.setContentIntent(pendingIntent);

                    //Build and issue notification.
                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(uniqueID, notification.build());
                    System.out.println("M being printed: " + m);
                }
            }


            socket.close();
            dbCon.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
}
