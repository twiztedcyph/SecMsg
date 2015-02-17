package com.twizted.secmsg;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

/**
 * Created by Cypher on 11/02/2015.
 */
public class OutgoingMessageManager extends AsyncTask<Message, Void, String>
{
    private DBCon dbCon;
    private Context context;
    private Me me;

    public OutgoingMessageManager(Context context)
    {
        this.context = context;
    }

    @Override
    protected String doInBackground(Message[] params)
    {
        try
        {
            String result = "";

            dbCon = new DBCon(context, null);

            me = dbCon.getMe();
            Message message = params[0];
            Contact contact = dbCon.getContact(message.get_contactID());

            String fromUser = me.getPhoneNumber();
            String mContent = message.get_messageContent();
            String toUser = contact.get_phonenumber();

            String output = "sendMessage" + "%%" + fromUser + "%%" + mContent + "%%" + toUser;

            String serverIpAddress = "192.168.0.2";
            Socket socket = new Socket(serverIpAddress, 55555);

            while (!result.equals("1"))
            {

                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                objectOutputStream.writeObject(output);

                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                result = (String) objectInputStream.readObject();
            }


        } catch (ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
