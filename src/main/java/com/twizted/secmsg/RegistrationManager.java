package com.twizted.secmsg;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.BasicPermission;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

/**
 * Created by Cypher on 10/02/2015.
 */
public class RegistrationManager extends AsyncTask<Me, Void, String>
{
    private Encrypter encrypter;

    @Override
    protected String doInBackground(Me... params)
    {
        try
        {
            String result = "";

            encrypter = new Encrypter();

            Me me = params[0];
            String name = me.getName();
            String password = me.getPassword();
            String number = me.getPhoneNumber();

            byte[] pubKey = encrypter.getMyPubKey().getEncoded();

            String serverIpAddress = "192.168.0.2";

            Socket socket = new Socket(serverIpAddress, 55555);

            while(!result.equals("1"))
            {

                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                String output = "registerME%%"
                        + name + "%%"
                        + password + "%%"
                        + number + "%%"
                        + Base64.encodeToString(pubKey, Base64.DEFAULT);
                objectOutputStream.writeObject(output);

                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                result = (String) objectInputStream.readObject();
            }

        } catch (KeyStoreException | CertificateException |
                NoSuchAlgorithmException | IOException |
                UnrecoverableEntryException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
