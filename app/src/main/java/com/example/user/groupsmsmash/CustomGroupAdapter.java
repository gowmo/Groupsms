package com.example.user.groupsmsmash;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by user on 5/26/2016.
 */
public class CustomGroupAdapter extends CursorAdapter {

    public CustomGroupAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        return LayoutInflater.from(context).inflate(R.layout.singlegroupcontact,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView eachconteactofgroupteaxtview = (TextView)view.findViewById(R.id.eachcontactofgroup);

        String contactnamesource = cursor.getString(cursor.getColumnIndex("contactname"));

        eachconteactofgroupteaxtview.setText(contactnamesource);
    }
}
