package com.dev.tool.common.model;

import com.dev.tool.common.util.EventEnum;
import com.dev.tool.common.util.GroupToolEnum;

import java.util.Map;

public class Event {

    private GroupToolEnum groupToolEnum;
    private EventEnum eventEnum;
    private String eventSource;
    private Map<String,String> eventData;

    public GroupToolEnum getGroupToolEnum() {
        return groupToolEnum;
    }

    public void setGroupToolEnum(GroupToolEnum groupToolEnum) {
        this.groupToolEnum = groupToolEnum;
    }

    public EventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(EventEnum eventEnum) {
        this.eventEnum = eventEnum;
    }

    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    public Map<String, String> getEventData() {
        return eventData;
    }

    public void setEventData(Map<String, String> eventData) {
        this.eventData = eventData;
    }
}
