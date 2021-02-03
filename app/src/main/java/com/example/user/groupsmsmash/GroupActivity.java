package com.example.user.groupsmsmash;

import android.app.Activity;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.provider.ContactsContract.Contacts;

import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class GroupActivity extends Activity {

    Resources appres;
    ListView contactlistview;
    String[] names = {"contact1","contact2","contact3","contact4","contact5","contact6"};
    List<String> contactlist;
    ArrayAdapter<String> contactnameadapter;
    GroupDatabase grpdb;
    SQLiteDatabase db;
    CustomContactAdapter contactcursoradapter;
    Cursor contactcursor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);





        appres = getResources();
        grpdb = new GroupDatabase(getApplicationContext(),"Groups_DB",null,31);
       db = grpdb.getWritableDatabase();
        //retriveing data from contacts native provider

        final ContentResolver cres = getContentResolver();
//              getting the intent data
        Intent groupnameintent = getIntent();
       final String currentgroup= groupnameintent.getStringExtra("group_name");




         contactcursor =  cres.query(Contacts.CONTENT_URI, new String[]{Contacts._ID,Contacts.DISPLAY_NAME },Contacts.HAS_PHONE_NUMBER+"=1",null,null );

         // Log.v("inbuiltcursor",DatabaseUtils.dumpCursorToString(contactcursor));


//         contactcursoradapter = new SimpleCursorAdapter(getApplicationContext(),R.layout.contactlayout,contactcursor,
//        new String[]{Contacts.DISPLAY_NAME}, new int[]{R.id.contactlayout},SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        contactcursoradapter = new CustomContactAdapter(getApplicationContext(),contactcursor,1);


        contactlistview = (ListView)findViewById(R.id.contactlistview);

        contactlistview.setAdapter(contactcursoradapter);



        contactlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {

                contactlistview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

                contactlistview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                        mode.setTitle(contactlistview.getCheckedItemCount()+"Selected");
                        contactlistview.setItemsCanFocus(true);
                        TextView focusview = (TextView) findViewById(R.id.contactlayout);
                        focusview.setHighlightColor(Color.GREEN);

                        contactcursoradapter.toggleselected(position);


                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.groupaddmenu,menu);

                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                             if(item.getItemId()==R.id.addcontact){

long rowid;
                                 GroupDataContent contentgroup = new GroupDataContent();
                                 //retrieve the groupid for the groupname

                                 ArrayList<ContactData> selectedContacts = new ArrayList<ContactData>();

                                 Cursor groupidcursor = db.rawQuery("SELECT  id _id,groupname FROM groups WHERE groupname =?", new String[]{currentgroup});

//
                                 if(groupidcursor.moveToFirst()){
                                     do {
                                            contentgroup.setID(Integer.parseInt(groupidcursor.getString(0)));
                                            contentgroup.setGroupname(groupidcursor.getString(1));
                                     }while(groupidcursor.moveToNext());

                                 }


                                 Cursor cr = contactcursoradapter.getCursor();


                                 for(int i =0;i<contactcursoradapter.selectedIDs.size();i++)
                                 {
                                     int positionofselected = contactcursoradapter.selectedIDs.get(i);
if(cr!=null && cr.getCount()>0)
{
       cr.moveToFirst();
         cr.moveToPosition(positionofselected);
         ContactData contactData = new ContactData();
         contactData.setID(Integer.parseInt(cr.getString(cr.getColumnIndexOrThrow(Contacts._ID))));
         contactData.setContactname(cr.getString(cr.getColumnIndexOrThrow(Contacts.DISPLAY_NAME)));
         selectedContacts.add(contactData);

 }


                                 }
                                 ///checking logcat for the displayed arraylist

                                 for(int j=0;j<selectedContacts.size();j++){

                                     ContentValues contactcontentvalues = new ContentValues();

                                     contactcontentvalues.put("groupid",contentgroup.getID());
                                     contactcontentvalues.put("contactname",selectedContacts.get(j).getContactname());
                                       if(db.isOpen()){
                                           rowid = db.insert("groupcontacts",null,contactcontentvalues);

                                       }


                                    Log.v("arraylistcheck",selectedContacts.get(j).getID()+"name="+selectedContacts.get(j).getContactname()+"");

                                 }


                                 int contactid = contentgroup.getID();

                                 Intent backtogroupintent = new Intent(getApplicationContext(),EachGroupActivity.class);
                                 backtogroupintent.putExtra("group_name",currentgroup);
                                 backtogroupintent.putExtra("group_id",contentgroup.getID());
                                 startActivity(backtogroupintent);
                             }


                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {


                    }
                });
                return false;
            }
        });



    }


    public void onDestroy(){
        super.onDestroy();

        contactcursor.close();

    }
}
