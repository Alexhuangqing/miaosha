package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Alex
 * @Desc <p>接受者</p>
 * @Date 2018/7/26 6:53
 */
@Service
public class MQReceiver {
    public static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    GoodsService goodsService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receiveMiaosha(String msg) {
        logger.info("秒杀信息出队:" + msg);
        MiaoshaMessage mm = redisService.stringToBean(msg, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();
        //数据库中查看库存
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        if (goodsVo.getStockCount() <= 0) {

            //todo 排队失败 redis中要做防刷处理
            return;

        }


        //检验是否已经存在订单
        MiaoshaOrder order = orderService.getOrderByUserAndGoods(user.getId(), goodsId);
        if (order != null) {
            // todo 排队失败 redis中要做防刷处理
            return;
        }
        //秒杀业务  去库存 下订单 将订单信息写入缓存 得到订单信息
        OrderInfo orderInfo = miaoshaService.doMiaosha(user, goodsVo);

    }


    /**
     * Direct 模式
     */
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String msg) {
        logger.info("Direct____接受" + msg);
    }

    /**
     * Header 模式
     */
    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
    public void receive(byte[] msg) {
        logger.info("Header____接受" + new String(msg));
    }

    /**
     * Topic 模式 Fanout 模式
     */
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String msg) {
        logger.info("Topic1____接受" + msg);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String msg) {
        logger.info("Topic2____接受" + msg);
    }
}
