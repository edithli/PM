package com.example.myapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import com.example.myapp.R;
import com.example.myapp.database.UserInfo;
import com.example.myapp.database.UserInfoDBHelper;
import com.example.myapp.util.Constants;

/**
 * Created by lss on 2016/2/25.
 */
public class PasswordHintActivity extends Activity{
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_hint);
        TextView textView = (TextView) findViewById(R.id.password_hint);
        textView.setTextSize(30);
        Intent intent = getIntent();
        String username = intent.getStringExtra(Constants.USERNAME);
        if (username == null || username.equals(""))
            textView.setText(R.string.input_username);
        // find the password hints from the database
        UserInfo userInfo = new UserInfo(getApplicationContext());
        String pwdHint = userInfo.queryPasswordHint(username);
        if (pwdHint == null)
            textView.setText(R.string.user_not_registered);
        else textView.setText(pwdHint);
    }
}
