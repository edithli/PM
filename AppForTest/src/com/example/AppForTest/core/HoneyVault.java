package com.example.AppForTest.core;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by lss on 2016/4/5.
 */
public class HoneyVault {
    private List<String> pwdList;
    private TrainedGrammar tg;
    private SubGrammar sg;

    public HoneyVault(TrainedGrammar tg, SubGrammar sg) {
        pwdList = new ArrayList<>();
        this.tg = tg;
        this.sg = sg;
    }

    public void addPassword(String pwd) {
        pwdList.add(pwd);
    }

    /**
     *
     * @param pwdList
     * @return
     */
    public byte[] encodeVault(List<String> pwdList) {
        List<Rule> subGrammar = new ArrayList<>();
        Map<String, List<Rule>> ptMap = new HashMap<>();
        for (String pwd : pwdList) {
            List<Rule> pt = tg.parseString(pwd);
            ptMap.put(pwd, pt);
            subGrammar.addAll(pt);
        }
        sg.buildSubGrammar(subGrammar);
        byte[] result = new byte[MyDTE.SUB_GRAMMAR_SIZE + pwdList.size() * MyDTE.PWD_ENCODE_LENGTH];
        ByteBuffer buffer = ByteBuffer.wrap(result);
        // encode sub grammar
        buffer.put(sg.encodeSubGrammar());
        // encode each rule using the sub grammar
        for (String pwd : pwdList) {
            List<Rule> pt = ptMap.get(pwd);
            // encode each rule in the parse tree
            buffer.put(encodeParseTree(pt));
        }
        return buffer.array();
    }

    public List<String> decodeVault(byte[] bytes) {
        List<String> result = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        // first decode sub grammar
        byte[] grammarBytes = new byte[MyDTE.SUB_GRAMMAR_SIZE];
        buffer.get(grammarBytes);
        sg.decodeSubGrammar(grammarBytes);
        // then decode all rest rules
        byte[] ruleBytes = new byte[MyDTE.PWD_ENCODE_LENGTH];
        while (buffer.position() < buffer.capacity()) {
            buffer.get(ruleBytes);
            result.add(decodeParseTree(ruleBytes));
        }
        Log.d("test", "decode vault result: " + result.toString());
        return result;
    }

    /**
     * SubGrammar and Trained Grammar are required to be built before invoking this method.
     * @param pt
     * @return
     */
    public byte[] encodeParseTree(List<Rule> pt) {
        if (sg == null || tg == null || !sg.isAvailable())
            Log.e("HV", "encode parse tree with no grammar!");
        byte[] result = MyDTE.randomBytes(MyDTE.PWD_ENCODE_LENGTH);
        ByteBuffer buffer = ByteBuffer.wrap(result);
        for (Rule r : pt) {
//            Log.d("test", "SG encode rule " + r.toString());
            buffer.put(sg.encodeRule(r));
        }
        return buffer.array();
    }

    public String decodeParseTree(byte[] ptBytes) {
//        Log.d("test", "decode bytes length: " + ptBytes.length);
        List<Rule> result = new ArrayList<>();
        String start = "G";
        ByteBuffer buffer = ByteBuffer.wrap(ptBytes);
        // get the first rule codes
        byte[] tmp = new byte[MyDTE.BYTE_NUM];
        buffer.get(tmp);
        Rule root = sg.decodeRule(tmp, start);
        result.add(root);
        List<String> lhsList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(root.rhs, ",");
        while (st.hasMoreElements())
            lhsList.add(st.nextToken());
        for (String lhs : lhsList) {
            buffer.get(tmp);
            if (lhs.startsWith("W")) {
                Rule wordRule = sg.decodeRule(tmp, lhs);
                result.add(wordRule);
                buffer.get(tmp);
                Rule lRule = tg.decodeRule(tmp, "L");
                result.add(lRule);
                if (lRule.rhs.equals("l33t"))
                    for (char c: wordRule.rhs.toCharArray()) {
                        buffer.get(tmp);
                        result.add(tg.decodeRule(tmp, "L_" + c));
                    }
            } else if (lhs.equals("T")) {
                Rule tRule = sg.decodeRule(tmp, lhs);
                result.add(tRule);
                Map<String, Rule> tmap = new HashMap<>();
                for (char c: tRule.rhs.toCharArray()) {
                    buffer.get(tmp);
                    Rule tmpR = sg.decodeRule(tmp, "T_" + c);
                    result.add(tmpR);
                }
            } else result.add(sg.decodeRule(tmp, lhs));
        }
        Log.d("test", "decode rule list: " + result.toString());
        return tg.deriveString(result);
    }
}
