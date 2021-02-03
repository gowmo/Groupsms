package com.example.user.groupsmsmash;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by user on 5/19/2016.
 */
public class GroupDatabase extends  SQLiteOpenHelper {

    public GroupDatabase(Context ctxt,
                         String dbname,
                         SQLiteDatabase.CursorFactory factory,
                         int dbversion){
        super(ctxt,dbname,factory,dbversion);

        Log.d("DBCheck","Constructor");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create groups table
           db.execSQL("CREATE TABLE groups" +
                   "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                   "groupname TEXT UNIQUE NOT NULL)");
        //create groupcontacts table
        db.execSQL("CREATE TABLE groupcontacts" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "groupid INTEGER NOT NULL," +
                "contactname TEXT UNIQUE NOT NULL ," +
                "FOREIGN KEY(groupid) REFERENCES groups(id))");
        //logging check
Log.d("DBCHECK","OnCreate");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS groupcontacts");
        db.execSQL("DROP TABLE IF EXISTS groups");
        onCreate(db);

        //logging check
        Log.d("DBCHECK","OnUpgrade");

    }


}
