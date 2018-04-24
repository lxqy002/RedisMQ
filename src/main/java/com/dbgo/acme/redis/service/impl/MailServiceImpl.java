package com.dbgo.acme.redis.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbgo.acme.redis.adapter.MailAdapter;
import com.dbgo.acme.redis.model.MQModel;
import com.dbgo.acme.redis.service.CustomerService;

@Service
public class MailServiceImpl implements CustomerService {
	private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

	@Autowired
	MailAdapter mailAdapter;

	@Override
	public boolean doProcess(MQModel model) {
		mailAdapter.sendMail((String) model.getDataEntity().get("email"));
		return true;
	}

	@Override
	public String getProducerType() {
		return "MAIL";
	}
}
