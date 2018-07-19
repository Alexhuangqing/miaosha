package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping("/do_miaosha")
    public String miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
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
        return "miaosha_success";
    }

}
