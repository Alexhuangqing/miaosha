package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.GoodsDao;
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

    public void reduceStock(GoodsVo goodsVo) {

    }
}
