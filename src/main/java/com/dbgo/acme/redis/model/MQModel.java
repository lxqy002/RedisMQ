package com.dbgo.acme.redis.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 生产者 - 实体对象
 * 
 * @author lixiao
 * @date 2018年4月20日
 * @version V1.0
 */
public class MQModel {

	/** 生产者ID **/
	private String id;
	/** 生产者类型 **/
	private String type;
	/** 处理数据对象 **/
	Map<String, Object> dataEntity = new HashMap<String, Object>();

	public Map<String, Object> getDataEntity() {
		return dataEntity;
	}

	public void setDataEntity(Map<String, Object> dataEntity) {
		this.dataEntity = dataEntity;
	}

	public MQModel() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
