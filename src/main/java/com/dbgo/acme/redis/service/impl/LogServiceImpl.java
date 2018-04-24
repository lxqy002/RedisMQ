package com.dbgo.acme.redis.service.impl;

import com.dbgo.acme.redis.util.StackTraceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dbgo.acme.redis.model.MQModel;
import com.dbgo.acme.redis.service.CustomerService;


@Service
public class LogServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    @Override
    public boolean doProcess(MQModel model) {
        try {
            if(model.getId().equals("1")){

            }
            Thread.sleep(5000);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }



    @Override
    public String getProducerType() {
        return "LOG";
    }

}


