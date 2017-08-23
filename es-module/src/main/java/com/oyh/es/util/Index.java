package com.oyh.es.util;

/**
 * Index对象类
 * Created by hang.ouyang on 2017/8/19 16:12.
 */
public class Index {

    private String index;                   //索引名
    private String type;                    //type表名
    private Integer shardNum;       //分片数
    private Integer replicaNum;     //备份数
    private String fieldJson;               //字段类型

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getShardNum() {
        return shardNum;
    }

    public void setShardNum(Integer shardNum) {
        this.shardNum = shardNum;
    }

    public Integer getReplicaNum() {
        return replicaNum;
    }

    public void setReplicaNum(Integer replicaNum) {
        this.replicaNum = replicaNum;
    }

    public String getFieldJson() {
        return fieldJson;
    }

    public void setFieldJson(String fieldJson) {
        this.fieldJson = fieldJson;
    }
}
