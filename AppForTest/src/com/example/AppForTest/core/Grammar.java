package com.example.AppForTest.core;

import android.util.Log;
import android.util.Pair;
import com.example.AppForTest.util.MyTrie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;

/**
 * Created by lss on 2016/3/23.
 *
 * @TODO: no rules for keyboard sequence
 */
public class Grammar {
    private JSONObject jsonGrammar;
    private JSONArray nonTerminals;
    private Map<String, Integer> frequencyMap;
    private Map<String, Integer> timeList, yearList, dateList, monthList, gRules;
//    private Hashtable<String, Integer> letterTable, symbolTable, digitTable, gTable, rTable;
//    private MyDAWG dawg;
    private MyTrie trie;

    // Map<nonT, Map<rhs, freq>>
    private Map<String, Map<String, Integer>> G = new HashMap<>();

    private Map<Character, Character> l33tReplacement = new HashMap<>();

    {
        l33tReplacement.put('3', 'e');
        l33tReplacement.put('4', 'a');
        l33tReplacement.put('@', 'a');
        l33tReplacement.put('$', 's');
        l33tReplacement.put('0', 'o');
        l33tReplacement.put('1', 'i');
        l33tReplacement.put('z', 's');
    }

//    public Grammar(String file) {
    public Grammar(InputStream fis) {
        frequencyMap = new HashMap<>();
        initialize();
//        interpretGrammarFile(file);
        interpretGrammarFile(fis);
    }

    private void initialize() {
        trie = new MyTrie(l33tReplacement);
//        wordList = new Hashtable<>();
        timeList = new Hashtable<>();
        yearList = new Hashtable<>();
        dateList = new Hashtable<>();
        monthList = new Hashtable<>();
        gRules = new HashMap<>();
//        letterTable = new Hashtable<>();
//        symbolTable = new Hashtable<>();
//        digitTable = new Hashtable<>();
//        gTable = new Hashtable<>();
    }

//    private void interpretGrammarFile(String path) {
    private void interpretGrammarFile(InputStream fis) {
        try {
//            FileInputStream fis = new FileInputStream(path);
            int size = fis.available();
            byte[] bytes = new byte[size];
            if (fis.read(bytes) != size)
                Log.w("Grammar", "file size mismatch");
            fis.close();
            jsonGrammar = new JSONObject(new String(bytes, "UTF-8"));
            nonTerminals = jsonGrammar.names();
            for (int i = 0; i < nonTerminals.length(); i++) {
                String nonT = (String) nonTerminals.get(i);
                JSONObject rhslist = jsonGrammar.getJSONObject(nonT);
                JSONArray names = rhslist.names();
                Map<String, Integer> rhsTable = new HashMap<>();
                int total = 0;
                for (int j = 0; j < names.length(); j++) {
                    String tmp = (String) names.get(j);
                    int freq = rhslist.getInt(tmp);
                    total += freq;
                    if (nonT.startsWith("W"))
                        trie.put(tmp, freq);
//                        wordList.put(tmp, freq);
//                    else if (nonT.startsWith("L_"))
//                        letterTable.put(tmp, freq);
//                    else if (nonT.startsWith("D"))
//                        digitTable.put(tmp, freq);
                    switch (nonT) {
                        case "T": timeList.put(tmp, freq);
                            break;
                        case "T_Y": yearList.put(tmp, freq);
                            break;
                        case "T_y": yearList.put(tmp, freq);
                            break;
                        case "T_m": monthList.put(tmp, freq);
                            break;
                        case "T_d": dateList.put(tmp, freq);
                            break;
                        case "G": gRules.put(tmp, freq);
                            break;
//                        case "Y1": symbolTable.put(tmp, freq);
//                            break;
//                        case "G": gTable.put(tmp, freq);
//                            break;
//                        case "R": rTable.put(tmp, freq);
//                            break;
                    }
                    rhsTable.put(tmp, freq);
                }
                frequencyMap.put(nonT, total);
                G.put(nonT, rhsTable);
            }
//            for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
//                Log.d("Grammar", entry.getKey() + " - " + entry.getValue());
//            }
//            dawg = new MyDAWG(new ArrayList<>(wordList.keySet()));
//            dawg.setReplacementMap(l33tReplacement);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public Rule parse(String s) {
        int length = s.length();
        Rule[][] pi = new Rule[length][length];
        List<Rule> rules = new ArrayList<>();
        // first parse every single character
        // then try to parse segments and find the most probable parsing
        // find combination parsing
        // i & k ~ [0, length-1]; j ~ [1, length]
        for (int l = 0; l < length; l++) {
            for (int i = 0; i < length - l; i++) {
                int j = i + l; // substring from i to j
//                Log.d("Grammar", "parse: " + s.substring(i, j+1));
                pi[i][j] = getMatchRules(s.substring(i, j + 1));
                if (pi[i][j] != null)
                    rules.add(pi[i][j]);
                for (int k = i; k < j; k++)
                    if (pi[i][k] != null && pi[k + 1][j] != null
                            && !startWithTL(pi[i][k]) && !startWithTL(pi[k+1][j]))
                        rules.add(combineRules(pi[i][k], pi[k + 1][j]));
//                Log.d("Grammar", rules.toString());
                if (!rules.isEmpty())
                    pi[i][j] = getMaxRule(rules);
                rules.clear();
            }
        }
        Rule result = pi[0][length - 1];
        if (result != null)
            return result;
        // build a default parse tree
        return new Rule("G", s);
    }

    private Rule getMaxRule(List<Rule> rules) {
        double max = 0.0;
        Rule result = null;
        for (Rule r : rules) {
            if (max < r.getProb()) {
                max = r.getProb();
                result = r;
            }
        }
        return result;
    }

    public static Rule combineRules(Rule r1, Rule r2) {
        if (r1 == null || r2 == null) return null;
        Rule r = new Rule(r1.lhs + "\t" + r2.lhs, r1.rhs + "\t" + r2.rhs, r1.getProb() * r2.getProb());
        List<Rule> extras = new ArrayList<>();
        if (r1.getExtras() != null)
            extras.addAll(r1.getExtras());
        if (r2.getExtras() != null)
            extras.addAll(r2.getExtras());
        r.setExtras(extras);
        return r;
    }

    private boolean startWithTL(Rule rule) {
        if (rule.lhs.startsWith("L_") || rule.lhs.startsWith("T_"))
            return true;
        return false;
    }

    public Rule getMatchRules(String seg) {
        List<Rule> rules = new ArrayList<>();
        // first look for word rules
        Rule rule = parseWord(seg);
        if (rule != null)
            rules.add(rule);
        // then check if it is a datetime expression
        rule = parseDatetime(seg);
        if (rule != null)
            rules.add(rule);
//        Log.d("Grammar", "datetime rule: \n" + rule.toString());
//        List<Rule> rules = new ArrayList<>();
//        String similarWord = dawg.getSimilarWord(seg);
//        if (similarWord != null) {
//            int segLength = seg.length();
//            String wordGroup = getWordGroup(similarWord);
//            double totalFreq = frequencyMap.get(wordGroup);
//            double similarWordProb = ((double)wordList.get(similarWord)) / totalFreq;
//            Log.d("Grammar", "similar word probability:  " + similarWordProb);
//            // parse the word segment
//            List<Rule> extraL = new ArrayList<>();
//            String word = seg.toLowerCase();
//            if (similarWord.equals(word)) {
//                if (seg.matches("[a-z]+"))
//                    extraL.add(new Rule("L", "lower", 1.0));
//                else if (seg.matches("[A-Z]+"))
//                    extraL.add(new Rule("L", "UPPER", 1.0));
//                else if (seg.matches("[A-Z][a-z]+"))
//                    extraL.add(new Rule("L", "Caps", 1.0));
//                else {
//                    extraL.add(new Rule("L", "l33t", 1.0));
//                    for (int i = 0; i < segLength; i++)
//                        extraL.add(new Rule("L_" + word.charAt(i), String.valueOf(seg.charAt(i)), 0.0));
//                }
//            }else {
//                int countDiff = 0;
//                for (int i = 0; i < segLength; i++)
//                    if (word.charAt(i) != similarWord.charAt(i))
//                        countDiff++;
//                extraL.add(new Rule("L", "l33t", 1.0 - (((double)countDiff) / ((double)segLength))));
//                for (int i = 0; i < segLength; i++)
//                    extraL.add(new Rule("L_" + similarWord.charAt(i), String.valueOf(seg.charAt(i)), 0.0));
//            }
//            Rule rule = new Rule(wordGroup, similarWord, similarWordProb);
//            rule.setExtras(extraL);
//            rules.add(rule);
//        }

        // for testing word rule list
//        for (Rule r: rules) {
//            Log.d("Grammar", "rule: " + r.toString());
//            List<Rule> extras = r.getExtras();
//            if (extras != null)
//                for (Rule re: extras)
//                    Log.d("Grammar", " extra rule: " + re.toString());
//        }

        // check all other rules
        for (int i = 0; i < nonTerminals.length(); i++) {
            try {
                String nt = (String) nonTerminals.get(i);
                if (nt.startsWith("W") || nt.startsWith("T") || nt.startsWith("L"))
                    continue;
                JSONObject terminals = jsonGrammar.getJSONObject(nt);
                try {
                    int freq = terminals.getInt(seg);
                    rules.add(new Rule(nt, seg, freq / (double)frequencyMap.get(nt)));
                }catch (JSONException ignored) {
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return getMaxRule(rules);

        // find the most probable rule
//        Log.d("Grammar", "all matched rules for " + seg + " : ");
//        double maxProb = -1.0;
//        Rule result = null;
//        for (Rule r: rules) {
//            Log.d("Grammar", r.toString());
//            double prob = r.getProb();
//            if (maxProb >= prob)
//                continue;
//            result = r;
//            maxProb = prob;
//        }
//        Log.d("Grammar", "the most probable rule: " + ((result != null) ? result.toString() : "null"));
//        return result;
    }

    private Rule parseWord(String s) {
        if (s == null) {
            Log.e("Grammar", "null input");
            return null;
        }
//        String similarWord = dawg.getSimilarWord(s);
//        if (similarWord != null) {
        MyTrie.WordPair pair = trie.findWord(s);
        if (pair != null) {
            String similarWord = pair.word;
            int wordFreq = pair.freq;
//            Log.d("Grammar", "similar word: " + similarWord);
            int segLength = s.length();
            String wordGroup = getWordGroup(similarWord);
            double totalFreq = (double)frequencyMap.get(wordGroup);
            double similarWordProb = wordFreq / totalFreq;
//            double similarWordProb = ((double)wordList.get(similarWord)) / totalFreq;
//            Log.d("Grammar", "for word: " + s + " wordFreq: " + wordFreq);
//            Log.d("Grammar", "totalFreq: " + totalFreq + " similar word probability:  " + similarWordProb);
            // parse the word segment
            List<Rule> extraL = new ArrayList<>();
            String word = s.toLowerCase();
            if (similarWord.equals(word)) {
                if (s.matches("[a-z]+"))
                    extraL.add(new Rule("L", "lower", 1.0));
                else if (s.matches("[A-Z]+"))
                    extraL.add(new Rule("L", "UPPER", 1.0));
                else if (s.matches("[A-Z][a-z]+"))
                    extraL.add(new Rule("L", "Caps", 1.0));
                else {
                    extraL.add(new Rule("L", "l33t", 1.0));
                    for (int i = 0; i < segLength; i++)
                        extraL.add(new Rule("L_" + word.charAt(i), String.valueOf(s.charAt(i)), 0.0));
                }
            }else {
                int countDiff = 0;
                for (int i = 0; i < segLength; i++)
                    if (word.charAt(i) != similarWord.charAt(i))
                        countDiff++;
                extraL.add(new Rule("L", "l33t", 1.0 - (((double)countDiff) / ((double)segLength))));
                for (int i = 0; i < segLength; i++)
                    extraL.add(new Rule("L_" + similarWord.charAt(i), String.valueOf(s.charAt(i)), 0.0));
            }
            Rule rule = new Rule(wordGroup, similarWord, similarWordProb);
            rule.setExtras(extraL);
            return rule;
        }
        return null;
    }

    private String getWordGroup(String word) {
        int length = word.length();
        if (length == 1)
            return "W1";
        else if (length < 9)
            return "W" + (length - 1);
        else return "W9";
    }

    private Rule parseDatetime(String s) {
        // It seems Android don't support named-capturing
        String dd = "([0-2][0-9]|3[01])";
        String mm = "(0[0-9]|1[0-2])";
        String yy = "([0-9]{2})";
        String yyyy = "(19[6-9][0-9]|20[0-1][0-9])";
        String dmy = "(" + dd + mm + yy + ")";
        String dmY = "(" + dd + mm + yyyy + ")";
        String Ymd = "(" + yyyy + mm + dd + ")";
        String ymd = "(" + yy + mm + dd + ")";
        String md = "(" + mm + dd + ")";
        String mdy = "(" + mm + dd + yy + ")";
        String mdY = "(" + mm + dd + yyyy + ")";
//        Pattern datePattern = Pattern.compile(
//                "(" + dd + mm + yy + ")|" +
//                "(" + dd + mm + yyyy + ")|" +
//                "(" + yyyy + mm + dd + ")|" +
//                "(" + yy + mm + dd + ")|" +
//                "(" + mm + dd + ")|" +
//                "(" + mm + dd + yy + ")|" +
//                "(" + mm + dd + yyyy + ")|" +
//                "(" + yy + ")|" +
//                "(" + yyyy + ")");
//        if (!datePattern.matcher(s).matches())
//            return null;

        // WTF
        // @TODO: find a way to simplify these redundant codes
        String td, tm, ty, tY, rhs;
        td = tm = ty = tY = null;
        double pd, pm, py, pY;
        Rule result;
        List<Rule> rules = new ArrayList<>();

        if (s.matches(dmy)){
            rhs = "dmy";
            td = s.substring(0, 2);
            tm = s.substring(2, 4);
            ty = s.substring(4, 6);
        }else if (s.matches(dmY)) {
            rhs = "dmY";
            td = s.substring(0, 2);
            tm = s.substring(2, 4);
            tY = s.substring(4, 8);
        }else if (s.matches(Ymd)) {
            rhs = "Ymd";
            tY = s.substring(0, 4);
            tm = s.substring(4, 6);
            td = s.substring(6, 8);
        }else if (s.matches(ymd)) {
            rhs = "ymd";
            ty = s.substring(0, 2);
            tm = s.substring(2, 4);
            td = s.substring(4, 6);
        }else if (s.matches(md)) {
            rhs = "md";
            tm = s.substring(0, 2);
            td = s.substring(2, 4);
        }else if (s.matches(mdy)) {
            rhs = "mdy";
            tm = s.substring(0, 2);
            td = s.substring(2, 4);
            ty = s.substring(4, 6);
        }else if (s.matches(mdY)) {
            rhs = "mdY";
            tm = s.substring(0, 2);
            td = s.substring(2, 4);
            tY = s.substring(4, 8);
        }else if (s.matches(yy)) {
            rhs = "y";
            ty = s;
        }else if (s.matches(yyyy)) {
            rhs = "Y";
            tY = s;
        }else return null;
        double prob = Math.pow(10, s.length() - 8);
        prob *= timeList.get(rhs) / (double)frequencyMap.get("T");
        if (td != null) {
            pd = (double)(dateList.get(td) != null ? dateList.get(td) : 0) / frequencyMap.get("T_d");
            rules.add(new Rule("T_d", td, pd));
            prob *= pd;
        }
        if (tm != null) {
            pm = (double)(monthList.get(tm) != null ? monthList.get(tm) : 0) / frequencyMap.get("T_m");
            rules.add(new Rule("T_m", tm, pm));
            prob *= pm;
        }
        if (ty != null) {
            py = (double)(yearList.get(ty) != null ? yearList.get(ty) : 0) / frequencyMap.get("T_y");
            rules.add(new Rule("T_y", ty, py));
            prob *= py;
        }
        if (tY != null) {
            pY = (double)(yearList.get(tY) != null ? yearList.get(tY) : 0) / frequencyMap.get("T_Y");
            rules.add(new Rule("T_Y", tY, pY));
            prob *= pY;
        }
        result = new Rule("T", rhs, prob);
        result.setExtras(rules);
        return result;
    }

    public String derivation(List<Rule> rules) {
        StringBuilder sb = new StringBuilder();
        for (Rule rule : rules) {
            if (rule.lhs.equals("G")) {

            } else if (rule.lhs.startsWith("W")) {
                // word rules
                List<Rule> extras = rule.getExtras();
//                Rule l = extras.remove(0);
                switch (extras.remove(0).rhs) {
                    case "lower": sb.append(rule.rhs);
                        break;
                    case "UPPER": sb.append(rule.rhs.toUpperCase());
                        break;
                    case "Caps":
                        char c = rule.rhs.charAt(0);
                        sb.append(rule.rhs.replaceFirst(String.valueOf(c), String.valueOf((char)(c+('A'-'a')))));
                        break;
                    case "l33t":
                        for (Rule r: extras)
                            sb.append(r.rhs);
                        break;
                }
            } else if (rule.lhs.equals("T")) {
                // datetime rules
                Map<String, String> tmp = new HashMap<>();
                for (Rule r: rule.getExtras())
                    tmp.put(r.lhs, r.rhs);
                for (char c: rule.rhs.toCharArray())
                    sb.append(tmp.get("T_" + c));
            } else sb.append(rule.rhs);
        }
        return sb.toString();
    }

    public List<Rule> buildParseTree(Rule r) {
        if (r.lhs.equals("G"))
            buildDefaultParseTree(r.rhs);
        List<Rule> pt = new ArrayList<>();
        List<Rule> extras = r.getExtras();
        String[] st1 = r.lhs.split("\t");
        String[] st2 = r.rhs.split("\t");
        for (int i = 0; i < st1.length; i++) {
            pt.add(new Rule("G", st1[i] + ((i == st1.length - 1) ? "" : ",G")));
            if (st1[i].startsWith("W")) {
                Rule rule = new Rule(st1[i], st2[i]);
                List<Rule> tmp = new ArrayList<>();
                tmp.add(extras.remove(0));
                if (tmp.get(0).rhs.equals("l33t")) {
                    for (int j = 0; j < st2[i].length(); j++) {
                        tmp.add(extras.remove(0));
                    }
                }
                rule.setExtras(tmp);
                pt.add(rule);
            } else if (st1[i].equals("T")) {
                Rule rule = new Rule(st1[i], st2[i]);
                List<Rule> tmp = new ArrayList<>();
                for (int j = 0; j < st2[i].length(); j++)
                    tmp.add(extras.remove(0));
                rule.setExtras(tmp);
                pt.add(rule);
            } else pt.add(new Rule(st1[i], st2[i]));
        }
        return pt;
    }

    // build a default parse tree as catch-all rule
    // in the form of G -> W1,G | D1,G | Y1,G | W1 | D1 | Y1
    private List<Rule> buildDefaultParseTree(String s) {
        List<Rule> pt = new ArrayList<>();
        // @TODO: how to handle rule that does not exist in the grammar file e.g. W1->n / o
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String append = (i == s.length() - 1) ? "" : ",G";
            if (c >= 'a' && c <= 'z') { // lowercase letter
                pt.add(new Rule("G", "W1" + append));
                Rule r = new Rule("W1", String.valueOf(c));
                r.addExtra(new Rule("L", "lower"));
                pt.add(r);
            } else if (c >= 'A' && c <= 'Z') {
                pt.add(new Rule("G", "W1" + append));
                Rule r = new Rule("W1", String.valueOf((char)(c - ('A' - 'a'))));
                r.addExtra(new Rule("L", "UPPER"));
                pt.add(r);
            } else if (c >= '0' && c <= '9') {
                pt.add(new Rule("G", "D1" + append));
                pt.add(new Rule("D1", String.valueOf(c)));
            } else {
                pt.add(new Rule("G", "Y1" + append));
                pt.add(new Rule("Y1", String.valueOf(c)));
            }
        }
        return pt;
    }

    // deal with all G rules, to build a probability parsing rule list
    // if there are more than one element in the right hand side, then add it as a "_,G" rule part
    // else add it as a single rule
    private void handleGRules() {
        JSONObject gTable = new JSONObject();
        try {
            for (Map.Entry<String, Integer> entry : gRules.entrySet()) {
                String rhs = entry.getKey();
                int freq = entry.getValue();
                if (rhs.contains(",")) {
                    StringTokenizer st = new StringTokenizer(rhs, ",");
                    if (st.hasMoreElements()) {
                        String ruleName = st.nextToken() + ",G";
                        if (gTable.has(ruleName))
                            gTable.put(ruleName, gTable.getInt(ruleName) + freq);
                        else gTable.put(ruleName, freq);
                    }
                } else {
                    gTable.put(rhs, freq);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * encode the parse tree into a list of probability pairs
     * @param pt
     * @return a list of pairs contains the frequency count and cumulative frequencies
     */
    public List<Pair<Integer, Integer>> encodeParseTree(List<Rule> pt) {
        handleGRules();
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        try {
            for (Rule rule : pt) {
                int q = frequencyMap.get(rule.lhs), p = -1;
                int cumFreq = 0;
                JSONObject rhslist = jsonGrammar.getJSONObject(rule.lhs);
                JSONArray namelist = rhslist.names();
                for (int i = 0; i < namelist.length(); i++) {
                    String name = namelist.getString(i);
                    int freq = rhslist.getInt(name);
                    if (name.equals(rule.lhs)) {
                        p = random.nextInt(freq) + cumFreq;
                    } else {
                        cumFreq += freq;
                    }
                }
                if (p == -1) {
                    Log.e("Grammar", "error for encoding parse tree - rule not found! : " + rule.toString());
                    return null;
                }
                result.add(new Pair<>(p, q));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
