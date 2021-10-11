package com.example.springbootchocourse.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class User {

    private Long id;
    private String name;
    private String password;
    private String salt;
    private Date register_date;
    private Date last_login_date;
    private Integer login_count;
    private Integer course_limit;
    private String role;
}
