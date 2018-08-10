package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Alex
 * @Desc <p>发送者</p>
 * @Date 2018/7/26 6:53
 */
@Service
public class MQSender {
    public static Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate ;




    /**
     *  direct模式 发送信息
     */
    public void senderMiaosha(Object message){
        String msg =RedisService.beanToString(message);
        logger.info("秒杀信息入队:"+msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
    }














    /**
     *  direct模式 发送信息
     */
    public void sender(Object message){
       String msg =RedisService.beanToString(message);
        logger.info("direct模式____发送"+msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }

    /**
     * topic模式 发送信息
     */
    public void senderTopic(Object message){
       String msg =RedisService.beanToString(message);
        logger.info("topic模式____发送"+msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key++",msg+"3");
    }
    /**
     * Fanout模式 发送信息
     */
    public void senderFanout(Object message){
       String msg =RedisService.beanToString(message);
        logger.info("Fanout模式 发送信息"+msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg+"Fanout模式");

    }
    /**
     * Header模式 发送信息
     */
    public void senderHeader(Object message){
       String msg =RedisService.beanToString(message);
        logger.info("Header模式"+msg);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("id","123");
        messageProperties.setHeader("pw","123");
        Message info = new Message(msg.getBytes(),messageProperties);
        amqpTemplate.convertAndSend(MQConfig.HEADER_EXCHANGE,"",info);

    }


}
