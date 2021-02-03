package com.example.user.groupsmsmash;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by user on 5/23/2016.
 */
public class CustomArrayAdapter extends CursorAdapter {


    public CustomArrayAdapter(Context context,Cursor cursor, int flags){
        super(context,cursor,flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        return LayoutInflater.from(context).inflate(R.layout.groupindividuallayout,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView groupnametextview = (TextView)view.findViewById(R.id.groupnameinlayout);

        String groupnamesource = cursor.getString(cursor.getColumnIndex("groupname"));

        groupnametextview.setText(groupnamesource);

    }


}
