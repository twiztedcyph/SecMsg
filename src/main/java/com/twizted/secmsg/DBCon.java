package com.twizted.secmsg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBCon extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "secmsg.db";

    private static final String TABLE_ME = "me";
    public static final String COLUMN_MY_ID = "_id";
    public static final String COLUMN_MY_NAME = "name";
    public static final String COLUMN_MY_PASSWORD = "password";
    public static final String COLUMN_MY_NUMBER = "contact_number";

    private static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NUMBER = "contact_number";
    public static final String COLUMN_KEY = "key";

    private static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "_id";
    public static final String COLUMN_CONTACT_ID = "contact_id";
    public static final String COLUMN_FROM_ME = "from_me";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_TIMESTAMP = "date_time";

    public final String TAG = "com.twizted.secmsg";

    public DBCon(Context context, SQLiteDatabase.CursorFactory factory)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        getWritableDatabase();

        close();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.e(TAG, "Database onCreate called.");
        String createMeTable = "CREATE TABLE " + TABLE_ME +
                " (" +
                COLUMN_MY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MY_NAME + " TEXT NOT NULL, " +
                COLUMN_MY_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_MY_NUMBER + " TEXT NOT NULL" +
                ");";

        String createContactsTable = "CREATE TABLE " + TABLE_CONTACTS +
                " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_NUMBER + " TEXT NOT NULL," +
                COLUMN_KEY + " BLOB NOT NULL" +
                ");";

        String createMessagesTable = "CREATE TABLE " + TABLE_MESSAGES +
                " (" +
                COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONTACT_ID + " INTEGER REFERENCES "
                + TABLE_CONTACTS +
                "(" +
                COLUMN_CONTACT_ID +
                ") ON DELETE CASCADE, " +
                COLUMN_FROM_ME + " INTEGER NOT NULL, " +
                COLUMN_MESSAGE + " TEXT NOT NULL, " +
                COLUMN_TIMESTAMP + " TEXT NOT NULL" +
                ");";

        db.execSQL(createMeTable);
        db.execSQL(createContactsTable);
        db.execSQL(createMessagesTable);
    }

    public void testConnect()
    {

    }

    public void destroyDB()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    public void setMessage(Message message)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_CONTACT_ID, message.get_contactID());
        if (message.is_fromME())
        {
            contentValues.put(COLUMN_FROM_ME, 1);
        }
        else
        {
            contentValues.put(COLUMN_FROM_ME, 0);
        }
        contentValues.put(COLUMN_MESSAGE, message.get_messageContent());
        contentValues.put(COLUMN_TIMESTAMP, message.get_timeStamp());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MESSAGES, null, contentValues);
        db.close();
    }

    public Message[] getMessages(Contact contact)
    {
        ArrayList<Message> resultsArrayList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_MESSAGES +
                " WHERE " + COLUMN_CONTACT_ID + " = " +
                contact.get_id() + ";";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            if (cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE)) != null)
            {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_MESSAGE_ID));
                int fromID = cursor.getInt(cursor.getColumnIndex(COLUMN_CONTACT_ID));
                boolean fromMe = (cursor.getInt(cursor.getColumnIndex(COLUMN_FROM_ME)) == 1);
                String message = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE));

                resultsArrayList.add(new Message(id, fromID, fromMe, message));
                cursor.moveToNext();
            }
        }
        db.close();

        Message results[] = new Message[resultsArrayList.size()];
        resultsArrayList.toArray(results);

        return results;
    }

    public void deleteMessage(int messageID)
    {
        SQLiteDatabase db = getWritableDatabase();

        String delStatement = "DELETE FROM " + TABLE_MESSAGES +
                " WHERE " + COLUMN_MESSAGE_ID + " = " +
                messageID + ";";

        db.execSQL(delStatement);

        db.close();
    }

    public void deleteAllMessages()
    {
        SQLiteDatabase db = getWritableDatabase();

        String delStatement = "DELETE FROM " + TABLE_MESSAGES + ";";

        db.execSQL(delStatement);
        db.close();
    }

    public void setContact(Contact contact)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, contact.get_name());
        contentValues.put(COLUMN_NUMBER, contact.get_phonenumber());
        contentValues.put(COLUMN_KEY, contact.get_key());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CONTACTS, null, contentValues);
        db.close();
    }

    public Contact getContact(String contact_number)
    {
        Log.e(TAG, "Incoming number: " + contact_number);
        Contact result = new Contact();

        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_NUMBER + " = '" + contact_number + "';";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            Log.e(TAG, "Entered outer loop.");
            if (cursor.getString(cursor.getColumnIndex(COLUMN_NAME)) != null)
            {
                Log.e(TAG, "Entered inner loop.");
                result = new Contact(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)),
                        cursor.getBlob(cursor.getColumnIndex(COLUMN_KEY)));
                System.out.println("Inner loop: " + result);
                cursor.moveToNext();
            }
        }
        db.close();
        cursor.close();
        return result;
    }

    public Contact getContact(int contact_id)
    {
        Contact result = new Contact();

        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_ID + " = '" + contact_id + "';";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            Log.e(TAG, "Entered outer loop.");
            if (cursor.getString(cursor.getColumnIndex(COLUMN_NAME)) != null)
            {
                Log.e(TAG, "Entered inner loop.");
                result = new Contact(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)),
                        cursor.getBlob(cursor.getColumnIndex(COLUMN_KEY)));
                System.out.println("Inner loop: " + result);
                cursor.moveToNext();
            }
        }
        db.close();
        cursor.close();
        return result;
    }

    public Contact[] getContacts()
    {
        ArrayList<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " +
                TABLE_CONTACTS + " WHERE 1;";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            if (cursor.getString(cursor.getColumnIndex(COLUMN_NAME)) != null)
            {
                contactList.add(new Contact(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER)),
                        cursor.getBlob(cursor.getColumnIndex(COLUMN_KEY))));
                cursor.moveToNext();
            }
        }
        db.close();
        cursor.close();
        Contact[] contactArray = new Contact[contactList.size()];
        contactList.toArray(contactArray);

        return contactArray;
    }

    public void deleteContact(int contactID)
    {
        SQLiteDatabase db = getWritableDatabase();
        String delStatement = "DELETE FROM " +
                TABLE_CONTACTS + " WHERE " +
                COLUMN_ID + " = " +
                contactID + ";";

        db.execSQL(delStatement);
        db.close();
    }

    public void addMe(Me me)
    {
        System.out.println(me);

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MY_NAME, me.getName());
        contentValues.put(COLUMN_MY_PASSWORD, me.getPassword());
        contentValues.put(COLUMN_MY_NUMBER, me.getPhoneNumber());

        db.insert(TABLE_ME, null, contentValues);
        db.close();
    }

    public Me getMe()
    {
        Me me = new Me();

        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " +
                TABLE_ME + " WHERE 1;";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            if (cursor.getString(cursor.getColumnIndex(COLUMN_MY_NAME)) != null)
            {
                me.setName(cursor.getString(cursor.getColumnIndex(COLUMN_MY_NAME)));
                me.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_MY_PASSWORD)));
                me.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_MY_NUMBER)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return me;
    }
}
