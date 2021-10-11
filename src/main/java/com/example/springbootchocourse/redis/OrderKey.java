package com.example.springbootchocourse.redis;

public class OrderKey extends BasePrefix{

    public OrderKey(String prefix) {
        super(prefix);
    }
    public static OrderKey getOrderMapByUidCid = new OrderKey("chocourse");
}
