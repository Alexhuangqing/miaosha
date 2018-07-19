package com.imooc.miaosha.service;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Alex
 * @Desc <p></p>
 * @Date 2018/7/19 23:56
 */
@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;


    /**
     * 多表操作，涉及到事物
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    public OrderInfo doMiaosha(MiaoshaUser user, GoodsVo goodsVo) {
        //减库存 下订单 并且返回订单的详细信息
        goodsService.reduceStock(goodsVo);
        return orderService.CreateOrder(user,goodsVo);
    }
}
