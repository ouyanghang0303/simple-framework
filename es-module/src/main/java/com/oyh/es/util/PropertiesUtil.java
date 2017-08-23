package com.oyh.es.util;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by hang.ouyang on 2017/8/19 16:16.
 */
public class PropertiesUtil {
     static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    public static Properties loadFiles(String fileName){
        if(!fileName.endsWith("properties")){
            logger.error("file type is not support");
            return null;
        }
        Properties properties = new Properties();
        try( InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)){
            properties.load(new InputStreamReader(is,"UTF-8"));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        logger.info("load file {} with data: {}",fileName,JSON.toJSON(properties));
        return properties;
    }


    public static Properties loadEsProperties(){
        return loadFiles("elasticsearch.properties");
    }


    public static void main(String[] args) {
        logger.info(JSON.toJSONString(loadEsProperties()));
    }

}
