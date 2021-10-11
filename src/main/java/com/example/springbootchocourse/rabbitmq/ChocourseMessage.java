package com.example.springbootchocourse.rabbitmq;

import com.example.springbootchocourse.bean.User;
import lombok.Data;

@Data
public class ChocourseMessage {

    private User user;
    private int courseId;
    private int decOrInc;//判断减库存还是增库存，1代表减，0代表增
}
