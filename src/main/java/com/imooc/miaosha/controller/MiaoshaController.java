package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.rabbitmq.MiaoshaMessage;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Alex
 * @Desc <p>秒杀前端控制</p>
 * @Date 2018/7/19 23:31
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqsender;

    //商品秒杀的状态位 当商品秒杀完成后 将不会对redis进行访问 共享变量
    private Map<String,Boolean> over = new HashMap<String, Boolean>();

    /**
     * 系统初始化，将秒杀商品库存加载到redis中去
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();

        for(GoodsVo goods:goodsVos){
            //将每条秒杀商品的库存预加到redis中 同时初始化内存商品状态位
            redisService.set(GoodsKey.getGoodsStock,""+goods.getId(),goods.getStockCount());
            over.put(""+goods.getId(), false);

        }

    }

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


    @RequestMapping("{path}/do_miaosha")
    @ResponseBody
    public Result<Integer> miaosha(MiaoshaUser user, @RequestParam("goodsId") long goodsId,
                                   @PathVariable("path") String path){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

       boolean check = miaoshaService.cheackPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }



        if(over.get("" + goodsId)){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }


        //利用redis缓存中的数据，预先减少库存
        //todo 缓存中的数据可能被同一用户重复刷，导致缓存中数据小于数据库中的数据,
        // todo 或者一个队列中有压入多条该人的相同订单消息
        long decr = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);
        if(decr < 0){
            over.put(""+goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //检验是否已经存在订单
        MiaoshaOrder order = orderService.getOrderByUserAndGoods(user.getId(), goodsId);
        if(order!=null){
            return Result.error(CodeMsg.MIAO_SHA_REPEAT);
        }

        //请求入队,异步
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setGoodsId(goodsId);
        mm.setUser(user);

        mqsender.senderMiaosha(mm);

        return Result.success(0);
        /**
         *  //查看库存
         *
         *         //重复秒杀（秒杀表里面是否有记录）
         *         MiaoshaOrder miaoshaOrder =  orderService.getOrderByUserAndGoods(user.getId(),goodsId);
         *         if(miaoshaOrder!=null){
         *             return Result.error(CodeMsg.MIAO_SHA_REPEAT);
         *
         *         }
         *
         *         //减库存 下订单  订单数据转给前台
         *        OrderInfo  orderInfo =  miaoshaService.doMiaosha(user,goodsVo);
         *         //问题一：在没有生成订单之前，一个用户两个请求可能同时到这里，形成一个用户两个订单
         *         //问题二：这里可能出现多请求走到这里，出现买超秒杀库存的情况
         * //        OrderDetailVo orderDetailVo = new OrderDetailVo();
         * //        orderDetailVo.setOrder(orderInfo);
         * //        orderDetailVo.setGoods(goodsVo);
         *           return Result.success(orderDetailVo);
         */
    }


    /**
     *orderInfoId : 订单详情id
     * 0；排队中
     * -1：排队失败
     */
    @RequestMapping("/result")
    @ResponseBody
    public Result<Long> miaoshaResult(MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        long rt = miaoshaService.miaoshaResult(user.getId(), goodsId);
        return Result.success(rt);
    }

    @RequestMapping("/path")
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoshaUser user, @RequestParam("goodsId") long goodsId){


        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        String path = miaoshaService.createPath(user,goodsId);

        return Result.success(path);
    }
}
