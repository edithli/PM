package com.example.myapp.logic;

/**
 * Created by lss on 2016/3/7.
 */
public class Content {
    private String key;
    private String value;

    Content(String key, String value) {
        this.key = key;
        this.value = value;
    }

    Content(String content) {
        this.key = content;
        this.value = null;
    }

    public String getKey(){
        return key;
    }

    public String getValue(){
        return value;
    }

    void deleteValue() {
        this.value = "";
    }

    void setValue(String v) {
        this.value = v;
    }
}
