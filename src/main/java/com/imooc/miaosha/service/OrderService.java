package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.OrderDao;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Alex
 * @Desc <p></p>
 * @Date 2018/7/19 23:45
 */
@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    public MiaoshaOrder getOrderByUserAndGoods(Long userId, long goodsId) {
        return  orderDao.getOrderByUserAndGoods(userId,goodsId);
    }

    @Transactional
    public OrderInfo CreateOrder(MiaoshaUser user, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        return  orderInfo;
    }
}
