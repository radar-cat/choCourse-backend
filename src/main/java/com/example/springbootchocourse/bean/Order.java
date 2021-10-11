package com.example.springbootchocourse.bean;

import lombok.Data;

import java.util.Date;

@Data
public class Order {

    private Integer id;
    private Long user_id;
    private Integer course_id;
    private String course_name;
    private Date create_date;
}
