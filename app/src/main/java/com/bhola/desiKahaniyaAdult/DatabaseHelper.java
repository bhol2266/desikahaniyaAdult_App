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
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "TAGA";
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
        DbPath = "/data/data/" + "com.bhola.desiKahaniyaAdult" + "/databases/";
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
        } catch (Exception e) {

            e.printStackTrace();
        }
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


    public Cursor readsingleRow(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("StoryItems", null, "Title=?", new String[]{encryption(title)}, null, null, null, null);
        return cursor;

    }

    public int readLatestStoryDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("StoryItems", null, null, null, null, null, "completeDate DESC", "1");
        cursor.moveToFirst();
        int completeDate = cursor.getInt(9);
        cursor.close();
        return completeDate;

    }

    public Cursor readalldata() {

        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery("select * from StoryItems", null);
        return cursor;

    }

    public Cursor readAudioStories(int page) {
        int skip = (page - 1) * 15;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("StoryItems", null, "audio=?", new String[]{"1"}, null, null, "completeDate DESC",String.valueOf(skip) + ",15");
        return cursor;

    }

    public Cursor readGridItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("GridItems", null, null, null, null, null, null, null);
        return cursor;
    }


    public Cursor readaDataByCategory(String category, int page) {

        int skip = (page - 1) * 15;
        SQLiteDatabase db = this.getWritableDatabase();
        if (category.equals("Latest Stories")) {
            Cursor cursor = db.query("StoryItems", null, null, null, null, null, "completeDate DESC", String.valueOf(skip) + ",15");
            return cursor;
        } else {
            Cursor cursor = db.query("StoryItems", null, "category=?", new String[]{category}, null, null, "completeDate DESC", String.valueOf(skip) + ",15");
            return cursor;
        }


    }

    public String updaterecord(String title, int like_value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("like", like_value);

        float res = db.update("StoryItems", cv, "Title = ?", new String[]{encryption(title)});
        if (res == -1)
            return "Failed";
        else
            return "Liked";

    }

    public String updateStoryParagraph(String title, String story) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("story", story);

        float res = db.update("StoryItems", cv, "Title = ?", new String[]{encryption(title)});
        if (res == -1)
            return "Failed";
        else
            return "Liked";
    }


    public String addstories(HashMap<String, String> m_li) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", encryption(m_li.get("Title")));
        values.put("href", encryption(m_li.get("href")));
        values.put("date", m_li.get("date"));
        values.put("views", m_li.get("views"));
        values.put("description", encryption(m_li.get("description")));
        values.put("audiolink", encryption(m_li.get("audiolink")));
        values.put("category", m_li.get("category"));
        values.put("tags", m_li.get("tags"));
        values.put("relatedStories", m_li.get("relatedStories"));
        values.put("completeDate", Integer.parseInt(m_li.get("completeDate")));
        values.put("like", 0);
        values.put("story", m_li.get("story"));
        if (m_li.get("audiolink").trim().length() != 0) {
            values.put("audio", 1);
        } else {
            values.put("audio", 0);
        }
        values.put("storiesInsideParagraph", m_li.get("storiesInsideParagraph"));

        float res = db.insert(Database_tableNo, null, values);
        if (res == -1)
            return "Failed";
        else
            return "Sucess";

    }

    private String encryption(String text) {

        int key = 5;
        char[] chars = text.toCharArray();
        String encryptedText = "";
        String decryptedText = "";

        //Encryption
        for (char c : chars) {
            c += key;
            encryptedText = encryptedText + c;
        }

        //Decryption
        char[] chars2 = encryptedText.toCharArray();
        for (char c : chars2) {
            c -= key;
            decryptedText = decryptedText + c;
        }
        return encryptedText;
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }


}
