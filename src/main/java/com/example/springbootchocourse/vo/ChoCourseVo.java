package com.example.springbootchocourse.vo;

import com.example.springbootchocourse.bean.User;
import lombok.Data;

@Data
public class ChoCourseVo {

    private User user;
    private Integer courseId;
}
