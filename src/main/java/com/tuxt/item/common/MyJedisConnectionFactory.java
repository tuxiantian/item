package com.tuxt.item.common;

import java.util.Arrays;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import com.tuxt.item.util.Constants.CONFIG_NAME;
import com.tuxt.item.util.PropertiesUtil;

import redis.clients.jedis.JedisPoolConfig;

public class MyJedisConnectionFactory {

	public static JedisConnectionFactory jedisConnectionFactory() {
        String[] jedisClusterNodes = PropertiesUtil.getString(CONFIG_NAME.REDIS,"jedisClusterNodes").split(",");
        RedisClusterConfiguration clusterConfig=new RedisClusterConfiguration(Arrays.asList(jedisClusterNodes));
        clusterConfig.setMaxRedirects(Integer.valueOf( PropertiesUtil.getString(CONFIG_NAME.REDIS,"MaxRedirections")));
        JedisPoolConfig poolConfig= PoolConfigFactory.getPoolConfig();
        return new JedisConnectionFactory(clusterConfig,poolConfig);
    }
}
