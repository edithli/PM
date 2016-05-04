package com.example.AppForTest.useless;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lss on 2016/3/28.
 */
public class DirectedGraph<T> {
    private Vertex start, end;
    private List<Vertex> vertices;
    private List<Edge> edges;

    public DirectedGraph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        start = new Vertex();
        end = new Vertex();
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
        Vertex from = edge.getFromVertex();
        Vertex to = edge.getEndVertex();
        from.insertOutEdge(edge);
        to.insertInEdge(edge);
    }

    public Vertex getStart() {
        return start;
    }

    public Vertex getEnd() {
        return end;
    }

    public void printGraph(String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
            StringBuilder sb = new StringBuilder();
            sb.append("digraph g {\n");
            for (Edge e : edges) {
                sb.append(e.getFromVertex().hashCode() + " -> " + e.getEndVertex().hashCode()
                        + "[label=\"" + e.getContent() + "\"]\n");
            }
            sb.append("}");
            bw.write(sb.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
