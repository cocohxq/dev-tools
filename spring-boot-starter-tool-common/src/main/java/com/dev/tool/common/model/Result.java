package com.dev.tool.common.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashMap;
import java.util.Map;

public class Result {

    private int code;
    private String message;
    private Object data;

    public boolean isSuccess(){
        return code == -1 ? false : true;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toJSON(){
        return JSONObject.toJSONString(this, SerializerFeature.WriteMapNullValue);
    }
}
