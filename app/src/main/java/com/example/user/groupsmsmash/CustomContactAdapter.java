package com.example.user.groupsmsmash;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.provider.ContactsContract.Contacts;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static android.provider.ContactsContract.Contacts.*;

/**
 * Created by user on 5/25/2016.
 */
public class CustomContactAdapter extends CursorAdapter {



    public CustomContactAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
    }



    public ArrayList<Integer> selectedIDs = new ArrayList<Integer>();

//public ArrayList<ContactData> selectedContacts = new ArrayList<ContactData>();

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.contactlayout,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView contactnametextview = (TextView)view.findViewById(R.id.contactlayout);

           String contactnamesource = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));

        contactnametextview.setText(contactnamesource);



    }

public void toggleselected(Integer position){

    if(selectedIDs.contains(position))
    {
        selectedIDs.remove(position);
    }

    else
    {
        selectedIDs.add(position);
    }
}

    public ArrayList<Integer> getSelectedIDs(){
        return  selectedIDs;
    }


//    public ArrayList<ContactData> getSelectedContacts(){

//        return selectedContacts;
//    }

}
