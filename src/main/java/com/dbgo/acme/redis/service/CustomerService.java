package com.dbgo.acme.redis.service;

import com.dbgo.acme.redis.model.MQModel;


/**
 * 消费处理基类
 * 
 * @author lixiao
 * @date 2018年4月20日
 * @version V1.0
 */

public interface CustomerService {

	/**
	 * 根据生产者类型分发不同的业务实现
	 * 
	 * @param model
	 *            生产者 - 对象
	 */
	public boolean doProcess(MQModel model);

	/**
	 * 生产者类型
	 */
	public String getProducerType();
}
