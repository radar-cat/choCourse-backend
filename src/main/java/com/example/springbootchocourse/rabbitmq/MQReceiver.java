package com.example.springbootchocourse.rabbitmq;


import com.example.springbootchocourse.bean.Course;
import com.example.springbootchocourse.bean.OrderMap;
import com.example.springbootchocourse.bean.User;
import com.example.springbootchocourse.redis.RedisService;
import com.example.springbootchocourse.service.ChoCourseService;
import com.example.springbootchocourse.service.CourseService;
import com.example.springbootchocourse.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jiangyunxiong on 2018/5/29.
 */
@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    CourseService courseService;

    @Autowired
    OrderService orderService;

    @Autowired
    ChoCourseService choCourseService;

    @RabbitListener(queues=MQConfig.QUEUE)
    public void receive(String message){
        log.info("receive message:"+message);
        ChocourseMessage m = RedisService.stringToBean(message, ChocourseMessage.class);
        User user = m.getUser();
        int courseId = m.getCourseId();
        int decOrInc = m.getDecOrInc();

        Course course = courseService.getCourseByCourseId(courseId);
        int stock = course.getStock();
        if(stock <= 0){
            return;
        }

        OrderMap orderMap = orderService.getOrderMapByUidCid(user.getId(), courseId);
        //减库存的情况
        if(decOrInc == 1){
            //判断重复秒杀
            if(orderMap != null) {
                return;
            }

            //减库存 下订单 写入秒杀订单
            choCourseService.choCourse(user, course);
        }else{
            //增库存的情况
            if(orderMap == null) {
                return;
            }

            //增库存 删除订单 删除秒杀订单
            choCourseService.quitCourse(user, course);
        }

    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info(" topic  queue1 message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info(" topic  queue2 message:" + message);
    }
}

