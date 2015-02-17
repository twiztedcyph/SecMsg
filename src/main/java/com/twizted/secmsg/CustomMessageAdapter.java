package com.twizted.secmsg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CustomMessageAdapter extends ArrayAdapter
{
    private final String TAG = "com.twizted.secmsg";
    private Contact c;
    private Encrypter encrypter;

    public CustomMessageAdapter(Context context, Message[] messages, Contact c)
    {
        super(context, R.layout.custom_message_list, messages);
        this.c = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        encrypter = new Encrypter();
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customContactView = layoutInflater.inflate(R.layout.custom_message_list, parent, false);

        Message m = (Message) getItem(position);

        TextView nameDisplay = (TextView) customContactView.findViewById(R.id.messageNameDisplay);
        TextView messageDisplay = (TextView) customContactView.findViewById(R.id.messageMessageDisplay);

        System.out.println("IS FROM ME: " + m.is_fromME());

        if (m.is_fromME())
        {
            nameDisplay.setText("Me");

            try
            {
                messageDisplay.setText(encrypter.decrypt(m.get_messageContent(), encrypter.getMyPrivKey()));
            } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                    InvalidKeyException | IllegalBlockSizeException |
                    BadPaddingException | CertificateException |
                    IOException | KeyStoreException |
                    UnrecoverableEntryException |
                    NoSuchProviderException e)
            {
                Log.i(TAG, "A fuckup has occurred");
                e.printStackTrace();
            }
        }
        else
        {
            nameDisplay.setText(c.get_name());
            messageDisplay.setText(m.get_messageContent());
        }
        return customContactView;
    }
}
