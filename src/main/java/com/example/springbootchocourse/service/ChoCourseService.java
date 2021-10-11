package com.example.springbootchocourse.service;

import com.example.springbootchocourse.bean.Course;
import com.example.springbootchocourse.bean.Order;
import com.example.springbootchocourse.bean.OrderMap;
import com.example.springbootchocourse.bean.User;
import com.example.springbootchocourse.rabbitmq.ChocourseMessage;
import com.example.springbootchocourse.rabbitmq.MQSender;
import com.example.springbootchocourse.redis.ChoCourseKey;
import com.example.springbootchocourse.redis.CourseKey;
import com.example.springbootchocourse.redis.RedisService;
import com.example.springbootchocourse.redis.UserKey;
import com.example.springbootchocourse.result.CodeMsg;
import com.example.springbootchocourse.result.Result;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ChoCourseService implements InitializingBean {

    @Autowired
    RedisService redisService;

    @Autowired
    UserService userService;

    @Autowired
    CourseService courseService;

    @Autowired
    OrderService orderService;

    @Autowired
    MQSender sender;

    //基于令牌桶算法的限流实现类
    RateLimiter rateLimiter = RateLimiter.create(10);

    //做标记，判断该商品是否被处理过了
    private HashMap<Integer, Boolean> localOverMap = new HashMap<Integer, Boolean>();

    public Result<Integer> chooseCourse(User user, Integer courseId){
        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //判断是否在选课时间
        String startDate = redisService.get(CourseKey.getStartDate, "" + courseId, String.class);
        String endDate = redisService.get(CourseKey.getEndDate, "" + courseId, String.class);
//        System.out.println(startDate);
        if(startDate == null || endDate == null){
            afterPropertiesSet();
            Result.error(CodeMsg.TRY_AGAIN);
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        if(startDate.compareTo(df.format(new Date())) > 0){
            return Result.error(CodeMsg.TOO_EARLY);
        }
        if(endDate.compareTo(df.format(new Date())) < 0){
            return Result.error(CodeMsg.TOO_LATE);
        }

        //内存标记，减少redis访问
        boolean over = localOverMap.get(courseId);
        if (over) {
            return Result.error(CodeMsg.CHOCOURSE_OVER);
        }

        //判断是否达到选课限额
        User user1 = redisService.get(UserKey.getById, "" + user.getId(), User.class);
        if(user1 ==null){
            return Result.error(CodeMsg.CANNOT_LOAD);
        }
        int limit = user1.getCourse_limit();
        if(limit <= 0){
            return Result.error(CodeMsg.REACH_LIMIT);
        }

        //预减库存
        long stock = redisService.decr(CourseKey.getCourseStock, "" + courseId);
        if (stock < 0) {
            afterPropertiesSet();
            long stock2 = redisService.decr(CourseKey.getCourseStock, "" + courseId);
            if(stock2 < 0){
                localOverMap.put(courseId, true);
                return Result.error(CodeMsg.CHOCOURSE_OVER);
            }
        }

        //判断重复秒杀
        OrderMap order = orderService.getOrderMapByUidCid(user.getId(), courseId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_CHOCOURSE);
        }

        //可选课程数-1
        user1.setCourse_limit(limit-1);
        redisService.set(UserKey.getById, "" + user.getId(), user1);

        //入队
        ChocourseMessage message = new ChocourseMessage();
        message.setUser(user);
        message.setCourseId(courseId);
        message.setDecOrInc(1);
        sender.sendChocourseMessage(message);
        return Result.success(0);//排队中
    }

    public Result<Integer> quitCourse(User user, Integer courseId){
        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //判断是否在选课时间
        String startDate = redisService.get(CourseKey.getStartDate, "" + courseId, String.class);
        String endDate = redisService.get(CourseKey.getEndDate, "" + courseId, String.class);
        if(startDate == null || endDate == null){
            afterPropertiesSet();
            Result.error(CodeMsg.TRY_AGAIN);
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        if(startDate.compareTo(df.format(new Date())) > 0){
            return Result.error(CodeMsg.TOO_EARLY);
        }
        if(endDate.compareTo(df.format(new Date())) < 0){
            return Result.error(CodeMsg.TOO_LATE);
        }

        //判断重复退选
        OrderMap order = orderService.getOrderMapByUidCid(user.getId(), courseId);
        if (order == null) {
            return Result.error(CodeMsg.REPEATE_QUITCOURSE);
        }

        //预增库存
        long stock = redisService.incr(CourseKey.getCourseStock, "" + courseId);
        if (stock == 1) {
            afterPropertiesSet();
            long stock2 = redisService.incr(CourseKey.getCourseStock, "" + courseId);
            if(stock2 == 1){
                localOverMap.put(courseId, false);
            }
        }

        //限选课程数+1
        User user1 = redisService.get(UserKey.getById, "" + user.getId(), User.class);
        if(user1 ==null){
            return Result.error(CodeMsg.CANNOT_LOAD);
        }
        int limit = user1.getCourse_limit();
        user1.setCourse_limit(++limit);
        redisService.set(UserKey.getById, "" + user.getId(), user1);

        //入队
        ChocourseMessage message = new ChocourseMessage();
        message.setUser(user);
        message.setCourseId(courseId);
        message.setDecOrInc(0);
        sender.sendChocourseMessage(message);
        return Result.success(0);//排队中
    }

    /**
     * 系统初始化,将课程信息加载到redis和本地内存
     */
    @Override
    public void afterPropertiesSet() {
        List<Course> courseList = courseService.getCourseList();
        if (courseList == null) {
            return;
        }
        for (Course course : courseList) {
            redisService.set(CourseKey.getCourseStock, "" + course.getId(), course.getStock());
            redisService.set(CourseKey.getStartDate, "" + course.getId(), course.getStart_date());
            redisService.set(CourseKey.getEndDate, "" + course.getId(), course.getEnd_date());
            //初始化课程都是没有处理过的
            localOverMap.put(course.getId(), false);
        }
    }

    //保证这三个操作，减库存、下订单、写入秒杀订单是一个事务
    @Transactional
    public Order choCourse(User user, Course course){
        //减库存
        boolean success = courseService.reduceStock(course);
        if (success){
            //下订单 写入秒杀订单
            return orderService.createOrder(user, course);
        }else {
            setCourseOver(course.getId());
            return null;
        }
    }

    //保证这三个操作，增库存、删订单、删除秒杀订单是一个事务
    @Transactional
    public void quitCourse(User user, Course course){
        //增库存
        boolean success = courseService.increaseStock(course);
        if (success){
            //删除订单 删除秒杀订单
            orderService.deleteOrder(user, course);
        }
    }

    private void setCourseOver(Integer courseId) {
        redisService.set(ChoCourseKey.isCourseOver, ""+courseId, true);
    }
}
