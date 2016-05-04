package com.example.myapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.myapp.R;
import com.example.myapp.database.UserInfo;
import com.example.myapp.logic.Crypto;
import com.example.myapp.logic.User;
import com.example.myapp.util.CommonFunction;
import com.example.myapp.util.Constants;

/**
 * Created by lss on 2016/2/23.
 */
public class RegisterActivity extends Activity{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
    }

    /**
     * after finishing register forms, generate a key for enc/dec, and jump to the homepage for the register user
     */
    public void finishRegister(View view){
        // @TODO: generate a key using PBKDF with user choosing master password
        // @TODO: generate an initial encrypted file for the new user

        // @TODO: add the username and password hints to a configuration file

        // jump to the homepage
        EditText usernameET = (EditText) findViewById(R.id.register_username);
        String username = usernameET.getText().toString();
        if (CommonFunction.emptyEditText(usernameET, R.string.input_username))
            return;
        EditText pwdET = (EditText)findViewById(R.id.register_pwd);
        EditText pwdEnsureET = (EditText)findViewById(R.id.register_ensure_pwd);
        String pwd = pwdET.getText().toString();
        String pwd_ensure = pwdEnsureET.getText().toString();
        if (CommonFunction.emptyEditText(pwdET, R.string.register_mpw))
            return;
        if (CommonFunction.emptyEditText(pwdEnsureET, R.string.hint_ensure_pwd)) return;
        EditText pwdHintET = (EditText)findViewById(R.id.register_pwd_hints);
        String pwdHint = pwdHintET.getText().toString();
        if (CommonFunction.emptyEditText(pwdHintET, R.string.register_pwd_hint)) return;
        EditText checksumET = (EditText)findViewById(R.id.register_checksum);
        String checksum = checksumET.getText().toString();
        if (CommonFunction.emptyEditText(checksumET, R.string.hint_checksum)) return;
        if (!pwd.equals(pwd_ensure)) {
            pwdEnsureET.requestFocus();
            pwdEnsureET.setText(null);
            pwdEnsureET.setHintTextColor(Color.RED);
            pwdEnsureET.setHint(R.string.hint_ensure_pwd);
            return;
        }
        if (pwd.equals(pwdHint)){
            pwdHintET.setText(null);
            pwdHintET.setHintTextColor(Color.RED);
            pwdHintET.setHint(R.string.bad_pwd_hint);
            return;
        }
        // store the username and pwd_hint information
        // @TODO: encrypt the DB file ?
        UserInfo userInfo = new UserInfo(getApplicationContext());
        if (userInfo.isUserExisted(username)) {
            usernameET.requestFocus();
            usernameET.setText(null);
            usernameET.setHintTextColor(Color.RED);
            usernameET.setHint(R.string.user_existed);
            return;
        }else userInfo.insertUserInfo(username, pwdHint);

        // initialize a user-specific file and put the checksum in
        Crypto.init(pwd);
        User user = User.getInstance(username, getApplicationContext());
        user.register(checksum);

        // start the homepage activity
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra(Constants.USERNAME, username);
        startActivity(intent);
        finish();
    }
}
