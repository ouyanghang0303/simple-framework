package com.oyh.test;

import com.oyh.common.SiteMain;
import com.oyh.common.model.User;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by hang.ouyang on 2017/8/23 16:49.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SiteMain.class)
public class RedisTest {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, User> userRedisTemplate;

    @Test
    public void test() throws Exception {
        // 保存字符串
        stringRedisTemplate.opsForValue().set("aaa", "111");
        Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
    }

    public void testObjectSerial(){
        // 保存对象
        User user = new User("超人", 20);
        userRedisTemplate.opsForValue().set(user.getUsername(), user);
        user = new User("蝙蝠侠", 30);
        userRedisTemplate.opsForValue().set(user.getUsername(), user);
        user = new User("蜘蛛侠", 40);
        userRedisTemplate.opsForValue().set(user.getUsername(), user);
        Assert.assertEquals(20, userRedisTemplate.opsForValue().get("超人").getAge().longValue());
        Assert.assertEquals(30, userRedisTemplate.opsForValue().get("蝙蝠侠").getAge().longValue());
        Assert.assertEquals(40, userRedisTemplate.opsForValue().get("蜘蛛侠").getAge().longValue());
    }

}
