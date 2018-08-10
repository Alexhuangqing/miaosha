package com.imooc.miaosha.rabbitmq;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author Alex
 * @Desc <p>初始化bean</p>
 * @Date 2018/7/26 6:52
 */
@Configuration
public class MQConfig {





    //    Direct模式
    public static final String QUEUE = "queue";
    //    Topic模式
    public static final String TOPIC_QUEUE1 = "topic_queue1";
    public static final String TOPIC_QUEUE2 = "topic_queue2";
    public static final String TOPIC_EXCHANGE = "topic_exchange";
    public static final String TOPIC_KEY1 = "topic.key1"; //路由匹配规则
    //public static  final String TOPIC_KEY3 = "topic.key*"; // * 占位符，匹配一个字符
    public static final String TOPIC_KEY2 = "topic.#";  // # 匹配0个 或 1个字符
    //    Fanout模式
    public static final String FANOUT_EXCHANGE = "fanout_exchange";
    //Header模式
    public static final String HEADER_EXCHANGE = "header_exchange";
    public static final String HEADER_QUEUE = "header_queue";

    //秒杀业务
    public static final String MIAOSHA_QUEUE = "miaosha_queue";


    @Bean
    public Queue miaosha(){
        return new Queue(MIAOSHA_QUEUE,true);
    }

















    /**
     * Direct 模式
     */
/*
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }
*/

    /**
     * Topic 模式(路由列表)
     */
 /*   @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);
    }
*/
    /**
     * 这里返回值只能为具体的TopicExchange类型，不能为Exchange
     * 因为后面绑定中，不同的参数（Exchange）类型，会重载不同的方法
     */
 /*   @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding binding1() {
        //topic_queue1  路由  topic.key1
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(TOPIC_KEY1);
    }

    @Bean
    public Binding binding2() {
        //topic_queue2  路由  topic.#
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(TOPIC_KEY2);
    }
*/

    /**
     * Fanout 模式（广播）
     */
  /*  @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding bindingFanout1() {

        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding bindingFanout2() {

        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }*/

    /**
     * Header 模式（过滤消息）
     */
  /*  @Bean
    public Queue queue3() {
        return new Queue(HEADER_QUEUE, true);
    }

    @Bean
    public HeadersExchange headerExchange() {
        return new HeadersExchange(HEADER_EXCHANGE);
    }
    @Bean
    public Binding bindingHeader() {
        Map<String,Object> headerValues = new HashMap();
        headerValues.put("id","123");
        headerValues.put("pw","123");
        return BindingBuilder.bind(queue3()).to(headerExchange()).whereAll(headerValues).match();
    }*/


}
