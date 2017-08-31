package com.oyh.test;

import com.oyh.common.SiteMain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;


/**
 * Created by hang.ouyang on 2017/8/23 16:49.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SiteMain.class)
public class RedisTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    JedisCluster jedisCluster;


    @Test
    public void test() throws Exception {
        String key = "redisTestKey";
        String value = "I am test value";

        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

        //数据插入测试：
        opsForValue.set(key, value);
        String valueFromRedis = opsForValue.get(key);
        logger.info("redis value after set: {}", valueFromRedis);

        //数据删除测试：
        redisTemplate.delete(key);
        valueFromRedis = opsForValue.get(key);
        logger.info("redis value after delete: {}", valueFromRedis);
    }


    @Test
    public void testJedisCluster(){
        String key = "redisTestKey";
        String value = "I am test value";
        String result = jedisCluster.set(key,value);
        logger.info("redis value after set: {}", result);
        logger.info("redis value after set: {}", jedisCluster.get(key));
        jedisCluster.del(key);
        logger.info("redis value after delete: {}", jedisCluster.get(key));
    }


}
