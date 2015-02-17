package com.twizted.secmsg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CustomContactAdapter extends ArrayAdapter
{
    public CustomContactAdapter(Context context, Contact[] contacts)
    {
        super(context, R.layout.custom_contact_list, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customContactView = layoutInflater.inflate(R.layout.custom_contact_list, parent, false);

        Contact c = (Contact) getItem(position);

        TextView nameDisplay = (TextView) customContactView.findViewById(R.id.contactNameDisplay);
        TextView numberDisplay = (TextView) customContactView.findViewById(R.id.contactNumberDisplay);

        nameDisplay.setText(c.get_name());
        numberDisplay.setText(c.get_phonenumber());

        return customContactView;
    }
}
