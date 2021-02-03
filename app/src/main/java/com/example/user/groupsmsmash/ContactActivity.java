package com.example.user.groupsmsmash;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by user on 5/18/2016.
 */
public class ContactActivity extends Activity {


    GroupDatabase groupdb ;
    SQLiteDatabase db;
    Button addgroupbutton ;
    TextView grouptextview ;
    Dialog groupdialog;
    long rowid;
    ContentValues groupscontentvalues;
    SimpleCursorAdapter groupcursoradapter;
    ListView grouplistview;
    CursorAdapter groocursoradapter;

    public void listviepopulation() {

    Cursor ck = db.query("groups", new String[]{"rowid _id", "groupname"}, null, null, null, null, null);


    groupcursoradapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.groupindividuallayout, ck,
            new String[]{"groupname"}, new int[]{R.id.groupnameinlayout}, SimpleCursorAdapter.IGNORE_ITEM_VIEW_TYPE);

    groupcursoradapter.notifyDataSetChanged();
    grouplistview = (ListView) findViewById(R.id.grouplistview);
    Log.d("cursorcheck", ck.getCount() + "nnn");
    grouplistview.setAdapter(groupcursoradapter);

}

    public void listviewpopulationusingcustomadapter(){
        Cursor todoCursor = db.rawQuery("SELECT  id _id,groupname FROM groups", null);

        Log.v("cursoerview", DatabaseUtils.dumpCursorToString(todoCursor));

        CustomArrayAdapter groupnameadapter = new CustomArrayAdapter(getApplicationContext(),todoCursor,1);
        grouplistview = (ListView)findViewById(R.id.grouplistview);
        grouplistview.setTextFilterEnabled(true);
        grouplistview.setAdapter(groupnameadapter);
    }

    public void groupinsertindb(){
        EditText groupnamedittext = (EditText)groupdialog.findViewById(R.id.groupnameedit);

        if(db.isOpen()){
            groupscontentvalues = new ContentValues();

            groupscontentvalues.put("groupname",groupnamedittext.getText().toString());
            Log.d("contentvaluecheck",groupscontentvalues.getAsString("groupname"));

        }
    }

    //retrieving groupid



    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.grouplayout);

        groupdb = new GroupDatabase(getApplicationContext(),"Groups_DB",null,31);
                   db = groupdb.getWritableDatabase();


        grouptextview = (TextView)findViewById(R.id.groups_textview) ;


         addgroupbutton = (Button)this.findViewById(R.id.addgroup_button);




///////////////////////////
        addgroupbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                groupdialog = new Dialog(ContactActivity.this);
                groupdialog.setContentView(R.layout.groupnamedialog);
                     groupdialog.setTitle("GROUP NAME");


                  Button dialogsumbit = (Button)groupdialog.findViewById(R.id.submitButton);
             // inserting the group into the database


                dialogsumbit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View vi) {


                        groupinsertindb();

                        rowid = db.insert("groups",null,groupscontentvalues);

                        if(rowid==-1){
                            Toast.makeText(getApplicationContext(),"FAILED TO ADD GROUP",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"SUCCESSFULLY ADDED THE GROUP",Toast.LENGTH_LONG).show();

                        // listviepopulation();
                              listviewpopulationusingcustomadapter();

                        }

                       groupdialog.dismiss();

                        groupscontentvalues.clear();

                    }
                });


                groupdialog.show();

            }

        });

        // listviepopulation();
        listviewpopulationusingcustomadapter();


        grouplistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent eachgroupintent = new Intent(getApplicationContext(),EachGroupActivity.class);


                TextView listgrouptextview = (TextView)view.findViewById(R.id.groupnameinlayout);

                eachgroupintent.putExtra("group_name",listgrouptextview.getText());

                //retrieving groupid

                Cursor groupidcursor = db.rawQuery("SELECT  id _id,groupname FROM groups WHERE groupname =?", new String[]{listgrouptextview.getText().toString()});

//                                 Cursor groupidcursor = db.query("groups",new String[]{"rowid _id","groupname"},"groupname",new String[]{currentgroup},null,null,null

                Integer groupid = 0;

                if(groupidcursor.moveToFirst()){
                    do {
                       groupid = Integer.parseInt(groupidcursor.getString(0));

                    }while(groupidcursor.moveToNext());

                }

                eachgroupintent.putExtra("group_id",groupid);

                startActivity(eachgroupintent);
            }
        });


    }
};












