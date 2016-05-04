package com.example.myapp.util;

import android.graphics.Color;
import android.widget.EditText;

/**
 * Created by lss on 2016/3/2.
 */
public class CommonFunction {

    public static boolean emptyEditText(EditText et, int hint) {
        String text = et.getText().toString();
        if (text == null || text.equals("")){
            et.setText(null);
            et.requestFocus();
            et.setHintTextColor(Color.RED);
            et.setHint(hint);
            return true;
        }
        return false;
    }
}
