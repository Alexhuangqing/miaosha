package com.imooc.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @Author Alex
 * @Desc <p>接受者</p>
 * @Date 2018/7/26 6:53
 */
@Service
public class MQReceiver {
    public static Logger logger = LoggerFactory.getLogger(MQReceiver.class);


    /**
     *
     * Direct 模式
     */
    @RabbitListener(queues=MQConfig.QUEUE)
    public void receive(String msg){
        logger.info("Direct____接受"+msg);
    }
    /**
     *
     *  Header 模式
     */
    @RabbitListener(queues=MQConfig.HEADER_QUEUE)
    public void receive(byte[] msg){
        logger.info("Header____接受"+new String(msg));
    }

    /**
     * Topic 模式 Fanout 模式
     */
    @RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String msg){
        logger.info("Topic1____接受"+msg);
    }
    @RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String msg){
        logger.info("Topic2____接受"+msg);
    }
}
