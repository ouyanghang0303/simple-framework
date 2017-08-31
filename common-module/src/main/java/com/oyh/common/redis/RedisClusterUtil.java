package com.oyh.common.redis;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by hang.ouyang on 2017/8/28 15:40.
 */
@Component
public class RedisClusterUtil {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    JedisCluster jedisCluster;

    /**
     * 存储对象到缓存中，并制定过期时间和当Key存在时是否覆盖。
     * @param key
     * @param object
     * @param nxxx   值只能取NX或者XX，如果取NX，则只有当key不存在是才进行set，如果取XX，则只有当key已经存在时才进行set
     * @param expx   expx的值只能取EX或者PX，代表数据过期时间的单位，EX代表秒，PX代表毫秒。
     * @param time   过期时间，单位是expx所代表的单位。
     * @return
     */
    public String setObject(String key, Object object, String nxxx, String expx, long time) {
        String value = JSON.toJSONString(object);
        return jedisCluster.set(key, value, nxxx, expx, time);
    }

    /**
     * 存储字符串到缓存中，并制定过期时间和当Key存在时是否覆盖。
     * @param key
     * @param value
     * @param nxxx   值只能取NX或者XX，如果取NX，则只有当key不存在是才进行set，如果取XX，则只有当key已经存在时才进行set
     * @param expx   expx的值只能取EX或者PX，代表数据过期时间的单位，EX代表秒，PX代表毫秒。
     * @param time   过期时间，单位是expx所代表的单位。
     * @return
     */
    public String setString(String key, String value, String nxxx, String expx, long time) {
        return jedisCluster.set(key, value, nxxx, expx, time);
    }


    public String setObject(String key, Object object) {
        String value = JSON.toJSONString(object);
        return this.setString(key, value);
    }

    public Long setObjectAndExpire(String key, Object object, int seconds) {
        setObject(key, object);
        return expire(key, seconds);
    }

    public Long setObjectAndExpireAt(String key, Object object, long unixTime) {
        setObject(key, object);
        return expireAt(key, unixTime);
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = getString(key);
        T ret = JSON.parseObject(value, clazz);
        return ret;
    }

    public String getString(String key) {
        return jedisCluster.get(key);
    }

    public long del(String key) {
        return jedisCluster.del(key);
    }

    public String setString(String key, String value) {
        return jedisCluster.set(key, value);
    }

    public <T> List<T> getArray(String key, Class<T> clazz) {
        String value = getString(key);
        List<T> ret = JSON.parseArray(value, clazz);
        return ret;
    }
    /**
     * 为key设置一个特定的过期时间，单位为秒。过期时间一到，redis将会从缓存中删除掉该key。
     * 即使是有过期时间的key，redis也会在持久化时将其写到硬盘中，并把相对过期时间改为绝对的Unix过期时间。
     * 在一个有设置过期时间的key上重复设置过期时间将会覆盖原先设置的过期时间。
     * @param key
     * @param seconds
     * @return 返回1表示成功设置过期时间，返回0表示key不存在。
     */
    public Long expire(String key, int seconds) {
        return jedisCluster.expire(key, seconds);
    }

    /**
     * 与expire不一样，expireAt设置的时间不是能存活多久，而是固定的UNIX时间（从1970年开始算起），单位为秒。
     * @param key
     * @param unixTime
     * @return
     */
    public Long expireAt(String key, long unixTime) {
        return jedisCluster.expireAt(key, unixTime);
    }

    /**
     * 返回一个key还能活多久，单位为秒
     * @param key
     * @return 如果该key本来并没有设置过期时间，则返回-1，如果该key不存在，则返回-2
     */
    public long ttl(String key) {
        return jedisCluster.ttl(key);
    }

    public boolean exists(String key) {
        return jedisCluster.exists(key);
    }

    public long incr(String key) {
        return jedisCluster.incr(key);
    }

    /**
     * 加锁
     *
     * @param keyName 锁的标识名
     * @param timeoutMs 循环获取锁的等待超时时间(毫秒)，在此时间内会一直尝试获取锁直到超时，为0表示失败后直接返回不等待
     * @param expireMs 当前锁的最大生存时间(毫秒)，必须大于0，如果超过生存时间锁仍未被释放，则系统会自动强制释放
     * @param waitIntervalMs 获取锁失败后挂起再试的时间间隔(毫秒)
     * @return [type] [description]
     */
    public long loopLock(String keyName, long timeoutMs, long expireMs, long waitIntervalMs) {
        if (keyName == null || keyName.trim().length() == 0) {
            return 0;
        }

        logger.info("keyname:【{}】,timeout【{}ms】,expire【{}ms】,waitInterval【{}ms】",
                keyName,
                timeoutMs,
                expireMs,
                waitIntervalMs);

        long timeoutAt = System.currentTimeMillis();
        if (timeoutMs > 0) {
            timeoutAt = timeoutAt + timeoutMs;
        }

        long times = 0;
        while (true) {
            long now = System.currentTimeMillis();
            long expireAt = now + expireMs;

            String statusCode = setObject(keyName, String.valueOf(expireAt), "NX", "PX", expireMs);
            if ("OK".equals(statusCode)) {
                logger.info("keyName:【{}】success.", keyName);
                return expireAt;
            }

            if (timeoutMs <= 0 || timeoutAt < now) {
                logger.info("keyName:【{}】fail.", keyName);
                return 0;
            }

            try {
                Thread.sleep(waitIntervalMs);
            } catch (Exception e) {

            }
            logger.info("keyName:【{}】,times of:【{}】.", keyName, ++times);
        }
    }

    /**
     * 释放锁(安全起见,增加一个3毫秒)
     * @param name
     * @param expireAt
     */
    public void releaseLock(String name, long expireAt) {
        if (expireAt - System.currentTimeMillis() > 3) {
            del(name);
        }
    }


}
