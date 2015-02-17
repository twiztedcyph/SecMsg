package com.twizted.secmsg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;


public class ViewMessagesActivity extends ActionBarActivity
{
    private DBCon dbCon;
    private Contact c;
    private ListAdapter customListAdapter;
    private ListView messageListView;
    private String TAG = "com.twizted.secmsg";
    private Encrypter encrypter;
    private IncomingMessageManager imm;
    private OutgoingMessageManager omm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_messages);

        imm = new IncomingMessageManager(this);
        imm.execute();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        int contactID = intent.getIntExtra("contactID", 0);

        if (contactID > 0)
        {

            String name = intent.getStringExtra("contactName");
            String number = intent.getStringExtra("contactNumber");
            byte[] key = intent.getByteArrayExtra("contactKey");

            c = new Contact(contactID, name, number, key);

            dbCon = new DBCon(this, null);

            customListAdapter = new CustomMessageAdapter(this, dbCon.getMessages(c), c);
            messageListView = (ListView) findViewById(R.id.messageListView);
            messageListView.setAdapter(customListAdapter);

            messageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Message m = (Message) parent.getItemAtPosition(position);
                    dbCon.deleteMessage(m.get_id());

                    customListAdapter = new CustomMessageAdapter(ViewMessagesActivity.this, dbCon.getMessages(c), c);
                    messageListView = (ListView) findViewById(R.id.messageListView);
                    messageListView.setAdapter(customListAdapter);

                    return true;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_messages, menu);
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

    public void sendMessage(View view)
    {
        EditText messageInput = (EditText) findViewById(R.id.messageInput);
        String message = messageInput.getText().toString();

        encrypter = new Encrypter();

        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(c.get_key());
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

            String secMsg = encrypter.encrypt(message, publicKey);

            SmsManager smsManager = SmsManager.getDefault();

            ArrayList<String> parts = smsManager.divideMessage(secMsg);


            if (!message.isEmpty())
            {
                System.out.println("MESSAGE NOT EMPTY");
                Message m = new Message(c.get_id(), true, encrypter.encrypt(message, encrypter.getMyPubKey()), new Timestamp(new Date().getTime()).toString());
                omm = new OutgoingMessageManager(this);
                omm.execute(m);
                dbCon.setMessage(m);
                messageInput.setText("");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }


        InputMethodManager inputMethodManage = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManage.hideSoftInputFromWindow(messageInput.getWindowToken(), 0);

        customListAdapter = new CustomMessageAdapter(this, dbCon.getMessages(c), c);
        messageListView = (ListView) findViewById(R.id.messageListView);
        messageListView.setAdapter(customListAdapter);
    }
}
