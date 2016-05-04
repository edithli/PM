package com.example.myapp.core;

import android.util.Log;
import com.example.myapp.util.MyTrie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by lss on 2016/4/4.
 */
public class TrainedGrammar {
    private final static int DEFAULT_FREQUENCY = 1;
    private MyTrie trie = new MyTrie();

    // Map<nonT, Map<rhs, freq>>
    private Map<String, Map<String, Integer>> G = new HashMap<>();
    private Map<String, Integer> totalFreq = new HashMap<>();

    private static TrainedGrammar tg;

    public static TrainedGrammar getInstance() {
        if (tg == null)
            Log.e("TG", "TrainedGrammar uninitialized!");
        return tg;
    }

    public static void initialize(InputStream is) {
        tg = new TrainedGrammar(is);
    }

    /**
     * build a trained grammar based on the grammar file in JSON form
     * @param is The InputStream of the grammar file
     */
    private TrainedGrammar(InputStream is) {
        // interpret the grammar file and get the grammar
        try {
            byte[] allBytes = new byte[is.available()];
            is.read(allBytes);
            String content = new String(allBytes, "utf-8");
            JSONObject jFile = new JSONObject(content);
            JSONArray nonTerminals = jFile.names();
            for (int i = 0; i < nonTerminals.length(); i++) {
                int total = 0;
                boolean addWord = false;
                String nonT = nonTerminals.getString(i);
                if (nonT.startsWith("W")) addWord = true;
                JSONObject jRHS = jFile.getJSONObject(nonT);
                JSONArray rhsNames = jRHS.names();
                Map<String, Integer> rhsTable = new LinkedHashMap<>();
                for (int j = 0; j < rhsNames.length(); j++) {
                    String rhs = rhsNames.getString(j);
                    int freq = jRHS.getInt(rhs);
                    rhsTable.put(rhs, freq);
                    total += freq;
                    if (addWord) trie.put(rhs, freq);
                }
                G.put(nonT, rhsTable);
                totalFreq.put(nonT, total);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        // TEST: print all elements in G - G correctly read
    }

    /**
     * Parse the string using the file specified grammar
     * If the string is  not acceptable by the default grammar, add some catch-all rules to the basic grammar
     * @TODO: remember to save the updated grammar file back
     * @param s
     * @return ParseTree which constitutes of a list of rules
     */
    public List<Rule> parseString(String s) {
        List<Rule> candidates = new ArrayList<>();
        int length = s.length();
        Rule[][] pi = new Rule[length][length];
        for (int l = 0; l < length; l++) {
            for (int i = 0; i < length - l; i++) {
                int j = i + l;
                pi[i][j] = findMatchRule(s.substring(i, j + 1));
                if (pi[i][j] != null) candidates.add(pi[i][j]);
                for (int k = i; k < j; k++) {
                    Rule r = Grammar.combineRules(pi[i][k], pi[k+1][j]);
                    if (r != null) candidates.add(r);
                }
                if (!candidates.isEmpty())
                    pi[i][j] = getMax(candidates);
                candidates.clear();
            }
        }
        Rule total = pi[0][length-1];
        if (total == null || total.getProb() <= 0) {
            Log.e("TG", "cannot find a parse for string: " + s);
            // @TODO: build a default tree and add catch all rules if needed
            return null;
        }
        // TEST: print the final parsing
        Log.d("TG", "parse result for " + s + " : " + total.toString());
        // if there is a rule build a leftmost parse tree
        return buildParseTree(total);
    }

    /**
     * Derive a string according to the parse tree
     * @param list Parse tree of a certain string starting with a 'G' rule
     * @return a string derived from the parse tree
     */
    public String deriveString(List<Rule> list) {
        List<Rule> pt = new ArrayList<>(list);
        // the first rule of the parse tree must be a 'G' rule
        Rule root = pt.remove(0);
        if (!root.lhs.equals("G")) {
            Log.e("TG", "invalid parse tree in derivation!");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] lhsList = root.rhs.split(",");
        for (String lhs : lhsList) {
            // if the pt is not matching, return current string
            if (pt.isEmpty())
                break;
            if (lhs.startsWith("W")) {
                Rule wRule = pt.remove(0);
                if (pt.isEmpty()) break;
                Rule extra1 = pt.remove(0);
                switch (extra1.rhs) {
                    case "lower": sb.append(wRule.rhs);
                        break;
                    case "UPPER": sb.append(wRule.rhs.toUpperCase());
                        break;
                    case "Caps":
                        sb.append(String.valueOf((char)(wRule.rhs.charAt(0) + 'A' - 'a')))
                            .append(wRule.rhs.substring(1));
                        break;
                    case "l33t":
                        for (int i = 0; i < wRule.rhs.length() && !pt.isEmpty(); i++) {
                            if (pt.isEmpty()) return sb.toString();
                            sb.append(pt.remove(0).rhs);
                        }
                        break;
                }
            } else if (lhs.equals("T")) {
                Rule tRule = pt.remove(0);
                for (char c : tRule.rhs.toCharArray()) {
                    if (pt.isEmpty()) return sb.toString();
                    Rule r = pt.remove(0);
                    sb.append(r.rhs);
                }
            } else
                sb.append(pt.remove(0).rhs);
        }
        return sb.toString();
    }

    // @TODO: remember to delete this if this mechanism is wrong!
    // add frequency count for each rule and add newly introduced rules if they don't exist beforehand
    private List<Rule> buildParseTree(Rule rule) {
        List<Rule> pt = new ArrayList<>();
        List<Rule> extras = new ArrayList<>(rule.getExtras());
        StringTokenizer st1 = new StringTokenizer(rule.lhs, "\t");
        StringTokenizer st2 = new StringTokenizer(rule.rhs, "\t");
        pt.add(new Rule("G", rule.lhs.replaceAll("\t", ",")));
        while (st1.hasMoreElements()) {
            String lhs = st1.nextToken();
            String rhs = st2.nextToken();
            pt.add(new Rule(lhs, rhs));
            if (lhs.startsWith("W")) {
                Rule r = extras.remove(0);
                if (r.rhs.equals("l33t")) {
                    pt.add(r);
                    for (int i = 0; i < rhs.length(); i++)
                        pt.add(extras.remove(0));
                } else pt.add(r);
            } else if (lhs.equals("T")) {
                for (int i = 0; i < rhs.length(); i++)
                    pt.add(extras.remove(0));
            }
        }
        // TEST: print all rule in the parse tree
        Log.d("TG", "parse tree: " + pt.toString());
        for (Rule r: pt) {
            Map<String, Integer> rhsMap = G.get(r.lhs);
            int newF;
            if (rhsMap.containsKey(r.rhs))
                newF = rhsMap.get(r.rhs) + 1;
            else
                newF = DEFAULT_FREQUENCY;
            rhsMap.put(r.rhs, newF);
            totalFreq.put(r.lhs, totalFreq.get(r.lhs) + 1);
        }
        return pt;
    }

    // @TODO
    private List<Rule> defaultParseTree(String s) {
        List<Rule> pt = new ArrayList<>();
        for (char c : s.toCharArray()) {

        }
        return pt;
    }

    private Rule findMatchRule(String s) {
        // traverse all rules in the grammar
        List<Rule> candidates = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> g : G.entrySet()) {
            String lhs = g.getKey();
            Map<String, Integer> rhsTable = g.getValue();
            if (startWithTL(lhs)) continue;
            if (lhs.startsWith("W") && lhs.equals(getWordGroup(s))) { // W word rules
                Rule r = parseWord(s);
                if (r != null)  candidates.add(r);
            } else if (lhs.equals("T")) {
                // parse datetime
                Rule r = parseTime(s);
                if (r != null) candidates.add(r);
            } else if (rhsTable.get(s) != null)
                candidates.add(new Rule(lhs, s, rhsTable.get(s) / (double) totalFreq.get(lhs)));
        }
        // TEST: print all candidates
        Rule result = getMax(candidates);
        // TEST: print the max rule
        return result;
    }

    private Rule parseTime(String s) {
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

        // WTF
        // @TODO: find a way to simplify these redundant codes
        String td, tm, ty, tY, rhs;
        double pd, pm, py, pY;
        List<Rule> rules = new ArrayList<>();
        Rule result;

        double prob;

        if (s.matches(dmy)){
            rhs = "dmy";
            td = s.substring(0, 2);
            tm = s.substring(2, 4);
            ty = s.substring(4, 6);
            pd = getDateProb(td);
            pm = getMonthProb(tm);
            py = getyearProb(ty);
            prob = getTProb(rhs) * pd * pm * py;
            result = new Rule("T", rhs, prob);
            rules.add(new Rule("T_d", td, pd));
            rules.add(new Rule("T_m", tm, pm));
            rules.add(new Rule("T_y", ty, py));
            result.setExtras(rules);
            return result;
        }else if (s.matches(dmY)) {
            rhs = "dmY";
            td = s.substring(0, 2);
            tm = s.substring(2, 4);
            tY = s.substring(4, 8);
            pd = getDateProb(td);
            pm = getMonthProb(tm);
            pY = getYearProb(tY);
            prob = getTProb(rhs) * pd * pm * pY;
            result = new Rule("T", rhs, prob);
            result.addExtra(new Rule("T_d", td, pd));
            result.addExtra(new Rule("T_m", tm, pm));
            result.addExtra(new Rule("T_Y", tY, pY));
            return result;
        }else if (s.matches(Ymd)) {
            rhs = "Ymd";
            tY = s.substring(0, 4);
            tm = s.substring(4, 6);
            td = s.substring(6, 8);
            pd = getDateProb(td);
            pm = getMonthProb(tm);
            pY = getYearProb(tY);
            prob = getTProb(rhs) * pd * pm * pY;
            result = new Rule("T", rhs, prob);
            result.addExtra(new Rule("T_Y", tY, pY));
            result.addExtra(new Rule("T_m", tm, pm));
            result.addExtra(new Rule("T_d", td, pd));
            return result;
        }else if (s.matches(ymd)) {
            rhs = "ymd";
            ty = s.substring(0, 2);
            tm = s.substring(2, 4);
            td = s.substring(4, 6);
            pd = getDateProb(td);
            pm = getMonthProb(tm);
            py = getyearProb(ty);
            prob = getTProb(rhs) * pd * pm * py;
            result = new Rule("T", rhs, prob);
            rules.add(new Rule("T_y", ty, py));
            rules.add(new Rule("T_m", tm, pm));
            rules.add(new Rule("T_d", td, pd));
            result.setExtras(rules);
            return result;
        }else if (s.matches(md)) {
            rhs = "md";
            tm = s.substring(0, 2);
            td = s.substring(2, 4);
            pd = getDateProb(td);
            pm = getMonthProb(tm);
            prob = getTProb(rhs) * pd * pm;
            result = new Rule("T", rhs, prob);
            rules.add(new Rule("T_m", tm, pm));
            rules.add(new Rule("T_d", td, pd));
            result.setExtras(rules);
            return result;
        }else if (s.matches(mdy)) {
            rhs = "mdy";
            tm = s.substring(0, 2);
            td = s.substring(2, 4);
            ty = s.substring(4, 6);
            pd = getDateProb(td);
            pm = getMonthProb(tm);
            py = getyearProb(ty);
            prob = getTProb(rhs) * pd * pm * py;
            result = new Rule("T", rhs, prob);
            rules.add(new Rule("T_m", tm, pm));
            rules.add(new Rule("T_d", td, pd));
            rules.add(new Rule("T_y", ty, py));
            result.setExtras(rules);
            return result;
        }else if (s.matches(mdY)) {
            rhs = "mdY";
            tm = s.substring(0, 2);
            td = s.substring(2, 4);
            tY = s.substring(4, 8);
            pd = getDateProb(td);
            pm = getMonthProb(tm);
            pY = getYearProb(tY);
            prob = getTProb(rhs) * pd * pm * pY;
            result = new Rule("T", rhs, prob);
            result.addExtra(new Rule("T_m", tm, pm));
            result.addExtra(new Rule("T_d", td, pd));
            result.addExtra(new Rule("T_Y", tY, pY));
            return result;
        }else if (s.matches(yy)) {
            rhs = "y";
            ty = s;
            py = getyearProb(ty);
            prob = getTProb(rhs) * py;
            result = new Rule("T", rhs, prob);
            result.addExtra(new Rule("T_y", ty, py));
            return result;
        }else if (s.matches(yyyy)) {
            rhs = "Y";
            tY = s;
            pY = getYearProb(tY);
            prob = getTProb(rhs) * pY;
            result = new Rule("T", rhs, prob);
            result.addExtra(new Rule("T_Y", tY, pY));
            return result;
        }else
            return null;
    }

    private double getTProb(String rhs) {
        return G.get("T").get(rhs) / (double) totalFreq.get("T");
    }

    private double getDateProb(String td) {
        return (double)(G.get("T_d").get(td) != null ? G.get("T_d").get(td) : 0) / totalFreq.get("T_d");
    }

    private double getMonthProb(String tm) {
        return (double)(G.get("T_m").get(tm) != null ? G.get("T_m").get(tm) : 0) / totalFreq.get("T_m");
    }

    private double getyearProb(String ty) {
        return (double)(G.get("T_y").get(ty) != null ? G.get("T_y").get(ty) : 0) / totalFreq.get("T_y");
    }

    private double getYearProb(String tY) {
        return (double)(G.get("T_Y").get(tY) != null ? G.get("T_Y").get(tY) : 0) / totalFreq.get("T_Y");
    }

    private Rule parseWord(String s) {
        MyTrie.WordPair pair = trie.findWord(s);
        if (pair != null) {
            String similarWord = pair.word;
            int wordFreq = pair.freq;
            int segLength = s.length();
            String wordGroup = getWordGroup(similarWord);
            Rule rule = new Rule(wordGroup, similarWord, wordFreq / (double)totalFreq.get(wordGroup));
            String word = s.toLowerCase();
            if (similarWord.equals(word)) {
                if (s.matches("[a-z]+"))
                    rule.addExtra("L", "lower");
                else if (s.matches("[A-Z]+"))
                    rule.addExtra("L", "UPPER");
                else if (s.matches("[A-Z][a-z]+"))
                    rule.addExtra("L", "Caps");
                else {
                    rule.addExtra("L", "l33t");
                    for (int i = 0; i < segLength; i++)
                        rule.addExtra(new Rule("L_" + similarWord.charAt(i), String.valueOf(s.charAt(i))));
                }
            }else {
                rule.addExtra("L", "l33t");
                for (int i = 0; i < segLength; i++)
                        rule.addExtra(new Rule("L_" + similarWord.charAt(i), String.valueOf(s.charAt(i))));
            }
            // TEST: print rule of word parsing
//            Log.d("TG", "parse word result: " + rule.toString());
            return rule;
        } else if (s.matches("[\\p{Alpha}]+")) {
            // @TODO
            // insert the new word into corresponding word group
            // update its frequency and the total frequency
            return null;
        }
        return null;
    }

    private String getWordGroup(String s) {
        int length = s.length();
        if (length == 1)
            return "W1";
        else if (length <=8)
            return "W" + String.valueOf(length - 1);
        else return "W9";
    }

    private boolean startWithTL(String lhs) {
        return lhs.startsWith("T_") || lhs.startsWith("L");
    }

    private Rule getMax(List<Rule> rules) {
        // find and return the rule with highest probability
        double max = -1;
        Rule result = null;
        for (Rule r : rules) {
            if (r.getProb() > max) {
                result = r;
                max = r.getProb();
            }
        }
        return result;
    }

    /**
     * Encode the rule using the trained grammar.
     * The rule should be guaranteed to exist and the frequencies have been updated already
     * @param r
     * @return
     */
    public byte[] encodeRule(Rule r) {
        return encodeRule(r.lhs, r.rhs);
    }

    public byte[] encodeRule(String lhs, String rhs) {
        int q = totalFreq.get(lhs);
        int l = 0, f = 0;

        for (Map.Entry<String, Integer> entry: G.get(lhs).entrySet()) {
            if (entry.getKey().equals(rhs)) {
                f = entry.getValue();
                break;
            }
            l += entry.getValue();
        }
        if (f == 0) {
            Log.e("TG", "invalid rule in encoding " + lhs + " -> " + rhs);
            return null;
        }
        return MyDTE.encodeProbability(l, f, q);
    }

    /**
     * Get a rule according to the random number and the left hand side
     * @param p
     * @param lhs
     * @return
     */
    public Rule decodeRule(int p, String lhs) {
        for (Map.Entry<String, Integer> entry : G.get(lhs).entrySet()) {
            if (p < entry.getValue())
                return new Rule(lhs, entry.getKey());
            else p -= entry.getValue();
        }
        Log.e("TG", "something wrong with encoding or decoding ! - lhs: " + lhs);
        return null;
    }

    public Rule decodeRule(byte[] bytes, String lhs) {
        int q = totalFreq.get(lhs);
        int p = MyDTE.decodeProbability(bytes, q);
        Log.d("test", "decode p: " + p + " , q: " + q + " - " + lhs);
        return decodeRule(p, lhs);
    }

    /**
     * Get the frequency of the rule in the trained grammar. (Only called after the parse tree is determined since this method would update frequency.)
     * If the rule does not exist in grammar, add the rule into it, set its frequency to 1 and update the total frequency count
     * @param rule
     * @return
     */
    public int getFrequency(Rule rule) {
        // @TODO: remember to store the grammar back into the grammar file - NOT REASONABLE since newly added rules are not equally distributed as others
        if (G.containsKey(rule.lhs) && G.get(rule.lhs).containsKey(rule.rhs))
            return G.get(rule.lhs).get(rule.rhs);
        else return 0;
    }
}
