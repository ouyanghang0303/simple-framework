package com.oyh.es;

import com.alibaba.fastjson.JSON;
import com.oyh.es.util.EsClientUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by hang.ouyang on 2017/8/19 15:58.
 */
@SpringBootTest(classes = SiteMain.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class IndexUtilTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    EsClientUtil esClientUtil;

    @Test
    public void indexExisis(){
        String indexName = "simple.index";
        boolean bool =  esClientUtil.isIndexExists(indexName);
        logger.info("index {} is {} ",indexName, bool ? "exists" : "not exists");
    }

    @Test
    public void batchCreateIndex(){
        esClientUtil.batchCreateIndex();
        List<String> indexList = esClientUtil.allIndex();
        logger.info("all index names :{}",JSON.toJSONString(indexList));
    }

}
