package com.twizted.secmsg;

import java.util.Arrays;

public class Contact
{
    private int _id;
    private String _name;
    private String _phonenumber;
    private byte[] _key;

    public Contact()
    {
    }

    public Contact(int id, String name, String phonenumber, byte[] key)
    {
        this._id = id;
        this._name = name;
        this._phonenumber = phonenumber;
        this._key = key;
    }

    public Contact(String name, String phonenumber, byte[] key)
    {
        this._name = name;
        this._phonenumber = phonenumber;
        this._key = key;
    }

    public Contact(String _name, String _phonenumber)
    {
        this._name = _name;
        this._phonenumber = _phonenumber;
    }

    public int get_id()
    {
        return _id;
    }

    public String get_phonenumber()
    {
        return _phonenumber;
    }

    public void set_phonenumber(String _phonenumber)
    {
        this._phonenumber = _phonenumber;
    }

    public String get_name()
    {
        return _name;
    }

    public void set_name(String _name)
    {
        this._name = _name;
    }

    public byte[] get_key()
    {
        return _key;
    }

    public void set_key(byte[] _key)
    {
        this._key = _key;
    }

    @Override
    public String toString()
    {
        return "Contact{" +
                "_id=" + _id +
                ", _name='" + _name + '\'' +
                ", _phonenumber='" + _phonenumber + '\'' +
                ", _key=" + Arrays.toString(_key) +
                '}';
    }
}
