package com.example.springbootchocourse.controller;

import com.alibaba.fastjson.JSON;
import com.example.springbootchocourse.bean.Course;
import com.example.springbootchocourse.result.Result;
import com.example.springbootchocourse.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app")
public class CourseController {

    @Autowired
    CourseService courseService;

    @GetMapping("/all_course")
    public String getCourseList(){
        List<Course> courseList = courseService.getCourseList();
        return JSON.toJSONString(Result.success(courseList));
    }

    @GetMapping("/query_course")
    public String getSearchCourse(@RequestParam("query") String query){
        List<Course> searchCourse = courseService.getSearchCourse("%"+query+"%");
        return JSON.toJSONString(Result.success(searchCourse));
    }

    @PostMapping("/add_course")
    public String addCourse(@RequestBody Course course){
        return JSON.toJSONString(courseService.addCourse(course));
    }

    @GetMapping("/get_update_course")
    public String getUpdateCourse(int id){
        return JSON.toJSONString(courseService.getUpdateCourse(id));
    }

    @PostMapping("/edit_course")
    public String editCourse(@RequestBody Course course){
        return JSON.toJSONString(courseService.editCourse(course));
    }

    @PostMapping("/delete_course")
    public String deleteCourse(int id){
        return JSON.toJSONString(courseService.deleteCourse(id));
    }
}
