package com.oyh.es;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 * Created by hang.ouyang on 2017/8/19 15:04.
 */
public class EsClientTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    TransportClient client;

    String indexName = "simple.index";

    @Before
    public void before(){
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                         .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void addIndex(){
        IndicesAdminClient adminClient = client.admin().indices();
        if(!adminClient.prepareExists(indexName).get().isExists()){
            CreateIndexResponse response = adminClient.prepareCreate(indexName).get();
            logger.info(response.toString());
        }else {
            logger.info("index {} already exists",indexName);
            try {
                GetIndexResponse response = adminClient.prepareGetIndex().execute().get();
                logger.info(JSON.toJSONString(response),true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }


}
