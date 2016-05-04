package com.example.AppForTest.useless;

import com.example.AppForTest.useless.Edge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lss on 2016/3/28.
 */
public class Vertex{
    private int freq;
    public List<Edge> inEdge, outEdge;
//    public Hashtable<T, Edge> inEdges, outEdges;

    public Vertex() {
        inEdge = new ArrayList<>();
        outEdge = new ArrayList<>();
//        inEdges = new Hashtable<>();
//        outEdges = new Hashtable<>();
    }

    public void insertInEdge(Edge edge) {
        inEdge.add(edge);
    }

    public void insertOutEdge(Edge edge) {
        outEdge.add(edge);
    }

    public Edge comingEdge(char c) {
        for (Edge e : inEdge) {
            if (e.getContent() == c)
                return e;
        }
        return null;
    }

    public Edge goingEdge(char c) {
        for (Edge e: outEdge)
            if (e.getContent() == c)
                return e;
        return null;
    }
}
