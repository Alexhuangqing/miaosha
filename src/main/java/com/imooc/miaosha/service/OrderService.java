package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.OrderDao;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.OrderKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author Alex
 * @Desc <p></p>
 * @Date 2018/7/19 23:45
 */
@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;
    @Autowired
    RedisService redisService;

    public MiaoshaOrder getOrderByUserAndGoods(Long userId, long goodsId) {
//        return  orderDao.getOrderByUserAndGoods(userId,goodsId);
      return    redisService.get(OrderKey.getOrderByUserIdAndGoodsId,""+userId+"_"+goodsId,MiaoshaOrder.class);
    }


    @Transactional
    public OrderInfo CreateOrder(MiaoshaUser user, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
         orderDao.insetOrdereInfo(orderInfo);//插入后，自动将自增ID封装到bean中

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrder.setGoodsId(goodsVo.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        //存入redis缓存
        redisService.set(OrderKey.getOrderByUserIdAndGoodsId,""+user.getId()+"_"+goodsVo.getId(),miaoshaOrder);
        return  orderInfo;
    }

    //获取完整订单信息
    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
