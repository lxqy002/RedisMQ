package com.dbgo.acme.redis.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.dbgo.acme.redis.adapter.JedisAdapter;
import com.dbgo.acme.redis.model.MQModel;
import com.dbgo.acme.redis.service.CustomerService;
import com.dbgo.acme.redis.util.Constant;
import com.dbgo.acme.redis.util.StackTraceUtil;

/**
 * 消息消费者
 *
 * @author lixiao
 * @version V1.0
 * @date 2018年4月20日
 */

@Service
public class Customer implements ApplicationContextAware {

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    Producer producer;

    private ApplicationContext applicationContext;

    // 定长线程池，支持定时及周期性任务执行
    ScheduledExecutorService daemonThreadPool = Executors.newScheduledThreadPool(1);

    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

    // 消息类型与消息处理的Handler
    private Map<String, CustomerService> config = new HashMap<String, CustomerService>();

    private static final Logger logger = LoggerFactory.getLogger(Customer.class);

    @PostConstruct
    public void initHandler() {
        // 获取所有实现CustomerService接口的子类
        Map<String, CustomerService> customerServiceMap = applicationContext.getBeansOfType(CustomerService.class);
        if (customerServiceMap.size() > 0) {
            // 初始化消费者类型线程池
            fixedThreadPool = Executors.newFixedThreadPool(customerServiceMap.size());

            for (Map.Entry<String, CustomerService> entry : customerServiceMap.entrySet()) {
                // 生产者类型 与 消费子类对应
                CustomerService customerService = entry.getValue();
                String producerType = customerService.getProducerType();
                if (!StringUtils.isEmpty(producerType)) {
                    config.put(producerType, customerService);
                }
            }
        }

        // 开启消费线程
        Thread customerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // 取出生产者实体对象 - 阻塞队列
                    String dataEntity = jedisAdapter.brpoplpush(Constant.SOURCE_KEY, Constant.DESTINATION_KEY);
                    // 非阻塞
                    //String dataEntity = jedisAdapter.rpoplpush(Constant.SOURCE_KEY, Constant.DESTINATION_KEY);
                    if (StringUtils.isEmpty(dataEntity)) {
                        continue;
                    }
                    MQModel model = JSON.parseObject(dataEntity, MQModel.class);
                    if (!config.containsKey(model.getType())) {
                        logger.error("{}-生产者类型未被定义", model.getType());
                        continue;
                    }
                    // 消费者类型线程池
                    process(model, dataEntity);
                }
            }
        });
        logger.debug("已开启消费线程");
        customerThread.start();

        // 开启"守护线程"-监控消息队列副本
//        Thread daemonThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        // 每次取最后一个列表元素（消费异常）
//                        String events = jedisAdapter.rpop(Constant.DESTINATION_KEY);
//                        if (!StringUtils.isEmpty(events)) {
//                            // 重新放入 生产者队列
//                            MQModel model = JSON.parseObject(events, MQModel.class);
//                            producer.createMessageQueue(model);
//                        }
//                        // 每隔50秒扫描一次
//                        Thread.sleep(50000);
//                    } catch (InterruptedException e) {
//                        logger.error("守护线程 - 监控消息队列副本异常{}", StackTraceUtil.getStackTrace(e));
//                    }
//                }
//            }
//        });
        //logger.debug("已开启守护线程-监控消息队列副本");
        //daemonThread.start();
    }

    /**
     * 消费者类型线程池
     *
     * @param model
     * @return
     */
    void process(MQModel model, String dataEntity) {
        // 根据生产者类型匹配消费者
        CustomerService service = config.get(model.getType());
        // 消费者类型
        FutureTask<Object> future =
                new FutureTask<Object>(new Callable<Object>() {
                    public Object call() {
                        StopWatch sw = new StopWatch();
                        sw.start();
                        // 开始消费
                        boolean isSuccess = process(model, service, dataEntity);
                        sw.stop();
                        logger.info("生产者消息主键:{},消息类型:{}已处理,共耗时{}秒", model.getId(), model.getType(), sw.getTotalTimeSeconds());
                        return isSuccess;
                    }
                });
        fixedThreadPool.execute(future);
        //return (boolean) future.get();
    }

    /**
     * 执行单个消费者类型
     *
     * @param model
     * @param service
     * @param dataEntity
     */
    boolean process(MQModel model, CustomerService service, String dataEntity) {
        try {
            // 执行消息类型代码块
            boolean isSuccess = service.doProcess(model);
            if (isSuccess) {
                // 消费者处理成功后同步消息队列副本
                jedisAdapter.lrem(Constant.DESTINATION_KEY, dataEntity);
            }
            return isSuccess;
        } catch (Exception e) {
            daemonThread();
            // 消费异常时 消息队列副本不会同步处理（消息队列安全）
            logger.error("生产者消息主键:{},消息类型:{}-消费者处理失败{}", model.getId(), model.getType(), StackTraceUtil.getStackTrace(e));
            return false;
        }
    }

    void daemonThread() {
        daemonThreadPool.schedule(new Runnable() {
            public void run() {
                // 每次取最后一个列表元素（消费异常）
                String events = jedisAdapter.rpop(Constant.DESTINATION_KEY);
                if (!StringUtils.isEmpty(events)) {
                    // 重新放入 生产者队列
                    MQModel model = JSON.parseObject(events, MQModel.class);
                    producer.createMessageQueue(model);
                }
            }
        }, 10, TimeUnit.SECONDS);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
