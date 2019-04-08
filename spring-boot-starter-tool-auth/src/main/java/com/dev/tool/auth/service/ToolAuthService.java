package com.dev.tool.auth.service;

import com.dev.tool.common.model.Event;

public class ToolAuthService {


    private String target;
    private String method;


    public void initialize(String target,String method){
        this.target = target;
        this.method = method;
    }

    public boolean canAccess(String token, Event event){
        return true;
    }




}
