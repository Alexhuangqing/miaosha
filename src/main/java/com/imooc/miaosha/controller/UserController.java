package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
/**
 * @Author Alex
 * @Desc <p>测试Jmeter</p>
 * @Date 2018/7/19 23:31
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/info")
	@ResponseBody
    public Result<MiaoshaUser> list(MiaoshaUser user) {

		return Result.success(user);
    }
}
