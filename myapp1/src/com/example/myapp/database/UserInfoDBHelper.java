package com.example.myapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lss on 2016/2/26.
 */
public class UserInfoDBHelper extends SQLiteOpenHelper {
    public final static String TB_NAME = "UserInfo";
    public final static String DB_NAME = "users.db";
    public final static int DB_VERSION = 1;

    public UserInfoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + "("
                + UserInfo.USERNAME + " varchar primary key, "
                + UserInfo.PWD_HINT + " varchar)");
        Log.v("database", "UserInfo create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        Log.v("database", "UserInfo dropped");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
