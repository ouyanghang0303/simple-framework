package com.oyh.es.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Created by hang.ouyang on 2017/8/19 15:49.
 */
@Component
public class EsClientUtil {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    TransportClient client;

    public boolean isIndexExists(String indexName) {
        if (StringUtils.isEmpty(indexName)) {
            logger.info("isIndexExists 索引名称为空");
            return false;
        }
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        //方法1
        IndicesExistsResponse response = indicesAdminClient.prepareExists(indexName).get();
        return response.isExists();
        //方法2
//        IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(indexName);
//        IndicesExistsResponse response = indicesAdminClient.exists(indicesExistsRequest).actionGet();
//        return response.isExists();
    }


    /**
     * 查询所有索引
     * @return
     */
    public List<String> allIndex(){
        IndicesAdminClient adminClient = client.admin().indices();
        try {
            String[] indices = adminClient.prepareGetIndex().execute().get().getIndices();
            return Arrays.asList(indices);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    /**
     * 创建空索引
     * @param index
     * @return
     */
    public boolean createSimpleIndex(String index){
        IndicesAdminClient adminClient = client.admin().indices();
        CreateIndexResponse response = adminClient.prepareCreate(index).get();
        return response.isAcknowledged();
    }

    /**
     * 创建索引
     * @param index 索引名称
     * @param type  类型名称
     * @param fieldJson 字段类型
     * @param shardNum 分片数
     * @param replicaNum 备份数
     * @return
     */
    public boolean createIndex(String index, String type, String fieldJson,Integer shardNum,Integer replicaNum){
        IndicesAdminClient adminClient = client.admin().indices();
        if (isIndexExists(index)) {
            return  false;
        }
        adminClient.prepareCreate(index)
                .setSettings(Settings.builder().put("index.number_of_shards", shardNum)
                                                .put("index.number_of_replicas", replicaNum))
                .addMapping(type, fieldJson)
                .get();
        return true;
    }

    /**
     * 读取配置创建索引
     */
    public void batchCreateIndex() {
        List<Index> indexList  = getIndexList();
        for(Index index : indexList){
            if (createIndex(index.getIndex(),index.getType(),index.getFieldJson(),index.getShardNum(),index.getReplicaNum())) {
                logger.info("index {} creation success",index.getIndex());
            }
        }
    }

    /**
     * 判断类型是否存在
     * @param index
     * @param type
     * @return
     */
    public boolean isTypeExists(String index, String type) {
        if(!isIndexExists(index)){
            logger.info("isTypeExists 索引 [{}] 不存在",index);
            return false;
        }
        IndicesAdminClient adminClient = client.admin().indices();
        TypesExistsResponse response = adminClient.prepareTypesExists(index).setTypes(type).get();
        return response.isExists();
    }


    /**
     * 删除索引
     * @param index
     * @return
     */
    public boolean deleteIndex(String index) {
        IndicesAdminClient adminClient = client.admin().indices();
        DeleteIndexResponse response = adminClient.prepareDelete(index).execute().actionGet();
        return response.isAcknowledged();
    }

    /**
     * 关闭索引
     * @param index
     * @return
     */
    public boolean closeIndex(String index){
        IndicesAdminClient adminClient = client.admin().indices();
        CloseIndexResponse response = adminClient.prepareClose(index).get();
        return response.isAcknowledged();
    }

    /**
     * 打开关闭的索引
     * @param index
     * @return
     */
    public boolean openIndex(String index){
        IndicesAdminClient adminClient = client.admin().indices();
        OpenIndexResponse response = adminClient.prepareOpen(index).get();
        return response.isAcknowledged();

    }

    private List<Index> getIndexList() {
        List<Index> result = new ArrayList<Index>();
        JSONArray jsonArray = JsonUtil.loadIndexFile();
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Index indexObject = new Index();
            String index = jsonObject.getString("index");
            String type = jsonObject.getString("type");
            Integer shardNum = jsonObject.getInteger("shardNum");
            Integer replicaNum = jsonObject.getInteger("replicaNum");
            String fieldRType = jsonObject.get("fieldType").toString();
            indexObject.setIndex(index);
            indexObject.setType(type);
            indexObject.setFieldJson(fieldRType);
            indexObject.setShardNum(shardNum);
            indexObject.setReplicaNum(replicaNum);
            result.add(indexObject);
        }
        return result;
    }

}
