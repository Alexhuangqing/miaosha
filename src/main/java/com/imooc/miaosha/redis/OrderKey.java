package com.imooc.miaosha.redis;

public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
		super( prefix);
	}

	public static  OrderKey getOrderByUserIdAndGoodsId = new OrderKey("uandgId");

}
