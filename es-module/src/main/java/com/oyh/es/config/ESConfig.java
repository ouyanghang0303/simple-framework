package com.oyh.es.config;

import com.oyh.es.util.PropertiesUtil;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by hang.ouyang on 2017/8/19 15:44.
 */
@Configuration
public class ESConfig {

    @Bean
    public TransportClient transportClient() throws UnknownHostException {
        //读取配置信息
        Properties properties = PropertiesUtil.loadEsProperties();
        String clusterName = properties.getProperty("clusterName");
        String[] intelAddresses = properties.getProperty("hosts").split(",");

        //构建ES配置
        Settings settings = Settings.builder()
                                    .put("cluster.name",clusterName)
                                    .put("client.transport.sniff",true)
                                    //.put("client.transport.ping_timeout",10)
                                    .build();
        //初始化client
        TransportClient client = new PreBuiltTransportClient(settings);

        //添加集权机器
        for (int i = 0; i < intelAddresses.length; i++) {
            String[] tmpArray = intelAddresses[i].split(":");
            String ip = tmpArray[0];
            int port = Integer.valueOf(tmpArray[1]);
            client = ((TransportClient)client).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), port));
        }
        return client;
    }

}
