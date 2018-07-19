package com.imooc.miaosha.controller;

import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.service.MiaoshaUserService;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;
	
    @RequestMapping("/to_list")
    public String list(Model model,MiaoshaUser user) {
    	model.addAttribute("user", user);
		List<GoodsVo> goodsVos = goodsService.listGoodsVo();
		model.addAttribute("goodsList",goodsVos);
		return "goods_list";
    }

	@RequestMapping("/to_detail/{goodsId}")
	public String detail(Model model, MiaoshaUser user, @PathVariable long goodsId) {

		GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
		//根据不同的状态位显示文案
      long  atStart = goodsVo.getStartDate().getTime();
      long  atEnd = goodsVo.getEndDate().getTime();
      long atNow = System.currentTimeMillis();
      int miaoshaStatus = 0;
      long remainSeconds = 0L;
      if(atNow < atStart){
      	//秒杀未开始
		  remainSeconds = (atStart - atNow)/1000;

	  }else if(atNow < atEnd){
      	//秒杀进行中
		  miaoshaStatus = 1;
		  remainSeconds = 0;

	  }else {
      	//秒杀已结束
		  miaoshaStatus = 2;
		  remainSeconds = -1;
	  }
		model.addAttribute("user", user);
		model.addAttribute("goods",goodsVo);
		model.addAttribute("miaoshaStatus",miaoshaStatus);
		model.addAttribute("remainSeconds",remainSeconds);

		return "goods_detail";
	}
    
}
