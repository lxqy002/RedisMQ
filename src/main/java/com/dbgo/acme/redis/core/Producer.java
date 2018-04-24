package com.dbgo.acme.redis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dbgo.acme.redis.adapter.JedisAdapter;
import com.dbgo.acme.redis.model.MQModel;
import com.dbgo.acme.redis.util.Constant;
import com.dbgo.acme.redis.util.StackTraceUtil;

/**
 * 消息生产者
 *
 * @author lxiao
 * @date 2018年4月20日
 */
@Service
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 创建消息队列
     *
     * @param model
     * @return
     */

    public boolean createMessageQueue(MQModel model) {
        try {
            // 序列化
            String modelJson = JSONObject.toJSONString(model);
            // 放入工作队列
            jedisAdapter.lpush(Constant.SOURCE_KEY, modelJson);
            logger.info("消息主键{},消息类型{}已放入队列", model.getId(), model.getType());
            return true;
        } catch (Exception e) {
            logger.error("Producer.createMessageQueue异常 ：" + StackTraceUtil.getStackTrace(e));
            return false;
        }

    }
}
