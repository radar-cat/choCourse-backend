package com.example.springbootchocourse.bean;

import lombok.Data;

@Data
public class OrderMap {

    private Integer id;
    private Long user_id;
    private Integer order_id;
    private Integer course_id;
}
