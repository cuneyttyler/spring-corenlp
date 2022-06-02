package com.gsu.knowledgebase.model;

import java.util.HashMap;

/**
 * Created by cnytync on 01/03/2018.
 */
public class UnexpectedError {
    private String url;
    private HashMap<String, Object> data;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
