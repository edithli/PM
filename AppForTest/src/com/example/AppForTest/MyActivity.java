package com.example.AppForTest;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import com.example.AppForTest.core.*;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        AssetManager assets = getAssets();
        try {
            TrainedGrammar tg = new TrainedGrammar(assets.open("grammar.cfg"));
            // TEST tg parse and derive
//            List<Rule> pt1 = tg.parseString("Hellob0ok20120314");
//            List<Rule> pt2 = tg.parseString("s@mb0##888888");
//            List<Rule> pt = new ArrayList<>(pt1);
//            pt.addAll(pt2);
//            Log.d("test", tg.deriveString(pt));

            // TEST rule encoding and decoding
//            Rule r = new Rule("W9", "helloworld");
//            byte[] b = tg.encodeRule(r);
//            Log.d("TG", tg.decodeRule(b, "W9").toString());

            SubGrammar sg = new SubGrammar(assets.open("vault_dist.cfg"), tg);
//            sg.buildSubGrammar(pt);

            HoneyVault hv = new HoneyVault(tg, sg);
//            // TEST: encodeParseTree
//            byte[] b = hv.encodeParseTree(pt);
//            Log.d("test", hv.decodeParseTree(b));

            // TEST vault encode and decode
            List<String> pwdList = new ArrayList<>();
            pwdList.add("hello980315");
            pwdList.add("s@mb0##888888");
            pwdList.add("12345passy@126");
            pwdList.add("helloworld");
            pwdList.add("password123");
            pwdList.add("olala@@@@74742903");
//            List<Rule> pt = new ArrayList<>();
//            for (String s: pwdList)
//                pt.addAll(tg.parseString(s));
//            // TEST sub grammar encode and decode
//            sg.buildSubGrammar(pt);
//            byte[] bb = sg.encodeSubGrammar();
//            sg.decodeSubGrammar(bb);

            byte[] b = hv.encodeVault(pwdList);
            Log.d("test", hv.decodeVault(b).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
