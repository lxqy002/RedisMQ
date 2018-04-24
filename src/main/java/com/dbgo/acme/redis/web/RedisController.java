package com.dbgo.acme.redis.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.dbgo.acme.redis.helper.MQHelper;
import com.dbgo.acme.redis.model.MQModel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletRequest;


@RestController
@ResponseBody
@RequestMapping("redisMQ")
public class RedisController {


    @RequestMapping(path = {""}, method = {RequestMethod.GET})
    public String user(HttpServletRequest request) {
        CountDownLatch latch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            MQModel model = new MQModel();
            model.setId(i + "");
            if (i % 2 == 0) {
                model.setType("MAIL");
                Map<String, Object> entity = new HashMap<String, Object>();
                entity.put("email", "123@qq.com");
                model.setDataEntity(entity);
            } else {
                model.setType("LOG");
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MQHelper.createMQ(model);
                    latch.countDown();
                }
            }).start();
        }


        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "sucess";
    }

}
