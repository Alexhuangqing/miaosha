package com.imooc.miaosha.controller;

import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.vo.GoodsDetailVo;
import com.imooc.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.service.MiaoshaUserService;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	@Autowired
	ApplicationContext appctx;

	/**
	 * 优化前：
	 *100连接数，每个链接循环500次请求 ，load：12，QPS：1955
	 * 缓存优化后
	 * load：1~2 ,QPS：5087
	 */
    @RequestMapping(value = "/to_list",produces = "text/html")
	@ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response,Model model, MiaoshaUser user) {
    	//1.从缓存中取走缓存的静态页面源代码，能取到就返回
		String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
		if(StringUtils.isNotBlank(html)){
			return html;
		}
		//2.如缓存中没有数据，就到数据库中读取数据，用视图解析器手动渲染模板与数据
    	model.addAttribute("user", user);
		List<GoodsVo> goodsVos = goodsService.listGoodsVo();
		model.addAttribute("goodsList",goodsVos);

		IContext ctx = new SpringWebContext(request,response,request.getServletContext(),request.getLocale(),model
				.asMap(),appctx);
		 html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
		 if(StringUtils.isNotBlank(html)){
		 	redisService.set(GoodsKey.getGoodsList, "", html);
		 }
		return html;
    }

	@RequestMapping(value = "/to_detail1/{goodsId}",produces = "text/html")
	@ResponseBody
	public String detail1(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
						  @PathVariable long goodsId) {
		//1.从缓存中取走缓存的静态页面源代码，能取到就返回
		String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
		if(StringUtils.isNotBlank(html)){
			return html;
		}
		//2.如缓存中没有数据，就到数据库中读取数据，用视图解析器手动渲染模板与数据
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
		IContext ctx = new SpringWebContext(request,response,request.getServletContext(),request.getLocale(),model
				.asMap(),appctx);
		html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
		if(StringUtils.isNotBlank(html)){
			redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
		}
		return html;

	}
	@RequestMapping(value = "/detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> detail(MiaoshaUser user,
						 @PathVariable long goodsId) {

		GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
		//根据不同的状态位显示文案
      int  atStart = (int)goodsVo.getStartDate().getTime();
      int  atEnd = (int)goodsVo.getEndDate().getTime();
      int atNow = (int)System.currentTimeMillis();
      int miaoshaStatus = 0;
      int remainSeconds = 0;
      if(atNow < atStart){
      	//秒杀未开始
		  remainSeconds = (atStart - atNow)/1000;

	  }else if(atNow < atEnd){
      	//秒杀进行中
		  miaoshaStatus = 1;


	  }else {
      	//秒杀已结束
		  miaoshaStatus = 2;
		  remainSeconds = -1;
	  }

		GoodsDetailVo vo = new GoodsDetailVo();
		vo.setGoods(goodsVo);
		vo.setUser(user);
		vo.setRemainSeconds(remainSeconds);
		vo.setMiaoshaStatus(miaoshaStatus);
		return Result.success(vo);

	}

}
