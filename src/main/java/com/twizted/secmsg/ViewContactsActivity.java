package com.twizted.secmsg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import static android.nfc.NdefRecord.createMime;


public class ViewContactsActivity
        extends ActionBarActivity
        implements NfcAdapter.CreateNdefMessageCallback
{
    private DBCon dbCon;
    protected static ListAdapter customListAdapter;
    protected static ListView contactListView;
    private final String TAG = "com.twizted.secmsg";
    private Encrypter encrypter;
    private SharedPreferences prefs;
    private NfcAdapter nfcAdapter;
    private Me me;
    private IncomingMessageManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        prefs = getSharedPreferences(TAG, MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true))
        {
            try
            {
                encrypter = new Encrypter();
                encrypter.genKeys(this, "myKey");
                encrypter = null;
                prefs = null;
            } catch (NoSuchProviderException |
                    NoSuchAlgorithmException |
                    InvalidAlgorithmParameterException e)
            {
                e.printStackTrace();
            }

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    startActivity(new Intent(ViewContactsActivity.this, RegisterActivity.class));
                }
            }, 2000);

        }
        else
        {
            Intent i = new Intent(this, MessageChecker.class);
            startService(i);

            dbCon = new DBCon(this, null);
            Log.e(TAG, "NOT FIRST RUN");
            TextView meDisplay = (TextView) findViewById(R.id.meDisplay);
            Me me = dbCon.getMe();
            meDisplay.setText(me.getName() + "\n" + me.getPhoneNumber());

            customListAdapter = new CustomContactAdapter(this, dbCon.getContacts());
            contactListView = (ListView) findViewById(R.id.contactListView);
            contactListView.setAdapter(customListAdapter);

//            Intent intent = getIntent();
//            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
//            {
//                Parcelable[] rawMessages = intent.getParcelableArrayExtra(
//                        NfcAdapter.EXTRA_NDEF_MESSAGES);
//
//                NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
//                Toast.makeText(this, "NFC received", Toast.LENGTH_LONG).show();
//            }


            contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Contact c = (Contact) parent.getItemAtPosition(position);
                    Intent intent = new Intent(ViewContactsActivity.this, ViewMessagesActivity.class);
                    intent.putExtra("contactID", c.get_id());
                    intent.putExtra("contactName", c.get_name());
                    intent.putExtra("contactNumber", c.get_phonenumber());
                    intent.putExtra("contactKey", c.get_key());
                    startActivity(intent);
                }
            });

            contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Contact c = (Contact) parent.getItemAtPosition(position);
                    dbCon.deleteContact(c.get_id());

                    customListAdapter = new CustomContactAdapter(ViewContactsActivity.this, dbCon.getContacts());
                    contactListView = (ListView) findViewById(R.id.contactListView);
                    contactListView.setAdapter(customListAdapter);

                    return true;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);
    }

    private void processIntent(Intent intent)
    {
        Log.e(TAG, "CALLED");
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String s = new String(msg.getRecords()[0].getPayload());
        Log.e(TAG, s + "HAI!!!");
    }

    public void addNewContact(View view)
    {
        Intent intent = new Intent(ViewContactsActivity.this, AddContactActivity.class);
        startActivity(intent);


    }

    public void shareMyDetails(View view)
    {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
        {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        nfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event)
    {
        me = dbCon.getMe();
        String name = me.getName();
        String number = me.getPhoneNumber();
        String pubKey = "";
        try
        {
            pubKey = encrypter.getMyPubKey().getEncoded().toString();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException e)
        {
            Log.e(TAG, "A fuckup has occurred.");
            e.printStackTrace();
        }
        String text = name + "__" + number + "__";
        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{createMime(
                        "application/" + TAG, text.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                         */
                        //,NdefRecord.createApplicationRecord(TAG)
                });

        String s = new String(msg.getRecords()[0].getPayload());
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

        return msg;
    }
}
