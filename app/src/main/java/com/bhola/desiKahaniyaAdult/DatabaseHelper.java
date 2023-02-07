package com.bhola.desiKahaniyaAdult;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    String DbName;
    String DbPath;
    Context context;
    String Database_tableNo;
    Cursor cursor;

    public DatabaseHelper(@Nullable Context mcontext, String name, int version, String Database_tableNo) {
        super(mcontext, name, null, version);
        this.context = mcontext;
        this.DbName = name;
        this.Database_tableNo = Database_tableNo;
        DbPath = "/data/data/" + "com.bhola.desiKahaniya" + "/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("TAGA", "oldVersion: " + oldVersion);
        Log.d("TAGA", "newVersion: " + newVersion);


    }

    public void CheckDatabases() {
        try {
            String path = DbPath + DbName;
            SQLiteDatabase.openDatabase(path, null, 0);
//            db_delete();
            //Database file is Copied here
            checkandUpdateLoginTimes_UpdateDatabaseCheck();
        } catch (Exception e) {
            this.getReadableDatabase();
            Log.d("TAGA", "CheckDatabases: " + "First Time Copying " + DbName);
            CopyDatabases();
        }
    }

    public void CopyDatabases() {


        try {
            InputStream mInputStream = context.getAssets().open(DbName);
            String outFilename = DbPath + DbName;
            OutputStream mOutputstream = new FileOutputStream(outFilename);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = mInputStream.read(buffer)) > 0) {
                mOutputstream.write(buffer, 0, length);
            }
            mOutputstream.flush();
            mOutputstream.close();
            mInputStream.close();
            //Database file is Copied here
            checkandUpdateLoginTimes_UpdateDatabaseCheck();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void checkandUpdateLoginTimes_UpdateDatabaseCheck() {

        //       Check for Database Update

        Cursor cursor1 = new DatabaseHelper(context, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "DB_VERSION").readalldata();
        while (cursor1.moveToNext()) {
            int DB_VERSION_FROM_DATABASE = cursor1.getInt(1);

            if (DB_VERSION_FROM_DATABASE != SplashScreen.DB_VERSION_INSIDE_TABLE) {
                DatabaseHelper databaseHelper2 = new DatabaseHelper(context, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "DB_VERSION");
                databaseHelper2.db_delete();
            }

        }
        cursor1.close();

    }

    public void db_delete() {

        File file = new File(DbPath + DbName);
        if (file.exists()) {
            file.delete();
            Log.d("TAGA", "db_delete: " + "Database Deleted " + DbName);

        }
        CopyDatabases();
    }

    public void OpenDatabase() {
        String path = DbPath + DbName;
        SQLiteDatabase.openDatabase(path, null, 0);

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
        if (Database_tableNo.equals("UserInformation")) {
            qry = "select * from UserInformation";
        }

        if (Database_tableNo.equals("DB_VERSION")) {
            qry = "select * from DB_VERSION";
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
        if (Database_tableNo.equals("Audio_Story")) {
            qry = "select * from Audio_Story";
        }
        if (Database_tableNo.equals("Audio_Story_Fake")) {
            qry = "select * from Audio_Story_Fake";
        }


        SQLiteDatabase db = this.getWritableDatabase();

        cursor = db.rawQuery(qry, null);
        return cursor;

    }

    public String updaterecord(int _id, int like_value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Liked", like_value);

        float res = db.update(Database_tableNo, cv, "id=" + _id, null);
        if (res == -1)
            return "Failed";
        else
            return "Liked";

    }


    public String addAudioStoriesLinks(String title, String link) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put("storyName", title);
        values.put("storyURL", link);

        // after adding all values we are passing
        // content values to our table.
        float res = db.insert("Audio_Story_Fake", null, values);
        if (res == -1)
            return "Failed";
        else
            return "Sucess";
    }

    public String addstories(String Date, String Heading, String Title) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Date", Date);
        values.put("Heading", Heading);
        values.put("Title", Title);
        values.put("Liked", 0);

        float res = db.insert(Database_tableNo, null, values);
        if (res == -1)
            return "Failed";
        else
            return "Sucess";

    }

    public String deleteRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        float res = db.delete(Database_tableNo, null, null);
        if (res == -1)
            return "Failed";
        else
            return "Deleted all rows";
    }


    public String updateEncryptStory(int _id, String encryptedStory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Heading", encryptedStory);

        float res = db.update(Database_tableNo, cv, "id=" + _id, null);
        if (res == -1)
            return "Failed";
        else
            return Database_tableNo + " " + _id + ": Sucess";

    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }


}
