package com.oyh.es.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by hang.ouyang on 2017/8/19 16:36.
 */
public class JsonUtil {

    static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static JSONArray loadFile(String fileName){
        if(!fileName.endsWith(".json")){
            logger.error("file type is not support");
            return null;
        }
        try (InputStream stream = JsonUtil.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))){
            StringBuffer sb = new StringBuffer();
            String tmp = null;
            while((tmp = reader.readLine()) != null){
                sb.append(tmp);
            }
            JSONArray result = JSONArray.parseArray(sb.toString());
            logger.info("load file {} with data: {}",fileName, result.toJSONString());
            return result;
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    public static JSONArray loadIndexFile(){
        return loadFile("index.json");
    }

    public static void main(String[] args) {
        loadIndexFile();
    }

}
