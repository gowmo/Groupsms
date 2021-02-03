package com.example.user.groupsmsmash;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by user on 5/24/2016.
 */
public class EachGroupActivity  extends Activity {

    Button sendsms;
    Button addcontact;
    GroupDatabase gdb;
    SQLiteDatabase db;
    CustomGroupAdapter contactcursoradapter;
    ContentResolver contactcontentresolver;

//    SmsManager smsManager = SmsManager.getDefault() ;




    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.eachgroupdetails);


        sendsms = (Button)findViewById(R.id.sendsmsbutton);
        Intent groupnameintent = getIntent();

contactcontentresolver = getContentResolver();


        gdb = new GroupDatabase(getApplicationContext(),"Groups_DB",null,31);
         db = gdb.getWritableDatabase();

        addcontact = (Button) findViewById(R.id.addcontactbutton);
//        addcontact.setText(groupnameintent.getStringExtra("group_name"));



       final String currentgroupname = groupnameintent.getStringExtra("group_name");

        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent contactaddintent = new Intent(getApplicationContext(),GroupActivity.class);
                 contactaddintent.putExtra("group_name",currentgroupname);
                startActivity(contactaddintent);
            }
        });


        //populate the listview for each group through customadapterlater

                  String belowgroup= groupnameintent.getStringExtra("group_name");
                Integer group_id = groupnameintent.getIntExtra("group_id",0);

        Cursor contacteachcursor = db.rawQuery("SELECT id _id,contactname FROM groupcontacts WHERE groupid=?",new String[]{group_id.toString()});

        contactcursoradapter = new CustomGroupAdapter(getApplicationContext(),contacteachcursor,1);

        ListView singlegrouplistview = (ListView)findViewById(R.id.singlegrouplist);
        singlegrouplistview.setTextFilterEnabled(true);
        singlegrouplistview.setAdapter(contactcursoradapter);

        Log.v("eachgroupcursorcheck", DatabaseUtils.dumpCursorToString(contacteachcursor));


        //get the actual contacts cursor for retreiving the contact actualids


       Cursor originalidscursor = contactcontentresolver.query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},null,null,null);

///get the contact numbers for the respective contactnames

                final ArrayList<String> phonenumbers = new ArrayList<String>();



        String[] contactidslist = new String[contacteachcursor.getCount()];



        for(int a=0;a<contactidslist.length;a++){

            if(contacteachcursor.moveToFirst()) {
                contacteachcursor.moveToPosition(a);
                String tempname ;
                tempname =  contacteachcursor.getString(contacteachcursor.getColumnIndex("contactname"));

                     Cursor cr = contactcontentresolver.query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts._ID}, ContactsContract.Contacts.DISPLAY_NAME+"= ?",new String[]{tempname},null);



                Log.v("eachid", contacteachcursor.getString(contacteachcursor.getColumnIndex(ContactsContract.Contacts._ID)));


                if(cr.moveToFirst()){
                    do {
                        String contactid = cr.getString(cr.getColumnIndex(ContactsContract.Contacts._ID));
                        contactidslist[a] = contactid;

                        Cursor phones = contactcontentresolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactid}, null);

                        Log.v("pleasebagawan", contactid);
                        Log.v("phonescursor", DatabaseUtils.dumpCursorToString(phones));

                        if(phones.moveToFirst()){

                            do{
                                phonenumbers.add(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

                            }while (phones.moveToNext());

                        }


                    }while(cr.moveToNext());
                }
            }
        }



        sendsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String SENTACTION = "sent";
                String DELIVERACTION = "delivered";

                Intent sentIntent = new Intent(SENTACTION);
                 Intent deliIntent = new Intent(DELIVERACTION);

               final PendingIntent pisent  =  PendingIntent.getBroadcast(getApplicationContext(),10,sentIntent,PendingIntent.FLAG_ONE_SHOT);
               final  PendingIntent pideli = PendingIntent.getBroadcast(getApplicationContext(),20,deliIntent,PendingIntent.FLAG_ONE_SHOT);
try {


//reciever for sms sent
    registerReceiver(new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getApplicationContext(),"SMS sent",Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getApplicationContext(),"Generic Failure",Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getApplicationContext(),"No Service",Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getApplicationContext(),"Nll PDU",Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getApplicationContext(),"radio off",Toast.LENGTH_LONG).show();
                    break;


            }




        }
    }, new IntentFilter(SENTACTION));

//receiver for sms delivered
    registerReceiver(new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){

                case Activity.RESULT_OK:
                    Toast.makeText(getApplicationContext(),"Sms delivered",Toast.LENGTH_LONG).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getApplicationContext(),"Sms delivery Cancelled",Toast.LENGTH_LONG).show();
                    break;
            }


        }
    },new IntentFilter(DELIVERACTION));




    //////set the popup for message description


sendsms.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        Dialog messagedescdialog = new Dialog(EachGroupActivity.this);

        messagedescdialog.setContentView(R.layout.messagedescriptiondialog);
                messagedescdialog.setTitle("GROUP SMS");

        final EditText actualmessage = (EditText)messagedescdialog.findViewById(R.id.messagedescedit);
        Button finalsendbutton =(Button) messagedescdialog.findViewById(R.id.finalsendbutton);



        finalsendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int k =0;k<phonenumbers.size();k++){

                    phonenumbers.get(k);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phonenumbers.get(k), "+919849087001", actualmessage.getText().toString(), pisent, pideli);
                }


                //come back to all groups activity

                Intent backtogroupsintent = new Intent(getApplicationContext(),ContactActivity.class);
                startActivity(backtogroupsintent);

            }
        });

        messagedescdialog.show();

    }
});








}
catch (Exception exception){;
    Toast.makeText(getApplicationContext(),"Message not sent ",Toast.LENGTH_LONG).show();
                }

            }
        });

    }


}
