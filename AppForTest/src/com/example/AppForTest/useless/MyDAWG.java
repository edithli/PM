package com.example.AppForTest.useless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lss on 2016/3/28.
 *
 * My implementation of Directed Acyclic Finite State Automaton
 */
public class MyDAWG {
    private static final char EOW = '#';

    private DirectedGraph graph;
    private List<Vertex> EOWlist;
    private Map<Character, Character> replacementMap;

    public MyDAWG(List<String> list) {
        graph = new DirectedGraph();
        EOWlist = new ArrayList<>();
        buildDAWG(list);
//        graph.printGraph();
    }

    public void printGraph(String path) {
        graph.printGraph(path);
    }

    public void setReplacementMap(Map<Character, Character> replacementMap) {
        this.replacementMap = replacementMap;
    }

    /**
     * find the most similar word that is also in the dictionary
     * @param s: the word that is going to be searched
     * @return the most similar word with possible l33t replacements which can be the word itself
     *         return null if the word and all its replacements don't exist
     */
    public String getSimilarWord(String s) {
        String word = s.concat(String.valueOf(EOW)).toLowerCase();
        Vertex v = graph.getStart();
        StringBuilder sb = new StringBuilder();
        Edge e;
        for (Character c : word.toCharArray()) {
            char r;
            if ((e = v.goingEdge(c)) != null) {
                v = e.getEndVertex();
                r = c;
//                sb.append(c);
            } else if ((replacementMap.get(c) != null) && (e = v.goingEdge((r = replacementMap.get(c)))) != null) {
                v = e.getEndVertex();
//                sb.append(r);
            } else break;
            if (r != EOW)
                sb.append(r);
        }
        if (v == graph.getEnd())
            return sb.toString();
//        if ((e = v.goingEdge(EOW)) != null && e.getEndVertex() == graph.getEnd())
//            return sb.toString();
        return null;
    }

    private void buildDAWG(List<String> list) {
        for (String s : list) {
            addString(s);
        }
    }

    // @WARNING: total wrong codes
    // THINK TWICE BEFORE WRITING CODES !!!
    // waste three days on these, f***
    private void addString(String s) {
        int length = s.length();
        Vertex end = graph.getEnd();
        int i, j;
        Vertex tmpEnd = end;
        Edge tmpEdge = findSuffix(s.charAt(length - 1));
        if (tmpEdge != null) {
            tmpEnd = tmpEdge.getFromVertex();
            // find existed suffix with length longer than 2
            if (tmpEnd != graph.getStart())
                for (i = length - 2; i >= 0; i--) {
                    if ((tmpEdge = tmpEnd.comingEdge(s.charAt(i))) != null) {
                        if (i == 0) return; // the string is already existed
                        tmpEnd = tmpEdge.getFromVertex();
                    } else break;
                }
            else i = length - 1;
            if (i >= length - 2) {
                tmpEnd = end;
                i = length - 1;
            }
        }else i = length - 1;
        // find prefix
        Vertex tmpStart = graph.getStart();
        for (j = 0; j < i; j++) {
            if ((tmpEdge = tmpStart.goingEdge(s.charAt(j))) != null && tmpEdge.getEndVertex() != end)
                tmpStart = tmpEdge.getEndVertex();
            else break;
        }
        // add vertices and edges from tmpStart to tmpEnd with char at from j to i
        for (int k = j; k < i; k++) {
            Vertex v = new Vertex();
            Edge e = new Edge(tmpStart, v, s.charAt(k));
            graph.addVertex(v);
            graph.addEdge(e);
            tmpStart = v;
        }
        if (tmpEnd != end) {
            Edge e = new Edge(tmpStart, tmpEnd, s.charAt(i));
            graph.addEdge(e);
        }else if ((tmpEdge = tmpStart.goingEdge(s.charAt(j))) != null) {
            Vertex v = new Vertex();
            Edge e = new Edge(tmpEdge.getEndVertex(), end, EOW);
            graph.addVertex(v);
            graph.addEdge(e);
        } else {
            Vertex v = new Vertex();
            Edge e = new Edge(tmpStart, v, s.charAt(i));
            graph.addVertex(v);
            graph.addEdge(e);
            Edge e2 = new Edge(v, end, EOW);
            graph.addEdge(e2);
        }
    }

    private Edge findSuffix(char c) {
        Vertex end = graph.getEnd();
        Edge result;
        for (Edge edge: end.inEdge) {
            Vertex v = edge.getFromVertex();
            if (v != graph.getStart() && (result = v.comingEdge(c)) != null)
                return result;
        }
        return null;
    }

    public static void main(String[] args) {
        Map<Character, Character> l33tReplacement = new HashMap<>();
        l33tReplacement.put('3', 'e');
        l33tReplacement.put('4', 'a');
        l33tReplacement.put('@', 'a');
        l33tReplacement.put('$', 's');
        l33tReplacement.put('0', 'o');
        l33tReplacement.put('1', 'i');
        l33tReplacement.put('z', 's');
        List<String> list = new ArrayList<>();
        list.add("hello");
        list.add("world");
        list.add("a");
        list.add("aa");
        list.add("top");
        list.add("tap");
        list.add("taps");
        list.add("gap");
        list.add("prise");
        MyDAWG dawg = new MyDAWG(list);
        dawg.setReplacementMap(l33tReplacement);
        System.out.println(dawg.getSimilarWord("w0rld"));
        System.out.println(dawg.getSimilarWord("pr1se"));
        System.out.println(dawg.getSimilarWord("@"));
        System.out.println(dawg.getSimilarWord("a") == null);
        System.out.println(dawg.getSimilarWord("aa"));
    }
}
