package com.twizted.secmsg;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Cypher on 17/02/2015.
 */
public class PublicKeyManager extends AsyncTask<Contact, Void, String>
{
    private DBCon dbCon;
    private Context context;

    public PublicKeyManager(Context context)
    {
        this.context = context;
    }

    @Override
    protected String doInBackground(Contact... params)
    {
        System.out.println("PublicKeyManager started");
        try
        {
            String serverIpAddress = "192.168.0.2"; //change this
            Socket socket = new Socket(serverIpAddress, 55555);

            Contact c = params[0];

            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            String message = "getPubKey" + "%%" + c.get_phonenumber();
            objectOutputStream.writeObject(message);

            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            String result = (String) objectInputStream.readObject();

            if (!result.isEmpty() && !result.equals("0"))
            {
                c.set_key(Base64.decode(result, Base64.DEFAULT));
                dbCon = new DBCon(context, null);

                dbCon.setContact(c);
                System.out.println("PublicKeyManager completed");


            }

        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);


    }
}
