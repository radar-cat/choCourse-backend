package com.example.springbootchocourse;

import com.example.springbootchocourse.util.MD5Util;

public class UserTest {
    public static void main(String[] args){
        String formPass = "123456";
        String dbPass = MD5Util.formPassToDBPass(formPass, MD5Util.salt);
        System.out.println(dbPass);
    }
}
