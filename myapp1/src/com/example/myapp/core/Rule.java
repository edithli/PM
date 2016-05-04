package com.example.myapp.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lss on 2016/3/23.
 */
public class Rule {
    String lhs, rhs;
    private double prob;
    private List<Rule> rhsParseRules = null;

    public Rule(String lhs, String rhs, double pr) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.prob = pr;
    }

    public Rule(String lhs, String rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public double getProb() {
        return prob;
    }

    public void setExtras(List<Rule> list) {
        rhsParseRules = list;
    }

    public List<Rule> getExtras() {
        return rhsParseRules;
    }

    public void addExtra(String lhs, String rhs) {
        addExtra(new Rule(lhs, rhs));
    }

    public void addExtra(Rule rule) {
        if (rhsParseRules == null)
            rhsParseRules = new ArrayList<>();
        rhsParseRules.add(rule);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lhs + " -> " + rhs + " : " + prob);
        if (rhsParseRules != null && !rhsParseRules.isEmpty()){
            sb.append("\nextras: \n");
            for (Rule r: rhsParseRules)
                sb.append("\t" + r.toString() + "\n");
        }
        return sb.toString();
    }

}
