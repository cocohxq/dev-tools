package com.dev.tool.common.util;

import com.dev.tool.common.model.Result;

public class ResultUtils {

    public static final int SUCCESS = 0;
    public static final int ERROR = -1;

    public static Result successResult() {
        return generateResult(null, SUCCESS, null);
    }

    public static Result successResult(Object data) {
        return generateResult(data, SUCCESS, null);
    }

    public static Result errorResult(String message) {
        return generateResult(null, ERROR, message);
    }

    public static Result errorResult(String message,Exception e) {
        return generateResult(null, ERROR, message+","+e.toString());
    }


    public static Result generateResult(Object data, int code, String message) {
        Result result = new Result();
        result.setCode(code);
        result.setData(data);
        result.setMessage(message);
        return result;
    }

}
