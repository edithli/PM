package com.example.AppForTest.useless;

/**
 * Created by lss on 2016/3/28.
 */
public class Edge {
    private Vertex fromVertex, endVertex;
    private char content;

    public Edge(Vertex fromVertex, Vertex endVertex, char content) {
        setFromVertex(fromVertex);
        setEndVertex(endVertex);
        setContent(content);
    }

    public Vertex getFromVertex() {
        return fromVertex;
    }

    public void setFromVertex(Vertex fromVertex) {
        this.fromVertex = fromVertex;
    }

    public Vertex getEndVertex() {
        return endVertex;
    }

    public void setEndVertex(Vertex endVertex) {
        this.endVertex = endVertex;
    }

    public char getContent() {
        return content;
    }

    public void setContent(char content) {
        this.content = content;
    }
}
