package com.example.myapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by lss on 2016/2/26.
 */
public class UserInfo {
    public static final String USERNAME = "username";
    public static final String PWD_HINT = "pwdHint";

    private SQLiteDatabase db;
    private UserInfoDBHelper helper;
    private static final String LOG_TAG = "UserInfo";

    public UserInfo(Context context){
        helper = new UserInfoDBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void insertUserInfo(String username, String pwdHint){
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PWD_HINT, pwdHint);
        Long uid = db.insert(UserInfoDBHelper.TB_NAME, USERNAME, values);
        Log.v(LOG_TAG, "save user " + uid + " username: " + username);
    }

    public String queryPasswordHint(String username){
        Cursor cursor = db.query(UserInfoDBHelper.TB_NAME, new String[]{PWD_HINT},
                USERNAME + "=?", new String[]{username}, null, null, null);
        if (cursor == null)
            return null;
        cursor.moveToFirst();
        if (cursor.isAfterLast())
            return null;
        return cursor.getString(0);
    }

    public boolean isUserExisted(String username){
        return queryPasswordHint(username) != null;
    }

    public void close(){
        db.close();
        helper.close();
    }

    public void deleteUserInfo(String username) {
        db.delete(UserInfoDBHelper.TB_NAME, USERNAME + "=?", new String[]{username});
    }
}
