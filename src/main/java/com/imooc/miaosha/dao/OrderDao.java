package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

/**
 * @Author Alex
 * @Desc <p></p>
 * @Date 2018/7/20 0:13
 */
@Mapper
public interface OrderDao {
    @Select("select * from miaosha_order where user_id= #{userId} and  goods_id = #{goodsId}")
    MiaoshaOrder getOrderByUserAndGoods(@Param("userId") Long userId,@Param("goodsId")long goodsId);

    @Insert("insert order_info " +
            "(user_id,goods_id,delivery_addr_id,goods_name,goods_count,goods_price,order_channel,status,create_date)" +
            "values" +
            "(#{userId},#{goodsId},#{deliveryAddrId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel}," +
            "#{status},#{createDate})")
    @SelectKey(keyColumn = "id" ,keyProperty = "id",resultType = long.class ,before = false,statement = "select " +
            " last_insert_id()")
    long insetOrdereInfo(OrderInfo orderInfo);

    @Insert("insert miaosha_order (user_id,order_id,goods_id)" +
            " values(#{userId},#{orderId},#{goodsId})")
    void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
