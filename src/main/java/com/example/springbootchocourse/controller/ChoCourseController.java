package com.example.springbootchocourse.controller;

import com.alibaba.fastjson.JSON;
import com.example.springbootchocourse.result.Result;
import com.example.springbootchocourse.service.ChoCourseService;
import com.example.springbootchocourse.vo.ChoCourseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class ChoCourseController {

    @Autowired
    ChoCourseService choCourseService;

    @PostMapping("/choose_course")
    public String goChoCourse(@RequestBody ChoCourseVo choCourseVo){
        Result<Integer> res = choCourseService.chooseCourse(choCourseVo.getUser(), choCourseVo.getCourseId());
        return JSON.toJSONString(res);
    }

    @PostMapping("/quit_course")
    public String goQuitCourse(@RequestBody ChoCourseVo choCourseVo){
        Result<Integer> res = choCourseService.quitCourse(choCourseVo.getUser(), choCourseVo.getCourseId());
        return JSON.toJSONString(res);
    }
}
