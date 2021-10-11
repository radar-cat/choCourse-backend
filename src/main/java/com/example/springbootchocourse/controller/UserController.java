package com.example.springbootchocourse.controller;

import com.alibaba.fastjson.JSON;
import com.example.springbootchocourse.result.Result;
import com.example.springbootchocourse.service.UserService;
import com.example.springbootchocourse.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody LoginVo loginVo){
        String token = userService.login(loginVo);
        return JSON.toJSONString(Result.success(token));
    }

    @PostMapping("/edit_limit")
    public String editLimit(int limit){
        return JSON.toJSONString(userService.editLimit(limit));
    }
}
