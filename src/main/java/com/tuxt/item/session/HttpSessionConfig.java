package com.tuxt.item.session;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import redis.clients.jedis.JedisPoolConfig;

import com.tuxt.item.redis.PoolConfigFactory;
import com.tuxt.item.util.Constants;
import com.tuxt.item.util.Constants.CONFIG_NAME;
import com.tuxt.item.util.PropertiesUtil;




@EnableRedisHttpSession(maxInactiveIntervalInSeconds=Constants.SESSION_TIMEOUT)
public class HttpSessionConfig {
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        String[] jedisClusterNodes = PropertiesUtil.getString(CONFIG_NAME.REDIS,"jedisClusterNodes").split(",");
        RedisClusterConfiguration clusterConfig=new RedisClusterConfiguration(Arrays.asList(jedisClusterNodes));
        clusterConfig.setMaxRedirects(Integer.valueOf( PropertiesUtil.getString(CONFIG_NAME.REDIS,"MaxRedirections")));
        JedisPoolConfig poolConfig= PoolConfigFactory.getPoolConfig();
        return new JedisConnectionFactory(clusterConfig,poolConfig);
    }
}
