package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.GoodsDao;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Alex
 * @Desc <p></p>
 * @Date 2018/7/19 19:29
 */
@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){

        return  goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoById(long goodsId) {
        return  goodsDao.getGoodsVoById(goodsId);
    }

    public boolean reduceStock(GoodsVo goodsVo) {
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        miaoshaGoods.setGoodsId(goodsVo.getId());
        //更新后，返回更新的记录数目
        int updated = goodsDao.updateMiaoshaGoods(miaoshaGoods);
        return updated > 0;
    }


}
