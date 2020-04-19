package com.dev.tool.cache.redis.model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * redis运行信息
 * https://redis.io/commands/info
 */
public class RedisInfo {

    private String server;
    private String clients;
    private String memory;
    private String persistence;
    private String stats;
    private String replication;
    private String cpu;
    private String cluster;
    //# Keyspace
    //db0:keys=32888,expires=0,avg_ttl=0
    //db31:keys=1,expires=0,avg_ttl=0
    private String keyspace;
    private List<String> dbList;


    public RedisInfo() {
    }

    public RedisInfo(Properties properties) {
        this.server = properties.getProperty("server");
        this.clients = properties.getProperty("clients");
        this.memory = properties.getProperty("memory");
        this.persistence = properties.getProperty("persistence");
        this.stats = properties.getProperty("stats");
        this.replication = properties.getProperty("replication");
        this.cpu = properties.getProperty("cpu");
        this.cluster = properties.getProperty("cluster");
        this.keyspace = properties.getProperty("keyspace");
        //解析keyspace
        this.dbList = new ArrayList<>();
        Enumeration enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()){
            String key = (String)enumeration.nextElement();
            if(key.startsWith("db")){
                dbList.add(key);
            }
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getClients() {
        return clients;
    }

    public void setClients(String clients) {
        this.clients = clients;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getPersistence() {
        return persistence;
    }

    public void setPersistence(String persistence) {
        this.persistence = persistence;
    }

    public String getStats() {
        return stats;
    }

    public void setStats(String stats) {
        this.stats = stats;
    }

    public String getReplication() {
        return replication;
    }

    public void setReplication(String replication) {
        this.replication = replication;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public List<String> getDbList() {
        return dbList;
    }

    public void setDbList(List<String> dbList) {
        this.dbList = dbList;
    }
}
