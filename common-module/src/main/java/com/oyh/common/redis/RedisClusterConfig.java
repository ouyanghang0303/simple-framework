package com.oyh.common.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisCluster;

/**
 * Created by hang.ouyang on 2017/8/28 11:50.
 */
@Configuration
@ConditionalOnClass({JedisConnectionFactory.class,JedisCluster.class, RedisTemplate.class})
@EnableConfigurationProperties(RedisProperties.class)
public class RedisClusterConfig {

    @Autowired
    RedisProperties redisProperties;

    @Bean
    RedisClusterConfiguration redisClusterConfiguration(){
        RedisClusterConfiguration redisClusterConfiguration =  new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
        redisClusterConfiguration.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());
        return redisClusterConfiguration;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory jedisConnectionFactory =  new JedisConnectionFactory(redisClusterConfiguration());
        return jedisConnectionFactory;
    }

    @Bean
    public JedisCluster jedisCluster(){
        Object conn = jedisConnectionFactory().getConnection().getNativeConnection();
        if(conn instanceof JedisCluster) {
            return (JedisCluster) conn;
        }
        return null;
    }

    @Bean
    public RedisTemplate redisTemplate(){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

}
