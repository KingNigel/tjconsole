package com.gruszecm.tjconsole;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;


public class TJContext { 
	private MBeanServerConnection serverConnection;
	private ObjectName objectName;
	private final Map<String,Object> environment;
	
	public TJContext() {
		environment = new LinkedHashMap<String, Object>();
		environment.put("SSL", Boolean.FALSE);
		environment.put("USERNAME", "");
		environment.put("PASSWORD", "");
	}
	
	public Map<String, Object> getEnvironment() {
		return Collections.unmodifiableMap(environment);
	}
	
	public void setEnvironmentVariable(String key, Object value) {
		if (! environment.containsKey(key)) throw new IllegalArgumentException("Invalid key - " + key);
		Object old = environment.get(key);
		if (! old.getClass().equals(value.getClass())){
			throw new IllegalArgumentException("Invalid value type - " + value.getClass().getName() + " should be " + old.getClass().getName());
		}
		environment.put(key, value);
	}
	
	public MBeanServerConnection getServer() {
		return serverConnection;
	}
	public void setServer(MBeanServerConnection serverConnection) {
		this.serverConnection = serverConnection;
	}
	public ObjectName getObjectName() {
		return objectName;
	}
	public void setObjectName(ObjectName objectName) {
		this.objectName = objectName;
	}
	
	public boolean isConnected() {
		return serverConnection != null;
	}
	
	public List<MBeanAttributeInfo> getAttributes() throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
		if (serverConnection == null || objectName == null) return Collections.emptyList();
		MBeanInfo beanInfo = serverConnection.getMBeanInfo(objectName);
		return Arrays.asList(beanInfo.getAttributes());
	}
}
