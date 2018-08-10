package com.imooc.miaosha.service;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Alex
 * @Desc <p>秒杀业务实现</p>
 * @Date 2018/7/19 23:56
 */
@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;



    /**
     * 多表操作，涉及到事务，秒杀业务实现
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    public OrderInfo  doMiaosha(MiaoshaUser user, GoodsVo goodsVo) {
        //减库存 下订单 并且返回订单的详细信息
        boolean success = goodsService.reduceStock(goodsVo);
        if(success){
            //减库存成功，生成订单，redis中缓存订单 排队成功
            return orderService.CreateOrder(user, goodsVo);
        }else{
            //减库存失败，说明秒杀结束，redis中缓存失败信息 排队失败
            setGoodsOver(goodsVo.getId());
            return null;
        }

    }

    private void setGoodsOver(long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver,""+goodsId,true);


    }
    private boolean  getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);

    }

    /**
     * 返回秒杀结果
     *orderInfoId : 订单详情id
     * 0；排队中
     * -1：排队失败
     */
    public long miaoshaResult(long userId,long  goodsId){
        MiaoshaOrder order = orderService.getOrderByUserAndGoods(userId, goodsId);
        if(order!=null){
            return order.getOrderId();
        }else{
            if(getGoodsOver(goodsId)){
                return -1;
            }else {
                return 0;
            }
        }

    }

    /**
     * 校验秒杀路径
     */
    public boolean cheackPath(MiaoshaUser user, long goodsId, String path) {
        if(user==null || goodsId <= 0 || StringUtils.isEmpty(path)){
            return false;
        }
        String oldPath = redisService.get(MiaoshaKey.getMiaoshaPath,""+user.getId()+"_"+goodsId,String.class);
        return path.equals(oldPath);
    }

    /**
     * 生成秒杀路径
     */
    public String createPath(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <= 0 ){
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath,""+user.getId()+"_"+goodsId,str);
        return str;
    }
}
