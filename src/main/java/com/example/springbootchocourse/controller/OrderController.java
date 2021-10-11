package com.example.springbootchocourse.controller;

import com.alibaba.fastjson.JSON;
import com.example.springbootchocourse.bean.Order;
import com.example.springbootchocourse.bean.User;
import com.example.springbootchocourse.result.Result;
import com.example.springbootchocourse.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/order_list")
    public String getOrderByUid(@RequestBody User user){
        List<Order> oderList = orderService.getOrderByUid(user.getId());
        return JSON.toJSONString(Result.success(oderList));
    }
}
