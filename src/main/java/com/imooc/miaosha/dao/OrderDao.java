package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoshaOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author Alex
 * @Desc <p></p>
 * @Date 2018/7/20 0:13
 */
@Mapper
public interface OrderDao {
    @Select("select * from miaosha_order where user_id= #{userId} and  goods_id = #{goodsId}")
    MiaoshaOrder getOrderByUserAndGoods(@Param("userId") Long userId,@Param("goodsId")long goodsId);
}
