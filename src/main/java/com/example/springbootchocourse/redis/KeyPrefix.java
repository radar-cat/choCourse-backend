package com.example.springbootchocourse.redis;

public interface KeyPrefix {

    int expireSeconds();
    String getPrefix();
}
