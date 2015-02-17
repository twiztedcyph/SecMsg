package com.twizted.secmsg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class RegisterActivity extends ActionBarActivity
{

    private Encrypter encrypter;
    private SharedPreferences prefs;
    private Me me;
    private DBCon dbCon;
    private final String TAG = "com.twizted.secmsg";
    private EditText nameInput, passwordOne, phoneNumber;
    private RegistrationManager regMan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        prefs = getSharedPreferences(TAG, MODE_PRIVATE);
        dbCon = new DBCon(this, null);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void registerMe(View view)
    {
        nameInput = (EditText) findViewById(R.id.registerName);
        passwordOne = (EditText) findViewById(R.id.registerPasswordOne);
        phoneNumber = (EditText) findViewById(R.id.registerNumber);

        String name = nameInput.getText().toString();
        String pwOne = passwordOne.getText().toString();
        String number = phoneNumber.getText().toString();

        if (!(name.isEmpty() && pwOne.isEmpty() && number.isEmpty()))
        {
            me = new Me(name, pwOne, number);

            System.out.println(me);
            dbCon.addMe(me);

            regMan = new RegistrationManager();
            regMan.execute(me);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstrun", false);

            editor.commit();

            startActivity(new Intent(this, ViewContactsActivity.class));
        }
    }
}
