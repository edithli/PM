package com.example.myapp.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by lss on 2016/3/31.
 */
public class MyTrie {
    private static final char EOW = '#';

    private class MyNode {
        Map<Character, MyNode> childrenTable;
        int freq;

        MyNode(){
            childrenTable = new Hashtable<>();
        }

        MyNode(int freq) {
            this.freq = freq;
            childrenTable = null;
        }

        MyNode findChild(char c) {
            return (childrenTable == null) ? null : childrenTable.get(c);
        }

        MyNode addChild(char c) {
            MyNode child = new MyNode();
            childrenTable.put(c, child);
            return child;
        }

        MyNode addChild(int freq) {
            MyNode child = new MyNode(freq);
            childrenTable.put(EOW, child);
            return child;
        }
    }

    public class WordPair {
        public String word;
        public int freq;

        public WordPair(String word, int freq) {
            this.word = word;
            this.freq = freq;
        }
    }

    private MyNode root;
    private Map<Character, Character> replacementMap;

    public MyTrie() {
        root = new MyNode();
        replacementMap = new HashMap<>();
        replacementMap.put('3', 'e');
        replacementMap.put('4', 'a');
        replacementMap.put('@', 'a');
        replacementMap.put('$', 's');
        replacementMap.put('0', 'o');
        replacementMap.put('1', 'i');
        replacementMap.put('z', 's');
    }

    public MyTrie(Map<Character, Character> replacementMap) {
        root = new MyNode();
        this.replacementMap = replacementMap;
    }

    public void put(String word, int frequency) {
        MyNode tmp = root;
        word = word.toLowerCase();
        for (char c : word.toCharArray()) {
            if (tmp.findChild(c) == null) {
                tmp = tmp.addChild(c);
            } else {
                tmp = tmp.findChild(c);
            }
        }
        if (tmp.findChild(EOW) == null) {
            tmp.addChild(frequency);
        }
    }

    public WordPair findWord(String word) {
        MyNode tmp = root;
        word = word.toLowerCase();
        StringBuilder sb = new StringBuilder();
        char r;
        for (char c : word.toCharArray()) {
            if (tmp.findChild(c) != null) {
                tmp = tmp.findChild(c);
                sb.append(c);
            } else if (replacementMap.get(c) != null && tmp.findChild(r = replacementMap.get(c)) != null) {
                tmp = tmp.findChild(r);
                sb.append(r);
            } else return null;
        }
        if ((tmp = tmp.findChild(EOW)) != null){
            return new WordPair(sb.toString(), tmp.freq);
        }
        return null;
    }

//    public void setReplacementMap(Map<Character, Character> replacementMap) {
//        this.replacementMap = replacementMap;
//    }

    public static void main(String[] args) {
        Map<Character, Character> l33tReplacement = new HashMap<>();
        l33tReplacement.put('3', 'e');
        l33tReplacement.put('4', 'a');
        l33tReplacement.put('@', 'a');
        l33tReplacement.put('$', 's');
        l33tReplacement.put('0', 'o');
        l33tReplacement.put('1', 'i');
        l33tReplacement.put('z', 's');

        MyTrie t = new MyTrie(l33tReplacement);
        t.put("hello", 10);
        t.put("a", 1);
        t.put("aa", 2);
        t.put("aaa", 3);
        t.put("chester", 4);
        t.put("manchester", 5);
        WordPair p = t.findWord("@a");
        System.out.println(p.word + " - " + p.freq);
        p = t.findWord("h3ll0");
        System.out.println(p == null);
    }
}
