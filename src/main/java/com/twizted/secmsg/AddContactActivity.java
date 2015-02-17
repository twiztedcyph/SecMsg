package com.twizted.secmsg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;


public class AddContactActivity extends ActionBarActivity
{
    //test
    private final String TAG = "com.twizted.secmsg";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
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

    public void addContact(View view)
    {
        EditText nameInput = (EditText) findViewById(R.id.nameInput);
        EditText numberInput = (EditText) findViewById(R.id.numberInput);

        String name = nameInput.getText().toString();
        String number = numberInput.getText().toString();


        if (!(name.isEmpty() && number.isEmpty()))
        {
            PublicKeyManager pkm = new PublicKeyManager(this);

            Contact c = null;

            c = new Contact(name, number);

            pkm.execute(c);

            nameInput.setText("");
            numberInput.setText("");



            final Intent intent = new Intent(AddContactActivity.this, ViewContactsActivity.class);
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    startActivity(intent);
                }
            }, 1000);
        }
    }
}
