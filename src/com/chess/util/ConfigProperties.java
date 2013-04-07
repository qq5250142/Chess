package com.chess.util;

import java.io.IOException;
import java.util.Properties;

/*
 *配置类
 *@author benlin
 *@created 2010-5-19
 *返回为字符串
 *Sample:
 *ConfigProperties.getInstance().appSettings.getProperty("DBAcessServer.host")
 *@modify 2010-6-12
 *增加getConfigProperty()方法
 *Sample：
 *ConfigProperties.getConfigProperty("genius.spiritpower.dodge");
 * */
public class ConfigProperties {
	
	private static ConfigProperties instance;
	
	private Properties appSettings;
	public void setAppSettings(Properties appSettings) {
		this.appSettings = appSettings;
	}

	public Properties getAppSettings() {
		return appSettings;
	}
	
	public static String getConfigProperty(String key){
		return ConfigProperties.getInstance().appSettings.getProperty(key);
	}
	
	synchronized public static ConfigProperties getInstance()
	{
		if(instance==null)
		{
			instance=new ConfigProperties();
		}
		return instance;
	}
	
	public ConfigProperties()
	{
		if(this.appSettings==null)
		{
			//是否保存在缓存中
			//如果没有缓存
			this.appSettings = new Properties();
			try {
				this.appSettings.load(getClass().getResourceAsStream("/config.properties"));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
