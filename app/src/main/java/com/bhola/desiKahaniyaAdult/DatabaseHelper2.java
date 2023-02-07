package com.bhola.desiKahaniyaAdult;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper2 extends SQLiteOpenHelper {
    String DbName;
    String DbPath;
    Context context;
    String Database_tableNo;
    Cursor cursor;

//    When Deleting story from  Favourite_list "Database_tableNo" act as "Story Title"

    public DatabaseHelper2(@Nullable Context mcontext, String DB_NAME, int version, String Table_Number) {
        super(mcontext, DB_NAME, null, version);
        this.context = mcontext;
        this.DbName = DB_NAME;
        this.Database_tableNo = Table_Number;
        DbPath = "/data/data/" + "com.bhola.desiKahaniya" + "/databases/";
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public Cursor readalldata() {
        String qry = null;
        if (Database_tableNo.equals("Collection1")) {
            qry = "select * from Collection1";
        }
        if (Database_tableNo.equals("Collection2")) {
            qry = "select * from Collection2";
        }
        if (Database_tableNo.equals("Collection3")) {
            qry = "select * from Collection3";
        }
        if (Database_tableNo.equals("Collection4")) {
            qry = "select * from Collection4";
        }
        if (Database_tableNo.equals("Collection5")) {
            qry = "select * from Collection5";
        }
        if (Database_tableNo.equals("Collection6")) {
            qry = "select * from Collection6";
        }
        if (Database_tableNo.equals("Collection7")) {
            qry = "select * from Collection7";
        }
        if (Database_tableNo.equals("Collection8")) {
            qry = "select * from Collection8";
        }
        if (Database_tableNo.equals("Collection9")) {
            qry = "select * from Collection9";
        }
        if (Database_tableNo.equals("Collection10")) {
            qry = "select * from Collection10";
        }

        SQLiteDatabase db = this.getWritableDatabase();

        cursor = db.rawQuery(qry, null);
        return cursor;

    }

    public String updaterecord(int _id, int like_value) {



        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("Liked", like_value);

        float res= db.update(Database_tableNo, cv, "id=" + _id, null);
        if(res==-1)
            return "Failed";
        else
            return  "Liked";
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

}
