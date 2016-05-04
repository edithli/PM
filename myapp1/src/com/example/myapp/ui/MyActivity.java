package com.example.myapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.myapp.R;
import com.example.myapp.core.Grammar;
import com.example.myapp.core.SubGrammar;
import com.example.myapp.core.TrainedGrammar;
import com.example.myapp.database.UserInfo;
import com.example.myapp.logic.Crypto;
import com.example.myapp.util.CommonFunction;
import com.example.myapp.util.Constants;

import java.io.IOException;

public class MyActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText et = (EditText)findViewById(R.id.input_username);
        et.setText(null);
        ((EditText)findViewById(R.id.input_mpw)).setText(null);
        et.requestFocus();
    }

    public void login(View view){
        EditText usernameET = (EditText) findViewById(R.id.input_username);
        EditText pwdET = (EditText) findViewById(R.id.input_mpw);
        String username = usernameET.getText().toString();
        String pwd = pwdET.getText().toString();
        if (CommonFunction.emptyEditText(usernameET, R.string.input_username) ||
                CommonFunction.emptyEditText(pwdET, R.string.input_mpw))
            return;
        UserInfo userInfo = new UserInfo(getApplicationContext());
        if (!userInfo.isUserExisted(username)){
            usernameET.setText(null);
            pwdET.setText(null);
            usernameET.setHint(R.string.user_not_registered);
            usernameET.setHintTextColor(Color.RED);
            usernameET.requestFocus();
            return;
        }
        // initial Crypto
        Crypto.init(pwd);
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra(Constants.USERNAME, username);
//        intent.putExtra(Constants.PASSWORD, pwd);
        startActivity(intent);
    }

    public void register(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void hintPassword(View view) {
        String username = ((EditText) findViewById(R.id.input_username)).getText().toString();
        Intent intent = new Intent(this, PasswordHintActivity.class);
        intent.putExtra(Constants.USERNAME, username);
        startActivity(intent);
    }

    private void initialize() {
        AssetManager assets = getAssets();
        try {
            TrainedGrammar.initialize(assets.open("grammar.cfg"));
            SubGrammar.initialize(assets.open("vault_dist.cfg"), TrainedGrammar.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
