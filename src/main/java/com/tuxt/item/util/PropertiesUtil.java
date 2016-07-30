package com.tuxt.item.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * 功能：PropertiesUtil 读取propery属性 规则：propery 随时都有可能变更的方在这个里面
 */
public class PropertiesUtil {
	private static final String encoding = "UTF-8";
	private static Map<String,PropertiesConfiguration> propertiesMap=new HashMap<String,PropertiesConfiguration>();

	 
	public static PropertiesConfiguration getConfig(String configName) {
		PropertiesConfiguration	config =  propertiesMap.get(configName);
		try {
			if (null == config) {
				config = new PropertiesConfiguration("./config/"+configName+".properties");
				config.setReloadingStrategy(new FileChangedReloadingStrategy());
				config.setEncoding(encoding);
				propertiesMap.put(configName, config);
			}
		} catch (ConfigurationException e) {
		 System.out.println("----------------");
		}
		return config;
	}

	public static String getString(String configName,String key) {
		String str = "";
		try {
			str = getConfig(configName).getString(key);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return str;
	}

	public static int getInt(String configName,String key) throws Exception {
		String str =  getConfig(configName).getString(key);
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			throw new Exception("key = " + key + "取到的值不是整形！", e);
		}
	}

	public static Iterator<String> getKeys(String configName,String prefix) throws Exception {
		return getConfig(configName).getKeys(prefix);
	}
}