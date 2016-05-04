package com.example.myapp.core;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.*;

/**
 * Created by lss on 2016/4/4.
 */
public class SubGrammar {
    private TrainedGrammar baseGrammar;

    private Map<String, Map<String, Integer>> g;
    private Map<String, Integer> frequencyMap;

    private Map<String, Map<Integer, Integer>> sizeMap;
    private Map<String, Integer> sizeTotalMap;

    private static SubGrammar sg;

    public static void initialize(InputStream is, TrainedGrammar trainedGrammar) {
        sg = new SubGrammar(is, trainedGrammar);
    }

    public static SubGrammar getInstance() {
        if (sg == null)
            Log.e("SG", "Sub grammar not initialized!");
        return sg;
    }

    /**
     * Initialize a sub grammar based on the vault_dist.cfg and the base trained grammar
     * @param is InputStream of vault_dist.cfg
     * @param baseGrammar the base trained grammar used to encode the sub-grammar
     */
    private SubGrammar(InputStream is, TrainedGrammar baseGrammar) {
        this.baseGrammar = baseGrammar;
        interpretSizeFile(is);
    }

    private void interpretSizeFile(InputStream is) {
        sizeMap = new HashMap<>();
        sizeTotalMap = new HashMap<>();
        try {
            byte[] allBytes = new byte[is.available()];
            is.read(allBytes);
            String content = new String(allBytes, "utf-8");
            JSONObject jFile = new JSONObject(content);
            JSONArray nonTerminals = jFile.names();
            for (int i = 0; i < nonTerminals.length(); i++) {
                Map<Integer, Integer> rightMap = new LinkedHashMap<>();
                String nonT = nonTerminals.getString(i);
                JSONObject jRight = jFile.getJSONObject(nonT);
                JSONArray names = jRight.names();
                int total = 0;
                for (int j = 0; j < names.length(); j++) {
                    String size = names.getString(j);
                    int count = jRight.getInt(size);
                    rightMap.put(Integer.valueOf(size), count);
                    total += count;
                }
                sizeMap.put(nonT, rightMap);
                sizeTotalMap.put(nonT, total);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        // TEST: print sizeMap and sizeTotalMap - done
    }

    /**
     * Build a sub grammar based on all parse tree rules used by passwords in the vault.
     * In this method, the frequency of trained grammar would be updated by invoke getFrequency() but not stored back
     * @TODO: remember to store the updated rules back into the trained grammar file
     */
    public void buildSubGrammar(List<Rule> content) {
        g = new HashMap<>();
        frequencyMap = new HashMap<>();
        for (Rule r : content) {
            if (r.lhs.startsWith("L"))
                continue;
            int freq = baseGrammar.getFrequency(r);
            if (g.containsKey(r.lhs))
                g.get(r.lhs).put(r.rhs, freq);
            else {
                Map<String, Integer> tmp = new LinkedHashMap<>();
                tmp.put(r.rhs, freq);
                g.put(r.lhs, tmp);
            }
        }
        // get the total frequency map of all non terminals
        for (Map.Entry<String, Map<String, Integer>> gEntry : g.entrySet()) {
            Map<String, Integer> map = gEntry.getValue();
            int totalFreq = 0;
            for (Map.Entry<String, Integer> e: map.entrySet())
                totalFreq += e.getValue();
            frequencyMap.put(gEntry.getKey(), totalFreq);
        }

        // TEST: print g and frequency map
        Log.d("test", "build grammar: " + g.toString() + "\n" + frequencyMap.toString());
    }

    /**
     * Encode the sub grammar using the size file, and encode each rule with the trained grammar
     * @return
     */
    public byte[] encodeSubGrammar() {
        byte[] bytes = new byte[MyDTE.SUB_GRAMMAR_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        // start from the 'G' rule
        Stack<String> stack = new Stack<>();
        List<String> done = new ArrayList<>();
        stack.push("G");
        while (!stack.isEmpty()) {
            String nonT = stack.pop();
            done.add(nonT);
            Map<String, Integer> rhsMap = g.get(nonT);
            int ruleNum = rhsMap.size();
            // encode the number of rules with lhs as nonT
            int base = 0;
            Map<Integer, Integer> sizeFreqMap = sizeMap.get(nonT);
            if (!sizeFreqMap.containsKey(ruleNum))
                Log.e("SG", "invalid number of rules in encode sub grammar !");
            for (Map.Entry<Integer, Integer> entry: sizeFreqMap.entrySet()) {
                if (entry.getKey() != ruleNum)
                    base += entry.getValue();
                else break;
            }
            buffer.put(MyDTE.encodeProbability(base, sizeFreqMap.get(ruleNum), sizeTotalMap.get(nonT)));
            Log.d("test", "encode rule size: " + ruleNum + " base: " + base + " range: " + sizeFreqMap.get(ruleNum) + " q: " + sizeTotalMap.get(nonT));
            // then encode each rule of nonT using TG and get the list of other non terminals
            List<String> newNonT = new ArrayList<>();
            for (String rhs: rhsMap.keySet()) {
                Log.d("test", "encode rule: " + nonT + " -> " + rhs);
                buffer.put(baseGrammar.encodeRule(nonT, rhs));
                newNonT.addAll(nonTList(nonT, rhs));
            }
            // if the nonT has not been processed, push it into stack
            for (String st: newNonT)
                if (!done.contains(st) && !stack.contains(st))
                    stack.push(st);
            Log.d("test", "encode grammar stack: " + stack.toString());
        }
        // padding the buffer if not full
        int paddingLength = buffer.capacity() - buffer.position();
        if (paddingLength > 0) {
            byte[] padding = new byte[paddingLength];
            (new SecureRandom()).nextBytes(padding);
            buffer.put(padding);
        }
        Log.d("test", "encode sub grammar done!");
        return buffer.array();
    }

    /**
     * Decode the grammar encodings back into grammar rules using both the size file and the trained grammar.
     * Build a sub grammar based on the decode result
     * @param bytes
     */
    public void decodeSubGrammar(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        List<Rule> rules = new ArrayList<>();
        // start with 'G' rule
        Stack<String> stack = new Stack<>();
        List<String> done = new ArrayList<>();
        stack.push("G");
        byte[] tmp = new byte[MyDTE.BYTE_NUM];
        while (!stack.isEmpty()) {
            String nonT = stack.pop();
            done.add(nonT);
            // first decode the rule size
            int ruleNum = 0;
            int q = sizeTotalMap.get(nonT);
            buffer.get(tmp);
            int p = MyDTE.decodeProbability(tmp, q);
            Log.d("test", "decode rule size: p: " + p + " q: " + q);
            Map<Integer, Integer> sizeFreqMap = sizeMap.get(nonT);
            for (Map.Entry<Integer, Integer> entry: sizeFreqMap.entrySet()) {
                if (p < entry.getValue()) {
                    ruleNum = entry.getKey();
                    break;
                }else p -= entry.getValue();
            }
            if (ruleNum == 0)
                Log.e("SG", "wrong with ruleNum decoding for " + nonT);
            Log.d("test", "decode rule size: " + ruleNum);
            // then parse ruleNum rules starting with nonT
            List<String> newNonT = new ArrayList<>();
            for (int i = 0; i < ruleNum; i++) {
                buffer.get(tmp);
                Rule r = baseGrammar.decodeRule(tmp, nonT);
                Log.d("test", "decode rule: " + r.toString());
                rules.add(r);
                newNonT.addAll(nonTList(r.lhs, r.rhs));
            }
            // if the nonT has not been processed, push it into stack
            for (String st: newNonT)
                if (!done.contains(st) && !stack.contains(st))
                    stack.push(st);
            Log.d("test", "decode grammar stack: " + stack.toString());
        }
        // build grammar based on the rules
        if (rules.isEmpty())
            Log.e("SG", "something wrong with decode sub grammar - got empty rule list");
        buildSubGrammar(rules);
    }

    // get the list of rule types that are required to be encoded in the sub grammar
    // T, T_*, D*, W*, R, Y1
    private List<String> nonTList(String lhs, String rhs) {
        List<String> list = new ArrayList<>();
        switch (lhs) {
            case "G": StringTokenizer st = new StringTokenizer(rhs, ",");
                while (st.hasMoreElements())
                    list.add(st.nextToken());
                break;
            case "T":
                for (char c: rhs.toCharArray())
                    list.add("T_" + c);
                break;
            default:
        }
        return list;
    }

    /**
     * Encode parsing rules with sub grammar
     * @param r
     * @return A pair of integers representing the probability which can be used directly for DTE
     */
    public byte[] encodeRule(Rule r) {
        if (r.lhs.startsWith("L"))
            return baseGrammar.encodeRule(r);
        int q = frequencyMap.get(r.lhs);
        int l = 0, f = 0;
        for (Map.Entry<String, Integer> entry: g.get(r.lhs).entrySet()) {
            if (entry.getKey().equals(r.rhs)) {
                f = entry.getValue();
                break;
            }
            l += entry.getValue();
        }
        if (f == 0) {
            Log.e("SG", "invalid rule in encoding " + r.toString());
            return null;
        }
        return MyDTE.encodeProbability(l, f, q);
    }

    /**
     * Get a rule according to the random number and the left hand side using the sub grammar
     * @param p
     * @param lhs
     * @return
     */
    public Rule decodeRule(int p, String lhs) {
        for (Map.Entry<String, Integer> entry : g.get(lhs).entrySet()) {
            if (p < entry.getValue())
                return new Rule(lhs, entry.getKey());
            else p -= entry.getValue();
        }
        Log.e("SG", "something wrong with encoding or decoding ! - lhs: " + lhs);
        return null;
    }

    public Rule decodeRule(byte[] b, String lhs) {
        if (lhs.startsWith("L"))
            baseGrammar.decodeRule(b, lhs);
        int q = frequencyMap.get(lhs);
        int p = MyDTE.decodeProbability(b, q);
        return decodeRule(p, lhs);
    }

    public boolean isAvailable() {
        return g != null && frequencyMap != null && !g.isEmpty() && !frequencyMap.isEmpty();
    }
}
