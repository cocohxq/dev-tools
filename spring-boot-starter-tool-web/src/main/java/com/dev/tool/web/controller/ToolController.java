package com.dev.tool.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dev.tool.auth.service.ToolAuthService;
import com.dev.tool.common.model.Event;
import com.dev.tool.common.model.Result;
import com.dev.tool.common.model.ToolGroup;
import com.dev.tool.common.util.*;
import com.dev.tool.web.service.ToolService;
import com.dev.tool.web.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工具主页控制器
 */
@Controller
@RequestMapping("/")
public class ToolController {

    private final String COOKIE_SUFFIX = "_config";
    private Logger logger = LoggerFactory.getLogger(ToolController.class);
    @Autowired(required = false)
    private ToolAuthService toolAuthService;

    @Autowired
    private ToolService toolService;

    //step1 进入选择页
    @GetMapping("/")
    public String init(Model model) {
        List<ToolGroup> toolGroupList = toolService.getGroupList();
        model.addAttribute("groupList",toolGroupList);
        List<String> tools = new ArrayList<>();
        toolGroupList.stream().forEach(l ->{
            l.getToolList().stream().forEach(k ->{
                tools.add(k.getGroupToolEnum().getName());
                if(k.getGroupToolEnum().equals(GroupToolEnum.DUBBO) && CacheUtils.getDubboJarInfoHashMap().size() > 0){
                    model.addAttribute("jarInfos", new ArrayList<>(CacheUtils.getDubboJarInfoHashMap().keySet()));
                }
            });
        });
        model.addAttribute("tools",","+tools.stream().collect(Collectors.joining(","))+",");
        return "index";
    }

    @PostMapping("/execute")
    @ResponseBody
    public String doExecute(@RequestBody Map<String, String> param, HttpServletResponse response, HttpServletRequest request) {
        Event event = null;
        try {
            event = checkAndInitEvent(param);
        } catch (Exception e) {
            logger.error("请求转换event异常", e);
            return ResultUtils.errorResult("请求转换event异常", e).toJSON();
        }
        String token = null;
        //权限服务不为空需要校验权限
        if (null != toolAuthService && !toolAuthService.canAccess(token, event)) {
            return ResultUtils.errorResult("该用户没有此权限").toJSON();
        }
        try {
            if ("true".equals(param.get("addCookie"))) {
                response.addCookie(CookieUtil.newCookie(event.getGroupToolEnum().getName() + COOKIE_SUFFIX, URLEncoder.encode(JSONObject.toJSONString(param), "utf-8"), request.getServerName(), 30 * 24 * 60 * 60 * 1000, "/"));
            }
        } catch (Exception e) {
            logger.error("记录cookie异常", e);
        }

        Result ret = toolService.execute(event);

        if(null != event.getEventData().get("resultType") && event.getEventData().get("resultType").equals("formatJson")){
            return ret.getCode()+"&&"+ret.getMessage()+"&&"+JSONObject.toJSONString(ret.getData(), SerializerFeature.WriteNullListAsEmpty,SerializerFeature.WriteMapNullValue,SerializerFeature.PrettyFormat);
        }else{
            return ret.toJSON();
        }
    }

    private Event checkAndInitEvent(Map<String, String> param) throws Exception {
        if (null == param || null == param.get("toolName") || null == param.get("eventName")) {
            throw new IllegalArgumentException("必要的请求参数为空,param:" + (null == param ? "" : param.toString()));
        }

        Event event = new Event();
        event.setEventEnum(Enum.valueOf(EventEnum.class, param.get("eventName").toUpperCase()));
        event.setGroupToolEnum(Enum.valueOf(GroupToolEnum.class, param.get("toolName").toUpperCase()));
        event.setEventSource(param.get("eventSource"));
        event.setEventData(param);
        return event;
    }

}
