package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author Alex
 * @Desc <p>秒杀前端控制</p>
 * @Date 2018/7/19 23:31
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    /**
     * 优化前
     * qps:1629
     *1000*50
     */
    @RequestMapping("/do_miaosha1")
    public String miaosha1(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        if(user == null){
            return "login";
        }
        //查看库存
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        if(goodsVo.getStockCount()<=0){
            model.addAttribute("errMsg",CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //重复秒杀
        MiaoshaOrder miaoshaOrder =  orderService.getOrderByUserAndGoods(user.getId(),goodsId);
        if(miaoshaOrder!=null){
            model.addAttribute("errMsg",CodeMsg.MIAO_SHA_REPEAT.getMsg());
            return "miaosha_fail";
        }

        //减库存 下订单 并且返回订单的详细信息
       OrderInfo  orderInfo =  miaoshaService.doMiaosha(user,goodsVo);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVo);
        return "order_datail";
    }


    @RequestMapping("/do_miaosha")
    @ResponseBody
    public Result<OrderInfo> miaosha(MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //查看库存
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        if(goodsVo.getStockCount()<=0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);

        }
        //重复秒杀（秒杀表里面是否有记录）
        MiaoshaOrder miaoshaOrder =  orderService.getOrderByUserAndGoods(user.getId(),goodsId);
        if(miaoshaOrder!=null){
            return Result.error(CodeMsg.MIAO_SHA_REPEAT);

        }

        //减库存 下订单  订单数据转给前台
       OrderInfo  orderInfo =  miaoshaService.doMiaosha(user,goodsVo);
        //问题一：在没有生成订单之前，一个用户两个请求可能同时到这里，形成一个用户两个订单
        //问题二：这里可能出现多请求走到这里，出现买超秒杀库存的情况
//        OrderDetailVo orderDetailVo = new OrderDetailVo();
//        orderDetailVo.setOrder(orderInfo);
//        orderDetailVo.setGoods(goodsVo);
        return Result.success(orderInfo);
    }

}
