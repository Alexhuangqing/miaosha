package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Author Alex
 * @Desc <p></p>
 * @Date 2018/7/19 19:17
 */
@Mapper
public interface GoodsDao {
    @Select("select  g.* , mg.miaosha_price ,mg.stock_count,mg.start_date,mg.end_date,mg.stock_count from  " +
            "  miaosha_goods mg left join goods  g  on  mg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();


    @Select("select  g.* , mg.miaosha_price ,mg.stock_count,mg.start_date,mg.end_date,mg.stock_count from  " +
            "  miaosha_goods mg left join goods  g  on  mg.goods_id = g.id  where  goods_id = #{goodsId}")
    GoodsVo getGoodsVoById(@Param("goodsId") long goodsId);



    @Update("UPDATE miaosha_goods SET stock_count = stock_count - 1 WHERE  goods_id = #{goodsId} and stock_count > 0 ")
    void updateMiaoshaGoods(MiaoshaGoods miaoshaGoods);
}
