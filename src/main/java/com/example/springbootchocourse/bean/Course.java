package com.example.springbootchocourse.bean;

import lombok.Data;

@Data
public class Course {

    private Integer id;
    private String name;
    private Double counts;
    private String teacher;
    private String details;
    private String start_date;
    private String end_date;
    private Integer version;
    private Integer stock;
}
