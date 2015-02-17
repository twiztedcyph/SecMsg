package com.twizted.secmsg;

public class Message
{
    private int _id, _contactID;
    private boolean _fromME;
    private String _messageContent;
    private String _timeStamp;

    public Message()
    {
    }

    public Message(int id, int contactID, boolean fromME, String messageContent)
    {
        this._id = id;
        this._contactID = contactID;
        this._fromME = fromME;
        this._messageContent = messageContent;
    }

    public Message(int contactID, boolean fromME, String messageContent, String _timeStamp)
    {
        this._contactID = contactID;
        this._fromME = fromME;
        this._messageContent = messageContent;
        this._timeStamp = _timeStamp;
    }

    public int get_id()
    {
        return _id;
    }

    public int get_contactID()
    {
        return _contactID;
    }

    public void set_contactID(int contactID)
    {
        this._contactID = contactID;
    }

    public boolean is_fromME()
    {
        return _fromME;
    }

    public void set_fromME(boolean fromME)
    {
        this._fromME = fromME;
    }

    public String get_messageContent()
    {
        return _messageContent;
    }

    public String get_timeStamp()
    {
        return _timeStamp;
    }

    public void set_timeStamp(String _timeStamp)
    {
        this._timeStamp = _timeStamp;
    }

    public void set_messageContent(String messageContent)
    {
        this._messageContent = messageContent;
    }

    @Override
    public String toString()
    {
        return "Message{" +
                "_id=" + _id +
                ", _contactID=" + _contactID +
                ", _fromME=" + _fromME +
                ", _messageContent='" + _messageContent + '\'' +
                ", _timeStamp='" + _timeStamp + '\'' +
                '}';
    }
}
