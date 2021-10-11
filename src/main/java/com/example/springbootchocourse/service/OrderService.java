package com.example.springbootchocourse.service;

import com.example.springbootchocourse.bean.Course;
import com.example.springbootchocourse.bean.Order;
import com.example.springbootchocourse.bean.OrderMap;
import com.example.springbootchocourse.bean.User;
import com.example.springbootchocourse.mapper.OrderMapper;
import com.example.springbootchocourse.redis.OrderKey;
import com.example.springbootchocourse.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    RedisService redisService;

    @Autowired
    OrderMapper orderMapper;

    public OrderMap getOrderMapByUidCid(long userId, int courseId) {
        return redisService.get(OrderKey.getOrderMapByUidCid, "" + userId + "_" + courseId, OrderMap.class);
    }

    /**
     * 因为要同时分别在订单表和订单映射表都新增一条数据，所以要保证两个操作是一个事务
     */
    @Transactional
    public Order createOrder(User user, Course course) {
        Order order = new Order();
        order.setCreate_date(new Date());
        order.setCourse_id(course.getId());
        order.setCourse_name(course.getName());
        order.setUser_id(user.getId());
        orderMapper.insert(order);

        OrderMap orderMap = new OrderMap();
        orderMap.setCourse_id(course.getId());
        orderMap.setOrder_id(order.getId());
        orderMap.setUser_id(user.getId());
        orderMapper.insertOrderMap(orderMap);

        redisService.set(OrderKey.getOrderMapByUidCid, "" + user.getId() + "_" + course.getId(), orderMap);

        return order;
    }

    @Transactional
    public void deleteOrder(User user, Course course) {
        long uId = user.getId();
        int cId = course.getId();
        orderMapper.deleteOrder(uId, cId);
        orderMapper.deleteOrderMap(uId, cId);

        redisService.delete(OrderKey.getOrderMapByUidCid, "" + user.getId() + "_" + course.getId());
    }

    public List<Order> getOrderByUid(long id){
        return orderMapper.getOrderByUId(id);
    }
}
