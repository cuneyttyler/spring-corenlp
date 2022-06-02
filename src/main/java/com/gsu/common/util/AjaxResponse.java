package com.gsu.common.util;

public class AjaxResponse {

    private String message;
    private Object data;

    public AjaxResponse(String message) {
        this.message = message;
    }

    public AjaxResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
