package com.tuxt.item.common;

import com.tuxt.item.util.Constants.CONFIG_NAME;
import com.tuxt.item.util.PropertiesUtil;

import redis.clients.jedis.JedisPoolConfig;

 

/**
 * 初始化 GenericObjectPoolConfig 
 * 对象 对应配置文件为 redis.properties
 */
public class PoolConfigFactory {
	private static  JedisPoolConfig poolConfig =new JedisPoolConfig ();
	
	static{
		//连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		poolConfig.setBlockWhenExhausted(Boolean.parseBoolean(PropertiesUtil.getString(CONFIG_NAME.REDIS,"BlockWhenExhausted")));
		//设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
		poolConfig.setEvictionPolicyClassName(PropertiesUtil.getString(CONFIG_NAME.REDIS,"EvictionPolicyClassName"));
		//是否启用pool的jmx管理功能, 默认true
		poolConfig.setJmxEnabled(Boolean.parseBoolean(PropertiesUtil.getString(CONFIG_NAME.REDIS,"JmxEnabled")));
		//MBean ObjectName = new ObjectName("org.apache.commons.pool2:type=GenericObjectPool,name=" + "pool" + i); 默 认为"pool", JMX不熟,具体不知道是干啥的...默认就好.
		poolConfig.setJmxNamePrefix(PropertiesUtil.getString(CONFIG_NAME.REDIS,"JmxNamePrefix"));
		//是否启用后进先出, 默认true
		poolConfig.setLifo(Boolean.parseBoolean(PropertiesUtil.getString(CONFIG_NAME.REDIS,"Lifo")));
		//最大空闲连接数, 默认8个
		poolConfig.setMaxIdle(Integer.parseInt(PropertiesUtil.getString(CONFIG_NAME.REDIS,"MaxIdle")));
		//最大连接数, 默认8个
		poolConfig.setMaxTotal(Integer.parseInt(PropertiesUtil.getString(CONFIG_NAME.REDIS,"MaxTotal")));
		//获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
		poolConfig.setMaxWaitMillis(Integer.parseInt(PropertiesUtil.getString(CONFIG_NAME.REDIS,"MaxWaitMillis")));
		//逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
		poolConfig.setMinEvictableIdleTimeMillis(Integer.parseInt(PropertiesUtil.getString(CONFIG_NAME.REDIS,"MinEvictableIdleTimeMillis")));
		//最小空闲连接数, 默认0
		poolConfig.setMinIdle(Integer.parseInt(PropertiesUtil.getString(CONFIG_NAME.REDIS,"MinIdle")));
		//每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
		poolConfig.setNumTestsPerEvictionRun(Integer.parseInt(PropertiesUtil.getString(CONFIG_NAME.REDIS,"NumTestsPerEvictionRun")));
		//对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)   
		poolConfig.setSoftMinEvictableIdleTimeMillis(Integer.parseInt(PropertiesUtil.getString(CONFIG_NAME.REDIS,"SoftMinEvictableIdleTimeMillis")));
		//在获取连接的时候检查有效性, 默认false
		poolConfig.setTestOnBorrow(Boolean.parseBoolean(PropertiesUtil.getString(CONFIG_NAME.REDIS,"TestOnBorrow")));
		//在空闲时检查有效性, 默认false
		poolConfig.setTestWhileIdle(Boolean.parseBoolean(PropertiesUtil.getString(CONFIG_NAME.REDIS,"TestWhileIdle")));
		//逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
		poolConfig.setTimeBetweenEvictionRunsMillis(Integer.parseInt(PropertiesUtil.getString(CONFIG_NAME.REDIS,"TimeBetweenEvictionRunsMillis")));
	}
	public static JedisPoolConfig getPoolConfig(){
		return poolConfig;
	}
}
